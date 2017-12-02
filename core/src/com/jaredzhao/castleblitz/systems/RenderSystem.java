package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.RemoveTagComponent;
import com.jaredzhao.castleblitz.components.audio.MusicComponent;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.utils.LayerSorter;
import com.jaredzhao.castleblitz.utils.ShaderBatch;

public class RenderSystem extends EntitySystem {

    private SpriteBatch spriteBatch;
    private ShaderBatch shaderBatch, fogOfWarBatch, blurBatch;
    private OrthographicCamera orthographicCamera;
    private Sprite currentSprite;

    private FrameBuffer frameBufferA, frameBufferB;
    private TextureRegion frameBufferRegion;

    private int maxPointLights = 15;

    private float brightness = 0.12f;
    private float contrast = 1.6f;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont debugFont, font35, font25, font15;
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

    /**
     * System used to render entities in the correct order with the correct shaders
     *
     * @param ashleyEngine      AshleyEngine
     * @param camera            Orthographic Camera
     * @param settings          Game Settings
     * @param battleMechanics   Entity used to store game states
     * @param fogOfWar          Entity used to store fog of war data
     */
    public RenderSystem(Engine ashleyEngine, Entity camera, Entity settings, Entity battleMechanics, Entity fogOfWar, int mapHeight, float contrast, float brightness){

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
                        .replaceAll("SCALEMARKER", "" + cameraComponent.scale), 1000); //SpriteBatch for rendering entities
        fogOfWarBatch = new ShaderBatch(
                Gdx.files.internal("graphics/shaders/default.vert").readString(),
                Gdx.files.internal("graphics/shaders/fog_of_war.frag").readString(), 1000); //SpriteBatch for rendering entities
        blurBatch = new ShaderBatch(
                Gdx.files.internal("graphics/shaders/default.vert").readString(),
                Gdx.files.internal("graphics/shaders/blur.frag").readString(), 2);
        spriteBatch = new SpriteBatch(); //SpriteBatch for rendering UI / debug text

        shaderBatch.begin();
        shaderBatch.shader.setUniformf("contrast", contrast);
        shaderBatch.shader.setUniformf("brightness", brightness);
        shaderBatch.end();

        blurBatch.begin();
        blurBatch.shader.setUniformf("dir", 0f, 0f);
        blurBatch.shader.setUniformf("radius", .0025f);
        blurBatch.end();

        frameBufferA = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBufferB = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        frameBufferRegion = new TextureRegion(frameBufferA.getColorBufferTexture());
        frameBufferRegion.flip(false, true); // FBO uses lower left, TextureRegion uses

        orthographicCamera = camera.getComponent(CameraComponent.class).camera; //Camera for easy access and for determing render location

        debugFont = new BitmapFont();
        //font = new BitmapFont();
        //font.setColor(Color.WHITE);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/slkscrb.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.color = Color.WHITE;

        fontParameter.size = Gdx.graphics.getHeight() / 15;
        font35 = fontGenerator.generateFont(fontParameter);
        fontParameter.size = Gdx.graphics.getHeight() / 25;
        font25 = fontGenerator.generateFont(fontParameter);
        fontParameter.size = Gdx.graphics.getHeight() / 35;
        font15 = fontGenerator.generateFont(fontParameter);
        layout = new GlyphLayout();

