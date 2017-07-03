package com.jaredzhao.castleblitz.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.jaredzhao.castleblitz.GameEngine;

public class OpeningScene extends Scene {

    private SpriteBatch batch;
    private int timesRendered;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;
    private GlyphLayout layout;

    public OpeningScene(){
        IDENTIFIER = 0;
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
    }

    @Override
    public int render() throws InterruptedException {
        Gdx.gl.glClearColor(.1f, .1f, .2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();

        layout.setText(font, "Castle Blitz - " + GameEngine.version);
        font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 2 + 2 * layout.height);

        layout.setText(font, "by Jared Zhao");
        font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 2);

        batch.end();

        int nextScene;
        timesRendered++;
        if(timesRendered > 150){
            nextScene = 1;
            this.dispose();
            this.isRunning = false;
        } else {
            nextScene = IDENTIFIER;
        }
        return nextScene;
    }

    public void dispose(){
        batch.dispose();
    }
}
