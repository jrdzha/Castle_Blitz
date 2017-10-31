package com.jaredzhao.castleblitz.components.graphics;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class AnimationComponent implements Component{
    public ArrayList<ArrayList<Integer>> animationTimeList = new ArrayList<ArrayList<Integer>>();
    public int currentTrack = 0, currentFrame = 0;
    public int framesDisplayed = 0;
}