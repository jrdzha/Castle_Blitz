package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.map.UpdateTileComponent;
import com.jaredzhao.castleblitz.components.mechanics.CharacterPropertiesComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;

import java.util.ArrayList;

public class MapSystem extends EntitySystem {

    private ImmutableArray<Entity> tileEntities, selectableEntities, selectableTiles;

    private ComponentMapper<TileComponent> tileComponentComponentMapper = ComponentMapper.getFor(TileComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);

    private Entity map;

    public MapSystem(Entity map){
        this.map = map;
    }

    public void reset() {

    }

    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine){
        tileEntities = engine.getEntitiesFor(Family.all(TileComponent.class, PositionComponent.class, UpdateTileComponent.class).get());
        selectableEntities = engine.getEntitiesFor(Family.all(SelectableComponent.class, TileComponent.class, CharacterPropertiesComponent.class).get());
        selectableTiles = engine.getEntitiesFor(Family.all(SelectableComponent.class, TileComponent.class).get());
    }

    public void update(float deltaTime){ //Updates new map entities

        int[] moveTo = {-1, -1};

        for(int i = 0; i < selectableTiles.size(); ++i){
            Entity entity = selectableTiles.get(i);
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            if(entity.getComponent(CharacterPropertiesComponent.class) == null && selectableComponent.isSelected){
                moveTo[0] = tileComponent.tileX;
                moveTo[1] = tileComponent.tileY;
            }
        }

        for(int i = 0; i < selectableEntities.size(); ++i) {
            Entity entity = selectableEntities.get(i);
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            if(selectableComponent.isSelected && moveTo[0] != -1){
                entity.add(new UpdateTileComponent());
                map.getComponent(MapComponent.class).mapEntities[1][tileComponent.tileX][tileComponent.tileY] = null;
                tileComponent.tileX = moveTo[0];
                tileComponent.tileY = moveTo[1];
                map.getComponent(MapComponent.class).mapEntities[1][tileComponent.tileX][tileComponent.tileY] = entity;
            }
        }

        for(int i = 0; i < tileEntities.size(); ++i){
            Entity entity = tileEntities.get(i);
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            PositionComponent position = positionComponentComponentMapper.get(entity);
            position.x = tileComponent.tileX * 16;
            position.y = tileComponent.tileY * 16;
            entity.remove(UpdateTileComponent.class);
        }

        for(int i = 0; i < selectableEntities.size(); ++i) {
            Entity entity = selectableEntities.get(i);
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            CharacterPropertiesComponent characteristics = characterPropertiesComponentComponentMapper.get(entity);
            ArrayList<int[]> possibleMoves1 = new ArrayList<int[]>();
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            //Find appropriate range
            if(selectableComponent.isSelected) {
                for (int x = characteristics.movementRange * -1; x <= characteristics.movementRange; x++) {
                    for (int y = characteristics.movementRange * -1; y <= characteristics.movementRange; y++) {
                        if ((Math.pow(Math.pow(x, 2) + Math.pow(y, 2), .5)) <= characteristics.movementRange) {
                            int[] availableSpot = {x + tileComponent.tileX, y + tileComponent.tileY};
                            Entity tileCheck = map.getComponent(MapComponent.class).mapEntities[0][availableSpot[0]][availableSpot[1]];
                            if (tileCheck != null && map.getComponent(MapComponent.class).mapEntities[1][availableSpot[0]][availableSpot[1]] == null &&
                                    ((tileCheck.getComponent(TileComponent.class).type >= 0 && tileCheck.getComponent(TileComponent.class).type <= 11) ||
                                            (tileCheck.getComponent(TileComponent.class).type >= 0 && tileCheck.getComponent(TileComponent.class).type <= 11) ||
                                            (tileCheck.getComponent(TileComponent.class).type >= 21 && tileCheck.getComponent(TileComponent.class).type <= 26) ||
                                            (tileCheck.getComponent(TileComponent.class).type >= 28 && tileCheck.getComponent(TileComponent.class).type <= 32) ||
                                            (tileCheck.getComponent(TileComponent.class).type >= 42 && tileCheck.getComponent(TileComponent.class).type <= 52) ||
                                            (tileCheck.getComponent(TileComponent.class).type >= 63 && tileCheck.getComponent(TileComponent.class).type <= 70) ||
                                            (tileCheck.getComponent(TileComponent.class).type >= 84 && tileCheck.getComponent(TileComponent.class).type <= 89) ||
                                            (tileCheck.getComponent(TileComponent.class).type == 91)
                                    )) {
                                possibleMoves1.add(availableSpot);
                            }
                        }
                    }
                }

                ArrayList<int[]> possibleMoves2 = new ArrayList<int[]>();
                ArrayList<int[]> addList = new ArrayList<int[]>();
                int[] originalLocation = {tileComponent.tileX, tileComponent.tileY};
                possibleMoves2.add(originalLocation);

                for (int j = 0; j < characteristics.movementRange; j++) {
                    addList.removeAll(addList);
                    for (int[] branch : possibleMoves2) {
                        for (int[] check : possibleMoves1) {
                            if (branch[0] == check[0] && Math.abs(branch[1] - check[1]) == 1) {
                                addList.add(check);
                            } else if (branch[1] == check[1] && Math.abs(branch[0] - check[0]) == 1) {
                                addList.add(check);
                            }
                        }
                    }
                    possibleMoves2.addAll(addList);
                    possibleMoves1.removeAll(addList);
                }
                possibleMoves2.remove(originalLocation);

                characteristics.possibleMoves = possibleMoves2;
            }
        }
    }
}
