package com.jaredzhao.castleblitz.components.audio;

import com.badlogic.ashley.core.Component;

public class HasSoundEffectComponent implements Component{
    public String soundName;
    public boolean continuous = false, dynamicVolume = true;
    public float soundLength, elapsedTime = 0;
}
