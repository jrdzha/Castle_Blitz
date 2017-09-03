package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.LightComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;

public class LightSystem extends EntitySystem{

    private ImmutableArray<Entity> lights;

    private ComponentMapper<LightComponent> lightComponentComponentMapper = ComponentMapper.getFor(LightComponent.class);
    private ComponentMapper<TileComponent> tileComponentComponentMapper = ComponentMapper.getFor(TileComponent.class);

    public LightSystem(){

    }

    public void addedToEngine(Engine engine){
        lights = engine.getEntitiesFor(Family.all(LightComponent.class).get());
    }

    public void update(float deltaTime){
        for(Entity entity : lights){
            LightComponent lightComponent = lightComponentComponentMapper.get(entity);
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            lightComponent.x = tileComponent.tileX * 16;
            lightComponent.y = tileComponent.tileY * 16 + 6;

            /*
            if(lightComponent.intensity > 1) {
                lightComponent.r = (float) Math.random();
                lightComponent.g = (float) Math.random();
                lightComponent.b = (float) Math.random();
                lightComponent.intensity = 0f;
            }
            lightComponent.intensity += .1f;
            */
        }
    }

    public void dispose() {

    }
}
