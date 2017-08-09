package com.jaredzhao.castleblitz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.jaredzhao.castleblitz.scenes.*;
import com.jaredzhao.castleblitz.utils.FacebookAccessor;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;
import com.jaredzhao.castleblitz.utils.SocketAccessor;

import java.util.ArrayList;

public class GameEngine extends ApplicationAdapter {

	private ArrayList<Scene> sceneList; //ArrayList containing all scenes

	public static int currentScene; //Current scene number

	public static String version = "Build 36"; //Current build version

	public static float lifetime;

	public FacebookAccessor facebookAccessor;
	public PreferencesAccessor preferencesAccessor;
	//public SocketAccessor socketAccessor;


	public GameEngine(){
		facebookAccessor = new FacebookAccessor();
		preferencesAccessor = new PreferencesAccessor();
		//socketAccessor = new SocketAccessor();
	}

	@Override
	public void create () { //Called once when the game is started
		lifetime = 0;

		facebookAccessor.init();
		preferencesAccessor.init();
		//socketAccessor.init();

		sceneList = new ArrayList<Scene>();

		Scene gameScene = new SinglePlayerGameScene(preferencesAccessor); //Create new SinglePlayerGameScene
		Scene openingScene = new OpeningScene();
		Scene loginScene = new LoginScene(facebookAccessor, preferencesAccessor);
		Scene homeScene = new HomeScene(preferencesAccessor);

		sceneList.add(openingScene);
		sceneList.add(loginScene);
		sceneList.add(homeScene);
		sceneList.add(gameScene);

		currentScene = homeScene.IDENTIFIER; //Current scene is openingScene
	}

	@Override
	public void render () {
		//socketAccessor.update();

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
}
