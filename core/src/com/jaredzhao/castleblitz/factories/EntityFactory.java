package com.jaredzhao.castleblitz.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jaredzhao.castleblitz.components.audio.HasSoundEffectComponent;
import com.jaredzhao.castleblitz.components.audio.MusicComponent;
import com.jaredzhao.castleblitz.components.audio.SoundEffectComponent;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.map.UpdateTileComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.utils.TeamColorDecoder;

import java.util.ArrayList;

public class EntityFactory {

    private AnimationFactory animationFactory;
    private AudioFactory audioFactory;

    public EntityFactory(AnimationFactory animationFactory, AudioFactory audioFactory){
        this.animationFactory = animationFactory;
        this.audioFactory = audioFactory;
    }

    public Entity createCamera(int height){ //Create camera (only called once per game)
        Entity entity = new Entity();
        entity.add(new CameraComponent());
        entity.add(new PositionComponent());
        float scale = ((float) Gdx.graphics.getHeight() / height);
        float width = Gdx.graphics.getWidth() / scale;
        entity.getComponent(CameraComponent.class).cameraHeight = height;
        entity.getComponent(CameraComponent.class).scale = scale;
        entity.getComponent(CameraComponent.class).cameraWidth = width;
        entity.getComponent(CameraComponent.class).camera = new OrthographicCamera(width, height);
        return entity;
    }

    public Entity createMap(){ //Creates level map
        Entity entity = new Entity();
        entity.add(new MapComponent());
        return entity;
    }

