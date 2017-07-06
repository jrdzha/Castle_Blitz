package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.BattleMechanicsStatesComponent;
import com.jaredzhao.castleblitz.components.mechanics.CharacterPropertiesComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;

public class BattleMechanicsSystem extends EntitySystem{
    private ImmutableArray<Entity> selectedCharacters;

    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);

    private Entity map;

    private BattleMechanicsStatesComponent battleMechanicsStatesComponent;

    public BattleMechanicsSystem(Entity map, Entity battleMechanics){
        this.map = map;
        this.battleMechanicsStatesComponent = battleMechanics.getComponent(BattleMechanicsStatesComponent.class);
    }

    public void addedToEngine(Engine engine){
        selectedCharacters = engine.getEntitiesFor(Family.all(SelectableComponent.class, CharacterPropertiesComponent.class).get());
    }

    public void update(float deltaTime){
        for(Entity entity : selectedCharacters){
            CharacterPropertiesComponent characterPropertiesComponent = characterPropertiesComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

            if(battleMechanicsStatesComponent.move && selectableComponent.isSelected && !selectableComponent.removeSelection) {
                for (int[] position : characterPropertiesComponent.possibleMoves) {
                    Entity tile = map.getComponent(MapComponent.class).mapEntities[0][position[0]][position[1]];
                    if (tile != null && tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class) == null) {
                        tile.getComponent(HighlightComponent.class).highlight.add(new SelectableComponent());
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeX = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeY = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).name = "tile";
                    }
                }
            }
        }
    }
}