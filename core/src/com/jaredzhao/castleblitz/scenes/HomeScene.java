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

    private Engine ashleyEngine; //Engine controlling the Entity-Component System (ECS)

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on
    private Entity headingText;
    private Entity usernameText;
    private Entity goldText;
    private Entity shardsText;
    private Entity levelText;
    private Entity xpText;
    private Entity idText;
    private Entity preIdText;

    private float cameraScale;

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
    private HashMap<String, String> userData = new HashMap<String, String>();

    private GameServer characterSelectionServer;
    public static String team;

    public HomeScene(PreferencesAccessor preferencesAccessor, SocketAccessor socketAccessor){
        IDENTIFIER = 2;
        this.preferencesAccessor = preferencesAccessor;
        this.socketAccessor = socketAccessor;
    }

    @Override
    public void init() {
        socketAccessor.outputQueue.add("request.stats");

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

        if(GameEngine.safeAreaInsets.y != 0) {
            camera = entityFactory.createCamera(300);
        } else {
            camera = entityFactory.createCamera(250);
        }
        cameraScale = camera.getComponent(CameraComponent.class).scale;

        Entity fogOfWar = entityFactory.createFogOfWar(0, 0, 0, .3f, rawMap[0].length, rawMap[0][0].length);
        Entity settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        settingsComponent = settings.getComponent(SettingsComponent.class);

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settingsComponent.username = preferencesAccessor.loadUserData()[0];
        settingsComponent.homeScreen = "homeCastle";
        settingsComponent.soundOn = localSettings[0];
        settingsComponent.sfxOn = localSettings[1];

        headingText = entityFactory.createText("Castle", 0, Gdx.graphics.getHeight() * 7 / 20 - GameEngine.safeAreaInsets.y, Color.WHITE, (int)(16 * camera.getComponent(CameraComponent.class).scale), true);
        usernameText = entityFactory.createText(settingsComponent.username, -52 * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 16 * cameraScale, Color.WHITE, (int)(8 * camera.getComponent(CameraComponent.class).scale), false);
        camera.getComponent(PositionComponent.class).x = 8 * rawMap[0].length - 16;
        camera.getComponent(PositionComponent.class).y = 8 * rawMap[0][0].length - 16;

        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(headingText);
        ashleyEngine.addEntity(usernameText);
        ashleyEngine.addEntity(settings);

        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeShop", true,
                -36,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homePotions", true,
                -18,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeCastle", true,
                0,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeArmory", true,
                18,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeRanking", true,
                36,
                camera.getComponent(CameraComponent.class).cameraHeight / -2 + 30, 16, 32));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("battle", true, 0, -20, 80, 16));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeLevelStatus", true, -36, camera.getComponent(CameraComponent.class).cameraHeight / 2 - GameEngine.safeAreaInsets.y / cameraScale - 8, 32, 8));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeGoldStatus", true, 0, camera.getComponent(CameraComponent.class).cameraHeight / 2 - GameEngine.safeAreaInsets.y / cameraScale - 8, 32, 8));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("homeShardStatus", true, 36, camera.getComponent(CameraComponent.class).cameraHeight / 2 - GameEngine.safeAreaInsets.y / cameraScale - 8, 32, 8));

        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/armory.lvl"))));

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

        renderSystem.renderEntities = false;
        renderSystem.renderFogOfWar = false;

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

        while(settingsComponent.id.equals("") ||
                settingsComponent.rank.equals("") ||
                settingsComponent.gold.equals("") ||
                settingsComponent.shards.equals("") ||
                settingsComponent.xp.equals("") ||
                settingsComponent.level.equals("") ||
                settingsComponent.unlockedCharacters.size() == 0){
            socketAccessor.update();
            if(socketAccessor.inputQueue.size() > 0) {
                String[] input = socketAccessor.inputQueue.get(0).split("\\.");
                socketAccessor.inputQueue.remove(0);
                if (input[0].equals("id")) {
                    settingsComponent.id = input[1];
                    idText = entityFactory.createText(settingsComponent.id, 52 * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 16 * cameraScale, new Color(1, 0.5f, 0.5f, 1), (int)(8 * camera.getComponent(CameraComponent.class).scale), false);
                    idText.getComponent(PositionComponent.class).x -= idText.getComponent(TextComponent.class).glyphLayout.width;
                    preIdText = entityFactory.createText("/", 47 * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 16 * cameraScale, new Color(0.8f, 0.8f, 0.8f, 1), (int)(8 * camera.getComponent(CameraComponent.class).scale), false);
                    preIdText.getComponent(PositionComponent.class).x -= idText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(preIdText);
                    ashleyEngine.addEntity(idText);
                } else if (input[0].equals("rank")) {
                    settingsComponent.rank = input[1];
                } else if (input[0].equals("level")) {
                    settingsComponent.level = input[1];
                    levelText = entityFactory.createText(input[1], -47.75f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 4.75f * cameraScale, Color.WHITE, (int)(6 * camera.getComponent(CameraComponent.class).scale), true);
                    ashleyEngine.addEntity(levelText);
                } else if (input[0].equals("xp")) {
                    settingsComponent.xp = input[1];
                    xpText = entityFactory.createText(input[1], -21.25f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 6.5f * cameraScale, Color.WHITE, (int)(6 * camera.getComponent(CameraComponent.class).scale), false);
                    xpText.getComponent(PositionComponent.class).x -= xpText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(xpText);
                } else if (input[0].equals("shards")) {
                    settingsComponent.shards = input[1];
                    xpText = entityFactory.createText(input[1], 50.75f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 6.5f * cameraScale, Color.WHITE, (int)(6 * camera.getComponent(CameraComponent.class).scale), false);
                    xpText.getComponent(PositionComponent.class).x -= xpText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(xpText);
                } else if (input[0].equals("gold")) {
                    settingsComponent.gold = input[1];
                    xpText = entityFactory.createText(input[1], 14.75f * cameraScale, Gdx.graphics.getHeight() / 2 - GameEngine.safeAreaInsets.y - 6.5f * cameraScale, Color.WHITE, (int)(6 * camera.getComponent(CameraComponent.class).scale), false);
                    xpText.getComponent(PositionComponent.class).x -= xpText.getComponent(TextComponent.class).glyphLayout.width;
                    ashleyEngine.addEntity(xpText);
                } else if (input[0].equals("unlocked-characters")) {
                    for(int i = 1; i < input.length; i++){
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

        renderSystem.renderEntities = settingsComponent.homeScreen.equals("homeArmory");
        settingsComponent.sfxOn = renderSystem.renderEntities;

        if(settingsComponent.battle){
            this.dispose();
            this.isRunning = false;
            return GameEngine.singlePlayerGameScene.IDENTIFIER;
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