    public Entity createTorch(int tileX, int tileY){ //Create torch entity
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new TileComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new LayerComponent());
        entity.add(new UpdateTileComponent());
        entity.add(new LightComponent());
        entity.add(new HasSoundEffectComponent());
        entity.add(new VisibleComponent());
        entity.add(new DynamicScreenPositionComponent());
        Object[] sprite = animationFactory.createTorch();
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        entity.getComponent(LayerComponent.class).layer = 2;
        entity.getComponent(TileComponent.class).tileX = tileX;
        entity.getComponent(TileComponent.class).tileY = tileY;
        entity.getComponent(TileComponent.class).type = "TO";
        entity.getComponent(LightComponent.class).x = tileX * 16;
        entity.getComponent(LightComponent.class).y = tileY * 16 + 6;
        entity.getComponent(HasSoundEffectComponent.class).soundName = "audio/sfx/torch.wav";
        entity.getComponent(HasSoundEffectComponent.class).continuous = true;
        entity.getComponent(HasSoundEffectComponent.class).soundLength = 5.12f;
        return entity;
    }

    public Entity createCharacter(String type, int tileX, int tileY, String team){ //Create character entity
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new TileComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new LayerComponent());
        entity.add(new UpdateTileComponent());
        entity.add(new CharacterPropertiesComponent());
        entity.add(new HighlightComponent());
        entity.add(new AddHighlightComponent());
        entity.add(new SelectableComponent());
        entity.add(new VisibleComponent());
        entity.add(new DynamicScreenPositionComponent());
        //entity.add(new LightComponent());
        Object[] sprite = animationFactory.createCharacter(type);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        entity.getComponent(SelectableComponent.class).name = "character";
        entity.getComponent(SelectableComponent.class).sizeX = 16;
        entity.getComponent(SelectableComponent.class).sizeY = 16;
        entity.getComponent(LayerComponent.class).layer = 2;
        entity.getComponent(TileComponent.class).tileX = tileX;
        entity.getComponent(TileComponent.class).tileY = tileY;
        entity.getComponent(TileComponent.class).type = type;
        entity.getComponent(CharacterPropertiesComponent.class).team = team;
        float[] decodedColor = TeamColorDecoder.decodeColor(team);
        /*
        entity.getComponent(LightComponent.class).x = tileX * 16;
        entity.getComponent(LightComponent.class).y = tileY * 16;
        entity.getComponent(LightComponent.class).r = decodedColor[0];
        entity.getComponent(LightComponent.class).g = decodedColor[1];
        entity.getComponent(LightComponent.class).b = decodedColor[2];
        entity.getComponent(LightComponent.class).intensity = 2.0f;
        */
        entity.getComponent(HighlightComponent.class).highlight = createHighlight("team", decodedColor[0], decodedColor[1], decodedColor[2], .95f, tileX, tileY);
        return entity;
    }

    public Entity createPortrait(String type, float x, float y){ //Create character entity
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new SelectableComponent());
        entity.add(new LayerComponent());
        entity.add(new StaticScreenPositionComponent());
        entity.add(new UIComponent());

        Object[] sprite = animationFactory.createPortrait(type);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        entity.getComponent(LayerComponent.class).layer = 6;
        entity.getComponent(SelectableComponent.class).sizeX = 60;
        entity.getComponent(SelectableComponent.class).sizeY = 80;
        entity.getComponent(SelectableComponent.class).centerOffsetX = 0;
        entity.getComponent(SelectableComponent.class).centerOffsetY = 0;
        entity.getComponent(SelectableComponent.class).name = "portrait";
        entity.getComponent(StaticScreenPositionComponent.class).x = x;
        entity.getComponent(StaticScreenPositionComponent.class).y = y;
        return entity;
    }

    public Entity createProp(String type, int tileX, int tileY){ //Create character entity
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new TileComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new LayerComponent());
        entity.add(new UpdateTileComponent());
        entity.add(new VisibleComponent());
        entity.add(new DynamicScreenPositionComponent());
        Object[] sprite = animationFactory.createProp(type);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        entity.getComponent(LayerComponent.class).layer = 2;
        entity.getComponent(TileComponent.class).tileX = tileX;
        entity.getComponent(TileComponent.class).tileY = tileY;
        entity.getComponent(TileComponent.class).type = type;
        return entity;
    }

    public Entity createCastle(int tileX, int tileY){ //Create castle
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new TileComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new LayerComponent());
        entity.add(new UpdateTileComponent());
        entity.add(new VisibleComponent());
        entity.add(new DynamicScreenPositionComponent());
        Object[] sprite = animationFactory.createCastle();
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        entity.getComponent(LayerComponent.class).layer = 2;
        entity.getComponent(TileComponent.class).tileX = tileX;
        entity.getComponent(TileComponent.class).tileY = tileY;
        entity.getComponent(TileComponent.class).type = "CA";
        return entity;
    }

    public Entity createStaticPositionUI(String type, float x, float y, int sizeX, int sizeY){ //Create UI elements
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new SelectableComponent());
        entity.add(new VisibleComponent());
        entity.add(new LayerComponent());
        entity.add(new StaticScreenPositionComponent());
        entity.add(new UIComponent());

        int centerOffsetX = 0;
        int centerOffsetY = 0;
        int selectionSizeX = sizeX;
        int selectionSizeY = sizeY;

        if(type.equals("sound")){
            Object[] sprite = animationFactory.createUI("soundOn", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            sprite = animationFactory.createUI("soundOff", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        } else if(type.equals("sfx")){
            Object[] sprite = animationFactory.createUI("sfxOn", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            sprite = animationFactory.createUI("sfxOff", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        } else if(type.equals("homeCastle")){
            Object[] sprite = animationFactory.createUI("homeCastleOff", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            sprite = animationFactory.createUI("homeCastleOn", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            centerOffsetY = -8;
            selectionSizeY = 16;
        } else if(type.equals("homeTeam")){
            Object[] sprite = animationFactory.createUI("homeTeamOff", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            sprite = animationFactory.createUI("homeTeamOn", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            centerOffsetY = -8;
            selectionSizeY = 16;
        } else if(type.equals("homeShop")){
            Object[] sprite = animationFactory.createUI("homeShopOff", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            sprite = animationFactory.createUI("homeShopOn", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            centerOffsetY = -8;
            selectionSizeY = 16;
        } else if(type.equals("homeArmory")){
            Object[] sprite = animationFactory.createUI("homeArmoryOff", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            sprite = animationFactory.createUI("homeArmoryOn", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            centerOffsetY = -8;
            selectionSizeY = 16;
        } else if(type.equals("homeBrigade")){
            Object[] sprite = animationFactory.createUI("homeBrigadeOff", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            sprite = animationFactory.createUI("homeBrigadeOn", sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);

            centerOffsetY = -8;
            selectionSizeY = 16;
        } else {
            Object[] sprite = animationFactory.createUI(type, sizeX, sizeY, 1);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        }

        entity.getComponent(LayerComponent.class).layer = 6;
        entity.getComponent(SelectableComponent.class).sizeX = selectionSizeX;
        entity.getComponent(SelectableComponent.class).sizeY = selectionSizeY;
        entity.getComponent(SelectableComponent.class).centerOffsetX = centerOffsetX;
        entity.getComponent(SelectableComponent.class).centerOffsetY = centerOffsetY;
        entity.getComponent(SelectableComponent.class).name = type;
        entity.getComponent(StaticScreenPositionComponent.class).x = x;
        entity.getComponent(StaticScreenPositionComponent.class).y = y;
        return entity;
    }

    public Entity createDynamicPositionUI(String type, float x, float y, int sizeX, int sizeY){ //Create UI elements
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new SelectableComponent());
        entity.add(new VisibleComponent());
        entity.add(new LayerComponent());
        entity.add(new DynamicScreenPositionComponent());
        entity.add(new UIComponent());
        Object[] sprite = animationFactory.createUI(type, sizeX, sizeY, 1);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) sprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) sprite[0]);
        entity.getComponent(LayerComponent.class).layer = 5;
        entity.getComponent(SelectableComponent.class).sizeX = sizeX;
        entity.getComponent(SelectableComponent.class).sizeY = sizeY;
        entity.getComponent(SelectableComponent.class).name = type;
        entity.getComponent(PositionComponent.class).x = x;
        entity.getComponent(PositionComponent.class).y = y;
        return entity;
    }

    public Entity createSettings(){ //Create global settings
        Entity entity = new Entity();
        entity.add(new SettingsComponent());
        return entity;
    }

    public Entity createBattleMechanics(){ //Create global settings
        Entity entity = new Entity();
        entity.add(new BattleMechanicsStatesComponent());
        return entity;
    }

    public Entity createInhibitor(int tileX, int tileY){ //Create empty block on map where players can't walk
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new TileComponent());
        entity.add(new UpdateTileComponent());
        entity.getComponent(TileComponent.class).tileX = tileX;
        entity.getComponent(TileComponent.class).tileY = tileY;
        entity.getComponent(TileComponent.class).type = "IN";
        return entity;
    }

    public Entity createTile(int tileX, int tileY, int type, int layer){ //Create tile entity
        Entity entity = new Entity();
        entity.add(new TileComponent());
        entity.add(new PositionComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new LayerComponent());
        entity.add(new UpdateTileComponent());
        entity.add(new HighlightComponent());
        entity.add(new AddHighlightComponent());
        entity.add(new VisibleComponent());
        entity.add(new DynamicScreenPositionComponent());
        entity.getComponent(TileComponent.class).tileX = tileX;
        entity.getComponent(TileComponent.class).tileY = tileY;

        if(type != -1){
            entity.getComponent(LayerComponent.class).layer = layer;
            entity.getComponent(TileComponent.class).type = "" + type;
            Object[] tileSprite = animationFactory.createTile(type);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);
        } else {
            entity.getComponent(AnimationComponent.class).framesDisplayed = -1;
        }

        if(type == 74 || type == 75 || type == 76 || type == 95 || type == 96 || type == 97 || type == 98 || type == 57 || type == 58 || type == 59 ||type == 79 ||
                type == 80 || type == 81) {
            entity.add(new HasSoundEffectComponent());
            entity.getComponent(HasSoundEffectComponent.class).soundName = "audio/sfx/runningwater.wav";
            entity.getComponent(HasSoundEffectComponent.class).continuous = true;
            entity.getComponent(HasSoundEffectComponent.class).soundLength = 5.493f;
        }

        entity.getComponent(HighlightComponent.class).highlight = createHighlight("move", 1, 1, 1, .95f, tileX, tileY);
        entity.getComponent(HighlightComponent.class).highlight.remove(VisibleComponent.class);

        return entity;
    }

    public Entity createFogOfWar(float r, float g, float b, float a, int viewMapSizeX, int viewMapSizeY) {
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new LayerComponent());
        entity.add(new VisibleComponent());
        entity.add(new FogOfWarComponent());
        entity.add(new DynamicScreenPositionComponent());
        entity.getComponent(FogOfWarComponent.class).viewMap = new int[viewMapSizeX][viewMapSizeY];
        entity.getComponent(FogOfWarComponent.class).rawViewMap = new int[viewMapSizeX][viewMapSizeY];

        for (int i = 0; i < entity.getComponent(FogOfWarComponent.class).viewMap.length; i++) {
            for (int j = 0; j < entity.getComponent(FogOfWarComponent.class).viewMap[0].length; j++) {
                entity.getComponent(FogOfWarComponent.class).viewMap[i][j] = 0;
            }
        }

        entity.getComponent(LayerComponent.class).layer = 4;

        Object[] tileSprite;

        tileSprite = animationFactory.createHighlight(r, g, b, 1, 1.0015f, false);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

        tileSprite = animationFactory.createHighlight(r, g, b, .75f, 1.0015f, false);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

        tileSprite = animationFactory.createHighlight(r, g, b, .5f, 1.0015f, false);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

        tileSprite = animationFactory.createHighlight(r, g, b, .25f, 1.0015f, false);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

        tileSprite = animationFactory.createHighlight(r, g, b, 0, 1.0015f, false);
        entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
        entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

        return entity;
    }

    public Entity createHighlight(String type, float r, float g, float b, float scale, int tileX, int tileY){ //Create highlight entity
        Entity entity = new Entity();
        entity.add(new PositionComponent());
        entity.add(new TileComponent());
        entity.add(new UpdateTileComponent());
        entity.add(new SpriteComponent());
        entity.add(new AnimationComponent());
        entity.add(new LayerComponent());
        entity.add(new VisibleComponent());
        entity.add(new DynamicScreenPositionComponent());

        entity.getComponent(TileComponent.class).tileX = tileX;
        entity.getComponent(TileComponent.class).tileY = tileY;

        if(type.equals("move")) {
            entity.getComponent(LayerComponent.class).layer = 1;

            Object[] tileSprite = animationFactory.createHighlight(r, g, b, .15f, scale, true);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);
        } else if(type.equals("team")) {
            entity.getComponent(LayerComponent.class).layer = 1;

            Object[] tileSprite = animationFactory.createHighlight(r, g, b, .15f, scale, false);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

            tileSprite = animationFactory.createHighlight(r, g, b, .4f, scale, false);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);
        } else if(type.equals("fogOfWar")){
            entity.getComponent(LayerComponent.class).layer = 4;

            Object[] tileSprite = animationFactory.createHighlight(r, g, b, 1f, scale, false);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

            tileSprite = animationFactory.createHighlight(r, g, b, .5f, scale, false);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);

            tileSprite = animationFactory.createHighlight(r, g, b, 0f, scale, false);
            entity.getComponent(AnimationComponent.class).animationTimeList.add((ArrayList<Integer>) tileSprite[1]);
            entity.getComponent(SpriteComponent.class).spriteList.add((ArrayList<Sprite>) tileSprite[0]);
        }

        return entity;
    }

    public Entity createSoundEffect(String location){ //Create sound effect
        Entity entity = new Entity();
        entity.add(new SoundEffectComponent());
        entity.getComponent(SoundEffectComponent.class).sound = audioFactory.loadSound(location);
        if(location.equals("audio/sfx/runningwater.wav")){
            entity.getComponent(SoundEffectComponent.class).boost = .25f;
        }
        if(location.equals("audio/sfx/blop.wav")){
            entity.getComponent(SoundEffectComponent.class).boost = .4f;
        }
        return entity;
    }

    public Entity createMusic(String[] availableTracks){ //Create music
        Entity entity = new Entity();
        entity.add(new MusicComponent());
        entity.getComponent(MusicComponent.class).songs = audioFactory.loadMusicTitles();
        entity.getComponent(MusicComponent.class).volume = .16f;
        entity.getComponent(MusicComponent.class).availableTracks = availableTracks;
        entity.getComponent(MusicComponent.class).playRandom = true;
        return entity;
    }

}
