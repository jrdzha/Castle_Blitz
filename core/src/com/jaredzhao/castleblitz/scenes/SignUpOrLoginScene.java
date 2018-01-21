package com.jaredzhao.castleblitz.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.graphics.TextComponent;
import com.jaredzhao.castleblitz.components.graphics.VisibleComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.mechanics.StaticScreenPositionComponent;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class SignUpOrLoginScene extends Scene {

    private Engine ashleyEngine; //Engine controlling the Entity-Component DisposableEntitySystem (ECS)

    private float cameraScale;

    private EntityFactory entityFactory; //Entity factory used for creating all entities
    private AudioFactory audioFactory; //Audio factory for loading audio files
    private AnimationFactory animationFactory; //Animation factory for generating animations
    private MapFactory mapFactory; //Create map entity and load level data

    private Entity camera; //Camera for viewport
    private Entity map; //Map entity for easy access here *** Can probably be removed later on

    private Entity loginButton;
    private Entity signUpButton;
    private Entity backButton;
    private Entity editUsernameButton;
    private Entity editPasswordButton;
    private Entity editConfirmPasswordButton;

    private Entity usernameText;
    private Entity passwordText;
    private Entity confirmPasswordText;
    private Entity signUpLoginErrorText;

    private SettingsComponent settingsComponent;

    private HashMap<String, DisposableEntitySystem> systems = new HashMap<String, DisposableEntitySystem>();

    private PreferencesAccessor preferencesAccessor;
    private SocketAccessor socketAccessor;

    private GameServer characterSelectionServer;
    public static String team;

    MessageDigest digest;

    public SignUpOrLoginScene(PreferencesAccessor preferencesAccessor, SocketAccessor socketAccessor){
        IDENTIFIER = 1;
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

        camera = entityFactory.createCamera(115);

        Entity fogOfWar = entityFactory.createFogOfWar(0, 0, 0, .3f, rawMap[0].length, rawMap[0][0].length);
        Entity settings = entityFactory.createSettings();
        Entity battleMechanics = entityFactory.createBattleMechanics();
        settingsComponent = settings.getComponent(SettingsComponent.class);
        camera.getComponent(PositionComponent.class).x = 8 * rawMap[0].length - 8;
        camera.getComponent(PositionComponent.class).y = 8 * rawMap[0][0].length - 8;
        cameraScale = camera.getComponent(CameraComponent.class).scale;

        ashleyEngine.addEntity(camera);
        ashleyEngine.addEntity(map);
        ashleyEngine.addEntity(settings);

        editUsernameButton = entityFactory.createStaticPositionUI("editUsername", true, -camera.getComponent(CameraComponent.class).cameraWidth / 2 + 18, 73, 16, 16);
        editPasswordButton = entityFactory.createStaticPositionUI("editPassword", true, -camera.getComponent(CameraComponent.class).cameraWidth / 2 + 18, 53, 16, 16);
        editConfirmPasswordButton = entityFactory.createStaticPositionUI("editConfirmPassword", true, -camera.getComponent(CameraComponent.class).cameraWidth / 2 + 18, 33, 16, 16);
        loginButton = entityFactory.createStaticPositionUI("login", true, 0, -70, 80, 16);
        signUpButton = entityFactory.createStaticPositionUI("signUp", true, 0, -90, 80, 16);
        backButton = entityFactory.createStaticPositionUI("back", true, 0, -70, 80, 16);

        int textScale = 3;
        Entity testText = entityFactory.createText("***************", 0, 0, Color.WHITE, textScale, false);
        while(testText.getComponent(TextComponent.class).glyphLayout.width / Gdx.graphics.getWidth() < 0.65f){
            textScale++;
            testText.getComponent(TextComponent.class).freeTypeFontParameter.size = textScale;
            testText.getComponent(TextComponent.class).bitmapFont = testText.getComponent(TextComponent.class).freeTypeFontGenerator.generateFont(testText.getComponent(TextComponent.class).freeTypeFontParameter);
            testText.getComponent(TextComponent.class).glyphLayout.setText(testText.getComponent(TextComponent.class).bitmapFont, "***************");
        }

        settingsComponent.username = "";
        settingsComponent.password = "";
        settingsComponent.confirmPassword = "";
        settingsComponent.signUpLoginError = "";

        usernameText = entityFactory.createText("", -Gdx.graphics.getWidth() / 2 + 32 * cameraScale, 75 * cameraScale, new Color(1, 1, 1, 0.5f), textScale, false);
        passwordText = entityFactory.createText("", -Gdx.graphics.getWidth() / 2 + 32 * cameraScale, 55 * cameraScale, new Color(1, 1, 1, 0.5f), textScale, false);
        confirmPasswordText = entityFactory.createText("", -Gdx.graphics.getWidth() / 2 + 32 * cameraScale, 35 * cameraScale, new Color(1, 1, 1, 0.5f), textScale, false);
        signUpLoginErrorText = entityFactory.createText("", 0, 15 * cameraScale, Color.WHITE, textScale, true);

        setVisible(usernameText, false);
        setVisible(passwordText, false);
        setVisible(confirmPasswordText, false);

        ashleyEngine.addEntity(loginButton);
        ashleyEngine.addEntity(signUpButton);
        ashleyEngine.addEntity(backButton);
        ashleyEngine.addEntity(editPasswordButton);
        ashleyEngine.addEntity(editConfirmPasswordButton);
        ashleyEngine.addEntity(editUsernameButton);
        ashleyEngine.addEntity(usernameText);
        ashleyEngine.addEntity(passwordText);
        ashleyEngine.addEntity(confirmPasswordText);
        ashleyEngine.addEntity(signUpLoginErrorText);

        ashleyEngine.addEntity(entityFactory.createMusic(mapFactory.loadAvailableTracks(Gdx.files.internal("levels/test2.lvl"))));

        //Load settings
        boolean[] localSettings = preferencesAccessor.loadLocalSettings();
        settingsComponent.soundOn = localSettings[0];
        settingsComponent.sfxOn = localSettings[1];

        int mapHeight = map.getComponent(MapComponent.class).mapEntities[0][0].length;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Initialize systems
        systems.put("CameraEntitySystem", new CameraEntitySystem(map));
        systems.put("RenderEntitySystem", new RenderEntitySystem(ashleyEngine, camera, settings, fogOfWar, mapHeight, 1.6f, .12f));
        systems.put("MapEntitySystem", new MapEntitySystem(characterSelectionServer, map, fogOfWar, battleMechanics));
        systems.put("InputEntitySystem", new InputEntitySystem(ashleyEngine, characterSelectionServer, preferencesAccessor, entityFactory, camera, settings, battleMechanics, mapHeight));
        systems.put("ResourceManagementEntitySystem", new ResourceManagementEntitySystem(ashleyEngine));
        systems.put("LightEntitySystem", new LightEntitySystem());
        systems.put("AudioEntitySystem", new AudioEntitySystem(entityFactory, audioFactory, camera, settings));
        systems.put("HighlightEntitySystem", new HighlightEntitySystem(ashleyEngine));
        systems.put("AnimationManagerEntitySystem", new AnimationManagerEntitySystem(settings));
        systems.put("BattleMechanicsEntitySystem", new BattleMechanicsEntitySystem(map, characterSelectionServer, battleMechanics));
        ((RenderEntitySystem)systems.get("RenderEntitySystem")).renderGaussianBlur = true;

        //Add systems to ashleyEngine
        for(HashMap.Entry<String, DisposableEntitySystem> entry : systems.entrySet()) {
            ashleyEngine.addSystem(entry.getValue());
        }
        System.gc();
    }

    @Override
    public int render() throws InterruptedException {

        if (settingsComponent.editPassword || settingsComponent.editUsername || settingsComponent.editConfirmPassword) {
            Gdx.input.setOnscreenKeyboardVisible(true);
        } else {
            Gdx.input.setOnscreenKeyboardVisible(false);
        }

        signUpLoginErrorText.getComponent(TextComponent.class).setText(settingsComponent.signUpLoginError);

        if(settingsComponent.username.equals("") && !settingsComponent.editUsername){
            usernameText.getComponent(TextComponent.class).setText("Username");
            usernameText.getComponent(TextComponent.class).setColor(new Color(1, 1, 1, 0.5f));
        } else {
            if(settingsComponent.editUsername && (int)(GameEngine.lifetime * 10) % 10 < 5){
                usernameText.getComponent(TextComponent.class).setText(settingsComponent.username + "|");
            } else {
                usernameText.getComponent(TextComponent.class).setText(settingsComponent.username);
            }
            usernameText.getComponent(TextComponent.class).setColor(Color.WHITE);
        }

        if(settingsComponent.password.equals("") && !settingsComponent.editPassword){
            passwordText.getComponent(TextComponent.class).setText("Password");
            passwordText.getComponent(TextComponent.class).setColor(new Color(1, 1, 1, 0.5f));
        } else {
            char[] chars = new char[settingsComponent.password.length()];
            Arrays.fill(chars, '*');
            String text = new String(chars);
            if(settingsComponent.editPassword && (int)(GameEngine.lifetime * 10) % 10 < 5) {
                passwordText.getComponent(TextComponent.class).setText(text + "|");
            } else {
                passwordText.getComponent(TextComponent.class).setText(text);
            }
            passwordText.getComponent(TextComponent.class).setColor(Color.WHITE);
        }

        if(settingsComponent.confirmPassword.equals("") && !settingsComponent.editConfirmPassword){
            confirmPasswordText.getComponent(TextComponent.class).setText("Confirm");
            confirmPasswordText.getComponent(TextComponent.class).setColor(new Color(1, 1, 1, 0.5f));
        } else {
            char[] chars = new char[settingsComponent.confirmPassword.length()];
            Arrays.fill(chars, '*');
            String text = new String(chars);
            if(settingsComponent.editConfirmPassword && (int)(GameEngine.lifetime * 10) % 10 < 5){
                confirmPasswordText.getComponent(TextComponent.class).setText(text + "|");
            } else {
                confirmPasswordText.getComponent(TextComponent.class).setText(text);
            }
            confirmPasswordText.getComponent(TextComponent.class).setColor(Color.WHITE);
        }

        if (socketAccessor.inputQueue.size() != 0) {
            if (socketAccessor.inputQueue.get(0).equals("REGISTER.OK")) {
                String[] userData = preferencesAccessor.loadUserData();
                socketAccessor.outputQueue.add("LOGIN." + userData[0] + "." + userData[1]);
                socketAccessor.inputQueue.remove(0);
            } else if (socketAccessor.inputQueue.get(0).equals("LOGIN.OK")) {
                GameEngine.loggedInToServer = true;
                socketAccessor.inputQueue.remove(0);
            } else if (socketAccessor.inputQueue.get(0).equals("USERNAME.EXISTS")) {
                settingsComponent.signUpLoginError = "Username Exists Already";
                socketAccessor.inputQueue.remove(0);
            } else if (socketAccessor.inputQueue.get(0).equals("LOGIN.OK")) {
                socketAccessor.inputQueue.remove(0);
            } else if (socketAccessor.inputQueue.get(0).equals("LOGIN.FAIL")) {
                settingsComponent.signUpLoginError = "Incorrect Login";
                socketAccessor.inputQueue.remove(0);
            }
        }

        if(settingsComponent.accountScreen.equals("signUpOrLogin")){
            setVisible(loginButton, true);
            setVisible(signUpButton, true);
            setVisible(backButton, false);
            setVisible(editUsernameButton, false);
            setVisible(editPasswordButton, false);
            setVisible(editConfirmPasswordButton, false);
            setVisible(usernameText, false);
            setVisible(passwordText, false);
            setVisible(confirmPasswordText, false);

            setYPosition(loginButton, -70);
            setYPosition(signUpButton, -90);

            if(settingsComponent.login){
                settingsComponent.login = false;
                settingsComponent.accountScreen = "login";
                settingsComponent.editUsername = false;
                settingsComponent.editPassword = false;
                settingsComponent.editConfirmPassword = false;
            } else if(settingsComponent.signUp){
                settingsComponent.signUp = false;
                settingsComponent.accountScreen = "signUp";
                settingsComponent.editUsername = false;
                settingsComponent.editPassword = false;
                settingsComponent.editConfirmPassword = false;
            }
        } else if(settingsComponent.accountScreen.equals("signUp")){
            setVisible(loginButton, false);
            setVisible(signUpButton, true);
            setVisible(backButton, true);
            setVisible(editUsernameButton, true);
            setVisible(editPasswordButton, true);
            setVisible(editConfirmPasswordButton, true);
            setVisible(usernameText, true);
            setVisible(passwordText, true);
            setVisible(confirmPasswordText, true);

            setYPosition(signUpButton, -70);
            setYPosition(backButton, -90);

            if(settingsComponent.signUp){
                settingsComponent.signUp = false;
                settingsComponent.editUsername = false;
                settingsComponent.editPassword = false;
                settingsComponent.editConfirmPassword = false;
                settingsComponent.signUpLoginError = "";

                if (settingsComponent.username.length() > 5) {
                    if (settingsComponent.password.length() > 7) {
                        if (!settingsComponent.password.contains(".")) {
                            if (settingsComponent.password.equals(settingsComponent.confirmPassword)) {
                                StringBuffer hashedPasswordBuffer = new StringBuffer();
                                for (Byte b : digest.digest(settingsComponent.password.getBytes())) {
                                    hashedPasswordBuffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
                                }
                                preferencesAccessor.putString("USERNAME", settingsComponent.username.toUpperCase());
                                preferencesAccessor.putString("PASSWORD", hashedPasswordBuffer.toString());
                                socketAccessor.outputQueue.add("REGISTER." + settingsComponent.username.toUpperCase() + "." + hashedPasswordBuffer.toString());
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
            } else if(settingsComponent.back){
                settingsComponent.back = false;
                settingsComponent.accountScreen = "signUpOrLogin";
                settingsComponent.editUsername = false;
                settingsComponent.editPassword = false;
                settingsComponent.editConfirmPassword = false;
            }
        } else if(settingsComponent.accountScreen.equals("login")){
            setVisible(loginButton, true);
            setVisible(signUpButton, false);
            setVisible(backButton, true);
            setVisible(editUsernameButton, true);
            setVisible(editPasswordButton, true);
            setVisible(editConfirmPasswordButton, false);
            setVisible(usernameText, true);
            setVisible(passwordText, true);
            setVisible(confirmPasswordText, false);

            setYPosition(loginButton, -70);
            setYPosition(backButton, -90);

            if(settingsComponent.login){
                settingsComponent.login = false;
                settingsComponent.editUsername = false;
                settingsComponent.editPassword = false;
                settingsComponent.editConfirmPassword = false;
                settingsComponent.signUpLoginError = "";
                if(settingsComponent.username.length() > 0) {
                    if(settingsComponent.password.length() > 0) {
                        StringBuffer hashedPasswordBuffer = new StringBuffer();
                        for (Byte b : digest.digest(settingsComponent.password.getBytes())) {
                            hashedPasswordBuffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
                        }
                        preferencesAccessor.putString("username", settingsComponent.username.toUpperCase());
                        preferencesAccessor.putString("password", hashedPasswordBuffer.toString());
                        socketAccessor.outputQueue.add("LOGIN." + settingsComponent.username.toUpperCase() + "." + hashedPasswordBuffer.toString());
                        settingsComponent.login = false;
                    } else {
                        settingsComponent.signUpLoginError = "Enter password";
                    }
                } else {
                    settingsComponent.signUpLoginError = "Enter username";
                }
            } else if(settingsComponent.back){
                settingsComponent.back = false;
                settingsComponent.accountScreen = "signUpOrLogin";
                settingsComponent.editUsername = false;
                settingsComponent.editPassword = false;
                settingsComponent.editConfirmPassword = false;
            }
        }

        ashleyEngine.update(Gdx.graphics.getDeltaTime());

        if(GameEngine.loggedInToServer){
            return GameEngine.homeScene.IDENTIFIER;
        }
        return IDENTIFIER;
    }

    public void setYPosition(Entity entity, float y){
        if(entity.getComponent(StaticScreenPositionComponent.class).y != y){
            entity.getComponent(StaticScreenPositionComponent.class).y = y;
        }
    }

    public void setVisible(Entity entity, boolean visible){
        if(visible){
            if(entity.getComponent(VisibleComponent.class) == null){
                entity.add(new VisibleComponent());
            }
        } else {
            if(entity.getComponent(VisibleComponent.class) != null){
                entity.remove(VisibleComponent.class);
            }
        }
    }

    @Override
    public void dispose() {
        for(HashMap.Entry<String, DisposableEntitySystem> entry : systems.entrySet()) {
            if(entry.getValue() != null) {
                entry.getValue().dispose();
            }
        }
    }
}
