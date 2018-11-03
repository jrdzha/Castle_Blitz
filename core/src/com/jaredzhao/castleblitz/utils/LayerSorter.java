package com.jaredzhao.castleblitz.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.jaredzhao.castleblitz.components.graphics.LayerComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility for sorting entities into correct rendering order
 */
public class LayerSorter {

    private ComponentMapper<LayerComponent> layerComponentComponentMapper = ComponentMapper.getFor(LayerComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ArrayList<SortedWrapper> sortedWrappers;
    private int lastSortablesSize = -1;
    Map<Long, Entity> sortedMapLayers;
    private long height;

    private class SortedWrapper {

        private ImmutableArray<Entity> sortables;
        private Map<Long, Entity> sorted;

        private SortedWrapper(ImmutableArray<Entity> sortables, Map<Long, Entity> sorted) {
            this.sortables = sortables;
            this.sorted = sorted;
        }

    }

    public LayerSorter(int height) {
        this.height = height;
        this.sortedWrappers = new ArrayList<SortedWrapper>();
        this.sortedMapLayers = new TreeMap<Long, Entity>();
    }

    /**
     * Returns correctly sorted map of entities
     *
     * @param sortables Immutable ArrayList of entities to be sorted
     * @return Map contained properly sorted entities
     */
//    public Map<Long, Entity> sortByLayers(ImmutableArray<Entity> sortables){
//        boolean contains = false;
//        Map<Long, Entity> sortedMapLayers = null;
//        for(SortedWrapper sortedWrapper : sortedWrappers) {
//            if(sortedWrapper.sortables == sortables) {
//                sortedMapLayers = sortedWrapper.sorted;
//                contains = true;
//            }
//        }
//
//        boolean sortOK = true;
//        for(Entity entity : sortables){
//            LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
//            long sortCode = generateSortCode(entity);
//            if(layerComponent.sortCode != sortCode){
//                layerComponent.sortCode = sortCode;
//                sortOK = false;
//            }
//        }
//
//        if(!sortOK || !contains) {
//            sortedMapLayers = generateNewSort(sortables);
//        }
//
//        if(!contains) {
//            sortedWrappers.add(new SortedWrapper(sortables, sortedMapLayers));
//        }
//
//        return sortedMapLayers;
//    }
    public Map<Long, Entity> sortByLayers(ImmutableArray<Entity> sortables) {
        sortedMapLayers.clear();

        for (Entity entity : sortables) {
            LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
            layerComponent.sortCode = generateSortCode(entity);
            sortedMapLayers.put(new Long(layerComponent.sortCode), entity);
        }

        return sortedMapLayers;
    }

    /**
     * Generate sort code to be used as a key for a map
     *
     * @param entity Entity for which the code will be generated
     * @return Long code
     */
    public long generateSortCode(Entity entity) {
        LayerComponent layerComponent = layerComponentComponentMapper.get(entity);
        PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
        return layerComponent.layer * (long) Math.pow(10, 14) + lengthResizer((long) positionComponent.x, 7) + lengthResizer(this.height - (long) positionComponent.y, 7) * (long) Math.pow(10, 7);
    }

    /**
     * Properly formats long
     *
     * @param base   Original long
     * @param length Desired length of long
     * @return Adjusted length long
     */
    public long lengthResizer(long base, int length) {
        return 5 * (long) Math.pow(10, length - 1) + base;
    }
}