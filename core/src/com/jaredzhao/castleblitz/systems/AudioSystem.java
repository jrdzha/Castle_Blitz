package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jaredzhao.castleblitz.components.audio.HasSoundEffectComponent;
import com.jaredzhao.castleblitz.components.audio.MusicComponent;
import com.jaredzhao.castleblitz.components.audio.SoundEffectComponent;
import com.jaredzhao.castleblitz.components.audio.StopSoundComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;
import com.jaredzhao.castleblitz.factories.AudioFactory;
import com.jaredzhao.castleblitz.factories.EntityFactory;

import java.util.HashMap;

public class AudioSystem extends EntitySystem {

    private ImmutableArray<Entity> soundEffectsSources, musicSources;
    private HashMap soundEffects = new HashMap();

    private EntityFactory entityFactory;
    private AudioFactory audioFactory;
    private OrthographicCamera orthographicCamera;
    private Entity camera;
    private Entity settings;

    private ComponentMapper<HasSoundEffectComponent> hasSoundEffectComponentComponentMapper = ComponentMapper.getFor(HasSoundEffectComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<MusicComponent> musicComponentComponentMapper = ComponentMapper.getFor(MusicComponent.class);

    public AudioSystem(EntityFactory entityFactory, AudioFactory audioFactory, Entity camera, Entity settings){
        this.orthographicCamera = camera.getComponent(CameraComponent.class).camera;
        this.camera = camera;
        this.entityFactory = entityFactory;
        this.audioFactory = audioFactory;
        this.settings = settings;
    }

    public void addedToEngine(Engine engine){
        soundEffectsSources = engine.getEntitiesFor(Family.all(HasSoundEffectComponent.class, PositionComponent.class).get());
        musicSources = engine.getEntitiesFor(Family.all(MusicComponent.class).get());
    }

    public void update(float deltaTime){

        for(Entity entity : soundEffectsSources){
            HasSoundEffectComponent hsfx = hasSoundEffectComponentComponentMapper.get(entity);
            PositionComponent position = positionComponentComponentMapper.get(entity);

            if(!soundEffects.containsKey(hsfx.soundName.hashCode())){
                soundEffects.put(hsfx.soundName.hashCode(), entityFactory.createSoundEffect(hsfx.soundName));
                SoundEffectComponent soundEffectComponent = ((Entity)soundEffects.get(hsfx.soundName.hashCode())).getComponent(SoundEffectComponent.class);
                soundEffectComponent.id = soundEffectComponent.sound.play(0f);
                soundEffectComponent.sound.setLooping(soundEffectComponent.id, true);
            }

            float xPosition = Math.abs((position.x) - (orthographicCamera.position.x - (camera.getComponent(CameraComponent.class).cameraWidth / 2f)));
            float yPosition = Math.abs((position.y) - (orthographicCamera.position.y - (camera.getComponent(CameraComponent.class).cameraHeight / 2f)));

            SoundEffectComponent soundEffectComponent = ((Entity)soundEffects.get(hsfx.soundName.hashCode())).getComponent(SoundEffectComponent.class);
            soundEffectComponent.volume += (float)(75f / (Math.pow(Math.pow(xPosition, 2) + Math.pow(yPosition, 2), 1) + 512));

            if(entity.getComponent(StopSoundComponent.class) != null){
                entity.remove(SoundEffectComponent.class);
                entity.remove(StopSoundComponent.class);
            }

        }

        for(Object object : soundEffects.values()) {
            SoundEffectComponent soundEffectComponent = ((Entity)object).getComponent(SoundEffectComponent.class);
            soundEffectComponent.volume *= soundEffectComponent.boost;
            if(soundEffectComponent.volume > 1){
                soundEffectComponent.volume = 1;
            }
            soundEffectComponent.sound.setVolume(soundEffectComponent.id, soundEffectComponent.volume * soundEffectComponent.boost);
            soundEffectComponent.volume = 0;
        }

        for(Entity entity : musicSources){
            MusicComponent music = musicComponentComponentMapper.get(entity);

            if(settings.getComponent(SettingsComponent.class).fastForward){
                music.currentMusic.stop();
                settings.getComponent(SettingsComponent.class).fastForward = false;
            }

            if(music.playRandom && (!music.isPlaying || !music.currentMusic.isPlaying())){
                int nextTrackIndex;
                do {
                    nextTrackIndex = (int) (Math.random() * music.availableTracks.length - .0001);
                } while(music.currentMusicIndex == nextTrackIndex);
                music.currentMusicIndex = nextTrackIndex;

                String nextSong = "";
                for(String songName : music.songs){
                    if(songName.contains(music.availableTracks[music.currentMusicIndex])){
                        nextSong = songName;
                    }
                }
                entity.getComponent(MusicComponent.class).currentMusicName = nextSong;
                music.currentMusic = audioFactory.loadMusic(nextSong);

                music.isPlaying = true;
            } else if(!music.shouldLoop && (!music.isPlaying || !music.currentMusic.isPlaying())){
                if(music.currentMusicIndex >= music.availableTracks.length - 1){
                    music.currentMusicIndex = 0;
                } else {
                    music.currentMusicIndex++;
                }
                if(music.isPlaying){
                    music.currentMusic.dispose();
                }

                String nextSong = "";
                for(String songName : music.songs){
                    if(songName.contains(music.availableTracks[music.currentMusicIndex])){
                        nextSong = songName;
                    }
                }
                entity.getComponent(MusicComponent.class).currentMusicName = nextSong;
                music.currentMusic = audioFactory.loadMusic(nextSong);

                music.isPlaying = true;
            }
            if(music.currentMusic != null) {
                music.currentMusic.play();
                music.currentMusic.setVolume(music.volume);
            }
        }
    }
}