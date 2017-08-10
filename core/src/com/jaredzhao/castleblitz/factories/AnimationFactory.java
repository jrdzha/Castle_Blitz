package com.jaredzhao.castleblitz.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class AnimationFactory {

    private Texture textures2, textures4, texture5, light, highlight, ui, ui2;

    public AnimationFactory(){ //Load assets
        textures2 = new Texture(Gdx.files.internal("graphics/dungeon_textures2.png"));
        textures4 = new Texture(Gdx.files.internal("graphics/dungeon_textures4.png"));
        texture5 = new Texture(Gdx.files.internal("graphics/castle1.png"));
        light = new Texture(Gdx.files.internal("graphics/light.png"));
        highlight = new Texture(Gdx.files.internal("graphics/highlight.png"));
        ui = new Texture(Gdx.files.internal("ui/ui.png"));
        ui2 = new Texture(Gdx.files.internal("ui/ui2.png"));
    }

    public Sprite spriteRegion(Texture tex, int x, int y, int w, int h){ //Generate sprite with given dimensions and location from a texture
        return new Sprite(new TextureRegion(tex, x * w, y * h, w, h));
    }

    public Sprite spriteRegionForTile(Texture tex, int x, int y, int w, int h){ //Generate sprite with given dimensions and location from a texture
        Sprite sprite = new Sprite(new TextureRegion(tex, (x * 16) + x, (y * 16) + y, w, h));
        sprite.setScale(1.005f);
        return sprite;
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
        sprites.add(spriteRegionForTile(textures4, type % 21, type / 21, 16, 16));
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
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
        animations.add(Integer.valueOf(-1));
        completeAnimation[1] = animations;
        return completeAnimation;
    }

    public Object[] createHighlight(float r, float g, float b, float a, float scale){ //Generate highlight under characters when clicked
        Object[] completeAnimation = new Object[2];
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        Sprite tile = spriteRegionForTile(highlight, 0, 0, 16, 16);
        tile.setColor(r, g, b, a);
        tile.setScale(scale);
        sprites.add(tile);
        completeAnimation[0] = sprites;
        ArrayList<Integer> animations = new ArrayList<Integer>();
        animations.add(Integer.valueOf(-1));
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
            animations.add(Integer.valueOf(2));
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
            sprites.add(spriteRegion(textures2, 4, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 5, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("G")){
            sprites.add(spriteRegion(textures2, 2, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 3, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("B")){
            sprites.add(spriteRegion(textures2, 0, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 1, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("D")){
            sprites.add(spriteRegion(textures2, 0, 0, 24, 24));
            sprites.add(spriteRegion(textures2, 1, 0, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
        } else if(type.equals("K")){
            sprites.add(spriteRegion(textures2, 6, 1, 24, 24));
            sprites.add(spriteRegion(textures2, 7, 1, 24, 24));
            animations.add(Integer.valueOf(animationTime));
            animations.add(Integer.valueOf(animationTime));
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
        } else if(type.equals("SC")){
            sprites.add(spriteRegion(textures2, 1, 2, 24, 24));
            animations.add(-1);
        } else if(type.equals("LC")){
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
