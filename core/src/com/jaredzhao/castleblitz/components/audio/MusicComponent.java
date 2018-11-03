package com.jaredzhao.castleblitz.components.audio;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Music;

public class MusicComponent implements Component {
    public String[] songs;
    public String[] availableTracks;
    public Music currentMusic;
    public String currentMusicName;
    public int currentMusicIndex = -1;
    public float volume = 0f;
    public boolean shouldLoop = false;
    public boolean playRandom = false;
}
