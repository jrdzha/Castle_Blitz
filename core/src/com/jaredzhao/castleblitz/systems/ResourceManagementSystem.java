package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.RemoveTagComponent;

public class ResourceManagementSystem extends EntitySystem{

    private ImmutableArray<Entity> removeEntities;
    private Engine ashleyEngine;

    public ResourceManagementSystem(Engine ashleyEngine){
        this.ashleyEngine = ashleyEngine;
    }

    public void reset() {

    }

    public void addedToEngine(Engine engine){
        removeEntities = engine.getEntitiesFor(Family.all(RemoveTagComponent.class).get());
    }

    public void removeAll(){
        while(ashleyEngine.getEntities().size() > 0) {
            ImmutableArray<Entity> removeArray = ashleyEngine.getEntities();
            for (Entity entity : removeArray) {
                entity.removeAll();
                ashleyEngine.removeEntity(entity);
            }
        }
    }

    public void update(float deltaTime){
        for(int i = 0; i < removeEntities.size(); ++i){
            Entity entity = removeEntities.get(i);
            entity.removeAll();
            ashleyEngine.removeEntity(entity);
        }
    }
}