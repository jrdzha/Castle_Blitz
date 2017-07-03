package com.jaredzhao.castleblitz.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class AnimationFactory {

    Texture textures2, textures4, texture5, light, colorTiles, ui;

    public AnimationFactory(){ //Load assets
        textures2 = new Texture(Gdx.files.internal("graphics/dungeon_textures2.png"));
        textures4 = new Texture(Gdx.files.internal("graphics/dungeon_textures4.png"));
        texture5 = new Texture(Gdx.files.internal("graphics/castle1.png"));
        light = new Texture(Gdx.files.internal("graphics/light.png"));
        colorTiles = new Texture(Gdx.files.internal("graphics/ColorTiles.png"));
        ui = new Texture(Gdx.files.internal("ui/ui.png"));
    }

    public Sprite spriteRegion(Texture tex, int x, int y, int w, int h){ //Generate sprite with given dimensions and location from a texture
        Sprite sprite = new Sprite(new TextureRegion(tex, x * w, y * h, w, h));
        return sprite;
    }

    public Sprite spriteRegionForTile(Texture tex, int x, int y, int w, int h){ //Generate sprite with given dimensions and location from a texture
        Sprite sprite = new Sprite(new TextureRegion(tex, (x * w) + x, (y * h) + y, w, h));
        sprite.setScale(1.04f);
        return sprite;
    }

    public Object[] createUI(String type, float scale){ //Create UI elements
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        if(type.equals("pause")) {
            sprites.add(spriteRegionForTile(ui, 0, 0, 16, 16));
        } else if(type.equals("fastforward")){
            sprites.add(spriteRegionForTile(ui, 1, 0, 16, 16));
        } else if(type.equals("debug")){
            sprites.add(spriteRegionForTile(ui, 2, 0, 16, 16));
        }
        sprites.get(0).setScale(scale);
        sprites.get(0).setAlpha(.75f);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(new Integer(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createTile(int type){ //Create tile
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        sprites.add(spriteRegionForTile(textures4, type % 21, type / 21, 16, 16));
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(new Integer(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createCastle(){ //Create castle
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        Sprite castleSprite = spriteRegionForTile(texture5, 0, 0, 48, 48);
        castleSprite.setColor(new Color(.7f, .7f, .75f, .9f));
        sprites.add(castleSprite);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(new Integer(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createHighlight(int color, float alpha){ //Generate highlight under characters when clicked
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        Sprite tile = spriteRegionForTile(colorTiles, color, 0, 16, 16);
        float balance = 0f;
        if(color == 1){
            balance = .00f;
        } else if(color == 4){
            balance = -.00f;
        } else if(color == 5){
            balance = -.00f;
        }
        tile.setAlpha(alpha + balance);
        tile.setScale(0.95f);
        sprites.add(tile);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(new Integer(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createLight(int size){ //Generate randomly flickering light animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        for(int i = 0; i < 25; i++){
            Sprite scaledSprite = spriteRegion(light, 0, 0, 400, 400);
            scaledSprite.setAlpha(.5f);
            scaledSprite.setScale((1f + ((float)(Math.random() - .5) / 9f)) * (float)size / 400f);
            sprites.add(scaledSprite);
            animations.add(new Integer(2));
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
            Sprite sprite = spriteRegion(textures2, i, 0, 24, 24);
            sprites.add(sprite);
            animations.add(new Integer(4));
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
            sprites.add(spriteRegion(textures2, 4, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 5, 1, 24, 24));
            animations.add(new Integer(animationTime));
            animations.add(new Integer(animationTime));
        } else if(type.equals("G")){
            sprites.add(spriteRegion(textures2, 2, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 3, 1, 24, 24));
            animations.add(new Integer(animationTime));
            animations.add(new Integer(animationTime));
        } else if(type.equals("B")){
            sprites.add(spriteRegion(textures2, 0, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 1, 1, 24, 24));
            animations.add(new Integer(animationTime));
            animations.add(new Integer(animationTime));
        } else if(type.equals("D")){
            sprites.add(spriteRegion(textures2, 0, 0, 24, 24));
            sprites.add(spriteRegion(textures2, 1, 0, 24, 24));
            animations.add(new Integer(animationTime));
            animations.add(new Integer(animationTime));
        } else if(type.equals("K")){
            sprites.add(spriteRegion(textures2, 6, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 7, 1, 24, 24));
            animations.add(new Integer(animationTime));
            animations.add(new Integer(animationTime));
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

    public Object[] createProp(String type){ //Generate character animation
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        ArrayList<Integer> animations = new ArrayList<Integer>();
        int animationTime = 40;
        if(type.equals("CH")){
            sprites.add(spriteRegion(textures2, 4, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("BV")){
            sprites.add(spriteRegion(textures2, 2, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("BH")){
            sprites.add(spriteRegion(textures2, 3, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("BA")){
            sprites.add(spriteRegion(textures2, 0, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("SB")){
            sprites.add(spriteRegion(textures2, 1, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("BB")){
            sprites.add(spriteRegion(textures2, 5, 2, 24, 24));
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
