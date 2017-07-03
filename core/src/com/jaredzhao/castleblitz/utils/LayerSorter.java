package com.jaredzhao.castleblitz.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.LayerComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;

import java.util.ArrayList;

public class LayerSorter {

    private static ComponentMapper<LayerComponent> layerComponentComponentMapper = ComponentMapper.getFor(LayerComponent.class);
    private static ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);

    public static ArrayList<Entity> sortByLayers(ImmutableArray<Entity> sortables){
        int maxLayer = 0; //Find number of layers that need to be sorted
        for(int i = 0; i < sortables.size(); ++i) {
            Entity entity = sortables.get(i);
            LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
            if(layerComponent.layer > maxLayer){
                maxLayer = layerComponent.layer;
            }
        }

        ArrayList[] sortedByLayerSortables = new ArrayList[maxLayer + 1];

        for(int i = 0; i < sortedByLayerSortables.length; i++){
            sortedByLayerSortables[i] = new ArrayList<Entity>();
        }

        for(Entity entity : sortables){
            LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
            ((ArrayList<Entity>)sortedByLayerSortables[layerComponent.layer]).add(entity);
        }

        ArrayList<Entity> sortedSortables = new ArrayList<Entity>();

        for(ArrayList list : sortedByLayerSortables){
            while(!list.isEmpty()){
                //find max
                float maxY = -1f;
                for(Object object : list){
                    PositionComponent positionComponent = positionComponentComponentMapper.get((Entity)object);
                    if(positionComponent.y > maxY){
                        maxY = positionComponent.y;
                    }
                }
                //add max to sorted sortables
                for(int i = 0; i < list.size(); i++){
                    PositionComponent positionComponent = positionComponentComponentMapper.get((Entity)list.get(i));
                    if(positionComponent.y == maxY){
                        sortedSortables.add((Entity)list.get(i));
                        list.remove(list.get(i));
                    }
                }
            }
        }

        return sortedSortables;

    }

}
