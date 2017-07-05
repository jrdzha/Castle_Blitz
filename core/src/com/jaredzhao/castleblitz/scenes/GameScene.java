package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.AnimationFactory;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.factories.MapFactory;
import com.jaredzhao.castleblitz.systems.*;

public class GameScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component System (ECS)

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on

    private CameraSystem cameraSystem; //System for moving the camera
    private RenderSystem renderSystem; //System for rendering to the screen
    private MapSystem mapSystem; //System to create screen positions for new map entities
    private InputSystem inputSystem; //System for user input
    private ResourceManagementSystem resourceManagementSystem; //Garbage-Collection System
    private LightSystem lightSystem; //System to retrieve light components from new entities to add to ashleyEngine
    private AudioSystem audioSystem; //System for dynamic audio
    private HighlightSystem highlightSystem; //System for handling highlight updates
    private AnimationManagerSystem animationManagerSystem; //System for changing between different animation tracks

    public GameScene(){
        IDENTIFIER = 1;

        //Initialize ashleyEngine
        ashleyEngine = new Engine();

        //Initialize factories
        audioFactory = new AudioFactory();
        animationFactory = new AnimationFactory();
        entityFactory = new EntityFactory(animationFactory, audioFactory, camera);
        mapFactory = new MapFactory(ashleyEngine, entityFactory);

        //Load level data from disk
        Object[] levelData = mapFactory.loadMap(Gdx.files.internal("levels/test2.lvl"));

        //Create entities
        map = (Entity)levelData[0];
        camera = entityFactory.createCamera();
        camera.getComponent(PositionComponent.class).x = 8 * map.getComponent(MapComponent.class).mapEntities[0].length - 8;
        camera.getComponent(PositionComponent.class).y = 8 * map.getComponent(MapComponent.class).mapEntities[0][0].length - 8;
        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("pause", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 10, 115));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("fastforward", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 28, 115));
        ashleyEngine.addEntity(entityFactory.createStaticPositionUI("debug", camera.getComponent(CameraComponent.class).cameraWidth / 2 - 46, 115));
        ashleyEngine.addEntity(entityFactory.createMusic((String[])levelData[1]));
        Entity settings = entityFactory.createSettings();
        ashleyEngine.addEntity(settings);

        //Initialize systems
        cameraSystem = new CameraSystem(map);
        renderSystem = new RenderSystem(ashleyEngine, camera, settings);
        mapSystem = new MapSystem(map);
        inputSystem = new InputSystem(camera, settings);
        resourceManagementSystem = new ResourceManagementSystem(ashleyEngine);
        lightSystem = new LightSystem(ashleyEngine);
        audioSystem = new AudioSystem(entityFactory, audioFactory, camera, settings);
        highlightSystem = new HighlightSystem(ashleyEngine, entityFactory, map);
        animationManagerSystem = new AnimationManagerSystem();

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
    }

    @Override
    public void init() {
        System.gc();
    }

    @Override
    public int render() throws InterruptedException {
        ashleyEngine.update(Gdx.graphics.getDeltaTime());
        return IDENTIFIER;
    }
}
