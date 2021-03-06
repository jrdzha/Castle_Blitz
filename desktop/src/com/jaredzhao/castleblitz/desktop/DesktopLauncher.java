package com.jaredzhao.castleblitz.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jaredzhao.castleblitz.GameEngine;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = true;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;

		config.title = "Castle Blitz - " + GameEngine.version;

		String version = "iPhone X";
		String platform = "";

		if(version.equals("iPhone X")) {
			config.width = 1125 / 3;
			config.height = 2436 / 3;
			platform = "mobile";
			//gameEngine.safeAreaInsets = new Vector2(Gdx.graphics.getWidth(), 32);
		} else if(version.equals("iPhone 8")){
			config.width = 750 / 2;
			config.height = 1334 / 2;
			platform = "mobile";
		} else if(version.equals("iPhone 8 Plus")){
			config.width = 1080 / 2;
			config.height = 1920 / 2;
			platform = "mobile";
		} else if(version.equals("iPhone 5")){
			config.width = 640;
			config.height = 1136;
			platform = "mobile";
		} else if(version.equals("iPhone 4")){
			config.width = 640;
			config.height = 960;
			platform = "mobile";
		} else if(version.equals("Google Pixel")){
			config.width = 1080 / 2;
			config.height = 1920 / 2;
			platform = "mobile";
		} else if(version.equals("Google Pixel XL")){
			config.width = 1440 / 3;
			config.height = 2560 / 3;
			platform = "mobile";
		} else if(version.equals("Samsung Galaxy S8")){
			config.width = 1440 / 3;
			config.height = 2960 / 3;
			platform = "mobile";
		} else if(version.equals("Desktop")){
			config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
			config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
			config.fullscreen = true;
			platform = "desktop";
		} else if(version.equals("Desktop 1080")){
			config.width = 1920;
			config.height = 1080;
			config.fullscreen = true;
			platform = "desktop";
		} else if(version.equals("Desktop 1440")){
			config.width = 2560;
			config.height = 1440;
			config.fullscreen = true;
			platform = "desktop";
		}

		GameEngine gameEngine = new GameEngine(platform);
		new LwjglApplication(gameEngine, config);
	}
}