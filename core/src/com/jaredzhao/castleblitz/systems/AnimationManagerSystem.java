package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.AnimationComponent;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;

public class AnimationManagerSystem extends EntitySystem{
    private ImmutableArray<Entity> selectableHighlights;

    private ComponentMapper<HighlightComponent> highlightComponentComponentMapper = ComponentMapper.getFor(HighlightComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<AnimationComponent> animationComponentComponentMapper = ComponentMapper.getFor(AnimationComponent.class);

    public AnimationManagerSystem(){

    }

    public void addedToEngine(Engine engine){
        selectableHighlights = engine.getEntitiesFor(Family.all(SelectableComponent.class, HighlightComponent.class).get());
    }

    public void update(float deltaTime){
        for(Entity entity : selectableHighlights){
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            AnimationComponent animationComponent = animationComponentComponentMapper.get(highlightComponent.highlight);
            if(selectableComponent.isSelected){
                animationComponent.currentFrame = 0;
                animationComponent.currentTrack = 1;
                animationComponent.framesDisplayed = 0;
            } else {
                animationComponent.currentFrame = 0;
                animationComponent.currentTrack = 0;
                animationComponent.framesDisplayed = 0;
            }
        }
    }
}