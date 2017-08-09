package com.jaredzhao.castleblitz.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jaredzhao.castleblitz.GameEngine;

public class MapEditorLauncher {


    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        GameEngine engine = new GameEngine();

        config.title = "Castle Blitz - Map Editor - " + GameEngine.version;
        config.useGL30 = false;
        config.width = 1920;
        config.height = 1080;
        config.fullscreen = false;

        new LwjglApplication(engine, config);
    }
}