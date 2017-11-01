package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.AddHighlightComponent;
import com.jaredzhao.castleblitz.components.graphics.AnimationComponent;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.graphics.VisibleComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.BattleMechanicsStatesComponent;
import com.jaredzhao.castleblitz.components.mechanics.CharacterPropertiesComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;

public class HighlightSystem extends EntitySystem{

    private ImmutableArray<Entity> newHighlights, highlights;

    private ComponentMapper<HighlightComponent> highlightComponentComponentMapper = ComponentMapper.getFor(HighlightComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);

    private Engine ashleyEngine;

    public HighlightSystem(Engine ashleyEngine){
        this.ashleyEngine = ashleyEngine;
    }

    public void addedToEngine(Engine engine){
        newHighlights = engine.getEntitiesFor(Family.all(HighlightComponent.class, AddHighlightComponent.class).get());
        highlights = engine.getEntitiesFor(Family.all(HighlightComponent.class, PositionComponent.class).get());
    }

    public void update(float deltaTime){
        for(Entity entity : highlights){
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
            highlightComponent.highlight.getComponent(PositionComponent.class).x = positionComponent.x;
            highlightComponent.highlight.getComponent(PositionComponent.class).y = positionComponent.y;
        }

        for(Entity entity : newHighlights){
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            ashleyEngine.addEntity(highlightComponent.highlight);
            entity.remove(AddHighlightComponent.class);
        }
    }

    public void dispose() {

    }
}