package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by jaredzhao on 6/30/17.
 */

public class ShaderBatch extends SpriteBatch {

    static String vertexShader, fragmentShader;

    public ShaderProgram shader;

    public ShaderBatch(String vertexShader, String fragmentShader, int size) {
        super(size);
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if(shader.isCompiled()){
            setShader(shader);
        } else {
            System.out.println(shader.getLog());
        }
    }
}
