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
		new LwjglApplication(new GameEngine(), config);
	}
}