package com.jaredzhao.castleblitz.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.LayerComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;

import java.util.Map;
import java.util.TreeMap;

public class LayerSorter {

    private ComponentMapper<LayerComponent> layerComponentComponentMapper = ComponentMapper.getFor(LayerComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private Map<Long, Entity> sortedMapLayers;
    private int lastSortablesSize = -1;

    public Map<Long, Entity> sortByLayers(ImmutableArray<Entity> sortables){

        boolean didChange = false;
        for(Entity entity : sortables){
            LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
            layerComponent.sortCode = generateSortCode(entity);
            if(layerComponent.sortCode != layerComponent.lastSortCode){
                didChange = true;
            }
        }

        if(didChange || lastSortablesSize != sortables.size()) {
            sortedMapLayers = new TreeMap<Long, Entity>();

            for (Entity entity : sortables) {
                LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
                layerComponent.lastSortCode = layerComponent.sortCode;
                sortedMapLayers.put(new Long(layerComponent.sortCode), entity);
            }

            lastSortablesSize = sortables.size();
        }

        return sortedMapLayers;
    }

    public long generateSortCode(Entity entity){
        LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
        PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
        return layerComponent.layer * (long)Math.pow(10, 14) + lengthResizer((long)positionComponent.x, 7) * (long)Math.pow(10, 7) + lengthResizer((long)positionComponent.y, 7);
    }

    public long lengthResizer(long base, int length){
        return 5 * (long)Math.pow(10, length - 1) + base;
    }
}