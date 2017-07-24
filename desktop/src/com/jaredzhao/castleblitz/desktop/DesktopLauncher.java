package com.jaredzhao.castleblitz.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.jaredzhao.castleblitz.GameEngine;

public class DesktopLauncher{


	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		GameEngine engine = new GameEngine(new DesktopFirebaseAccessor(), new DesktopFacebookAccessor());

		config.title = "Castle Blitz - " + GameEngine.version;
		config.useGL30 = false;
		config.width = 750 / 2;
		config.height = 1334 / 2;
		config.fullscreen = false;

		new LwjglApplication(engine, config);
	}
}