package com.jaredzhao.castleblitz.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jaredzhao.castleblitz.GameEngine;

import java.util.ArrayList;

public class AnimationFactory {

    private Texture props, tiles1, tiles2, castle, highlight, knightall, ui, ui2, homeui;

    public AnimationFactory() { //Load assets
        props = new Texture(Gdx.files.internal("graphics/highres/dungeon_textures2.png"));
        tiles1 = new Texture(Gdx.files.internal("graphics/highres/dungeon_textures6.png"));
        tiles2 = new Texture(Gdx.files.internal("graphics/highres/dungeon_textures7.png"));
        castle = new Texture(Gdx.files.internal("graphics/highres/castle1.png"));
        highlight = new Texture(Gdx.files.internal("graphics/highres/highlight.png"));
        knightall = new Texture(Gdx.files.internal("graphics/highres/knightall2.png"));
        ui = new Texture(Gdx.files.internal("ui/highres/ui.png"));
        ui2 = new Texture(Gdx.files.internal("ui/highres/ui2.png"));
        homeui = new Texture(Gdx.files.internal("ui/highres/home-ui.png"));
    }

    public Sprite spriteRegion(Texture tex, int x, int y, int w, int h) { //Generate sprite with given dimensions and location from a texture
        return new Sprite(new TextureRegion(tex, x * w, y * h, w, h));
    }

    public Sprite spriteRegionForTile(Texture tex, int x, int y, int w, int h) { //Generate sprite with given dimensions and location from a texture
        return new Sprite(new TextureRegion(tex, (x * (GameEngine.tileSize / 16 + 1) * 16), (y * (GameEngine.tileSize / 16 + 1) * 16), w, h));
    }

