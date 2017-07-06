package com.jaredzhao.castleblitz;

import com.badlogic.gdx.ApplicationAdapter;
import com.jaredzhao.castleblitz.scenes.GameScene;
import com.jaredzhao.castleblitz.scenes.OpeningScene;
import com.jaredzhao.castleblitz.scenes.Scene;

import java.util.ArrayList;

public class GameEngine extends ApplicationAdapter {

	private ArrayList<Scene> sceneList; //ArrayList containing all scenes

	private int currentScene; //Current scene number

	public static String version = "Build 29"; //Current build version


	public GameEngine(){

	}

	@Override
	public void create () { //Called once when the game is started
		sceneList = new ArrayList<Scene>();
		Scene gameScene = new GameScene(); //Create new GameScene
		Scene openingScene = new OpeningScene();
		currentScene = openingScene.IDENTIFIER; //Current scene is gameScene
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
