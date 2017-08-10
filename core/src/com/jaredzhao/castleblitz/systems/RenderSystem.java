package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.RemoveTagComponent;
import com.jaredzhao.castleblitz.components.audio.MusicComponent;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.mechanics.BattleMechanicsStatesComponent;
import com.jaredzhao.castleblitz.components.mechanics.FixedScreenPositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.utils.LayerSorter;
import com.jaredzhao.castleblitz.utils.ShaderBatch;
import com.jaredzhao.castleblitz.utils.TeamColorDecoder;

import java.util.ArrayList;
import java.util.Map;

public class RenderSystem extends EntitySystem {

    private SpriteBatch uiBatch;
    private ShaderBatch batch;
    private OrthographicCamera orthographicCamera;
    private Sprite currentSprite;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont debugFont, pausedFont, signInFont1, signInFont2;
    private GlyphLayout layout;

    private Engine ashleyEngine;

    private SettingsComponent settingsComponent;
    private BattleMechanicsStatesComponent battleMechanicsStatesComponent;
    private FogOfWarComponent fogOfWarComponent;
    private Entity fogOfWar;

    private ImmutableArray<Entity> renderables;

    private LayerSorter layerSorter;

    private ComponentMapper<AnimationComponent> animationComponentComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SpriteComponent> spriteComponentComponentMapper = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<LayerComponent> layerComponentComponentMapper = ComponentMapper.getFor(LayerComponent.class);
    private ComponentMapper<FixedScreenPositionComponent> fixedScreenPositionComponentComponentMapper = ComponentMapper.getFor(FixedScreenPositionComponent.class);

    public RenderSystem(Engine ashleyEngine, Entity camera, Entity settings, Entity battleMechanics, Entity fogOfWar){

        batch = new ShaderBatch(100); //SpriteBatch for rendering entities
        batch.brightness = 0.12f;
        batch.contrast = 1.6f;
        uiBatch = new SpriteBatch(); //SpriteBatch for rendering UI / debug text
        orthographicCamera = camera.getComponent(CameraComponent.class).camera; //Camera for easy access and for determing render location

        this.ashleyEngine = ashleyEngine;
        this.settingsComponent = settings.getComponent(SettingsComponent.class);
        this.battleMechanicsStatesComponent = battleMechanics.getComponent(BattleMechanicsStatesComponent.class);
        this.fogOfWarComponent = fogOfWar.getComponent(FogOfWarComponent.class);
        this.fogOfWar = fogOfWar;

        debugFont = new BitmapFont();
        //font = new BitmapFont();
        //font.setColor(Color.WHITE);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/slkscrb.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.color = Color.WHITE;
        fontParameter.size = Gdx.graphics.getHeight() / 25;
        pausedFont = fontGenerator.generateFont(fontParameter);
        fontParameter.size = Gdx.graphics.getHeight() / 15;
        signInFont1 = fontGenerator.generateFont(fontParameter);
        fontParameter.size = Gdx.graphics.getHeight() / 35;
        signInFont2 = fontGenerator.generateFont(fontParameter);
        layout = new GlyphLayout();

        layerSorter = new LayerSorter();
    }

