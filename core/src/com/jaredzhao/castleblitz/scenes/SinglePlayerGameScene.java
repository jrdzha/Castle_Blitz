package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.graphics.TextComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.BattleMechanicsStatesComponent;
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

import java.lang.System;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SinglePlayerGameScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component DisposableEntitySystem (ECS)

    private PreferencesAccessor preferencesAccessor;

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private RenderEntitySystem renderEntitySystem; //Because we need reference to feed debug data later

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on
    private Entity battleMechanics;
    private Entity whosTurnText;
    private SettingsComponent settingsComponent;
    private String[][][] rawMap;

    private HashMap<String, DisposableEntitySystem> systems = new LinkedHashMap<String, DisposableEntitySystem>();

    public static String team;

    private GameServer singlePlayerGameServer;

    private PerformanceCounter performanceCounter;

    public SinglePlayerGameScene(PreferencesAccessor preferencesAccessor) {
        IDENTIFIER = 3;
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
        if (GameEngine.platform.equals("mobile")) {
            camera = entityFactory.createCamera(7 * GameEngine.tileSize);
        } else if (GameEngine.platform.equals("desktop")) {
            camera = entityFactory.createCamera(30 * GameEngine.tileSize);
        }

        Entity settings = entityFactory.createSettings();
        Entity fogOfWar = entityFactory.createFogOfWar(rawMap[0].length, rawMap[0][0].length);
        whosTurnText = entityFactory.createText("YOUR TURN", 0, Gdx.graphics.getHeight() * 2 / 5, Color.WHITE, (int) (10 * camera.getComponent(CameraComponent.class).scale * GameEngine.tileSize / 16), true);
        settingsComponent = settings.getComponent(SettingsComponent.class);
        battleMechanics = entityFactory.createBattleMechanics();
        battleMechanics.getComponent(BattleMechanicsStatesComponent.class).isMyTurn = true;
        camera.getComponent(PositionComponent.class).x = GameEngine.tileSize * rawMap[0].length / 2 - GameEngine.tileSize / 2;
        camera.getComponent(PositionComponent.class).y = GameEngine.tileSize * rawMap[0][0].length / 2 - GameEngine.tileSize / 2;
        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(settings);
        ashleyEngine.addEntity(whosTurnText);

        Vector2 insets = new Vector2(GameEngine.safeAreaInsets.x / camera.getComponent(CameraComponent.class).scale, GameEngine.safeAreaInsets.y / camera.getComponent(CameraComponent.class).scale);

        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("pause", true,
                camera.getComponent(CameraComponent.class).cameraWidth / 2 - 10 * GameEngine.tileSize / 16,
                camera.getComponent(CameraComponent.class).cameraHeight / 2 - 10 * GameEngine.tileSize / 16 - insets.y, GameEngine.tileSize, GameEngine.tileSize));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("sound", false,
                camera.getComponent(CameraComponent.class).cameraWidth / 2 - 28 * GameEngine.tileSize / 16,
                camera.getComponent(CameraComponent.class).cameraHeight / 2 - 10 * GameEngine.tileSize / 16 - insets.y, GameEngine.tileSize, GameEngine.tileSize));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("sfx", false,
                camera.getComponent(CameraComponent.class).cameraWidth / 2 - 46 * GameEngine.tileSize / 16,
                camera.getComponent(CameraComponent.class).cameraHeight / 2 - 10 * GameEngine.tileSize / 16 - insets.y, GameEngine.tileSize, GameEngine.tileSize));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("fastforward", false,
                camera.getComponent(CameraComponent.class).cameraWidth / 2 - 64 * GameEngine.tileSize / 16,
                camera.getComponent(CameraComponent.class).cameraHeight / 2 - 10 * GameEngine.tileSize / 16 - insets.y, GameEngine.tileSize, GameEngine.tileSize));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("home", false,
                camera.getComponent(CameraComponent.class).cameraWidth / 2 - 82 * GameEngine.tileSize / 16,
                camera.getComponent(CameraComponent.class).cameraHeight / 2 - 10 * GameEngine.tileSize / 16 - insets.y, GameEngine.tileSize, GameEngine.tileSize));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("debug", false,
                camera.getComponent(CameraComponent.class).cameraWidth / 2 - 100 * GameEngine.tileSize / 16,
                camera.getComponent(CameraComponent.class).cameraHeight / 2 - 10 * GameEngine.tileSize / 16 - insets.y, GameEngine.tileSize, GameEngine.tileSize));

        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/test2.lvl"))));

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settingsComponent.soundOn = localSettings[0];
        settingsComponent.sfxOn = localSettings[1];

        int mapHeight = map.getComponent(MapComponent.class).mapEntities[0][0].length;

        renderEntitySystem = new RenderEntitySystem(camera, settings, fogOfWar, mapHeight, 1.35f, .03f);

        //Initialize systems
        systems.put("CameraEntitySystem", new CameraEntitySystem(map));
        systems.put("RenderEntitySystem", renderEntitySystem);
        systems.put("MapEntitySystem", new MapEntitySystem(singlePlayerGameServer, map, fogOfWar, battleMechanics));
        systems.put("InputEntitySystem", new InputEntitySystem(singlePlayerGameServer, preferencesAccessor, entityFactory, camera, settings, battleMechanics, mapHeight));
        systems.put("ResourceManagementEntitySystem", new ResourceManagementEntitySystem());
        systems.put("LightEntitySystem", new LightEntitySystem());
        systems.put("AudioEntitySystem", new AudioEntitySystem(entityFactory, audioFactory, camera, settings));
        systems.put("HighlightEntitySystem", new HighlightEntitySystem());
        systems.put("AnimationManagerEntitySystem", new AnimationManagerEntitySystem(settings));
        systems.put("BattleMechanicsEntitySystem", new BattleMechanicsEntitySystem(map, singlePlayerGameServer, battleMechanics));

        //Add systems to ashleyEngine
        for (HashMap.Entry<String, DisposableEntitySystem> entry : systems.entrySet()) {
            ashleyEngine.addSystem(entry.getValue());
        }

        performanceCounter = new PerformanceCounter("SinglePlayerGameScene");

        System.gc();
    }

    @Override
    public int render() throws InterruptedException {
        performanceCounter.tick();
        performanceCounter.start();

        if (settingsComponent.isPaused) {
            whosTurnText.getComponent(TextComponent.class).setText("PAUSED");
            whosTurnText.getComponent(PositionComponent.class).y = Gdx.graphics.getHeight() / 5;
        } else if (battleMechanics.getComponent(BattleMechanicsStatesComponent.class).isMyTurn) {
            whosTurnText.getComponent(TextComponent.class).setText("YOUR TURN");
            whosTurnText.getComponent(PositionComponent.class).y = Gdx.graphics.getHeight() * 2 / 5;
        } else {
            whosTurnText.getComponent(TextComponent.class).setText("OPPONENT'S TURN");
            whosTurnText.getComponent(PositionComponent.class).y = Gdx.graphics.getHeight() * 2 / 5;
        }

        //renderEntitySystem.updatePerformanceProfile(new double[]{performanceCounter.load.value});
        ashleyEngine.update(Gdx.graphics.getDeltaTime());

        if (settingsComponent.goHome) {
            settingsComponent.goHome = false;
            return GameEngine.homeScene.IDENTIFIER;
        }

        performanceCounter.stop();

        return IDENTIFIER;
    }

    @Override
    public void dispose() {
        systems.get("AudioEntitySystem").dispose();
        for (HashMap.Entry<String, DisposableEntitySystem> entry : systems.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().dispose();
                ashleyEngine.removeSystem(entry.getValue());
            }
        }
        ashleyEngine.removeAllEntities();
    }
}
