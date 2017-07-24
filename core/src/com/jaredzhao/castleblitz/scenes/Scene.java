package com.jaredzhao.castleblitz.scenes;

public abstract class Scene {
    public int IDENTIFIER; //Unique identifier for different game states
    public boolean isRunning = false; //Tracks if the scene is running or not
    public abstract void init();
    public abstract int render() throws InterruptedException;
    public abstract void dispose();
}
