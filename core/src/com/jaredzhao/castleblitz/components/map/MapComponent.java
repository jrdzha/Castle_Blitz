package com.jaredzhao.castleblitz.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class MapComponent implements Component{
    public Entity[][][] mapEntities; // [map level] [level x] [level y]
    // level 0 is tiles
}