    public Object[] createUI(String type, int sizeX, int sizeY, float scale) { //Create UI elements
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        float alpha = 0.75f;
        if (type.equals("pause")) {
            sprites.add(spriteRegionForTile(ui, 0, 0, sizeX, sizeY));
        } else if (type.equals("fastforward")) {
            sprites.add(spriteRegionForTile(ui, 1, 0, sizeX, sizeY));
        } else if (type.equals("home")) {
            sprites.add(spriteRegionForTile(ui, 34, 0, sizeX, sizeY));
        } else if (type.equals("debug")) {
            sprites.add(spriteRegionForTile(ui, 2, 0, sizeX, sizeY));
        } else if (type.equals("move")) {
            sprites.add(spriteRegionForTile(ui, 3, 0, sizeX, sizeY));
        } else if (type.equals("attack")) {
            sprites.add(spriteRegionForTile(ui, 4, 0, sizeX, sizeY));
        } else if (type.equals("facebookLogin")) {
            sprites.add(spriteRegionForTile(ui, 5, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("battle")) {
            sprites.add(spriteRegionForTile(ui, 13, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("editUsername") || type.equals("editPassword") || type.equals("editConfirmPassword")) {
            sprites.add(spriteRegionForTile(ui, 18, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("signUp")) {
            sprites.add(spriteRegionForTile(ui, 19, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("login")) {
            sprites.add(spriteRegionForTile(ui, 24, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("back")) {
            sprites.add(spriteRegionForTile(ui, 35, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("sfxOn")) {
            sprites.add(spriteRegionForTile(ui, 9, 0, sizeX, sizeY));
        } else if (type.equals("sfxOff")) {
            sprites.add(spriteRegionForTile(ui, 10, 0, sizeX, sizeY));
        } else if (type.equals("soundOn")) {
            sprites.add(spriteRegionForTile(ui, 11, 0, sizeX, sizeY));
        } else if (type.equals("soundOff")) {
            sprites.add(spriteRegionForTile(ui, 12, 0, sizeX, sizeY));
        } else if (type.equals("homeCastleOff")) {
            sprites.add(spriteRegionForTile(ui2, 0, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeCastleOn")) {
            sprites.add(spriteRegionForTile(ui2, 1, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homePotionsOff")) {
            sprites.add(spriteRegionForTile(ui2, 2, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homePotionsOn")) {
            sprites.add(spriteRegionForTile(ui2, 3, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeShopOff")) {
            sprites.add(spriteRegionForTile(ui2, 4, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeShopOn")) {
            sprites.add(spriteRegionForTile(ui2, 5, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeArmoryOff")) {
            sprites.add(spriteRegionForTile(ui2, 6, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeArmoryOn")) {
            sprites.add(spriteRegionForTile(ui2, 7, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeRankingOff")) {
            sprites.add(spriteRegionForTile(ui2, 8, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeRankingOn")) {
            sprites.add(spriteRegionForTile(ui2, 9, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeLevelStatus")) {
            sprites.add(spriteRegionForTile(homeui, 0, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeGoldStatus")) {
            sprites.add(spriteRegionForTile(homeui, 2, 0, sizeX, sizeY));
            alpha = 1f;
        } else if (type.equals("homeShardStatus")) {
            sprites.add(spriteRegionForTile(homeui, 4, 0, sizeX, sizeY));
            alpha = 1f;
        }
        sprites.get(0).setScale(scale);
        sprites.get(0).setAlpha(alpha);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
        animations.add(Integer.valueOf(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createTile(int type) { //Create tile
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        if (type % 21 < 12) {
            sprites.add(spriteRegionForTile(tiles1, type % 21, type / 21, GameEngine.tileSize, GameEngine.tileSize));
        } else {
            sprites.add(spriteRegionForTile(tiles2, (type % 21) - 12, type / 21, GameEngine.tileSize, GameEngine.tileSize));
        }
        sprites.get(0).setScale(1.005f);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createCastle() { //Create castle
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        Sprite castleSprite = spriteRegionForTile(castle, 0, 0, GameEngine.tileSize * 3, GameEngine.tileSize * 3);
        castleSprite.setColor(new Color(.7f, .7f, .75f, .9f));
        sprites.add(castleSprite);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createHighlight(float r, float g, float b, float a, float scale, boolean flash) { //Generate highlight under characters when clicked
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();

        Sprite tile;

        if (flash) {
            for (double i = 0; i <= Math.PI; i += 0.1) {
                tile = spriteRegionForTile(highlight, 0, 0, GameEngine.tileSize, GameEngine.tileSize);
                tile.setColor(r, g, b, a * (float) Math.sin(i));
                tile.setScale(scale * 0.75f + scale * (float) Math.sin(i) * 0.25f);
                sprites.add(tile);
                animations.add(Integer.valueOf(1));
            }
        } else {
            tile = spriteRegionForTile(highlight, 0, 0, GameEngine.tileSize, GameEngine.tileSize);
            tile.setColor(r, g, b, a);
            tile.setScale(scale);

            sprites.add(tile);
            animations.add(Integer.valueOf(-1));
        }

        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createTorch() { //Generate torch animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        for (int i = 2; i < 8; i++) {
            Sprite sprite = spriteRegion(props, i, 0, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2);
            sprites.add(sprite);
            animations.add(Integer.valueOf(4));
        }
        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createCharacter(String type) { //Generate character animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        int animationTime = 40;
        if (type.equals("R")) {
            sprites.add(spriteRegion(props, 4, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            sprites.add(spriteRegion(props, 5, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if (type.equals("G")) {
            sprites.add(spriteRegion(props, 2, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            sprites.add(spriteRegion(props, 3, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if (type.equals("B")) {
            sprites.add(spriteRegion(props, 0, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            sprites.add(spriteRegion(props, 1, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if (type.equals("D")) {
            sprites.add(spriteRegion(props, 0, 0, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            sprites.add(spriteRegion(props, 1, 0, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if (type.equals("K")) {
            sprites.add(spriteRegion(props, 6, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            sprites.add(spriteRegion(props, 7, 1, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if (type.equals("NEW KNIGHT")) {
            sprites.add(spriteRegion(knightall, 0, 0, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            sprites.add(spriteRegion(knightall, 1, 0, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else {
            return null; //git remote set-url origin git://
        }
        for (Sprite s : sprites) {
            s.setScale(.7f);
        }
        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createProp(String type) { //Generate character animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        if (type.equals("CH")) {
            sprites.add(spriteRegion(props, 4, 2, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(-1);
        } else if (type.equals("BV")) {
            sprites.add(spriteRegion(props, 2, 2, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(-1);
        } else if (type.equals("BH")) {
            sprites.add(spriteRegion(props, 3, 2, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(-1);
        } else if (type.equals("BA")) {
            sprites.add(spriteRegion(props, 0, 2, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(-1);
        } else if (type.equals("SC")) {
            sprites.add(spriteRegion(props, 1, 2, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(-1);
        } else if (type.equals("LC")) {
            sprites.add(spriteRegion(props, 5, 2, GameEngine.tileSize * 3 / 2, GameEngine.tileSize * 3 / 2));
            animations.add(-1);
        } else {
            return null;
        }
        for (Sprite s : sprites) {
            s.setScale(.7f);
        }
        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }
}
