package com.jaredzhao.castleblitz.components.audio;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;

public class SoundEffectComponent implements Component{
    public Sound sound;
    public float volume = 0f;
    public long id;
    public float boost = 1f;
}
