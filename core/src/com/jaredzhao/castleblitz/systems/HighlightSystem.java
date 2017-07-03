package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.AddHighlightComponent;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.graphics.VisibleComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.mechanics.CharacterPropertiesComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;
import com.jaredzhao.castleblitz.components.mechanics.UpdateHighlightComponent;
import com.jaredzhao.castleblitz.factories.EntityFactory;

public class HighlightSystem extends EntitySystem{

    private ImmutableArray<Entity> newHighlights, updatedEntities, selectedCharacters, highlights;

    private ComponentMapper<HighlightComponent> highlightComponentComponentMapper = ComponentMapper.getFor(HighlightComponent.class);
    private ComponentMapper<UpdateHighlightComponent> updateHighlightComponentComponentMapper = ComponentMapper.getFor(UpdateHighlightComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);

    private Engine ashleyEngine;

    private EntityFactory entityFactory;
    private Entity map;

    public HighlightSystem(Engine ashleyEngine, EntityFactory entityFactory, Entity map){
        this.ashleyEngine = ashleyEngine;
        this.entityFactory = entityFactory;
        this.map = map;
    }

    public void reset() {

    }

    public void addedToEngine(Engine engine){
        newHighlights = engine.getEntitiesFor(Family.all(HighlightComponent.class, AddHighlightComponent.class).get());
        updatedEntities = engine.getEntitiesFor(Family.all(HighlightComponent.class, UpdateHighlightComponent.class).get());
        selectedCharacters = engine.getEntitiesFor(Family.all(SelectableComponent.class, CharacterPropertiesComponent.class).get());
        highlights = engine.getEntitiesFor(Family.all(HighlightComponent.class, PositionComponent.class).get());
    }

    public void update(float deltaTime){
        for(int i = 0; i < highlights.size(); ++i){
            Entity entity = highlights.get(i);
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            PositionComponent positionComponent = positionComponentComponentMapper.get(entity);
            highlightComponent.highlight.getComponent(PositionComponent.class).x = positionComponent.x;
            highlightComponent.highlight.getComponent(PositionComponent.class).y = positionComponent.y;
        }

        for(int i = 0; i < updatedEntities.size(); ++i){
            Entity entity = updatedEntities.get(i);
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            CharacterPropertiesComponent characterPropertiesComponent = characterPropertiesComponentComponentMapper.get(entity);
            UpdateHighlightComponent updatedHighlightComponent = updateHighlightComponentComponentMapper.get(entity);

            ashleyEngine.removeEntity(highlightComponent.highlight);
            highlightComponent.highlight = entityFactory.createHighlight(characterPropertiesComponent.team - 1, updatedHighlightComponent.alpha, highlightComponent.highlight.getComponent(TileComponent.class).tileX, highlightComponent.highlight.getComponent(TileComponent.class).tileY);
            ashleyEngine.addEntity(highlightComponent.highlight);

            entity.remove(UpdateHighlightComponent.class);
        }

        for(int i = 0; i < newHighlights.size(); ++i){
            Entity entity = newHighlights.get(i);
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

        for(int i = 0; i < selectedCharacters.size(); ++i){
            Entity entity = selectedCharacters.get(i);
            CharacterPropertiesComponent characterPropertiesComponent = characterPropertiesComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

            if(selectableComponent.isSelected && !selectableComponent.removeSelection) {
                for (int[] position : characterPropertiesComponent.possibleMoves) {
                    Entity tile = map.getComponent(MapComponent.class).mapEntities[0][position[0]][position[1]];
                    if (tile != null && tile.getComponent(HighlightComponent.class).highlight.getComponent(VisibleComponent.class) == null) {
                        tile.getComponent(HighlightComponent.class).highlight.add(new VisibleComponent());
                        tile.getComponent(HighlightComponent.class).highlight.add(new SelectableComponent());
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeX = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeY = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).name = "character";
                    }
                }
            }
        }
    }
}