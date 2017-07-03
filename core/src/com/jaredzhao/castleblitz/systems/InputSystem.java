package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jaredzhao.castleblitz.components.graphics.LayerComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.utils.LayerSorter;

import java.util.ArrayList;
import java.util.Collections;

public class InputSystem extends EntitySystem implements InputProcessor{

    private ImmutableArray<Entity> selectables;

    private boolean beingDragged, beingTapped;
    private int lastX, lastY, selectedX, selectedY;

    private OrthographicCamera orthographicCamera;
    private Entity camera;
    private Entity settings;

    private ArrayList<Entity> sortedSelectables;

    private float scale;

    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CameraComponent> cameraComponentComponentMapper = ComponentMapper.getFor(CameraComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);

    public InputSystem(Entity camera, Entity settings){
        Gdx.input.setInputProcessor(this);
        this.orthographicCamera = camera.getComponent(CameraComponent.class).camera;
        this.scale = camera.getComponent(CameraComponent.class).scale;
        this.camera = camera;
        this.settings = settings;
    }

    public void reset() {

    }

    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine){
        selectables = engine.getEntitiesFor(Family.all(SelectableComponent.class, PositionComponent.class, LayerComponent.class).get());
    }

    public void update(float deltaTime){

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

            for (int i = 0; i < sortedSelectables.size(); ++i) {
                Entity entity = sortedSelectables.get(i);
                PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
                SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

                selectedX = (int) Math.floor((screenX / scale) + orthographicCamera.position.x - orthographicCamera.viewportWidth + selectableComponent.sizeX / 2);
                selectedY = (int) Math.floor(((-1) * screenY / scale) + orthographicCamera.position.y + selectableComponent.sizeY / 2);

                if (selectableComponent.removeSelection) {
                    selectableComponent.isSelected = false;
                    selectableComponent.removeSelection = false;
                }

                if (selectedX > positionComponent.x &&
                        selectedX < positionComponent.x + selectableComponent.sizeX &&
                        selectedY > positionComponent.y &&
                        selectedY < positionComponent.y + selectableComponent.sizeY &&
                        nothingSelectedYet) {
                    if (selectableComponent.name.equals("pause")) {
                        settings.getComponent(SettingsComponent.class).isPaused = !settings.getComponent(SettingsComponent.class).isPaused;
                    } else if (selectableComponent.name.equals("fastforward")) {
                        settings.getComponent(SettingsComponent.class).fastForward = true;
                    } else if (selectableComponent.name.equals("debug")) {
                        settings.getComponent(SettingsComponent.class).debug = !settings.getComponent(SettingsComponent.class).debug;
                    } else if (selectableComponent.name.equals("character") && !settings.getComponent(SettingsComponent.class).isPaused) {

                        if (entity.getComponent(CharacterPropertiesComponent.class) == null) {
                            selectableComponent.isSelected = true;
                        }

                        if (entity.getComponent(CharacterPropertiesComponent.class) != null) {
                            if (!selectableComponent.isSelected) {
                                selectableComponent.isSelected = true;
                                entity.add(new UpdateHighlightComponent(0.35f));
                            } else if (selectableComponent.isSelected) {
                                selectableComponent.isSelected = false;
                                entity.add(new UpdateHighlightComponent(0.15f));
                            }
                        }
                    }
                    nothingSelectedYet = false;
                } else {
                    if (selectableComponent.name.equals("character")) {
                        if (selectableComponent.isSelected == true && entity.getComponent(CharacterPropertiesComponent.class) != null) {
                            entity.add(new UpdateHighlightComponent(0.15f));
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
