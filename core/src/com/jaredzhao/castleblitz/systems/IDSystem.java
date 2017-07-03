package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.mechanics.AssignIDComponent;
import com.jaredzhao.castleblitz.components.mechanics.IDComponent;

public class IDSystem extends EntitySystem{

    private ImmutableArray<Entity> idEntities;

    private int currentID;

    private ComponentMapper<IDComponent> idComponentComponentMapper = ComponentMapper.getFor(IDComponent.class);

    public IDSystem(){
        currentID = 0;
    }

    public void reset() {

    }

    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine){
        idEntities = engine.getEntitiesFor(Family.all(AssignIDComponent.class, IDComponent.class).get());
    }

    public void update(float deltaTime){
        for(int i = 0; i < idEntities.size(); ++i){
            Entity entity = idEntities.get(i);
            idComponentComponentMapper.get(entity).id = currentID;
            currentID++;
            entity.remove(AssignIDComponent.class);
        }
    }
}