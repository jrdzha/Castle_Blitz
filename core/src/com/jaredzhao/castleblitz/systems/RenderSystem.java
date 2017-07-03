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
import com.jaredzhao.castleblitz.components.graphics.AnimationComponent;
import com.jaredzhao.castleblitz.components.graphics.LayerComponent;
import com.jaredzhao.castleblitz.components.graphics.SpriteComponent;
import com.jaredzhao.castleblitz.components.graphics.VisibleComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.mechanics.FixedScreenPositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.utils.LayerSorter;
import com.jaredzhao.castleblitz.utils.ShaderBatch;

import java.util.ArrayList;

public class RenderSystem extends EntitySystem {

    public SpriteBatch uiBatch;
    public ShaderBatch batch;
    public OrthographicCamera orthographicCamera;
    public Sprite currentSprite;

    private float lifetime;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont debugFont, font;
    private GlyphLayout layout;

    private Engine ashleyEngine;
    private Entity settings;

    private ImmutableArray<Entity> renderables;
    private ArrayList<Entity> sortedRenderables;

    private ComponentMapper<AnimationComponent> animationComponentComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SpriteComponent> spriteComponentComponentMapper = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<FixedScreenPositionComponent> fixedScreenPositionComponentComponentMapper = ComponentMapper.getFor(FixedScreenPositionComponent.class);

    public RenderSystem(Engine ashleyEngine, Entity camera, Entity settings){
        lifetime = 0;

        batch = new ShaderBatch(100); //SpriteBatch for rendering entities
        batch.brightness = 0.05f;
        batch.contrast = 1.3f;
        uiBatch = new SpriteBatch(); //SpriteBatch for rendering UI / debug text
        orthographicCamera = camera.getComponent(CameraComponent.class).camera; //Camera for easy access and for determing render location
        this.ashleyEngine = ashleyEngine;
        this.settings = settings;

        debugFont = new BitmapFont();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/slkscrb.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.color = Color.WHITE;
        fontParameter.size = Gdx.graphics.getHeight() / 25;
        font = fontGenerator.generateFont(fontParameter);
        layout = new GlyphLayout();
    }

    public void reset() {

    }

    public void addedToEngine(Engine engine){
        renderables = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class).get());
    }

    public void updateFixedPositionRenderables(){
        for(int i = 0; i < sortedRenderables.size(); ++i){
            Entity entity = sortedRenderables.get(i);
            PositionComponent position = positionComponentComponentMapper.get(entity);
            FixedScreenPositionComponent fixedScreenPositionComponent = fixedScreenPositionComponentComponentMapper.get(entity);
            if(fixedScreenPositionComponent != null) {
                position.x = fixedScreenPositionComponent.x + orthographicCamera.position.x - orthographicCamera.viewportWidth / 2;
                position.y = fixedScreenPositionComponent.y + orthographicCamera.position.y - orthographicCamera.viewportHeight / 2;
            }
        }
    }

    public void update(float deltaTime){
        Gdx.gl.glClearColor(.1f, .1f, .2f, 1f); //Background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //Clear screen

        sortedRenderables = LayerSorter.sortByLayers(renderables);
        updateFixedPositionRenderables();

        orthographicCamera.update();
        batch.setProjectionMatrix(orthographicCamera.projection);

        batch.begin(); //Render entities

        for(int i = 0; i < sortedRenderables.size(); i++) { //Loop through each layer of rendering
            Entity entity = sortedRenderables.get(i);
            PositionComponent position = positionComponentComponentMapper.get(entity);
            SpriteComponent sprite = spriteComponentComponentMapper.get(entity);
            AnimationComponent animation = animationComponentComponentMapper.get(entity);
            if (animation.framesDisplayed != -1) {
                if (0 > animation.animationTimeList.get(animation.currentFrame[0]).get(animation.currentFrame[1])) {
                    currentSprite = sprite.spriteList.get(animation.currentFrame[0]).get(animation.currentFrame[1]);
                } else if (animation.framesDisplayed <= animation.animationTimeList.get(animation.currentFrame[0]).get(animation.currentFrame[1])) {
                    currentSprite = sprite.spriteList.get(animation.currentFrame[0]).get(animation.currentFrame[1]);
                    animation.framesDisplayed++;
                } else {
                    animation.framesDisplayed = 0;
                    if (animation.currentFrame[1] < animation.animationTimeList.get(animation.currentFrame[0]).size() - 1) {
                        animation.currentFrame[1]++;
                    } else {
                        animation.currentFrame[1] = 0;
                    }
                    currentSprite = sprite.spriteList.get(animation.currentFrame[0]).get(animation.currentFrame[1]);
                }
                if (entity.getComponent(TileComponent.class) != null) {
                    currentSprite.setPosition((position.x - orthographicCamera.position.x - (currentSprite.getWidth() / 2) + (orthographicCamera.viewportWidth / 2)),
                            (position.y - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - 8));
                } else {
                    currentSprite.setPosition((position.x - orthographicCamera.position.x - (currentSprite.getWidth() / 2) + (orthographicCamera.viewportWidth / 2)),
                            (position.y - orthographicCamera.position.y - (currentSprite.getHeight() / 2) + (orthographicCamera.viewportHeight / 2)));
                }
                currentSprite.draw(batch);
            } else {
                entity.add(new RemoveTagComponent());
            }
        }

        batch.end();

        uiBatch.begin(); //Render UI

        if(settings.getComponent(SettingsComponent.class).isPaused){
            layout.setText(font, "PAUSED");
            font.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 2);
        }

        if(settings.getComponent(SettingsComponent.class).debug) {

            debugFont.draw(uiBatch, "Castle Blitz - " + GameEngine.version, 10, Gdx.graphics.getHeight() - 10);
            debugFont.draw(uiBatch, "X: " + (orthographicCamera.position.x - (orthographicCamera.viewportWidth / 2)), 10, Gdx.graphics.getHeight() - 50);
            debugFont.draw(uiBatch, "Y: " + (orthographicCamera.position.y - (orthographicCamera.viewportHeight / 2)), 10, Gdx.graphics.getHeight() - 70);
            debugFont.draw(uiBatch, "Lifetime: " + ((int) (lifetime * 10f)) / 10f + " s", 10, Gdx.graphics.getHeight() - 110);
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

        lifetime += Gdx.graphics.getDeltaTime();

        /*
        if((int)(lifetime * 10f) % 300 == 0){
            System.gc();
        }
        */
    }
}
