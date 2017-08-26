package com.jaredzhao.castleblitz.components.graphics;

import com.badlogic.ashley.core.Component;

import java.util.Set;

public class FogOfWarComponent implements Component {
    public Set<Integer>[][] viewMap;
    public int[][] rawViewMap;
}