        layerSorter = new LayerSorter(mapHeight);
    }

    /**
     * Load entities as they are added to the Ashley Engine
     *
     * @param engine
     */
    public void addedToEngine(Engine engine){
        renderables = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class, DynamicScreenPositionComponent.class).get());
        staticUI = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class, StaticScreenPositionComponent.class).get());
        lights = engine.getEntitiesFor(Family.all(LightComponent.class).get());
    }

    /**
     * Update position of entities that should have a fixed position on the screen relative to camera movement
     */
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

    /**
     * Cleans screen, renders in appropriate layers
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
        //Gdx.gl.glClearColor(.06f, .06f, .22f, 1f); //Background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen

        updateFixedPositionRenderables();

        orthographicCamera.update();
        shaderBatch.setProjectionMatrix(orthographicCamera.projection);
        fogOfWarBatch.setProjectionMatrix(orthographicCamera.projection);

        if(settingsComponent.isPaused){
            frameBufferA.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
        }

        drawEntities(renderables);
        drawFogOfWar();

        if(settingsComponent.isPaused){
            frameBufferA.end();
            frameBufferRegion.setTexture(frameBufferA.getColorBufferTexture());

            frameBufferB.begin();
            blurBatch.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
            blurBatch.shader.setUniformf("radius", .008f);
            blurBatch.shader.setUniformf("dir", 0f, 1f);
            blurBatch.draw(frameBufferRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            blurBatch.end();
            frameBufferRegion.setTexture(frameBufferB.getColorBufferTexture());
            frameBufferB.end();

            frameBufferA.begin();
            blurBatch.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
            blurBatch.shader.setUniformf("dir", 1f, 0f);
            blurBatch.draw(frameBufferRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            blurBatch.end();
            frameBufferRegion.setTexture(frameBufferA.getColorBufferTexture());
            frameBufferA.end();

            frameBufferB.begin();
            blurBatch.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
            blurBatch.shader.setUniformf("radius", .003f);
            blurBatch.shader.setUniformf("dir", 0f, 1f);
            blurBatch.draw(frameBufferRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            blurBatch.end();
            frameBufferRegion.setTexture(frameBufferB.getColorBufferTexture());
            frameBufferB.end();

            frameBufferA.begin();
            blurBatch.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
            blurBatch.shader.setUniformf("dir", 1f, 0f);
            blurBatch.draw(frameBufferRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            blurBatch.end();
            frameBufferRegion.setTexture(frameBufferA.getColorBufferTexture());
            frameBufferA.end();

            frameBufferB.begin();
            blurBatch.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
            blurBatch.shader.setUniformf("radius", .001f);
            blurBatch.shader.setUniformf("dir", 0f, 1f);
            blurBatch.draw(frameBufferRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            blurBatch.end();
            frameBufferRegion.setTexture(frameBufferB.getColorBufferTexture());
            frameBufferB.end();

            blurBatch.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
            blurBatch.shader.setUniformf("dir", 1f, 0f);
            blurBatch.draw(frameBufferRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            blurBatch.end();
        }

        drawEntities(staticUI);
        drawUI();
    }

    /**
     * Draw in-game entities that should be rendered with the ShaderBatch
     *
     * @param entities      List of entities to be rendered
     */
    public void drawEntities(ImmutableArray<Entity> entities){
        shaderBatch.begin(); //Render entities

        {

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
                        currentSprite.setPosition(
                                (positionComponent.x - orthographicCamera.position.x - (currentSprite.getWidth() / 2) + (orthographicCamera.viewportWidth / 2)),
                                (positionComponent.y - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - 8));
                    } else {
                        currentSprite.setPosition(
                                (positionComponent.x - orthographicCamera.position.x - (currentSprite.getWidth() / 2) + (orthographicCamera.viewportWidth / 2)),
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

    /**
     * Render fog of war using standard ShaderBatch
     */
    public void drawFogOfWar() {

        fogOfWarBatch.begin();
        fogOfWarBatch.shader.setUniformf("contrast", contrast);
        fogOfWarBatch.shader.setUniformf("brightness", brightness);
        SpriteComponent fogOfWarSpriteComponent = spriteComponentComponentMapper.get(fogOfWar);
        for (int i = 0; i < fogOfWarComponent.viewMap.length; i++) {
            for (int j = 0; j < fogOfWarComponent.viewMap[0].length; j++) {
                int tileTypeInt = fogOfWarComponent.viewMap[i][j];
                fogOfWarSpriteComponent.spriteList.get(tileTypeInt).get(0).setPosition((i * 16 - orthographicCamera.position.x - 8 + (orthographicCamera.viewportWidth / 2)),
                        (j * 16 - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - 8));
                fogOfWarSpriteComponent.spriteList.get(tileTypeInt).get(0).draw(fogOfWarBatch);
            }
        }
        fogOfWarBatch.end();
    }

    /**
     * Render static UI
     */
    public void drawUI(){

        spriteBatch.begin(); //Render UI

        {
            if (settingsComponent.isPaused) {
                layout.setText(font25, "PAUSED");
                font25.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4 + 1.5f * layout.height);
            } else if (GameEngine.currentScene == 1) {
                if (battleMechanicsStatesComponent.isMyTurn) {
                    layout.setText(font15, "YOUR TURN");
                    font15.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 13 / 16 + 1.5f * layout.height);
                } else {
                    layout.setText(font15, "OPPONENT'S TURN");
                    font15.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 13 / 16 + 1.5f * layout.height);
                }
            } else if (GameEngine.currentScene == 2) {
                layout.setText(font35, "SIGN IN");
                font35.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4 + 1.5f * layout.height);

                layout.setText(font15, "IT'S GOOD FOR YOU");
                font15.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 3 / 4);
            } else if (GameEngine.currentScene == 3) {
                if (settingsComponent.homeScreen.equals("homeShop")) {
                    layout.setText(font35, "Shop");
                    font35.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeArmory")) {
                    layout.setText(font35, "Armory");
                    font35.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeCastle")) {
                    layout.setText(font35, "Castle");
                    font35.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeTeam")) {
                    layout.setText(font35, "Team");
                    font35.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                } else if (settingsComponent.homeScreen.equals("homeBrigade")) {
                    layout.setText(font35, "Brigade");
                    font35.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);
                }
            } else if (GameEngine.currentScene == 4) {
                layout.setText(font35, "Account");
                font35.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 7 / 8 + 1.5f * layout.height);

                if(settingsComponent.username.equals("")){
                    font15.setColor(1, 1, 1, 0.5f);
                    layout.setText(font15, "Username");
                } else {
                    layout.setText(font15, settingsComponent.username);
                }
                font15.draw(spriteBatch, layout, 32 * cameraComponent.scale, Gdx.graphics.getHeight() / 2 + 75 * cameraComponent.scale);
                font15.setColor(Color.WHITE);

                if(settingsComponent.password.equals("")){
                    font15.setColor(1, 1, 1, 0.5f);
                    layout.setText(font15, "Password");
                } else {
                    String placeHolder = "";
                    for(int i = 0; i < settingsComponent.password.length(); i++){
                        placeHolder = placeHolder + "*";
                    }
                    layout.setText(font15, placeHolder);
                }
                font15.draw(spriteBatch, layout, 32 * cameraComponent.scale, Gdx.graphics.getHeight() / 2 + 55 * cameraComponent.scale);
                font15.setColor(Color.WHITE);

                if(settingsComponent.confirmPassword.equals("")){
                    font15.setColor(1, 1, 1, 0.5f);
                    layout.setText(font15, "Confirm Password");
                } else {
                    String placeHolder = "";
                    for(int i = 0; i < settingsComponent.confirmPassword.length(); i++){
                        placeHolder = placeHolder + "*";
                    }
                    layout.setText(font15, placeHolder);
                }
                font15.draw(spriteBatch, layout, 32 * cameraComponent.scale, Gdx.graphics.getHeight() / 2 + 35 * cameraComponent.scale);
                font15.setColor(Color.RED);

                layout.setText(font15, settingsComponent.signUpLoginError);
                font15.draw(spriteBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 2 + 15 * cameraComponent.scale);
                font15.setColor(Color.WHITE);
            }

            if (settingsComponent.debug) {

                debugFont.draw(spriteBatch, "Castle Blitz - " + GameEngine.version, 10, Gdx.graphics.getHeight() - 10);
                debugFont.draw(spriteBatch, "X: " + (orthographicCamera.position.x - (orthographicCamera.viewportWidth / 2)), 10, Gdx.graphics.getHeight() - 50);
                debugFont.draw(spriteBatch, "Y: " + (orthographicCamera.position.y - (orthographicCamera.viewportHeight / 2)), 10, Gdx.graphics.getHeight() - 70);
                debugFont.draw(spriteBatch, "Lifetime: " + ((int) (GameEngine.lifetime * 10f)) / 10f + " s", 10, Gdx.graphics.getHeight() - 110);
                debugFont.draw(spriteBatch, "Render Calls: " + (float) ((int) (((float) (shaderBatch.totalRenderCalls)) / 100)) / 10 + " x 1000", 10, Gdx.graphics.getHeight() - 130);
                debugFont.draw(spriteBatch, "Render Calls / Frame: " + spriteBatch.renderCalls + shaderBatch.renderCalls + fogOfWarBatch.renderCalls, 10, Gdx.graphics.getHeight() - 150);
                debugFont.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 170);
                debugFont.draw(spriteBatch, "Entities: " + ashleyEngine.getEntities().size(), 10, Gdx.graphics.getHeight() - 190);
                debugFont.draw(spriteBatch, "Memory Usage: " + (float) (Gdx.app.getNativeHeap() / 100000) / 10f + "M", 10, Gdx.graphics.getHeight() - 210);
                MusicComponent musicComponent = ashleyEngine.getEntitiesFor(Family.all(MusicComponent.class).get()).get(0).getComponent(MusicComponent.class);
                String currentTrack = "";
                if (musicComponent.currentMusicIndex != -1) {
                    currentTrack = musicComponent.currentMusicName;
                    currentTrack = currentTrack.substring(33, currentTrack.length() - 4);
                }
                debugFont.draw(spriteBatch, "Music Playing: " + currentTrack, 10, Gdx.graphics.getHeight() - 230);

            }

        }

        spriteBatch.end();
    }

    /**
     * Dispose the system
     */
    public void dispose() {
        shaderBatch.dispose();
        spriteBatch.dispose();
        fontGenerator.dispose();
        font15.dispose();
        font25.dispose();
        font35.dispose();
        debugFont.dispose();
    }
}
