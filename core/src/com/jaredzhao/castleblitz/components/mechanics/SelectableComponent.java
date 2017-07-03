package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

public class SelectableComponent implements Component{
    public boolean isSelected = false;
    public boolean removeSelection = false;
    public String name;
    public float sizeX, sizeY;
}
