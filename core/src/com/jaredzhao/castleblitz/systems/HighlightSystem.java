package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.*;
import com.jaredzhao.castleblitz.factories.EntityFactory;

public class HighlightSystem extends EntitySystem{

    private ImmutableArray<Entity> newHighlights, selectedCharacters, highlights;

    private ComponentMapper<HighlightComponent> highlightComponentComponentMapper = ComponentMapper.getFor(HighlightComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);

    private Engine ashleyEngine;

    private Entity map;

    private BattleMechanicsStatesComponent battleMechanicsStatesComponent;

    public HighlightSystem(Engine ashleyEngine, Entity map, Entity battleMechanics){
        this.ashleyEngine = ashleyEngine;
        this.map = map;
        this.battleMechanicsStatesComponent = battleMechanics.getComponent(BattleMechanicsStatesComponent.class);
    }

    public void addedToEngine(Engine engine){
        newHighlights = engine.getEntitiesFor(Family.all(HighlightComponent.class, AddHighlightComponent.class).get());
        selectedCharacters = engine.getEntitiesFor(Family.all(SelectableComponent.class, CharacterPropertiesComponent.class).get());
        highlights = engine.getEntitiesFor(Family.all(HighlightComponent.class, PositionComponent.class).get());
    }

    public void update(float deltaTime){
        for(Entity entity : highlights){
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
            highlightComponent.highlight.getComponent(PositionComponent.class).x = positionComponent.x;
            highlightComponent.highlight.getComponent(PositionComponent.class).y = positionComponent.y;
        }

        for(Entity entity : newHighlights){
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            ashleyEngine.addEntity(highlightComponent.highlight);
            entity.remove(AddHighlightComponent.class);
        }

        for (Entity[] column : map.getComponent(MapComponent.class).mapEntities[0]) {
            for(Entity tile : column) {
                if(tile != null) {
                    tile.getComponent(HighlightComponent.class).highlight.remove(VisibleComponent.class);
                    tile.getComponent(HighlightComponent.class).highlight.remove(SelectableComponent.class);
                }
            }
        }

        for(Entity entity : selectedCharacters){
            CharacterPropertiesComponent characterPropertiesComponent = characterPropertiesComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

            if(battleMechanicsStatesComponent.move && selectableComponent.isSelected && !selectableComponent.removeSelection) {
                for (int[] position : characterPropertiesComponent.possibleMoves) {
                    Entity tile = map.getComponent(MapComponent.class).mapEntities[0][position[0]][position[1]];
                    if (tile != null && tile.getComponent(HighlightComponent.class).highlight.getComponent(VisibleComponent.class) == null) {
                        tile.getComponent(HighlightComponent.class).highlight.add(new VisibleComponent());
                    }
                }
            }
        }
    }

    public void dispose() {

    }
}