package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.player.CameraComponent;

public class CameraEntitySystem extends DisposableEntitySystem {
    private ImmutableArray<Entity> cameras;

    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CameraComponent> cameraComponentComponentMapper = ComponentMapper.getFor(CameraComponent.class);

    private Entity map;

    public CameraEntitySystem(Entity map){
        this.map = map;
    }

    public void addedToEngine(Engine engine){
        cameras = engine.getEntitiesFor(Family.all(PositionComponent.class, CameraComponent.class).get());
    }

    public void update(float deltaTime){

        for(Entity entity : cameras){
            PositionComponent position = positionComponentComponentMapper.get(entity);
            CameraComponent camera = cameraComponentComponentMapper.get(entity);
            if(position.x < 0){
                position.x = 0;
            } else if(position.x > map.getComponent(MapComponent.class).mapEntities[0].length * 16){
                position.x = map.getComponent(MapComponent.class).mapEntities[0].length * 16;
            }
            if(position.y < 0){
                position.y = 0;
            } else if(position.y > map.getComponent(MapComponent.class).mapEntities[0][0].length * 16){
                position.y = map.getComponent(MapComponent.class).mapEntities[0][0].length * 16;
            }
            camera.camera.position.set(
                    (position.x + camera.camera.viewportWidth / 2),
                    (position.y + camera.camera.viewportHeight / 2), 0); //Move camera appropriately
        }
    }

    @Override
    public void dispose() {

    }
}
