package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.factories.AnimationFactory;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.factories.MapFactory;
import com.jaredzhao.castleblitz.systems.*;

public class LoginScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component System (ECS)

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on
    private Entity settings;

    private CameraSystem cameraSystem; //System for moving the camera
    private MapSystem mapSystem; //System to create screen positions for new map entities
    private RenderSystem renderSystem; //System for rendering to the screen
    private InputSystem inputSystem; //System for user input
    private AudioSystem audioSystem; //System for dynamic audio
    private ResourceManagementSystem resourceManagementSystem; //Garbage-Collection System

    public LoginScene(){
        IDENTIFIER = 2;
    }

    @Override
    public void init() {

        //Initialize ashleyEngine
        ashleyEngine = new Engine();

        //Initialize factories
        audioFactory = new AudioFactory();
        animationFactory = new AnimationFactory();
        entityFactory = new EntityFactory(animationFactory, audioFactory, camera);
        mapFactory = new MapFactory(ashleyEngine, entityFactory);

        //Load level data from disk
        Object[] levelData = mapFactory.loadMap(Gdx.files.internal("levels/login.lvl"));

        //Create entities
        map = (Entity)levelData[0];
        camera = entityFactory.createCamera();
        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("facebookLogin", 0, -60, 65, 16));
        ashleyEngine.addEntity(entityFactory.createMusic((String[])levelData[1]));
        settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        ashleyEngine.addEntity(settings);

        //Initialize systems
        cameraSystem = new CameraSystem(map);
        mapSystem = new MapSystem(map);
        renderSystem = new RenderSystem(ashleyEngine, camera, settings);
        inputSystem = new InputSystem(ashleyEngine, entityFactory, camera, settings, battleMechanics);
        audioSystem = new AudioSystem(entityFactory, audioFactory, camera, settings);
        resourceManagementSystem = new ResourceManagementSystem(ashleyEngine);

        //Add systems to ashleyEngine
        ashleyEngine.addSystem(mapSystem);
        ashleyEngine.addSystem(inputSystem);
        ashleyEngine.addSystem(cameraSystem);
        ashleyEngine.addSystem(audioSystem);
        ashleyEngine.addSystem(renderSystem);
        ashleyEngine.addSystem(resourceManagementSystem);
        System.gc();
    }

    @Override
    public int render() throws InterruptedException {
        ashleyEngine.update(Gdx.graphics.getDeltaTime());

        int nextScene;
        if(settings.getComponent(SettingsComponent.class).facebookLogin){
            nextScene = 1;
            this.dispose();
            this.isRunning = false;
        } else {
            nextScene = IDENTIFIER;
        }
        return nextScene;
    }

    @Override
    public void dispose() {
        mapSystem.dispose();
        inputSystem.dispose();
        cameraSystem.dispose();
        audioSystem.dispose();
        renderSystem.dispose();
        resourceManagementSystem.disposeAll();
        resourceManagementSystem.dispose();
    }
}
