package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.AnimationComponent;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.graphics.SpriteComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;

public class AnimationManagerSystem extends EntitySystem{
    private ImmutableArray<Entity> selectableHighlights;
    private ImmutableArray<Entity> selectableUI;

    private ComponentMapper<HighlightComponent> highlightComponentComponentMapper = ComponentMapper.getFor(HighlightComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<AnimationComponent> animationComponentComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SpriteComponent> spriteComponentComponentMapper = ComponentMapper.getFor(SpriteComponent.class);

    public AnimationManagerSystem(){

    }

    public void addedToEngine(Engine engine){
        selectableHighlights = engine.getEntitiesFor(Family.all(SelectableComponent.class, HighlightComponent.class).get());
        selectableUI = engine.getEntitiesFor(Family.all(SelectableComponent.class, AnimationComponent.class).get());
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

        for(Entity entity : selectableUI){
            if(entity.getComponent(HighlightComponent.class) == null) {
                SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
                SpriteComponent spriteComponent = spriteComponentComponentMapper.get(entity);
                if (selectableComponent.touchDown) {
                    if(spriteComponent.spriteList.get(0).get(0).getScaleX() > .95f) {
                        spriteComponent.spriteList.get(0).get(0).scale(-.025f);
                    }
                } else {
                    if(spriteComponent.spriteList.get(0).get(0).getScaleX() < 1f) {
                        spriteComponent.spriteList.get(0).get(0).scale(.025f);
                    }
                }
                //System.out.println(spriteComponent.spriteList.get(0).get(0).getScaleX());
            }
        }
    }

    public void dispose() {

    }
}