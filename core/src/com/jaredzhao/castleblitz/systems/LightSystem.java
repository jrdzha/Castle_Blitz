package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.AddLightComponent;
import com.jaredzhao.castleblitz.components.graphics.LightComponent;

public class LightSystem extends EntitySystem{

    private ImmutableArray<Entity> lightEntities;

    private ComponentMapper<LightComponent> lightComponentComponentMapper = ComponentMapper.getFor(LightComponent.class);

    private Engine ashleyEngine;

    public LightSystem(Engine ashleyEngine){
        this.ashleyEngine = ashleyEngine;
    }

    public void reset() {

    }

    public void addedToEngine(Engine engine){
        lightEntities = engine.getEntitiesFor(Family.all(LightComponent.class, AddLightComponent.class).get());
    }

    public void update(float deltaTime){
        for(int i = 0; i < lightEntities.size(); ++i){
            Entity entity = lightEntities.get(i);
            LightComponent lightComponent = lightComponentComponentMapper.get(entity);
            ashleyEngine.addEntity(lightComponent.light);
            entity.remove(AddLightComponent.class);
        }
    }
}
