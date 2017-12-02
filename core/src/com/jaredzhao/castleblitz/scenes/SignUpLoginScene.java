package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.AnimationFactory;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.factories.MapFactory;
import com.jaredzhao.castleblitz.servers.EmptyServer;
import com.jaredzhao.castleblitz.systems.*;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;
import com.jaredzhao.castleblitz.utils.SocketAccessor;

public class SignUpLoginScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component System (ECS)

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on
    private Entity settings;
    private SettingsComponent settingsComponent;

    private CameraSystem cameraSystem; //System for moving the camera
    //private MapSystem mapSystem; //System to create screen positions for new map entities
    private RenderSystem renderSystem; //System for rendering to the screen
    private InputSystem inputSystem; //System for user input
    private AudioSystem audioSystem; //System for dynamic audio
    private ResourceManagementSystem resourceManagementSystem; //Garbage-Collection System
    private AnimationManagerSystem animationManagerSystem;

    private PreferencesAccessor preferencesAccessor;
    private SocketAccessor socketAccessor;

    private boolean loggedIn = false;

    public SignUpLoginScene(PreferencesAccessor preferencesAccessor, SocketAccessor socketAccessor){
        IDENTIFIER = 4;
        this.preferencesAccessor = preferencesAccessor;
        this.socketAccessor = socketAccessor;
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

        if(GameEngine.safeAreaInsets.y != 0) {
            camera = entityFactory.createCamera(300);
        } else {
            camera = entityFactory.createCamera(250);
        }

        Entity fogOfWar = entityFactory.createFogOfWar(0, 0, 0, .3f, rawMap[0].length, rawMap[0][0].length);
        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);

        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);

        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("editUsername", -cameraComponent.cameraWidth / 2 + 18, 73, 16, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("editPassword", -cameraComponent.cameraWidth / 2 + 18, 53, 16, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("editConfirmPassword", -cameraComponent.cameraWidth / 2 + 18, 33, 16, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("login", 0, -70, 80, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("signUp", 0, -90, 80, 16));

        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/home.lvl"))));
        settings = entityFactory.createSettings();
        settingsComponent = settings.getComponent(SettingsComponent.class);
        Entity battleMechanics = entityFactory.createBattleMechanics();
        ashleyEngine.addEntity(settings);

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settingsComponent.soundOn = localSettings[0];
        settingsComponent.sfxOn = localSettings[1];

        //Initialize systems
        cameraSystem = new CameraSystem(map);
        renderSystem = new RenderSystem(ashleyEngine, camera, settings, battleMechanics, fogOfWar, 0, 1, 0);
        inputSystem = new InputSystem(ashleyEngine, new EmptyServer(), preferencesAccessor, entityFactory, camera, settings, battleMechanics, 0);
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

        if(settingsComponent.editPassword || settingsComponent.editUsername){
            Gdx.input.setOnscreenKeyboardVisible(true);
        } else {
            Gdx.input.setOnscreenKeyboardVisible(false);
        }

        if(settingsComponent.signUp || settingsComponent.login){
            if(settingsComponent.username.length() > 5) {
                if (settingsComponent.password.length() > 7) {
                    if(!settingsComponent.password.contains(".")) {
                        if (settingsComponent.password.equals(settingsComponent.confirmPassword)) {
                            preferencesAccessor.putString("username", settingsComponent.username.toUpperCase());
                            preferencesAccessor.putString("password", settingsComponent.password);
                            if(settingsComponent.signUp) {
                                socketAccessor.outputQueue.add("register." + settingsComponent.username.toUpperCase() + "." + settingsComponent.password);
                            } else if(settingsComponent.login) {
                                socketAccessor.outputQueue.add("login." + settingsComponent.username.toUpperCase() + "." + settingsComponent.password);
                            }
                        } else {
                            settingsComponent.signUpLoginError = "Passwords don't match";
                        }
                    } else {
                        settingsComponent.signUpLoginError = "Password contains '.'";
                    }
                } else {
                    settingsComponent.signUpLoginError = "Password too short";
                }
            } else {
                settingsComponent.signUpLoginError = "Username too short";
            }
            settingsComponent.signUp = false;
            settingsComponent.login = false;
        }

        if(socketAccessor.inputQueue.size() != 0){
            if(socketAccessor.inputQueue.get(0).equals("register.successful")) {
                socketAccessor.inputQueue.remove(0);
                String[] userData = preferencesAccessor.loadUserData();
                socketAccessor.outputQueue.add("login." + userData[0] + "." + userData[1]);
            } else if(socketAccessor.inputQueue.get(0).equals("login.successful")) {
                loggedIn = true;
                socketAccessor.inputQueue.remove(0);
            }
        }

        int nextScene;
        if(loggedIn){
            nextScene = 3;
            this.dispose();
            this.isRunning = false;
        } else {
            nextScene = IDENTIFIER;
        }
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