    public void addedToEngine(Engine engine){
        renderables = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class).get());
    }

    public void updateFixedPositionRenderables(){
        for(Entity entity : renderables){
            PositionComponent position = positionComponentComponentMapper.get(entity);
            FixedScreenPositionComponent fixedScreenPositionComponent = fixedScreenPositionComponentComponentMapper.get(entity);
            if(fixedScreenPositionComponent != null) {
                position.x = fixedScreenPositionComponent.x + orthographicCamera.position.x - orthographicCamera.viewportWidth / 2;
                position.y = fixedScreenPositionComponent.y + orthographicCamera.position.y - orthographicCamera.viewportHeight / 2;
            }
        }
    }

    public void update(float deltaTime) {
        Gdx.gl.glClearColor(.06f, .06f, .22f, 1f); //Background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //Clear screen

        updateFixedPositionRenderables();

        boolean didRenderFogOfWar = false;

        orthographicCamera.update();
        batch.setProjectionMatrix(orthographicCamera.projection);

        batch.begin(); //Render entities

        for (Entity entity : layerSorter.sortByLayers(renderables).values()) {
            PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
            SpriteComponent spriteComponent = spriteComponentComponentMapper.get(entity);
            AnimationComponent animationComponent = animationComponentComponentMapper.get(entity);

            LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
            LayerComponent fogOfWarLayerComponent = layerComponentComponentMapper.get(fogOfWar);
            if(!didRenderFogOfWar && layerComponent.layer > fogOfWarLayerComponent.layer){
                SpriteComponent fogOfWarSpriteComponent = spriteComponentComponentMapper.get(fogOfWar);
                for (int i = 0; i < fogOfWarComponent.viewMap.length; i++) {
                    for (int j = 0; j < fogOfWarComponent.viewMap[0].length; j++) {
                        fogOfWarSpriteComponent.spriteList.get(fogOfWarComponent.viewMap[i][j]).get(0).setPosition((i * 16 - orthographicCamera.position.x - 8 + (orthographicCamera.viewportWidth / 2)),
                                (j * 16 - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - 8));
                        fogOfWarSpriteComponent.spriteList.get(fogOfWarComponent.viewMap[i][j]).get(0).draw(batch);
                    }
                }
                didRenderFogOfWar = true;
            }

            if (animationComponent.framesDisplayed != -1) {
                if (0 > animationComponent.animationTimeList.get(animationComponent.currentTrack).get(animationComponent.currentFrame)) {
                    currentSprite = spriteComponent.spriteList.get(animationComponent.currentTrack).get(animationComponent.currentFrame);
                } else if (animationComponent.framesDisplayed <= animationComponent.animationTimeList.get(animationComponent.currentTrack).get(animationComponent.currentFrame)) {
                    currentSprite = spriteComponent.spriteList.get(animationComponent.currentTrack).get(animationComponent.currentFrame);
                    animationComponent.framesDisplayed++;
                } else {
                    animationComponent.framesDisplayed = 0;
                    if (animationComponent.currentFrame < animationComponent.animationTimeList.get(animationComponent.currentTrack).size() - 1) {
                        animationComponent.currentFrame++;
                    } else {
                        animationComponent.currentFrame = 0;
                    }
                    currentSprite = spriteComponent.spriteList.get(animationComponent.currentTrack).get(animationComponent.currentFrame);
                }
                if (entity.getComponent(TileComponent.class) != null) {
                    currentSprite.setPosition((positionComponent.x - orthographicCamera.position.x - (currentSprite.getWidth() / 2) + (orthographicCamera.viewportWidth / 2)),
                            (positionComponent.y - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - 8));
                } else {
                    currentSprite.setPosition((positionComponent.x - orthographicCamera.position.x - (currentSprite.getWidth() / 2) + (orthographicCamera.viewportWidth / 2)),
                            (positionComponent.y - orthographicCamera.position.y - (currentSprite.getHeight() / 2) + (orthographicCamera.viewportHeight / 2)));
                }
                currentSprite.draw(batch);
            } else {
                entity.add(new RemoveTagComponent());
            }
        }

        batch.end();

        uiBatch.begin(); //Render UI

        if (settingsComponent.isPaused) {
            layout.setText(pausedFont, "PAUSED");
            pausedFont.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 2);
        }

        if (GameEngine.currentScene == 1) {
            if (battleMechanicsStatesComponent.isMyTurn) {
                layout.setText(signInFont2, "YOUR TURN");
                signInFont2.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 13 / 16 + 1.5f * layout.height);
            } else {
                layout.setText(signInFont2, "OPPONENT'S TURN");
                signInFont2.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 13 / 16 + 1.5f * layout.height);
            }
        } else if (GameEngine.currentScene == 2) {
            layout.setText(signInFont1, "SIGN IN");
            signInFont1.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4 + 1.5f * layout.height);

            layout.setText(signInFont2, "IT'S GOOD FOR YOU");
            signInFont2.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4);
        } else if (GameEngine.currentScene == 3) {
            if (settingsComponent.homeScreen.equals("homeShop")) {
                layout.setText(signInFont1, "Shop");
                signInFont1.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
            } else if (settingsComponent.homeScreen.equals("homeArmory")) {
                layout.setText(signInFont1, "Armory");
                signInFont1.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
            } else if (settingsComponent.homeScreen.equals("homeCastle")) {
                layout.setText(signInFont1, "Castle");
                signInFont1.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
            } else if (settingsComponent.homeScreen.equals("homeTeam")) {
                layout.setText(signInFont1, "Team");
                signInFont1.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
            } else if (settingsComponent.homeScreen.equals("homeBrigade")) {
                layout.setText(signInFont1, "Brigade");
                signInFont1.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
            }
        }

        if (settingsComponent.debug) {

            debugFont.draw(uiBatch, "Castle Blitz - " + GameEngine.version, 10, Gdx.graphics.getHeight() - 10);
            debugFont.draw(uiBatch, "X: " + (orthographicCamera.position.x - (orthographicCamera.viewportWidth / 2)), 10, Gdx.graphics.getHeight() - 50);
            debugFont.draw(uiBatch, "Y: " + (orthographicCamera.position.y - (orthographicCamera.viewportHeight / 2)), 10, Gdx.graphics.getHeight() - 70);
            debugFont.draw(uiBatch, "Lifetime: " + ((int) (GameEngine.lifetime * 10f)) / 10f + " s", 10, Gdx.graphics.getHeight() - 110);
            debugFont.draw(uiBatch, "Render Calls: " + (float) ((int) (((float) (batch.totalRenderCalls)) / 100)) / 10 + " x 1000", 10, Gdx.graphics.getHeight() - 130);
            debugFont.draw(uiBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 150);
            debugFont.draw(uiBatch, "Entities: " + ashleyEngine.getEntities().size(), 10, Gdx.graphics.getHeight() - 170);
            debugFont.draw(uiBatch, "Memory Usage: " + (float) (Gdx.app.getNativeHeap() / 100000) / 10f + "M", 10, Gdx.graphics.getHeight() - 190);
            MusicComponent musicComponent = ashleyEngine.getEntitiesFor(Family.all(MusicComponent.class).get()).get(0).getComponent(MusicComponent.class);
            String currentTrack = "";
            if (musicComponent.currentMusicIndex != -1) {
                currentTrack = musicComponent.currentMusicName;
                currentTrack = currentTrack.substring(33, currentTrack.length() - 4);
            }
            debugFont.draw(uiBatch, "Music Playing: " + currentTrack, 10, Gdx.graphics.getHeight() - 210);

        }

        uiBatch.end();
    }

    public void dispose() {
        batch.dispose();
        uiBatch.dispose();
        fontGenerator.dispose();
        pausedFont.dispose();
        signInFont1.dispose();
        signInFont2.dispose();
        debugFont.dispose();
    }
}
