package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Utility for loading and containing ShaderPrograms
 */
public class ShaderBatch extends SpriteBatch {

    static String vertexShader, fragmentShader;

    public ShaderProgram shader;

    /**
     * Load and compile ShaderProgram
     *
     * @param vertexShader   String containing vertex shader
     * @param fragmentShader String containing fragment shader
     * @param size           Size of the ShaderProgram
     */
    public ShaderBatch(String vertexShader, String fragmentShader, int size) {
        super(size);
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled()) {
            setShader(shader);
        } else {
            System.out.println(shader.getLog());
        }
    }
}
