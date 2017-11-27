package com.jaredzhao.castleblitz.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraComponent implements Component{
    public float cameraHeight;
    public float scale;
    public float cameraWidth;
    public OrthographicCamera camera;
}
