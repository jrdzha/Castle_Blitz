package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.AddLightComponent;
import com.jaredzhao.castleblitz.components.graphics.LightComponent;

public class LightSystem extends EntitySystem{

    private ImmutableArray<Entity> lights;

    private ComponentMapper<LightComponent> lightComponentComponentMapper = ComponentMapper.getFor(LightComponent.class);

    private Engine ashleyEngine;

    public LightSystem(Engine ashleyEngine){
        this.ashleyEngine = ashleyEngine;
    }

    public void addedToEngine(Engine engine){
        lights = engine.getEntitiesFor(Family.all(LightComponent.class, AddLightComponent.class).get());
    }

    public void update(float deltaTime){
        for(Entity entity : lights){
            LightComponent lightComponent = lightComponentComponentMapper.get(entity);
            ashleyEngine.addEntity(lightComponent.light);
            entity.remove(AddLightComponent.class);
        }
    }
}
