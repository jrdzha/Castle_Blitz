package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.audio.HasSoundEffectComponent;
import com.jaredzhao.castleblitz.components.graphics.LayerComponent;
import com.jaredzhao.castleblitz.components.graphics.VisibleComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.EntityFactory;
import com.jaredzhao.castleblitz.scenes.SinglePlayerGameScene;
import com.jaredzhao.castleblitz.servers.GameServer;
import com.jaredzhao.castleblitz.utils.LayerSorter;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;

import java.util.ArrayList;
import java.util.Collections;

public class InputEntitySystem extends DisposableEntitySystem implements InputProcessor {

    private ImmutableArray<Entity> selectables;

    private PreferencesAccessor preferencesAccessor;

    private boolean beingDragged, beingTapped;
    private int lastTouchX, lastTouchY, lastDragX, lastDragY, selectedX, selectedY;

    private EntityFactory entityFactory;
    private OrthographicCamera orthographicCamera;
    private Entity camera;
    private SettingsComponent settingsComponent;
    private BattleMechanicsStatesComponent battleMechanicsStatesComponent;

    private GameServer gameServer;

    private float scale;

    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CameraComponent> cameraComponentComponentMapper = ComponentMapper.getFor(CameraComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);

    private LayerSorter layerSorter;

    public InputEntitySystem(GameServer gameServer, PreferencesAccessor preferencesAccessor, EntityFactory entityFactory, Entity camera, Entity settings, Entity battleMechanics, int mapHeight){
        Gdx.input.setInputProcessor(this);
        this.gameServer = gameServer;
        this.preferencesAccessor = preferencesAccessor;
        this.orthographicCamera = camera.getComponent(CameraComponent.class).camera;
        this.scale = camera.getComponent(CameraComponent.class).scale;
        this.entityFactory = entityFactory;
        this.camera = camera;
        this.settingsComponent = settings.getComponent(SettingsComponent.class);
        this.battleMechanicsStatesComponent = battleMechanics.getComponent(BattleMechanicsStatesComponent.class);
        layerSorter = new LayerSorter(mapHeight);
    }

    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine){
        selectables = engine.getEntitiesFor(Family.all(SelectableComponent.class, PositionComponent.class, LayerComponent.class, VisibleComponent.class).get());
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
                entity.getComponent(HasSoundEffectComponent.class).soundLength = .038f;

                removeMoveAndAttackButtons();

                battleMechanicsStatesComponent.characterSelected = false;

                battleMechanicsStatesComponent.attack = false;
                battleMechanicsStatesComponent.move = false;
            }

            if (selectableComponent.addSelection) {
                selectableComponent.isSelected = true;
                selectableComponent.addSelection = false;
                entity.add(new HasSoundEffectComponent());
                entity.getComponent(HasSoundEffectComponent.class).soundName = "audio/sfx/blop.wav";
                entity.getComponent(HasSoundEffectComponent.class).continuous = false;
                entity.getComponent(HasSoundEffectComponent.class).dynamicVolume = false;
                entity.getComponent(HasSoundEffectComponent.class).soundLength = .038f;

                getEngine().addEntity(entityFactory.createDynamicPositionUI("move", positionComponent.x - 10, positionComponent.y + 18, 16, 16));
                getEngine().addEntity(entityFactory.createDynamicPositionUI("attack", positionComponent.x + 10, positionComponent.y + 18, 16, 16));

                battleMechanicsStatesComponent.characterSelected = true;
            }

            if(battleMechanicsStatesComponent.move || battleMechanicsStatesComponent.attack){
                removeMoveAndAttackButtons();
            }
        }
    }

    public void removeMoveAndAttackButtons(){
        for(int i = 0; i < selectables.size(); i++){
            Entity entity1 = selectables.get(i);
            SelectableComponent selectableComponent1 = selectableComponentComponentMapper.get(entity1);
            if(selectableComponent1.name.equals("move") || selectableComponent1.name.equals("attack")){
                getEngine().removeEntity(entity1);
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
        if(character == '\b'){
            if(settingsComponent.editUsername && settingsComponent.username.length() != 0){
                settingsComponent.username = settingsComponent.username.substring(0, settingsComponent.username.length() - 1);
            } else if(settingsComponent.editPassword && settingsComponent.password.length() != 0){
                settingsComponent.password = settingsComponent.password.substring(0, settingsComponent.password.length() - 1);
            } else if(settingsComponent.editConfirmPassword && settingsComponent.confirmPassword.length() != 0){
                settingsComponent.confirmPassword = settingsComponent.confirmPassword.substring(0, settingsComponent.confirmPassword.length() - 1);
            }
        } else if(character >= 32 && character <= 126){
            if (settingsComponent.editUsername && settingsComponent.username.length() < 17 &&
                    ((character >= 48 && character <= 57)
                    || (character >= 65 && character <= 90)
                    || (character >= 97 && character <= 122))) {
                settingsComponent.username = settingsComponent.username + character;
            } else if (settingsComponent.editPassword && settingsComponent.password.length() < 15) {
                settingsComponent.password = settingsComponent.password + character;
            } else if (settingsComponent.editConfirmPassword && settingsComponent.confirmPassword.length() < 15) {
                settingsComponent.confirmPassword = settingsComponent.confirmPassword + character;
            }
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        beingTapped = true;
        lastTouchX = screenX;
        lastTouchY = screenY;
        lastDragX = screenX;
        lastDragY = screenY;

        ArrayList<Entity> sortedSelectables = new ArrayList<Entity>(layerSorter.sortByLayers(selectables).values());
        Collections.reverse(sortedSelectables);

        boolean nothingSelectedYet = true;

        selectedX = (int) Math.floor((screenX / scale) + orthographicCamera.position.x - orthographicCamera.viewportWidth);
        selectedY = (int) Math.floor(((-1) * screenY / scale) + orthographicCamera.position.y);

        for (Entity entity : sortedSelectables){
            PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

            if (selectedX > positionComponent.x - selectableComponent.sizeX / 2 + selectableComponent.centerOffsetX &&
                    selectedX < positionComponent.x + selectableComponent.sizeX / 2 + selectableComponent.centerOffsetX &&
                    selectedY > positionComponent.y - selectableComponent.sizeY / 2 + selectableComponent.centerOffsetY &&
                    selectedY < positionComponent.y + selectableComponent.sizeY / 2 + selectableComponent.centerOffsetY &&
                    nothingSelectedYet) {

                if (selectableComponent.name.equals("pause")
                        || selectableComponent.name.equals("fastforward")
                        || selectableComponent.name.equals("debug")
                        || selectableComponent.name.equals("facebookLogin")
                        || selectableComponent.name.equals("sound")
                        || selectableComponent.name.equals("sfx")
                        || selectableComponent.name.equals("homeCastle")
                        || selectableComponent.name.equals("homePotions")
                        || selectableComponent.name.equals("homeShop")
                        || selectableComponent.name.equals("homeArmory")
                        || selectableComponent.name.equals("homeRanking")
                        || selectableComponent.name.equals("battle")
                        || selectableComponent.name.equals("editUsername")
                        || selectableComponent.name.equals("editPassword")
                        || selectableComponent.name.equals("editConfirmPassword")
                        || selectableComponent.name.equals("signUp")
                        || selectableComponent.name.equals("login")
                        || selectableComponent.name.equals("home")
                        || selectableComponent.name.equals("back")) {
                    selectableComponent.touchDown = true;
                    nothingSelectedYet = false;
                }

                if(!settingsComponent.isPaused && (
                        selectableComponent.name.equals("move")
                        || selectableComponent.name.equals("attack")
                        )){
                    selectableComponent.touchDown = true;
                    nothingSelectedYet = false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { //User input for selecting characters
        for (Entity entity : selectables){
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

            selectableComponent.touchDown = false;
        }
        if(beingTapped && !beingDragged) {

            ArrayList<Entity> sortedSelectables = new ArrayList<Entity>(layerSorter.sortByLayers(selectables).values());
            Collections.reverse(sortedSelectables);

            boolean nothingSelectedYet = true;

            selectedX = (int) Math.floor((screenX / scale) + orthographicCamera.position.x - orthographicCamera.viewportWidth);
            selectedY = (int) Math.floor(((-1) * screenY / scale) + orthographicCamera.position.y);

            for (Entity entity : sortedSelectables){
                PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
                SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
                CharacterPropertiesComponent characterPropertiesComponent = characterPropertiesComponentComponentMapper.get(entity);

                selectableComponent.touchDown = false;

                if (selectedX > positionComponent.x - selectableComponent.sizeX / 2 + selectableComponent.centerOffsetX &&
                        selectedX < positionComponent.x + selectableComponent.sizeX / 2 + selectableComponent.centerOffsetX &&
                        selectedY > positionComponent.y - selectableComponent.sizeY / 2 + selectableComponent.centerOffsetY &&
                        selectedY < positionComponent.y + selectableComponent.sizeY / 2 + selectableComponent.centerOffsetY &&
                        nothingSelectedYet) {

                    if (selectableComponent.name.equals("pause")) {
                        settingsComponent.isPaused = !settingsComponent.isPaused;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("home")) {
                        settingsComponent.goHome = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("fastforward")) {
                        settingsComponent.fastForward = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("sound")) {
                        settingsComponent.soundOn = !settingsComponent.soundOn;
                        preferencesAccessor.putBoolean("soundOn", settingsComponent.soundOn);
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("sfx")) {
                        settingsComponent.sfxOn = !settingsComponent.sfxOn;
                        preferencesAccessor.putBoolean("sfxOn", settingsComponent.sfxOn);
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("debug")) {
                        settingsComponent.debug = !settingsComponent.debug;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("battle")) {
                        settingsComponent.battle = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("editUsername")) {
                        settingsComponent.editUsername = true;
                        settingsComponent.editPassword = false;
                        settingsComponent.editConfirmPassword = false;
                        settingsComponent.username = "";
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("editPassword")) {
                        settingsComponent.editUsername = false;
                        settingsComponent.editPassword = true;
                        settingsComponent.editConfirmPassword = false;
                        settingsComponent.password = "";
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("editConfirmPassword")) {
                        settingsComponent.editUsername = false;
                        settingsComponent.editPassword = false;
                        settingsComponent.editConfirmPassword = true;
                        settingsComponent.confirmPassword = "";
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("signUp")) {
                        settingsComponent.signUp = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("login")) {
                        settingsComponent.login = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("back")) {
                        settingsComponent.back = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("facebookLogin")) {
                        settingsComponent.facebookLogin = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("homeCastle")
                            || selectableComponent.name.equals("homePotions")
                            || selectableComponent.name.equals("homeShop")
                            || selectableComponent.name.equals("homeArmory")
                            || selectableComponent.name.equals("homeRanking")) {
                        settingsComponent.homeScreen = selectableComponent.name;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("character")
                            && battleMechanicsStatesComponent.isMyTurn
                            && characterPropertiesComponent.team.equals(SinglePlayerGameScene.team)
                            && !settingsComponent.isPaused) {
                        if(!selectableComponent.isSelected) {
                            if(!battleMechanicsStatesComponent.move && !battleMechanicsStatesComponent.attack && !battleMechanicsStatesComponent.characterSelected) {
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

                    if (selectableComponent.name.equals("tile")
                            && battleMechanicsStatesComponent.isMyTurn
                            && !settingsComponent.isPaused) {
                        selectableComponent.isSelected = true;
                        battleMechanicsStatesComponent.move = false;
                        nothingSelectedYet = true;
                    }

                    if (selectableComponent.name.equals("move")
                            && battleMechanicsStatesComponent.isMyTurn
                            && !settingsComponent.isPaused) {
                        battleMechanicsStatesComponent.move = true;
                        nothingSelectedYet = false;
                    }

                    if (selectableComponent.name.equals("attack")
                            && battleMechanicsStatesComponent.isMyTurn
                            && !settingsComponent.isPaused) {
                        battleMechanicsStatesComponent.attack = true;
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

                    if (selectableComponent.name.equals("editUsername")) {
                        settingsComponent.editUsername = false;
                        selectableComponent.removeSelection = true;
                    }

                    if (selectableComponent.name.equals("editPassword")) {
                        settingsComponent.editPassword = false;
                        selectableComponent.removeSelection = true;
                    }

                    if (selectableComponent.name.equals("editConfirmPassword")) {
                        settingsComponent.editConfirmPassword = false;
                        selectableComponent.removeSelection = true;
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
        if(Math.abs(screenX - lastTouchX) / scale > 2 || Math.abs(screenY - lastTouchY) / scale > 2){
            beingDragged = true;
        }
        if(!settingsComponent.isPaused && GameEngine.currentScene == GameEngine.singlePlayerGameScene.IDENTIFIER) {
            PositionComponent positionComponent = positionComponentComponentMapper.get(camera);
            CameraComponent cameraComponent = cameraComponentComponentMapper.get(camera);
            positionComponent.x -= ((screenX - lastDragX) / cameraComponent.scale);
            positionComponent.y += ((screenY - lastDragY) / cameraComponent.scale);
            lastDragX = screenX;
            lastDragY = screenY;
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

    @Override
    public void dispose() {

    }
}
