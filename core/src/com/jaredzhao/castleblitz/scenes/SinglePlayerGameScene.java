package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.AnimationFactory;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.factories.MapFactory;
import com.jaredzhao.castleblitz.servers.GameServer;
import com.jaredzhao.castleblitz.servers.SinglePlayerGameServer;
import com.jaredzhao.castleblitz.systems.*;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;

public class SinglePlayerGameScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component System (ECS)

    private PreferencesAccessor preferencesAccessor;

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on
    private String[][][] rawMap;

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

    public static String team;

    private GameServer singlePlayerGameServer;

    public SinglePlayerGameScene(PreferencesAccessor preferencesAccessor){
        IDENTIFIER = 1;
        this.preferencesAccessor = preferencesAccessor;
    }

    @Override
    public void init() {
        //Initialize ashleyEngine
        ashleyEngine = new Engine();

        //Initialize SinglePlayerGameServer
        singlePlayerGameServer = new SinglePlayerGameServer();
        singlePlayerGameServer.init();
        team = singlePlayerGameServer.getTeam();

        //Initialize factories
        audioFactory = new AudioFactory();
        animationFactory = new AnimationFactory();
        entityFactory = new EntityFactory(animationFactory, audioFactory);
        mapFactory = new MapFactory(ashleyEngine, entityFactory);

        //Load level data from disk
        rawMap = mapFactory.loadRawMap(Gdx.files.internal("levels/test2.lvl"));
        singlePlayerGameServer.loadMap(rawMap);
        map = mapFactory.loadMap(rawMap);

        //Create entities
        camera = entityFactory.createCamera();
        Entity settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        //Entity fogOfWar = entityFactory.createFogOfWar(.15f, .15f, .25f, .6f, rawMap[0].length, rawMap[0][0].length);
        Entity fogOfWar = entityFactory.createFogOfWar(0, 0, 0, .3f, rawMap[0].length, rawMap[0][0].length);
        camera.getComponent(PositionComponent.class).x = 8 * rawMap[0].length - 8;
        camera.getComponent(PositionComponent.class).y = 8 * rawMap[0][0].length - 8;
        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(settings);
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("pause", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 10, 115, 16, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("sound", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 28, 115, 16, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("sfx", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 46, 115, 16, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("fastforward", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 64, 115, 16, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("debug", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 82, 115, 16, 16));
        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/test2.lvl"))));

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settings.getComponent(SettingsComponent.class).soundOn = localSettings[0];
        settings.getComponent(SettingsComponent.class).sfxOn = localSettings[1];

        //Initialize systems
        cameraSystem = new CameraSystem(map);
        renderSystem = new RenderSystem(ashleyEngine, camera, settings, battleMechanics, fogOfWar);
        mapSystem = new MapSystem(singlePlayerGameServer, map, fogOfWar);
        inputSystem = new InputSystem(ashleyEngine, singlePlayerGameServer, preferencesAccessor, entityFactory, camera, settings, battleMechanics);
        resourceManagementSystem = new ResourceManagementSystem(ashleyEngine);
        lightSystem = new LightSystem(ashleyEngine);
        audioSystem = new AudioSystem(entityFactory, audioFactory, camera, settings);
        highlightSystem = new HighlightSystem(ashleyEngine, map, battleMechanics);
        animationManagerSystem = new AnimationManagerSystem(settings);
        battleMechanicsSystem = new BattleMechanicsSystem(map, singlePlayerGameServer, battleMechanics);

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
