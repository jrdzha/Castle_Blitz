package com.jaredzhao.castleblitz.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.utils.PreferencesAccessor;
import com.jaredzhao.castleblitz.utils.SocketAccessor;

public class OpeningScene extends Scene {

    private SpriteBatch batch;
    private int timesRendered;

    private int nextScene;
    private boolean loggedIn;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;
    private GlyphLayout layout;

    private PreferencesAccessor preferencesAccessor;
    private SocketAccessor socketAccessor;

    private String[] userData;

    public OpeningScene(PreferencesAccessor preferencesAccessor, SocketAccessor socketAccessor){
        IDENTIFIER = 0;
        loggedIn = false;
        this.preferencesAccessor = preferencesAccessor;
        this.socketAccessor = socketAccessor;
    }

    @Override
    public void init() {
        timesRendered = 0;

        batch = new SpriteBatch();
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/slkscrb.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.color = Color.WHITE;
        fontParameter.size = Gdx.graphics.getHeight() / 35;
        font = fontGenerator.generateFont(fontParameter);
        layout = new GlyphLayout();

        preferencesAccessor.putString("username", "%DOESNOTEXIST%");
        preferencesAccessor.putString("password", "%DOESNOTEXIST%");

        userData = preferencesAccessor.loadUserData();

        if(userData[0].equals("%DOESNOTEXIST%")){
            //send to account creation
            nextScene = GameEngine.signUpOrLoginScene.IDENTIFIER;
        } else {
            //login
            socketAccessor.outputQueue.add("login." + userData[0] + "." + userData[1]);
            nextScene = GameEngine.homeScene.IDENTIFIER;
        }
    }

    @Override
    public int render() throws InterruptedException {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //Background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();

        font.setColor(Color.WHITE);

        layout.setText(font, "Castle Blitz");
        font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 1.5f + 4 * layout.height);

        layout.setText(font, GameEngine.version);
        font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 1.5f + 2 * layout.height);

        layout.setText(font, "by Jared Zhao");
        font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 1.5f);

        font.setColor(Color.GREEN);

        layout.setText(font, "Connecting to");
        font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 6 + 2 * layout.height);

        layout.setText(font, socketAccessor.host);
        font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 6);

        batch.end();

        if(socketAccessor.inputQueue.size() != 0){
            if(socketAccessor.inputQueue.get(0).equals("login.successful")) {
                loggedIn = true;
                socketAccessor.inputQueue.remove(0);
            }
        }

        if(!loggedIn && timesRendered % 600 == 599){
            socketAccessor.outputQueue.add("login." + userData[0] + "." + userData[1]);
        }

        timesRendered++;
        if (timesRendered > 120) {
            if ((nextScene == GameEngine.homeScene.IDENTIFIER && loggedIn) || nextScene == GameEngine.signUpOrLoginScene.IDENTIFIER) {
                this.dispose();
                this.isRunning = false;
                return nextScene;
            }
        }
        return IDENTIFIER;
    }

    public void dispose(){
        batch.dispose();
        fontGenerator.dispose();
        font.dispose();
    }
}
