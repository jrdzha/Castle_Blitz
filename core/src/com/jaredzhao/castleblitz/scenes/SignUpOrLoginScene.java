package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.AnimationFactory;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.factories.MapFactory;
import com.jaredzhao.castleblitz.servers.CharacterSelectionServer;
import com.jaredzhao.castleblitz.servers.EmptyServer;
import com.jaredzhao.castleblitz.servers.GameServer;
import com.jaredzhao.castleblitz.systems.*;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;
import com.jaredzhao.castleblitz.utils.SocketAccessor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpOrLoginScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component System (ECS)

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on

    private SettingsComponent settingsComponent;

    private CameraSystem cameraSystem; //System for moving the camera
    private RenderSystem renderSystem; //System for rendering to the screen
    private MapSystem mapSystem; //System to create screen positions for new map entities
    private InputSystem inputSystem; //System for user input
    private ResourceManagementSystem resourceManagementSystem; //Garbage-Collection System
    private LightSystem lightSystem; //System to retrieve light components from new entities to add to ashleyEngine
    private AudioSystem audioSystem; //System for dynamic audio
    private HighlightSystem highlightSystem; //System for handling highlight updates
    private AnimationManagerSystem animationManagerSystem; //System for changing between different animation tracks
    private BattleMechanicsSystem battleMechanicsSystem;

    private PreferencesAccessor preferencesAccessor;
    private SocketAccessor socketAccessor;

    private GameServer characterSelectionServer;
    public static String team;

    MessageDigest digest;

    public SignUpOrLoginScene(PreferencesAccessor preferencesAccessor, SocketAccessor socketAccessor){
        IDENTIFIER = 4;
        this.preferencesAccessor = preferencesAccessor;
        this.socketAccessor = socketAccessor;
    }

    @Override
    public void init() {

        //Initialize ashleyEngine
        ashleyEngine = new Engine();

        characterSelectionServer = new CharacterSelectionServer();
        characterSelectionServer.init();
        team = "None";

        //Initialize factories
        audioFactory = new AudioFactory();
        animationFactory = new AnimationFactory();
        entityFactory = new EntityFactory(animationFactory, audioFactory);
        mapFactory = new MapFactory(ashleyEngine, entityFactory);

        //Create entities
        String[][][] rawMap = mapFactory.loadRawMap(Gdx.files.internal("levels/test2.lvl"));
        characterSelectionServer.loadMap(rawMap);
        map = mapFactory.loadMap(rawMap);

        if(GameEngine.safeAreaInsets.y != 0) {
            camera = entityFactory.createCamera(300);
        } else {
            camera = entityFactory.createCamera(250);
        }

        Entity fogOfWar = entityFactory.createFogOfWar(0, 0, 0, .3f, rawMap[0].length, rawMap[0][0].length);
        Entity settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        settingsComponent = settings.getComponent(SettingsComponent.class);
        camera.getComponent(PositionComponent.class).x = 8 * rawMap[0].length - 8;
        camera.getComponent(PositionComponent.class).y = 8 * rawMap[0][0].length - 8;

        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(settings);

        Vector2 insets = new Vector2(GameEngine.safeAreaInsets.x / camera.getComponent(CameraComponent.class).scale, GameEngine.safeAreaInsets.y / camera.getComponent(CameraComponent.class).scale);

        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("login", true, 0, -70, 80, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("signUp", true, 0, -90, 80, 16));

        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/test2.lvl"))));

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settingsComponent.soundOn = localSettings[0];
        settingsComponent.sfxOn = localSettings[1];

        int mapHeight = map.getComponent(MapComponent.class).mapEntities[0][0].length;

        //Initialize systems
        cameraSystem = new CameraSystem(map);
        renderSystem = new RenderSystem(ashleyEngine, camera, settings, battleMechanics, fogOfWar, mapHeight, 1.6f, .12f);
        mapSystem = new MapSystem(characterSelectionServer, map, fogOfWar, battleMechanics);
        inputSystem = new InputSystem(ashleyEngine, characterSelectionServer, preferencesAccessor, entityFactory, camera, settings, battleMechanics, mapHeight);
        resourceManagementSystem = new ResourceManagementSystem(ashleyEngine);
        lightSystem = new LightSystem();
        audioSystem = new AudioSystem(entityFactory, audioFactory, camera, settings);
        highlightSystem = new HighlightSystem(ashleyEngine);
        animationManagerSystem = new AnimationManagerSystem(settings);
        battleMechanicsSystem = new BattleMechanicsSystem(map, characterSelectionServer, battleMechanics);

        renderSystem.renderGaussianBlur = true;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Add systems to ashleyEngine
        ashleyEngine.addSystem(mapSystem);
        ashleyEngine.addSystem(highlightSystem);
        ashleyEngine.addSystem(inputSystem);
        ashleyEngine.addSystem(cameraSystem);
        ashleyEngine.addSystem(lightSystem);
        ashleyEngine.addSystem(audioSystem);
        ashleyEngine.addSystem(renderSystem);
        ashleyEngine.addSystem(resourceManagementSystem);
        ashleyEngine.addSystem(animationManagerSystem);
        ashleyEngine.addSystem(battleMechanicsSystem);
        System.gc();
    }

    @Override
    public int render() throws InterruptedException {
        ashleyEngine.update(Gdx.graphics.getDeltaTime());

        if(settingsComponent.signUp){
            this.dispose();
            this.isRunning = false;
            return 2;
        } else if(settingsComponent.login){
            this.dispose();
            this.isRunning = false;
            return 5;
        }
        return IDENTIFIER;
    }

    @Override
    public void dispose() {
        mapSystem.dispose();
        highlightSystem.dispose();
        inputSystem.dispose();
        cameraSystem.dispose();
        lightSystem.dispose();
        audioSystem.dispose();
        renderSystem.dispose();
        animationManagerSystem.dispose();
        battleMechanicsSystem.dispose();
        resourceManagementSystem.disposeAll();
        resourceManagementSystem.dispose();
    }
}