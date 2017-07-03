package com.jaredzhao.castleblitz.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraComponent implements Component{
    public int cameraHeight = 250;
    public float scale = ((float) Gdx.graphics.getHeight() / (float)cameraHeight);
    public float cameraWidth = Gdx.graphics.getWidth() / scale;
    public OrthographicCamera camera = new OrthographicCamera(cameraWidth, cameraHeight);
}
