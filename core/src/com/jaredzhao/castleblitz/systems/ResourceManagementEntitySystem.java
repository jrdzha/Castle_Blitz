package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.RemoveTagComponent;

/**
 * Resource management system used to fully dispose of entities that are no longer needed
 */
public class ResourceManagementEntitySystem extends DisposableEntitySystem {

    private ImmutableArray<Entity> toBeRemoved;
    private Engine ashleyEngine;

    /**
     * Initialize ResourceManagementEntitySystem
     *
     * @param ashleyEngine      Ashley Engine
     */
    public ResourceManagementEntitySystem(Engine ashleyEngine){
        this.ashleyEngine = ashleyEngine;
    }

    /**
     * Load entities as they are added to the Ashley Engine
     *
     * @param engine
     */
    public void addedToEngine(Engine engine){
        toBeRemoved = engine.getEntitiesFor(Family.all(RemoveTagComponent.class).get());
    }

    /**
     * Remove all entities that have the RemoveTagComponent
     *
     * @param deltaTime
     */
    public void update(float deltaTime){
        for(Entity entity : toBeRemoved){
            entity.removeAll();
            ashleyEngine.removeEntity(entity);
        }
    }

    /**
     * Dispose the system
     */
    @Override
    public void dispose() {
        while(ashleyEngine.getEntities().size() > 0) {
            ImmutableArray<Entity> removeArray = ashleyEngine.getEntities();
            for (Entity entity : removeArray) {
                entity.removeAll();
                ashleyEngine.removeEntity(entity);
            }
        }
    }
}