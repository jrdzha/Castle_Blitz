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

	public static Vector2 safeAreaInsets;

	public static String version = "Build 51"; //Current build version

	public static float lifetime;

	//public FacebookAccessor facebookAccessor;
	public PreferencesAccessor preferencesAccessor;
	public SocketAccessor socketAccessor;

	/**
	 * Creates the game object and initializes Accessors
	 */
	public GameEngine(){
		//facebookAccessor = new FacebookAccessor();
		preferencesAccessor = new PreferencesAccessor();
		socketAccessor = new SocketAccessor("localhost");
	}

	/**
	 * Starts the game and initializes scenes
	 */
	@Override
	public void create () { //Called once when the game is started
		lifetime = 0;

		Gdx.graphics.setResizable(false);

		//facebookAccessor.init();
		preferencesAccessor.init();
		socketAccessor.init();

		sceneList = new ArrayList<Scene>();

		Scene gameScene = new SinglePlayerGameScene(preferencesAccessor); //Create new SinglePlayerGameScene
		Scene openingScene = new OpeningScene(preferencesAccessor, socketAccessor);
		//Scene loginScene = new LoginScene(preferencesAccessor);
		Scene homeScene = new HomeScene(preferencesAccessor);
		Scene signUpScene = new SignUpLoginScene(preferencesAccessor, socketAccessor);

		sceneList.add(openingScene);
		//sceneList.add(loginScene);
		sceneList.add(homeScene);
		sceneList.add(gameScene);
		sceneList.add(signUpScene);

		currentScene = openingScene.IDENTIFIER; //Current scene is openingScene

		safeAreaInsets = getIOSSafeAreaInsets();
	}

	/**
	 * Calls render in appropriate scenes
	 * Switches between scenes when necessary
	 */
	@Override
	public void render () {
		if((int)lifetime % 1 == 0) {
			socketAccessor.update();
		}

		for(Scene scene : sceneList){
			if(currentScene == scene.IDENTIFIER){
				if(!scene.isRunning){
					scene.init();
					scene.isRunning = true;
				}
				try {
					currentScene = scene.render();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
