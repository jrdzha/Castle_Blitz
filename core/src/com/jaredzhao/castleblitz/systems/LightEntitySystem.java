package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.graphics.LightComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;

public class LightEntitySystem extends DisposableEntitySystem {

    private ImmutableArray<Entity> lights;

    private ComponentMapper<LightComponent> lightComponentComponentMapper = ComponentMapper.getFor(LightComponent.class);
    private ComponentMapper<TileComponent> tileComponentComponentMapper = ComponentMapper.getFor(TileComponent.class);

    public LightEntitySystem() {

    }

    public void addedToEngine(Engine engine) {
        lights = engine.getEntitiesFor(Family.all(LightComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : lights) {
            LightComponent lightComponent = lightComponentComponentMapper.get(entity);
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            lightComponent.x = tileComponent.tileX * GameEngine.tileSize;
            lightComponent.y = tileComponent.tileY * GameEngine.tileSize + (GameEngine.tileSize * 6 / 16);
        }
    }

    @Override
    public void dispose() {

    }
}
