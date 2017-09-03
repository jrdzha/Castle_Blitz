package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.RemoveTagComponent;
import com.jaredzhao.castleblitz.components.audio.MusicComponent;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.utils.BlurUtils;
import com.jaredzhao.castleblitz.utils.LayerSorter;
import com.jaredzhao.castleblitz.utils.PixmapUtils;
import com.jaredzhao.castleblitz.utils.ShaderBatch;

public class RenderSystem extends EntitySystem {

    private SpriteBatch spriteBatch;
    private ShaderBatch shaderBatch, fogOfWarBatch;
    private OrthographicCamera orthographicCamera;
    private Sprite currentSprite;

    private FrameBuffer frameBuffer;
    private boolean isPaused = false;
    private Pixmap pixmap, blurredPixmap;
    private Texture blurredTexture;

    private int maxPointLights = 50;

    private float brightness = 0.12f;
    private float contrast = 1.6f;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont debugFont, pausedFont, signInFont1, signInFont2;
    private GlyphLayout layout;

    private Engine ashleyEngine;

    private SettingsComponent settingsComponent;
    private CameraComponent cameraComponent;
    private BattleMechanicsStatesComponent battleMechanicsStatesComponent;
    private FogOfWarComponent fogOfWarComponent;
    private Entity fogOfWar;

    private ImmutableArray<Entity> renderables, lights, staticUI;

    private LayerSorter layerSorter;

    private ComponentMapper<AnimationComponent> animationComponentComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SpriteComponent> spriteComponentComponentMapper = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<StaticScreenPositionComponent> fixedScreenPositionComponentComponentMapper = ComponentMapper.getFor(StaticScreenPositionComponent.class);
    private ComponentMapper<LightComponent> lightComponentComponentMapper = ComponentMapper.getFor(LightComponent.class);

