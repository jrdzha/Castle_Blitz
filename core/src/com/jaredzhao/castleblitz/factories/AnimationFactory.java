package com.jaredzhao.castleblitz.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class AnimationFactory {

    private Texture props, tiles, castle, highlight, ui, ui2, portraits;

    public AnimationFactory(){ //Load assets
        props = new Texture(Gdx.files.internal("graphics/dungeon_textures2.png"));
        tiles = new Texture(Gdx.files.internal("graphics/dungeon_textures4.png"));
        castle = new Texture(Gdx.files.internal("graphics/castle1.png"));
        highlight = new Texture(Gdx.files.internal("graphics/highlight.png"));
        ui = new Texture(Gdx.files.internal("ui/ui.png"));
        ui2 = new Texture(Gdx.files.internal("ui/ui2.png"));
        portraits = new Texture(Gdx.files.internal("graphics/portraits/portraits.png"));
    }

    public Sprite spriteRegion(Texture tex, int x, int y, int w, int h){ //Generate sprite with given dimensions and location from a texture
        return new Sprite(new TextureRegion(tex, x * w, y * h, w, h));
    }

    public Sprite spriteRegionForTile(Texture tex, int x, int y, int w, int h){ //Generate sprite with given dimensions and location from a texture
        return new Sprite(new TextureRegion(tex, (x * 16) + x, (y * 16) + y, w, h));
    }

    public Object[] createUI(String type, int sizeX, int sizeY, float scale){ //Create UI elements
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        float alpha = 0.75f;
        if(type.equals("pause")) {
            sprites.add(spriteRegionForTile(ui, 0, 0, sizeX, sizeY));
        } else if(type.equals("fastforward")){
            sprites.add(spriteRegionForTile(ui, 1, 0, sizeX, sizeY));
        } else if(type.equals("debug")){
            sprites.add(spriteRegionForTile(ui, 2, 0, sizeX, sizeY));
        } else if(type.equals("move")){
            sprites.add(spriteRegionForTile(ui, 3, 0, sizeX, sizeY));
        } else if(type.equals("attack")){
            sprites.add(spriteRegionForTile(ui, 4, 0, sizeX, sizeY));
        } else if(type.equals("facebookLogin")){
            sprites.add(spriteRegionForTile(ui, 5, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("battle")){
            sprites.add(spriteRegionForTile(ui, 13, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("sfxOn")){
            sprites.add(spriteRegionForTile(ui, 9, 0, sizeX, sizeY));
        } else if(type.equals("sfxOff")){
            sprites.add(spriteRegionForTile(ui, 10, 0, sizeX, sizeY));
        } else if(type.equals("soundOn")){
            sprites.add(spriteRegionForTile(ui, 11, 0, sizeX, sizeY));
        } else if(type.equals("soundOff")){
            sprites.add(spriteRegionForTile(ui, 12, 0, sizeX, sizeY));
        } else if(type.equals("homeCastleOff")){
            sprites.add(spriteRegionForTile(ui2, 0, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeCastleOn")){
            sprites.add(spriteRegionForTile(ui2, 1, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeTeamOff")){
            sprites.add(spriteRegionForTile(ui2, 2, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeTeamOn")){
            sprites.add(spriteRegionForTile(ui2, 3, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeShopOff")){
            sprites.add(spriteRegionForTile(ui2, 4, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeShopOn")){
            sprites.add(spriteRegionForTile(ui2, 5, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeArmoryOff")){
            sprites.add(spriteRegionForTile(ui2, 6, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeArmoryOn")){
            sprites.add(spriteRegionForTile(ui2, 7, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeBrigadeOff")){
            sprites.add(spriteRegionForTile(ui2, 8, 0, sizeX, sizeY));
            alpha = 1f;
        } else if(type.equals("homeBrigadeOn")){
            sprites.add(spriteRegionForTile(ui2, 9, 0, sizeX, sizeY));
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

    public Object[] createTile(int type){ //Create tile
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        sprites.add(spriteRegionForTile(tiles, type % 21, type / 21, 16, 16));
        sprites.get(0).setScale(1.003f);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createPortrait(String type){ //Create tile
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        if(type.equals("1")) {
            sprites.add(spriteRegion(portraits, 0, 0, 60, 80));
        } else if(type.equals("2")) {
            sprites.add(spriteRegion(portraits, 1, 0, 60, 80));
        } else if(type.equals("3")) {
            sprites.add(spriteRegion(portraits, 2, 0, 60, 80));
        } else if(type.equals("4")) {
            sprites.add(spriteRegion(portraits, 3, 0, 60, 80));
        } else if(type.equals("5")) {
            sprites.add(spriteRegion(portraits, 4, 0, 60, 80));
        } else if(type.equals("6")) {
            sprites.add(spriteRegion(portraits, 5, 0, 60, 80));
        } else if(type.equals("7")) {
            sprites.add(spriteRegion(portraits, 6, 0, 60, 80));
        } else if(type.equals("8")) {
            sprites.add(spriteRegion(portraits, 7, 0, 60, 80));
        } else if(type.equals("9")) {
            sprites.add(spriteRegion(portraits, 8, 0, 60, 80));
        } else if(type.equals("10")) {
            sprites.add(spriteRegion(portraits, 9, 0, 60, 80));
        }
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createCastle(){ //Create castle
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        Sprite castleSprite = spriteRegionForTile(castle, 0, 0, 48, 48);
        castleSprite.setColor(new Color(.7f, .7f, .75f, .9f));
        sprites.add(castleSprite);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createHighlight(float r, float g, float b, float a, float scale, boolean flash){ //Generate highlight under characters when clicked
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();

        Sprite tile;

        if(flash) {
            for (double i = 0; i <= Math.PI; i += 0.1) {
                tile = spriteRegionForTile(highlight, 0, 0, 16, 16);
                tile.setColor(r, g, b, a * (float)Math.sin(i));
                tile.setScale(scale * 0.75f + scale * (float)Math.sin(i) * 0.25f);
                sprites.add(tile);
                animations.add(Integer.valueOf(1));
            }
        } else {
            tile = spriteRegionForTile(highlight, 0, 0, 16, 16);
            tile.setColor(r, g, b, a);
            tile.setScale(scale);

            sprites.add(tile);
            animations.add(Integer.valueOf(-1));
        }

        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createTorch(){ //Generate torch animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        for(int i = 2; i < 8; i++){
            Sprite sprite = spriteRegion(props, i, 0, 24, 24);
            sprites.add(sprite);
            animations.add(Integer.valueOf(4));
        }
        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createCharacter(String type){ //Generate character animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        int animationTime = 40;
        if(type.equals("R")){
            sprites.add(spriteRegion(props, 4, 1, 24, 24));
            sprites.add(spriteRegion(props, 5, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("G")){
            sprites.add(spriteRegion(props, 2, 1, 24, 24));
            sprites.add(spriteRegion(props, 3, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("B")){
            sprites.add(spriteRegion(props, 0, 1, 24, 24));
            sprites.add(spriteRegion(props, 1, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("D")){
            sprites.add(spriteRegion(props, 0, 0, 24, 24));
            sprites.add(spriteRegion(props, 1, 0, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("K")){
            sprites.add(spriteRegion(props, 6, 1, 24, 24));
            sprites.add(spriteRegion(props, 7, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else {
            return null; //git remote set-url origin git://
        }
        for(Sprite s : sprites){
            s.setScale(.7f);
        }
        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createProp(String type){ //Generate character animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        if(type.equals("CH")){
            sprites.add(spriteRegion(props, 4, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("BV")){
            sprites.add(spriteRegion(props, 2, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("BH")){
            sprites.add(spriteRegion(props, 3, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("BA")){
            sprites.add(spriteRegion(props, 0, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("SC")){
            sprites.add(spriteRegion(props, 1, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("LC")){
            sprites.add(spriteRegion(props, 5, 2, 24, 24));
            animations.add(-1);
        } else {
            return null;
        }
        for(Sprite s : sprites){
            s.setScale(.7f);
        }
        completeAnimation[0] = sprites;
        completeAnimation[1] = animations;
        return completeAnimation;
    }
}
