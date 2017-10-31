package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.factories.AnimationFactory;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.factories.MapFactory;
import com.jaredzhao.castleblitz.servers.EmptyServer;
import com.jaredzhao.castleblitz.systems.*;
import com.jaredzhao.castleblitz.utils.FacebookAccessor;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;

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
    private RenderSystem renderSystem; //System for rendering to the screen
    private InputSystem inputSystem; //System for user input
    private AudioSystem audioSystem; //System for dynamic audio
    private ResourceManagementSystem resourceManagementSystem; //Garbage-Collection System
    private AnimationManagerSystem animationManagerSystem;

    private FacebookAccessor facebookAccessor;
    private PreferencesAccessor preferencesAccessor;

    public LoginScene(FacebookAccessor facebookAccessor, PreferencesAccessor preferencesAccessor){
        IDENTIFIER = 2;
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
        entityFactory = new EntityFactory(animationFactory, audioFactory);
        mapFactory = new MapFactory(ashleyEngine, entityFactory);

        //Create entities
        String[][][] rawMap = mapFactory.loadRawMap(Gdx.files.internal("levels/home.lvl"));
        map = mapFactory.loadMap(rawMap);
        camera = entityFactory.createCamera();
        //Entity fogOfWar = entityFactory.createFogOfWar(.15f, .15f, .25f, .6f, rawMap[0].length, rawMap[0][0].length);
        Entity fogOfWar = entityFactory.createFogOfWar(0, 0, 0, .3f, rawMap[0].length, rawMap[0][0].length);
        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("facebookLogin", 0, -60, 65, 16));
        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/home.lvl"))));
        settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        ashleyEngine.addEntity(settings);

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settings.getComponent(SettingsComponent.class).soundOn = localSettings[0];
        settings.getComponent(SettingsComponent.class).sfxOn = localSettings[1];

        //Initialize systems
        cameraSystem = new CameraSystem(map);
        renderSystem = new RenderSystem(ashleyEngine, camera, settings, battleMechanics, fogOfWar);
        inputSystem = new InputSystem(ashleyEngine, new EmptyServer(), preferencesAccessor, entityFactory, camera, settings, battleMechanics);
        audioSystem = new AudioSystem(entityFactory, audioFactory, camera, settings);
        resourceManagementSystem = new ResourceManagementSystem(ashleyEngine);
        animationManagerSystem = new AnimationManagerSystem(settings);

        //Add systems to ashleyEngine
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

        int nextScene = -1;
        if(settings.getComponent(SettingsComponent.class).facebookLogin){
            //facebookAccessor.login();
            settings.getComponent(SettingsComponent.class).facebookLogin = false;
        }
        /*
        if(facebookAccessor.gdxFacebook.isSignedIn()){
            nextScene = 3;
            this.dispose();
            this.isRunning = false;
        } else {
            nextScene = IDENTIFIER;
        }
        */
        return nextScene;
    }

    @Override
    public void dispose() {
        inputSystem.dispose();
        cameraSystem.dispose();
        audioSystem.dispose();
        renderSystem.dispose();
        resourceManagementSystem.disposeAll();
        resourceManagementSystem.dispose();
    }
}
