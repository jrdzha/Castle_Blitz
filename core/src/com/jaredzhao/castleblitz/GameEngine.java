package com.jaredzhao.castleblitz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.scenes.*;
import com.jaredzhao.castleblitz.utils.FacebookAccessor;
import com.jaredzhao.castleblitz.utils.FirebaseAccessor;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;

import java.util.ArrayList;

public class GameEngine extends ApplicationAdapter {

	private ArrayList<Scene> sceneList; //ArrayList containing all scenes

	public static int currentScene; //Current scene number

	public static String version = "Build 35"; //Current build version

	public FirebaseAccessor firebaseAccessor;
	public FacebookAccessor facebookAccessor;
	public PreferencesAccessor preferencesAccessor;


	public GameEngine(FirebaseAccessor firebaseAccessor){
		this.firebaseAccessor = firebaseAccessor;
		facebookAccessor = new FacebookAccessor();
		preferencesAccessor = new PreferencesAccessor();
	}

	@Override
	public void create () { //Called once when the game is started
		firebaseAccessor.init();
		facebookAccessor.init();
		preferencesAccessor.init();

		sceneList = new ArrayList<Scene>();

		Scene gameScene = new GameScene(preferencesAccessor); //Create new GameScene
		Scene openingScene = new OpeningScene();
		Scene loginScene = new LoginScene(firebaseAccessor, facebookAccessor, preferencesAccessor);
		Scene homeScene = new HomeScene(firebaseAccessor, facebookAccessor, preferencesAccessor);

		sceneList.add(openingScene);
		sceneList.add(loginScene);
		sceneList.add(homeScene);
		sceneList.add(gameScene);

		currentScene = homeScene.IDENTIFIER; //Current scene is openingScene
	}

	@Override
	public void render () {
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
	}
}
