package com.jaredzhao.castleblitz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.scenes.GameScene;
import com.jaredzhao.castleblitz.scenes.LoginScene;
import com.jaredzhao.castleblitz.scenes.OpeningScene;
import com.jaredzhao.castleblitz.scenes.Scene;
import com.jaredzhao.castleblitz.utils.FacebookAccessor;
import com.jaredzhao.castleblitz.utils.FirebaseAccessor;

import java.util.ArrayList;

public class GameEngine extends ApplicationAdapter {

	private ArrayList<Scene> sceneList; //ArrayList containing all scenes

	public static int currentScene; //Current scene number

	public static String version = "Build 32"; //Current build version

	public FirebaseAccessor firebaseAccessor;
	public FacebookAccessor facebookAccessor;


	public GameEngine(FirebaseAccessor firebaseAccessor){
		this.firebaseAccessor = firebaseAccessor;
		facebookAccessor = new FacebookAccessor();
	}

	@Override
	public void create () { //Called once when the game is started
		firebaseAccessor.init();
		facebookAccessor.init();
		sceneList = new ArrayList<Scene>();
		Scene gameScene = new GameScene(); //Create new GameScene
		Scene openingScene = new OpeningScene();
		Scene loginScene = new LoginScene(firebaseAccessor, facebookAccessor);
		currentScene = openingScene.IDENTIFIER; //Current scene is openingScene
		sceneList.add(loginScene);
		sceneList.add(openingScene);
		sceneList.add(gameScene); //Add gameScene to sceneList
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