    public RenderSystem(Engine ashleyEngine, Entity camera, Entity settings, Entity battleMechanics, Entity fogOfWar){

        this.ashleyEngine = ashleyEngine;
        this.cameraComponent = camera.getComponent(CameraComponent.class);
        this.settingsComponent = settings.getComponent(SettingsComponent.class);
        this.battleMechanicsStatesComponent = battleMechanics.getComponent(BattleMechanicsStatesComponent.class);
        this.fogOfWarComponent = fogOfWar.getComponent(FogOfWarComponent.class);
        this.fogOfWar = fogOfWar;

        shaderBatch = new ShaderBatch(
                Gdx.files.internal("graphics/shaders/default.vert").readString(),
                Gdx.files.internal("graphics/shaders/default.frag").readString()
                        .replaceAll("MAXLIGHTMARKER", "" + maxPointLights)
                        .replaceAll("SCALEMARKER", "" + cameraComponent.scale), 100); //SpriteBatch for rendering entities
        fogOfWarBatch = new ShaderBatch(
                Gdx.files.internal("graphics/shaders/default.vert").readString(),
                Gdx.files.internal("graphics/shaders/fog_of_war.frag").readString(), 100); //SpriteBatch for rendering entities
        spriteBatch = new SpriteBatch(); //SpriteBatch for rendering UI / debug text
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        orthographicCamera = camera.getComponent(CameraComponent.class).camera; //Camera for easy access and for determing render location

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
        renderables = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class, DynamicScreenPositionComponent.class).get());
        staticUI = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class, StaticScreenPositionComponent.class).get());
        lights = engine.getEntitiesFor(Family.all(LightComponent.class).get());
    }

    public void updateFixedPositionRenderables(){
        for(Entity entity : staticUI){
            PositionComponent position = positionComponentComponentMapper.get(entity);
            StaticScreenPositionComponent staticScreenPositionComponent = fixedScreenPositionComponentComponentMapper.get(entity);
            if(staticScreenPositionComponent != null) {
                position.x = staticScreenPositionComponent.x + orthographicCamera.position.x - orthographicCamera.viewportWidth / 2;
                position.y = staticScreenPositionComponent.y + orthographicCamera.position.y - orthographicCamera.viewportHeight / 2;
            }
        }
    }

    public void update(float deltaTime) {
        Gdx.gl.glClearColor(.0f, .0f, .0f, 1f); //Background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen

        updateFixedPositionRenderables();

        orthographicCamera.update();
        shaderBatch.setProjectionMatrix(orthographicCamera.projection);
        fogOfWarBatch.setProjectionMatrix(orthographicCamera.projection);

        if(settingsComponent.isPaused && !this.isPaused){
            frameBuffer.begin();
            Gdx.gl.glClearColor(.0f, .0f, .0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
        }
        drawEntities(renderables);
        drawFogOfWar();
        if(settingsComponent.isPaused && !this.isPaused){
            pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
            frameBuffer.end();
            int blurRadius = 4;
            int iterations = 3;
            int pixmapWidth = pixmap.getWidth();
            int pixMapHeight = pixmap.getHeight();
            blurredPixmap = BlurUtils.blur(pixmap, 0, 0, pixmapWidth, pixMapHeight,
                    0, 0, pixmapWidth, pixMapHeight,
                    blurRadius, iterations, true);
            blurredTexture = new Texture(PixmapUtils.flipPixmap(blurredPixmap));
            this.isPaused = true;
        }
        if(settingsComponent.isPaused && this.isPaused){
            spriteBatch.begin();
            spriteBatch.draw(blurredTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }
        if(!settingsComponent.isPaused && this.isPaused){
            this.isPaused = false;
        }
        drawEntities(staticUI);
        drawUI();
    }

    public void drawEntities(ImmutableArray<Entity> entities){
        shaderBatch.begin(); //Render entities

        {

            shaderBatch.shader.setUniformf("contrast", contrast);
            shaderBatch.shader.setUniformf("brightness", brightness);

            int pointLightRGBCounter = 0, pointLightXYCounter = 0, pointLightIntensityCounter = 0;
            float[] pointLightRGB = new float[maxPointLights * 3];
            float[] pointLightXY = new float[maxPointLights * 2];
            float[] pointLightIntensity = new float[maxPointLights];
            for (Entity entity : lights) {
                if(pointLightIntensityCounter >= maxPointLights){
                    break;
                }
                LightComponent lightComponent = lightComponentComponentMapper.get(entity);
                pointLightRGB[pointLightRGBCounter++] = lightComponent.r;
                pointLightRGB[pointLightRGBCounter++] = lightComponent.g;
                pointLightRGB[pointLightRGBCounter++] = lightComponent.b;
                pointLightXY[pointLightXYCounter++] = (lightComponent.x - orthographicCamera.position.x + (orthographicCamera.viewportWidth)) * cameraComponent.scale;
                pointLightXY[pointLightXYCounter++] = (lightComponent.y - orthographicCamera.position.y + (orthographicCamera.viewportHeight)) * cameraComponent.scale;
                pointLightIntensity[pointLightIntensityCounter++] = lightComponent.intensity;
            }

            shaderBatch.shader.setUniform3fv("pointLightRGB", pointLightRGB, 0, pointLightRGB.length);
            shaderBatch.shader.setUniform2fv("pointLightXY", pointLightXY, 0, pointLightXY.length);
            shaderBatch.shader.setUniform1fv("pointLightIntensity", pointLightIntensity, 0, pointLightIntensity.length);

            for (Entity entity : layerSorter.sortByLayers(entities).values()) {
                PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
                SpriteComponent spriteComponent = spriteComponentComponentMapper.get(entity);
                AnimationComponent animationComponent = animationComponentComponentMapper.get(entity);

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
                    currentSprite.draw(shaderBatch);
                } else {
                    entity.add(new RemoveTagComponent());
                }
            }

        }
        shaderBatch.end();
    }

    public void drawFogOfWar() {

        fogOfWarBatch.begin();
        fogOfWarBatch.shader.setUniformf("contrast", contrast);
        fogOfWarBatch.shader.setUniformf("brightness", brightness);
        SpriteComponent fogOfWarSpriteComponent = spriteComponentComponentMapper.get(fogOfWar);
        for (int i = 0; i < fogOfWarComponent.viewMap.length; i++) {
            for (int j = 0; j < fogOfWarComponent.viewMap[0].length; j++) {
                for (Integer tileTypeInteger : fogOfWarComponent.viewMap[i][j]) {
                    int tileTypeInt = tileTypeInteger.intValue();
                    fogOfWarSpriteComponent.spriteList.get(tileTypeInt).get(0).setPosition((i * 16 - orthographicCamera.position.x - 8 + (orthographicCamera.viewportWidth / 2)),
                            (j * 16 - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - 8));
                    fogOfWarSpriteComponent.spriteList.get(tileTypeInt).get(0).draw(fogOfWarBatch);
                }
            }
        }
        fogOfWarBatch.end();
    }

    public void drawUI(){

        spriteBatch.begin(); //Render UI

        {
            if (settingsComponent.isPaused) {
                layout.setText(pausedFont, "PAUSED");
                pausedFont.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4 + 1.5f * layout.height);
            } else if (GameEngine.currentScene == 1) {
                if (battleMechanicsStatesComponent.isMyTurn) {
                    layout.setText(signInFont2, "YOUR TURN");
                    signInFont2.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 13 / 16 + 1.5f * layout.height);
                } else {
                    layout.setText(signInFont2, "OPPONENT'S TURN");
                    signInFont2.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 13 / 16 + 1.5f * layout.height);
                }
            } else if (GameEngine.currentScene == 2) {
                layout.setText(signInFont1, "SIGN IN");
                signInFont1.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4 + 1.5f * layout.height);

                layout.setText(signInFont2, "IT'S GOOD FOR YOU");
                signInFont2.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4);
            } else if (GameEngine.currentScene == 3) {
                if (settingsComponent.homeScreen.equals("homeShop")) {
                    layout.setText(signInFont1, "Shop");
                    signInFont1.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeArmory")) {
                    layout.setText(signInFont1, "Armory");
                    signInFont1.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeCastle")) {
                    layout.setText(signInFont1, "Castle");
                    signInFont1.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeTeam")) {
                    layout.setText(signInFont1, "Team");
                    signInFont1.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeBrigade")) {
                    layout.setText(signInFont1, "Brigade");
                    signInFont1.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                }
            }

            if (settingsComponent.debug) {

                debugFont.draw(spriteBatch, "Castle Blitz - " + GameEngine.version, 10, Gdx.graphics.getHeight() - 10);
                debugFont.draw(spriteBatch, "X: " + (orthographicCamera.position.x - (orthographicCamera.viewportWidth / 2)), 10, Gdx.graphics.getHeight() - 50);
                debugFont.draw(spriteBatch, "Y: " + (orthographicCamera.position.y - (orthographicCamera.viewportHeight / 2)), 10, Gdx.graphics.getHeight() - 70);
                debugFont.draw(spriteBatch, "Lifetime: " + ((int) (GameEngine.lifetime * 10f)) / 10f + " s", 10, Gdx.graphics.getHeight() - 110);
                debugFont.draw(spriteBatch, "Render Calls: " + (float) ((int) (((float) (shaderBatch.totalRenderCalls)) / 100)) / 10 + " x 1000", 10, Gdx.graphics.getHeight() - 130);
                debugFont.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 150);
                debugFont.draw(spriteBatch, "Entities: " + ashleyEngine.getEntities().size(), 10, Gdx.graphics.getHeight() - 170);
                debugFont.draw(spriteBatch, "Memory Usage: " + (float) (Gdx.app.getNativeHeap() / 100000) / 10f + "M", 10, Gdx.graphics.getHeight() - 190);
                MusicComponent musicComponent = ashleyEngine.getEntitiesFor(Family.all(MusicComponent.class).get()).get(0).getComponent(MusicComponent.class);
                String currentTrack = "";
                if (musicComponent.currentMusicIndex != -1) {
                    currentTrack = musicComponent.currentMusicName;
                    currentTrack = currentTrack.substring(33, currentTrack.length() - 4);
                }
                debugFont.draw(spriteBatch, "Music Playing: " + currentTrack, 10, Gdx.graphics.getHeight() - 210);

            }

        }

        spriteBatch.end();
    }

    public void dispose() {
        shaderBatch.dispose();
        spriteBatch.dispose();
        fontGenerator.dispose();
        pausedFont.dispose();
        signInFont1.dispose();
        signInFont2.dispose();
        debugFont.dispose();
    }
}
