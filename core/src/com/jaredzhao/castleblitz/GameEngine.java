package com.jaredzhao.castleblitz;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jaredzhao.castleblitz.scenes.*;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;
import com.jaredzhao.castleblitz.utils.SocketAccessor;

import java.util.ArrayList;

/**
 * Abstract GameEngine used by all platforms
 */
public class GameEngine extends ApplicationAdapter {

    private ArrayList<Scene> sceneList; //ArrayList containing all scenes

    public static int currentScene; //Current scene number
    public static boolean loggedInToServer = false;

    public static Scene singlePlayerGameScene;
    public static Scene openingScene;
    public static Scene homeScene;
    public static Scene signUpOrLoginScene;

    public static Vector2 safeAreaInsets;

    public static String version = "Build 60"; //Current build version
    public static int tileSize = 256;
    public static float lifetime;
    public static String platform;

    public PreferencesAccessor preferencesAccessor;
    public SocketAccessor socketAccessor;

    /**
     * Creates the game object and initializes Accessors
     */
    public GameEngine(String platform) {
        preferencesAccessor = new PreferencesAccessor();
        socketAccessor = new SocketAccessor("jaredzhao.com");
        this.platform = platform;
    }

    /**
     * Starts the game and initializes scenes
     */
    @Override
    public void create() { //Called once when the game is started
        lifetime = 0;

        Gdx.graphics.setResizable(false);

        preferencesAccessor.init();
        socketAccessor.init();

        sceneList = new ArrayList<Scene>();

        initiateScenes();

        currentScene = openingScene.IDENTIFIER; //Current scene is openingScene

        safeAreaInsets = getIOSSafeAreaInsets();
        //safeAreaInsets = new Vector2(Gdx.graphics.getWidth(), 32);
    }

    public void initiateScenes() {
        singlePlayerGameScene = new SinglePlayerGameScene(preferencesAccessor); //Create new SinglePlayerGameScene
        openingScene = new OpeningScene(preferencesAccessor, socketAccessor);
        homeScene = new HomeScene(preferencesAccessor, socketAccessor);
        signUpOrLoginScene = new SignUpOrLoginScene(preferencesAccessor, socketAccessor);

        sceneList.add(openingScene); //IDENTIFIER = 0
        sceneList.add(signUpOrLoginScene); //IDENTIFIER = 1
        sceneList.add(homeScene); //IDENTIFIER = 2
        sceneList.add(singlePlayerGameScene); //IDENTIFIER = 3
    }

    /**
     * Calls render in appropriate scenes
     * Switches between scenes when necessary
     */
    @Override
    public void render() {
        socketAccessor.update();

        if (!loggedInToServer && currentScene != openingScene.IDENTIFIER && currentScene != signUpOrLoginScene.IDENTIFIER) {
            currentScene = openingScene.IDENTIFIER;
        }

        for (Scene scene : sceneList) {
            if (currentScene == scene.IDENTIFIER) {
                if (!scene.isRunning) {
                    scene.init();
                    scene.isRunning = true;
                }
                try {
                    currentScene = scene.render();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (scene.isRunning) {
                scene.dispose();
                scene.isRunning = false;
            }
        }

        lifetime += Gdx.graphics.getDeltaTime();
    }

    public static Vector2 getIOSSafeAreaInsets() {
        if (Gdx.app.getType() == Application.ApplicationType.iOS) {
            try {
                Class<?> IOSLauncher = Class.forName("com.jaredzhao.castleblitz.IOSLauncher");
                return (Vector2) IOSLauncher.getDeclaredMethod("getSafeAreaInsets").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Vector2();
    }
}
