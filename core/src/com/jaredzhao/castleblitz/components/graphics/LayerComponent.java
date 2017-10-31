package com.jaredzhao.castleblitz.components.graphics;

import com.badlogic.ashley.core.Component;

public class LayerComponent implements Component{
    public int layer = -1;
    public long sortCode = -1;
    public long lastSortCode = -2;
}
