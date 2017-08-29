package com.jaredzhao.castleblitz.components.graphics;

import com.badlogic.ashley.core.Component;

public class LightComponent implements Component{
    public int x, y;
    public float r = 1f, g = .6f, b = .3f;
    //public float r = (float)Math.random(), g = (float)Math.random(), b = (float)Math.random();
    //public float r = 1f, g = 1f, b = 1f;
    public float intensity = 40.0f;
}
