package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.AnimationComponent;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.graphics.VisibleComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.mechanics.BattleMechanicsStatesComponent;
import com.jaredzhao.castleblitz.components.mechanics.CharacterPropertiesComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;
import com.jaredzhao.castleblitz.servers.GameServer;
import com.jaredzhao.castleblitz.utils.Console;

public class BattleMechanicsEntitySystem extends DisposableEntitySystem {
    private ImmutableArray<Entity> selectedCharacters;

    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);

    private Entity map;

    private BattleMechanicsStatesComponent battleMechanicsStatesComponent;
    private GameServer gameServer;

    public BattleMechanicsEntitySystem(Entity map, GameServer gameServer, Entity battleMechanics){
        this.map = map;
        this.gameServer = gameServer;
        this.battleMechanicsStatesComponent = battleMechanics.getComponent(BattleMechanicsStatesComponent.class);
    }

    public void addedToEngine(Engine engine){
        selectedCharacters = engine.getEntitiesFor(Family.all(SelectableComponent.class, CharacterPropertiesComponent.class).get());
    }

    public void update(float deltaTime) {
        Console console = gameServer.getConsole();
        if (console != null && console.peekConsoleNewEntries() != null) {
            String nextEntry = console.peekConsoleNewEntries();
            if (nextEntry.equals("CLIENT.TURN")) {
                battleMechanicsStatesComponent.isMyTurn = true;
                console.pollConsoleNewEntries();
            }
        }

        if (!battleMechanicsStatesComponent.move && !battleMechanicsStatesComponent.attack) {
            for (Entity[] column : map.getComponent(MapComponent.class).mapEntities[0]) {
                for (Entity tile : column) {
                    if (tile != null && tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class) != null) {
                        tile.getComponent(HighlightComponent.class).highlight.remove(VisibleComponent.class);
                        tile.getComponent(HighlightComponent.class).highlight.remove(SelectableComponent.class);
                    }
                }
            }
        }

        for (Entity entity : selectedCharacters) {
            CharacterPropertiesComponent characterPropertiesComponent = characterPropertiesComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);

            if (battleMechanicsStatesComponent.move && selectableComponent.isSelected && !selectableComponent.removeSelection) {
                for (int[] position : characterPropertiesComponent.possibleMoves) {
                    Entity tile = map.getComponent(MapComponent.class).mapEntities[0][position[0]][position[1]];
                    if (tile != null && tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class) == null) {
                        tile.getComponent(HighlightComponent.class).highlight.add(new SelectableComponent());
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeX = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeY = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).name = "tile";

                        tile.getComponent(HighlightComponent.class).highlight.add(new VisibleComponent());
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(AnimationComponent.class).currentFrame = 0;
                    }
                }
            }

            if (battleMechanicsStatesComponent.attack && selectableComponent.isSelected && !selectableComponent.removeSelection) {
                for (int[] position : characterPropertiesComponent.possibleAttacks) {
                    Entity tile = map.getComponent(MapComponent.class).mapEntities[0][position[0]][position[1]];
                    if (tile != null && tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class) == null) {
                        tile.getComponent(HighlightComponent.class).highlight.add(new SelectableComponent());
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeX = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).sizeY = 16;
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(SelectableComponent.class).name = "tile";

                        tile.getComponent(HighlightComponent.class).highlight.add(new VisibleComponent());
                        tile.getComponent(HighlightComponent.class).highlight.getComponent(AnimationComponent.class).currentFrame = 0;
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {

    }
}