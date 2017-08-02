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
import com.jaredzhao.castleblitz.utils.FacebookAccessor;
import com.jaredzhao.castleblitz.utils.FirebaseAccessor;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;

import java.util.Set;

public class HomeScene extends Scene {

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
    private AnimationManagerSystem animationManagerSystem;

    private FirebaseAccessor firebaseAccessor;
    private FacebookAccessor facebookAccessor;
    private PreferencesAccessor preferencesAccessor;

    public HomeScene(FirebaseAccessor firebaseAccessor, FacebookAccessor facebookAccessor, PreferencesAccessor preferencesAccessor){
        IDENTIFIER = 3;
        this.firebaseAccessor = firebaseAccessor;
        this.facebookAccessor = facebookAccessor;
        this.preferencesAccessor = preferencesAccessor;
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
        Object[] levelData = mapFactory.loadMap(Gdx.files.internal("levels/home.lvl"));

        //Create entities
        map = (Entity)levelData[0];
        camera = entityFactory.createCamera();
        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeShop", -36, -90, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeArmory", -18, -90, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeCastle", 0, -90, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeTeam", 18, -90, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeBrigade", 36, -90, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("battle", 0, 20, 64, 16));
        ashleyEngine.addEntity(entityFactory.createMusic((String[])levelData[1]));
        settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        ashleyEngine.addEntity(settings);

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settings.getComponent(SettingsComponent.class).soundOn = localSettings[0];
        settings.getComponent(SettingsComponent.class).sfxOn = localSettings[1];

        //Initialize systems
        cameraSystem = new CameraSystem(map);
        mapSystem = new MapSystem(map);
        renderSystem = new RenderSystem(ashleyEngine, camera, settings);
        inputSystem = new InputSystem(ashleyEngine, preferencesAccessor, entityFactory, camera, settings, battleMechanics);
        audioSystem = new AudioSystem(entityFactory, audioFactory, camera, settings);
        resourceManagementSystem = new ResourceManagementSystem(ashleyEngine);
        animationManagerSystem = new AnimationManagerSystem(settings);

        //Add systems to ashleyEngine
        ashleyEngine.addSystem(mapSystem);
        ashleyEngine.addSystem(inputSystem);
        ashleyEngine.addSystem(cameraSystem);
        ashleyEngine.addSystem(audioSystem);
        ashleyEngine.addSystem(renderSystem);
        ashleyEngine.addSystem(resourceManagementSystem);
        ashleyEngine.addSystem(animationManagerSystem);
        System.gc();
    }

    @Override
    public int render() throws InterruptedException {
        ashleyEngine.update(Gdx.graphics.getDeltaTime());

        int nextScene;
        if(settings.getComponent(SettingsComponent.class).battle){
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