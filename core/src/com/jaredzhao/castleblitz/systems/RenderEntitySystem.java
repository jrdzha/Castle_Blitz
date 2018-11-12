package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.RemoveTagComponent;
import com.jaredzhao.castleblitz.components.audio.MusicComponent;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.utils.DebugRenderer;
import com.jaredzhao.castleblitz.utils.LayerSorter;
import com.jaredzhao.castleblitz.utils.ShaderBatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RenderEntitySystem extends DisposableEntitySystem {

    private SpriteBatch spriteBatch;
    private ShaderBatch shaderBatch, fogOfWarBatch, blurBatch;
    private OrthographicCamera orthographicCamera;
    private Sprite currentSprite;

    private FrameBuffer frameBufferA, frameBufferB;
    private TextureRegion frameBufferRegion;

    private DebugRenderer debugRenderer;
    private LinkedList<double[]> profile;
    private int profilerValues = 180;

    private int maxPointLights = 15;
    public boolean renderEntities = true;
    public boolean renderFogOfWar = true;
    public boolean renderGaussianBlur = false;

    private SettingsComponent settingsComponent;
    private CameraComponent cameraComponent;
    private FogOfWarComponent fogOfWarComponent;
    private ShapeRenderer shapeRenderer;

    private ImmutableArray<Entity> renderables, textRenderables, lights, staticUI;

    private LayerSorter layerSorter;

    private ComponentMapper<AnimationComponent> animationComponentComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SpriteComponent> spriteComponentComponentMapper = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<StaticScreenPositionComponent> fixedScreenPositionComponentComponentMapper = ComponentMapper.getFor(StaticScreenPositionComponent.class);
    private ComponentMapper<LightComponent> lightComponentComponentMapper = ComponentMapper.getFor(LightComponent.class);
    private ComponentMapper<TextComponent> textComponentComponentMapper = ComponentMapper.getFor(TextComponent.class);

    private PerformanceCounter renderEntitiesPerformanceCounter;
    private PerformanceCounter renderFogOfWarPerformanceCounter;
    private PerformanceCounter renderGaussianBlurPerformanceCounter;
    private PerformanceCounter renderStaticUIPerformanceCounter;
    private PerformanceCounter renderTextPerformanceCounter;

    /**
     * DisposableEntitySystem used to render entities in the correct order with the correct shaders
     *
     * @param camera   Orthographic Camera
     * @param settings Game Settings
     * @param fogOfWar Entity used to store fog of war data
     */
    public RenderEntitySystem(Entity camera, Entity settings, Entity fogOfWar, int mapHeight, float contrast, float brightness) {
        this.cameraComponent = camera.getComponent(CameraComponent.class);
        this.settingsComponent = settings.getComponent(SettingsComponent.class);
        this.fogOfWarComponent = fogOfWar.getComponent(FogOfWarComponent.class);

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

        shapeRenderer = new ShapeRenderer();

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

        layerSorter = new LayerSorter(mapHeight);

        ArrayList<Color> colors = new ArrayList<Color>();
        colors.add(Color.WHITE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        colors.add(Color.CYAN);
        debugRenderer = new DebugRenderer(spriteBatch, profilerValues, colors);

        profile = new LinkedList<double[]>();

        renderEntitiesPerformanceCounter = new PerformanceCounter("RenderEntities");
        renderFogOfWarPerformanceCounter = new PerformanceCounter("RenderFogOfWar");
        renderGaussianBlurPerformanceCounter = new PerformanceCounter("RenderGaussianBlur");
        renderStaticUIPerformanceCounter = new PerformanceCounter("RenderStaticUI");
        renderTextPerformanceCounter = new PerformanceCounter("RenderText");
    }

    /**
     * Load entities as they are added to the Ashley Engine
     *
     * @param engine
     */
    public void addedToEngine(Engine engine) {
        renderables = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class, DynamicScreenPositionComponent.class).get());
        textRenderables = engine.getEntitiesFor(Family.all(TextComponent.class, VisibleComponent.class, PositionComponent.class).get());
        staticUI = engine.getEntitiesFor(Family.all(AnimationComponent.class, SpriteComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class, StaticScreenPositionComponent.class).get());
        lights = engine.getEntitiesFor(Family.all(LightComponent.class).get());
    }

    /**
     * Update position of entities that should have a fixed position on the screen relative to camera movement
     */
    public void updateFixedPositionRenderables() {
        for (Entity entity : staticUI) {
            PositionComponent position = positionComponentComponentMapper.get(entity);
            StaticScreenPositionComponent staticScreenPositionComponent = fixedScreenPositionComponentComponentMapper.get(entity);
            if (staticScreenPositionComponent != null) {
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

        renderEntitiesPerformanceCounter.tick();
        renderFogOfWarPerformanceCounter.tick();
        renderGaussianBlurPerformanceCounter.tick();
        renderStaticUIPerformanceCounter.tick();
        renderTextPerformanceCounter.tick();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
        //Gdx.gl.glClearColor(.06f, .06f, .22f, 1f); //Background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen

        updateFixedPositionRenderables();

        orthographicCamera.update();
        shaderBatch.setProjectionMatrix(orthographicCamera.projection);
        fogOfWarBatch.setProjectionMatrix(orthographicCamera.projection);

        if (settingsComponent.isPaused || renderGaussianBlur) {
            frameBufferA.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); //Clear screen
        }

        renderEntitiesPerformanceCounter.start();
        if (renderEntities) {
            drawEntities(renderables);
        }
        renderEntitiesPerformanceCounter.stop();

        renderFogOfWarPerformanceCounter.start();
        if (renderFogOfWar) {
            drawFogOfWar();
        }
        renderFogOfWarPerformanceCounter.stop();

        renderGaussianBlurPerformanceCounter.start();
        if (settingsComponent.isPaused || renderGaussianBlur) {
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
        renderGaussianBlurPerformanceCounter.stop();

        renderStaticUIPerformanceCounter.start();
        drawEntities(staticUI);
        renderStaticUIPerformanceCounter.stop();

        renderTextPerformanceCounter.start();
        drawText();
        renderTextPerformanceCounter.stop();

        //System.out.println("" + renderEntitiesPerformanceCounter.load.value + ", " + renderFogOfWarPerformanceCounter.load.value + ", " + renderStaticUIPerformanceCounter.load.value + ", " + renderTextPerformanceCounter.load.value);
        this.updatePerformanceProfile(new double[]{renderEntitiesPerformanceCounter.load.latest, renderFogOfWarPerformanceCounter.load.latest, renderGaussianBlurPerformanceCounter.load.latest, renderStaticUIPerformanceCounter.load.latest, renderTextPerformanceCounter.load.latest});
    }

    /**
     * Draw in-game entities that should be rendered with the ShaderBatch
     *
     * @param entities List of entities to be rendered
     */
    public void drawEntities(ImmutableArray<Entity> entities) {
        shaderBatch.begin(); //Render entities

        {

            int pointLightRGBCounter = 0, pointLightXYCounter = 0, pointLightIntensityCounter = 0;
            float[] pointLightRGB = new float[maxPointLights * 3];
            float[] pointLightXY = new float[maxPointLights * 2];
            float[] pointLightIntensity = new float[maxPointLights];
            for (Entity entity : lights) {
                if (pointLightIntensityCounter >= maxPointLights) {
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
                                (positionComponent.y - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - (GameEngine.tileSize / 2)));
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

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(fogOfWarBatch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < fogOfWarComponent.viewMap.length; i++) {
            for (int j = 0; j < fogOfWarComponent.viewMap[0].length; j++) {
                int tileTypeInt = fogOfWarComponent.viewMap[i][j];
                if (tileTypeInt == 0) {
                    shapeRenderer.setColor(new Color(0, 0, 0, 1f));
                } else if (tileTypeInt == 1) {
                    shapeRenderer.setColor(new Color(0, 0, 0, 0.75f));
                } else if (tileTypeInt == 2) {
                    shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
                } else if (tileTypeInt == 3) {
                    shapeRenderer.setColor(new Color(0, 0, 0, 0.25f));
                } else if (tileTypeInt == 4) {
                    shapeRenderer.setColor(new Color(0, 0, 0, 0f));
                }
                shapeRenderer.rect((i * 16 - orthographicCamera.position.x - 8 + (orthographicCamera.viewportWidth / 2)), (j * 16 - orthographicCamera.position.y + (orthographicCamera.viewportHeight / 2) - 8), 16, 16);

            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Render static UI
     */
    public void drawText() {

        spriteBatch.begin();

        for (Entity entity : textRenderables) {
            PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
            TextComponent textComponent = textComponentComponentMapper.get(entity);

            if (textComponent.centered) {
                textComponent.bitmapFont.draw(spriteBatch, textComponent.glyphLayout,
                        Gdx.graphics.getWidth() / 2 - textComponent.glyphLayout.width / 2 + positionComponent.x,
                        Gdx.graphics.getHeight() / 2 - textComponent.glyphLayout.height / 2 + positionComponent.y);
            } else {
                textComponent.bitmapFont.draw(spriteBatch, textComponent.glyphLayout,
                        Gdx.graphics.getWidth() / 2 + positionComponent.x,
                        Gdx.graphics.getHeight() / 2 + positionComponent.y);
            }
        }

        spriteBatch.end();

        if (settingsComponent.debug) {
            ArrayList<String> debugText = new ArrayList<String>();

            debugText.add("Castle Blitz - " + GameEngine.version);
            debugText.add("");
            debugText.add("X: " + (orthographicCamera.position.x - (orthographicCamera.viewportWidth / 2)));
            debugText.add("Y: " + (orthographicCamera.position.y - (orthographicCamera.viewportHeight / 2)));
            debugText.add("");
            debugText.add("Lifetime: " + ((int) (GameEngine.lifetime * 10f)) / 10f + " s");
            debugText.add("Render Calls: " + (float) ((int) (((float) (shaderBatch.totalRenderCalls + spriteBatch.totalRenderCalls + fogOfWarBatch.totalRenderCalls)) / 100)) / 10 + " x 1000");
//            debugText.add("Sprite Render Calls / Frame: " + spriteBatch.renderCalls);
//            debugText.add("Shader Render Calls / Frame: " + shaderBatch.renderCalls);
//            debugText.add("FOW Render Calls / Frame: " + fogOfWarBatch.renderCalls);
//            debugText.add("Total Render Calls / Frame: " + (spriteBatch.renderCalls + shaderBatch.renderCalls + fogOfWarBatch.renderCalls));
            debugText.add("FPS: " + Gdx.graphics.getFramesPerSecond());
            debugText.add("Entities: " + getEngine().getEntities().size());
            debugText.add("Memory Usage: " + (float) (Gdx.app.getNativeHeap() / 100000) / 10f + "M");
            debugText.add("");
            MusicComponent musicComponent = getEngine().getEntitiesFor(Family.all(MusicComponent.class).get()).get(0).getComponent(MusicComponent.class);
            String currentTrack = "";
            if (musicComponent.currentMusicIndex != -1) {
                currentTrack = musicComponent.currentMusicName;
                currentTrack = currentTrack.substring(33, currentTrack.length() - 4);
            }
            debugText.add("Music Playing: " + currentTrack);

            debugRenderer.render(debugText, profile);
        }
    }

    public void updatePerformanceProfile(double[] profileUpdate) {
        profile.addLast(profileUpdate);
        while (profile.size() > profilerValues) {
            profile.removeFirst();
        }
    }

    /**
     * Dispose the system
     */
    @Override
    public void dispose() {
        shaderBatch.dispose();
        spriteBatch.dispose();
        debugRenderer.dispose();
    }
}
