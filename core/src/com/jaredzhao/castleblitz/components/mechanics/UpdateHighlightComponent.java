package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

public class UpdateHighlightComponent implements Component{

    public float alpha;

    public UpdateHighlightComponent(float alpha){
        this.alpha = alpha;
    }
}
