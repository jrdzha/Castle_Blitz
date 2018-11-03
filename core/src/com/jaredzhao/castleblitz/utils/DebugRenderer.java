package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class DebugRenderer {

    private SpriteBatch spriteBatch;
    private BitmapFont debugFont;
    private ShapeRenderer shapeRenderer;
    private ArrayList<Color> colors;
    private int xValues;

    public DebugRenderer(SpriteBatch spriteBatch, int xValues, ArrayList<Color> colors) {
        this.spriteBatch = spriteBatch;
        this.debugFont = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();
        this.colors = colors;
        this.xValues = xValues;
    }

    public void render(ArrayList<String> texts, List<double[]> profiler) {

        int y = 20;

        spriteBatch.begin();

        {
            if (texts != null) {
                for (String text : texts) {
                    debugFont.draw(spriteBatch, text, 10, Gdx.graphics.getHeight() - y);
                    y += 20;
                }
            }
        }

        spriteBatch.end();

        if (profiler != null) {

            double gap = -1.0;
            double barWidth = ((double)(Gdx.graphics.getWidth() - 40) / (double)xValues) - gap;
            barWidth = Math.max(barWidth, 1.0);
            double barHeight = (double)Gdx.graphics.getHeight() / 5.0;

            double x = 20.0;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            for (double[] bar : profiler) {
                int yPortion = 20;
                for (int i = 0; i < bar.length; i++) {
                    shapeRenderer.setColor(colors.get(i % colors.size()));
                    shapeRenderer.rect((int) x, yPortion, (int) barWidth, (int) (barHeight * bar[i]));
                    yPortion += (int)(barHeight * bar[i]);
                }

                x += gap + barWidth;
            }

            shapeRenderer.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(20, 20, Gdx.graphics.getWidth() - 40, Gdx.graphics.getHeight() / 5);
        shapeRenderer.end();

    }

    public void dispose() {
        debugFont.dispose();
    }

}
