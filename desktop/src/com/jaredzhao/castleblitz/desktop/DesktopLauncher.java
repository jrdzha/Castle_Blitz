package com.jaredzhao.castleblitz.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jaredzhao.castleblitz.GameEngine;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Castle Blitz - " + GameEngine.version;
		config.width = 750 / 2;
		config.height = 1334 / 2;

		config.width = 1920;
		config.height = 1080;
		config.fullscreen = true;
		config.vSyncEnabled = true;

		new LwjglApplication(new GameEngine(), config);
	}
}