package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.graphics.TextComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.AnimationFactory;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.factories.MapFactory;
import com.jaredzhao.castleblitz.servers.CharacterSelectionServer;
import com.jaredzhao.castleblitz.servers.GameServer;
import com.jaredzhao.castleblitz.systems.*;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;
import com.jaredzhao.castleblitz.utils.SocketAccessor;

import java.util.HashMap;


public class HomeScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component DisposableEntitySystem (ECS)

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on
    private Entity headingText;
    private Entity usernameText;
    private Entity levelText;
    private Entity xpText;
    private Entity idText;
    private Entity preIdText;

    private float cameraScale;

    private SettingsComponent settingsComponent;

    private HashMap<String, DisposableEntitySystem> systems = new HashMap<String, DisposableEntitySystem>();

    private PreferencesAccessor preferencesAccessor;
    private SocketAccessor socketAccessor;

    private GameServer characterSelectionServer;
    public static String team;

    public HomeScene(PreferencesAccessor preferencesAccessor, SocketAccessor socketAccessor) {
        IDENTIFIER = 2;
        this.preferencesAccessor = preferencesAccessor;
        this.socketAccessor = socketAccessor;
    }

    @Override
    public void init() {
        socketAccessor.outputQueue.add("REQUEST.STATS");

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
        String[][][] rawMap = mapFactory.loadRawMap(Gdx.files.internal("levels/armory.lvl"));
        characterSelectionServer.loadMap(rawMap);
        map = mapFactory.loadMap(rawMap);

        camera = entityFactory.createCamera(7 * GameEngine.tileSize);
        cameraScale = camera.getComponent(CameraComponent.class).scale;

        Entity fogOfWar = entityFactory.createFogOfWar(rawMap[0].length, rawMap[0][0].length);
        Entity settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        settingsComponent = settings.getComponent(SettingsComponent.class);

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settingsComponent.username = preferencesAccessor.loadUserData()[0];
        settingsComponent.homeScreen = "homeCastle";
        settingsComponent.soundOn = localSettings[0];
        settingsComponent.sfxOn = localSettings[1];

        headingText = entityFactory.createText("Castle", 0, Gdx.graphics.getHeight() * 7 / 20 - GameEngine.safeAreaInsets.y, Color.WHITE, (int) (16 * camera.getComponent(CameraComponent.class).scale), true);
        System.out.println(settingsComponent.username);
        usernameText = entityFactory.createText(settingsComponent.username, -52 * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 16 * cameraScale, Color.WHITE, (int) (8 * camera.getComponent(CameraComponent.class).scale), false);
        camera.getComponent(PositionComponent.class).x = 8 * rawMap[0].length - 16;
        camera.getComponent(PositionComponent.class).y = 8 * rawMap[0][0].length - 16;

        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(headingText);
        ashleyEngine.addEntity(usernameText);
        ashleyEngine.addEntity(settings);

        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeShop", true,
                -36,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, GameEngine.tileSize, GameEngine.tileSize * 2));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homePotions", true,
                -18,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, GameEngine.tileSize, GameEngine.tileSize * 2));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeCastle", true,
                0,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, GameEngine.tileSize, GameEngine.tileSize * 2));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeArmory", true,
                18,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, GameEngine.tileSize, GameEngine.tileSize * 2));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeRanking", true,
                36,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, GameEngine.tileSize, GameEngine.tileSize * 2));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("battle", true, 0, -20, GameEngine.tileSize * 5, GameEngine.tileSize));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeLevelStatus", true, -36, camera.getComponent(CameraComponent.class).cameraHeight / 2 - GameEngine.safeAreaInsets.y / cameraScale - 8, GameEngine.tileSize * 2, GameEngine.tileSize / 2));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeGoldStatus", true, 0, camera.getComponent(CameraComponent.class).cameraHeight / 2 - GameEngine.safeAreaInsets.y / cameraScale - 8, GameEngine.tileSize * 2, GameEngine.tileSize / 2));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeShardStatus", true, 36, camera.getComponent(CameraComponent.class).cameraHeight / 2 - GameEngine.safeAreaInsets.y / cameraScale - 8, GameEngine.tileSize * 2, GameEngine.tileSize / 2));

        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/armory.lvl"))));

        int mapHeight = map.getComponent(MapComponent.class).mapEntities[0][0].length;

        //Initialize systems
        systems.put("CameraEntitySystem", new CameraEntitySystem(map));
        systems.put("RenderEntitySystem", new RenderEntitySystem(camera, settings, fogOfWar, mapHeight, 1.6f, .12f));
        systems.put("MapEntitySystem", new MapEntitySystem(characterSelectionServer, map, fogOfWar, battleMechanics));
        systems.put("InputEntitySystem", new InputEntitySystem(characterSelectionServer, preferencesAccessor, entityFactory, camera, settings, battleMechanics, mapHeight));
        systems.put("ResourceManagementEntitySystem", new ResourceManagementEntitySystem());
        systems.put("LightEntitySystem", new LightEntitySystem());
        systems.put("AudioEntitySystem", new AudioEntitySystem(entityFactory, audioFactory, camera, settings));
        systems.put("HighlightEntitySystem", new HighlightEntitySystem());
        systems.put("AnimationManagerEntitySystem", new AnimationManagerEntitySystem(settings));
        systems.put("BattleMechanicsEntitySystem", new BattleMechanicsEntitySystem(map, characterSelectionServer, battleMechanics));
        ((RenderEntitySystem) systems.get("RenderEntitySystem")).renderGaussianBlur = false;
        ((RenderEntitySystem) systems.get("RenderEntitySystem")).renderFogOfWar = false;
        ((RenderEntitySystem) systems.get("RenderEntitySystem")).renderEntities = false;

        //Add systems to ashleyEngine
        for (HashMap.Entry<String, DisposableEntitySystem> entry : systems.entrySet()) {
            ashleyEngine.addSystem(entry.getValue());
        }

        while (settingsComponent.id.equals("") ||
                settingsComponent.rank.equals("") ||
                settingsComponent.gold.equals("") ||
                settingsComponent.shards.equals("") ||
                settingsComponent.xp.equals("") ||
                settingsComponent.level.equals("") ||
                settingsComponent.unlockedCharacters.size() == 0) {
            socketAccessor.update();
            if (socketAccessor.inputQueue.size() > 0) {
                String[] input = socketAccessor.inputQueue.get(0).split("\\.");
                socketAccessor.inputQueue.remove(0);
                if (input[0].equals("ID")) {
                    settingsComponent.id = input[1];
                    idText = entityFactory.createText(settingsComponent.id, 52 * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 16 * cameraScale, new Color(1, 0.5f, 0.5f, 1), (int) (8 * camera.getComponent(CameraComponent.class).scale), false);
                    idText.getComponent(PositionComponent.class).x -= idText.getComponent(TextComponent.class).glyphLayout.width;
                    preIdText = entityFactory.createText("/", 47 * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 16 * cameraScale, new Color(0.8f, 0.8f, 0.8f, 1), (int) (8 * camera.getComponent(CameraComponent.class).scale), false);
                    preIdText.getComponent(PositionComponent.class).x -= idText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(preIdText);
                    ashleyEngine.addEntity(idText);
                } else if (input[0].equals("RANK")) {
                    settingsComponent.rank = input[1];
                } else if (input[0].equals("LEVEL")) {
                    settingsComponent.level = input[1];
                    levelText = entityFactory.createText(input[1], -47.75f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 4.75f * cameraScale, Color.WHITE, (int) (6 * camera.getComponent(CameraComponent.class).scale), true);
                    ashleyEngine.addEntity(levelText);
                } else if (input[0].equals("XP")) {
                    settingsComponent.xp = input[1];
                    xpText = entityFactory.createText(input[1], -21.25f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 6.5f * cameraScale, Color.WHITE, (int) (6 * camera.getComponent(CameraComponent.class).scale), false);
                    xpText.getComponent(PositionComponent.class).x -= xpText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(xpText);
                } else if (input[0].equals("SHARDS")) {
                    settingsComponent.shards = input[1];
                    xpText = entityFactory.createText(input[1], 50.75f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 6.5f * cameraScale, Color.WHITE, (int) (6 * camera.getComponent(CameraComponent.class).scale), false);
                    xpText.getComponent(PositionComponent.class).x -= xpText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(xpText);
                } else if (input[0].equals("GOLD")) {
                    settingsComponent.gold = input[1];
                    xpText = entityFactory.createText(input[1], 14.75f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 6.5f * cameraScale, Color.WHITE, (int) (6 * camera.getComponent(CameraComponent.class).scale), false);
                    xpText.getComponent(PositionComponent.class).x -= xpText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(xpText);
                } else if (input[0].equals("UNLOCKED-CHARACTERS")) {
                    for (int i = 1; i < input.length; i++) {
                        settingsComponent.unlockedCharacters.add(input[i]);
                    }
                }
            }
        }

        System.gc();
    }

    @Override
    public int render() throws InterruptedException {

        ashleyEngine.update(Gdx.graphics.getDeltaTime());

        headingText.getComponent(TextComponent.class).setText(settingsComponent.homeScreen.substring(4));

        ((RenderEntitySystem) systems.get("RenderEntitySystem")).renderEntities = settingsComponent.homeScreen.equals("homeArmory");
        settingsComponent.sfxOn = settingsComponent.homeScreen.equals("homeArmory");

        if (settingsComponent.battle) {
            return GameEngine.singlePlayerGameScene.IDENTIFIER;
        }
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
