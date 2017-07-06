package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jaredzhao.castleblitz.components.audio.HasSoundEffectComponent;
import com.jaredzhao.castleblitz.components.graphics.LayerComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.utils.LayerSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class InputSystem extends EntitySystem implements InputProcessor{

    private ImmutableArray<Entity> selectables;

    private boolean beingDragged, beingTapped;
    private int lastX, lastY, selectedX, selectedY;

    private Engine ashleyEngine;
    private EntityFactory entityFactory;
    private OrthographicCamera orthographicCamera;
    private Entity camera;
    private Entity settings;

    private ArrayList<Entity> sortedSelectables;

    private float scale;

    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CameraComponent> cameraComponentComponentMapper = ComponentMapper.getFor(CameraComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);

    public InputSystem(Engine ashleyEngine, EntityFactory entityFactory, Entity camera, Entity settings){
        Gdx.input.setInputProcessor(this);
        this.orthographicCamera = camera.getComponent(CameraComponent.class).camera;
        this.scale = camera.getComponent(CameraComponent.class).scale;
        this.ashleyEngine = ashleyEngine;
        this.entityFactory = entityFactory;
        this.camera = camera;
        this.settings = settings;
    }

    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine){
        selectables = engine.getEntitiesFor(Family.all(SelectableComponent.class, PositionComponent.class, LayerComponent.class).get());
    }

    public void update(float deltaTime){
        for(Entity entity : selectables){
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
            if (selectableComponent.removeSelection) {
                selectableComponent.isSelected = false;
                selectableComponent.removeSelection = false;
                entity.add(new HasSoundEffectComponent());
                entity.getComponent(HasSoundEffectComponent.class).soundName = "audio/sfx/blop.wav";
                entity.getComponent(HasSoundEffectComponent.class).continuous = false;
                entity.getComponent(HasSoundEffectComponent.class).dynamicVolume = false;
                entity.getComponent(HasSoundEffectComponent.class).soundLength = .27f;

                removeMoveAndAttackButtons();

                settings.getComponent(SettingsComponent.class).attack = false;
                settings.getComponent(SettingsComponent.class).move = false;
            }

            if (selectableComponent.addSelection) {
                selectableComponent.isSelected = true;
                selectableComponent.addSelection = false;
                entity.add(new HasSoundEffectComponent());
                entity.getComponent(HasSoundEffectComponent.class).soundName = "audio/sfx/blop.wav";
                entity.getComponent(HasSoundEffectComponent.class).continuous = false;
                entity.getComponent(HasSoundEffectComponent.class).dynamicVolume = false;
                entity.getComponent(HasSoundEffectComponent.class).soundLength = .27f;

                ashleyEngine.addEntity(entityFactory.createDynamicPositionUI("move", positionComponent.x - 10, positionComponent.y + 18));
                ashleyEngine.addEntity(entityFactory.createDynamicPositionUI("attack", positionComponent.x + 10, positionComponent.y + 18));
            }

            if(settings.getComponent(SettingsComponent.class).move || settings.getComponent(SettingsComponent.class).attack){
                removeMoveAndAttackButtons();
            }
        }
    }

    public void removeMoveAndAttackButtons(){
        for(int i = 0; i < selectables.size(); i++){
            Entity entity1 = selectables.get(i);
            SelectableComponent selectableComponent1 = selectableComponentComponentMapper.get(entity1);
            if(selectableComponent1.name.equals("move") || selectableComponent1.name.equals("attack")){
                ashleyEngine.removeEntity(entity1);
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        beingTapped = true;
        lastX = screenX;
        lastY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { //User input for selecting characters
        if(beingTapped && !beingDragged) {

            sortedSelectables = LayerSorter.sortByLayers(selectables);
            Collections.reverse(sortedSelectables);

            boolean nothingSelectedYet = true;

            for (Entity entity : sortedSelectables){
                PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
                SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

                selectedX = (int) Math.floor((screenX / scale) + orthographicCamera.position.x - orthographicCamera.viewportWidth + selectableComponent.sizeX / 2);
                selectedY = (int) Math.floor(((-1) * screenY / scale) + orthographicCamera.position.y + selectableComponent.sizeY / 2);

                if (selectedX > positionComponent.x &&
                        selectedX < positionComponent.x + selectableComponent.sizeX &&
                        selectedY > positionComponent.y &&
                        selectedY < positionComponent.y + selectableComponent.sizeY &&
                        nothingSelectedYet) {

                    if (selectableComponent.name.equals("pause")) {
                        settings.getComponent(SettingsComponent.class).isPaused = !settings.getComponent(SettingsComponent.class).isPaused;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("fastforward")) {
                        settings.getComponent(SettingsComponent.class).fastForward = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("debug")) {
                        settings.getComponent(SettingsComponent.class).debug = !settings.getComponent(SettingsComponent.class).debug;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("character") && !settings.getComponent(SettingsComponent.class).isPaused) {
                        if(!selectableComponent.isSelected) {
                            if(!settings.getComponent(SettingsComponent.class).move && !settings.getComponent(SettingsComponent.class).attack) {
                                selectableComponent.addSelection = true;
                                nothingSelectedYet = false;
                            } else {
                                nothingSelectedYet = true;
                            }
                        } else {
                            selectableComponent.removeSelection = true;
                            nothingSelectedYet = false;
                        }
                    }

                    if (selectableComponent.name.equals("tile") && !settings.getComponent(SettingsComponent.class).isPaused) {
                        selectableComponent.isSelected = true;
                        settings.getComponent(SettingsComponent.class).move = false;
                        nothingSelectedYet = true;
                    }

                    if (selectableComponent.name.equals("move") && !settings.getComponent(SettingsComponent.class).isPaused) {
                        settings.getComponent(SettingsComponent.class).move = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("attack") && !settings.getComponent(SettingsComponent.class).isPaused) {
                        //settings.getComponent(SettingsComponent.class).attack = true;
                        nothingSelectedYet = false;
                    }

                }
            }
            if(nothingSelectedYet){
                for (Entity entity : sortedSelectables) {
                    SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
                    if (selectableComponent.name.equals("character")) {
                        if (selectableComponent.isSelected) {
                            selectableComponent.removeSelection = true;
                        }
                    }
                }
            }
        }
        beingDragged = false;
        beingTapped = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { //Move camera when screen is dragged
        beingDragged = true;
        if(!settings.getComponent(SettingsComponent.class).isPaused) {
            PositionComponent positionComponent = positionComponentComponentMapper.get(camera);
            CameraComponent cameraComponent = cameraComponentComponentMapper.get(camera);
            positionComponent.x -= ((screenX - lastX) / cameraComponent.scale);
            positionComponent.y += ((screenY - lastY) / cameraComponent.scale);
            lastX = screenX;
            lastY = screenY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
