package com.jaredzhao.castleblitz.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TextComponent implements Component {

    public BitmapFont bitmapFont;
    public GlyphLayout glyphLayout;
    public FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter;
    public FreeTypeFontGenerator freeTypeFontGenerator;
    public String text;
    public boolean centered;

    public void setText(String text) {
        this.text = text;
        glyphLayout.setText(bitmapFont, text);
    }

    public void setColor(Color color) {
        freeTypeFontParameter.color = color;
        bitmapFont.dispose();
        bitmapFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        glyphLayout.setText(bitmapFont, text);
    }

}
