package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

public class SelectableComponent implements Component{
    public boolean isSelected = false, removeSelection = false, addSelection = false, touchDown = false;
    public String name;
    public float sizeX, sizeY, centerOffsetX, centerOffsetY;
}
