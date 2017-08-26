package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.map.UpdateTileComponent;
import com.jaredzhao.castleblitz.components.mechanics.CharacterPropertiesComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;
import com.jaredzhao.castleblitz.servers.GameServer;
import com.jaredzhao.castleblitz.utils.Console;

import java.util.ArrayList;

public class MapSystem extends EntitySystem {

    private ImmutableArray<Entity> updateTileEntities, selectableEntities, selectableTiles, visibleCharacters;

    private ComponentMapper<TileComponent> tileComponentComponentMapper = ComponentMapper.getFor(TileComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);

    private Entity map;
    private FogOfWarComponent fogOfWarComponent;

    private GameServer gameServer;

    public MapSystem(GameServer gameServer, Entity map, Entity fogOfWar){
        this.gameServer = gameServer;
        this.map = map;
        this.fogOfWarComponent = fogOfWar.getComponent(FogOfWarComponent.class);
    }

    public void addedToEngine(Engine engine){
        updateTileEntities = engine.getEntitiesFor(Family.all(TileComponent.class, PositionComponent.class, UpdateTileComponent.class).get());
        selectableEntities = engine.getEntitiesFor(Family.all(SelectableComponent.class, TileComponent.class, CharacterPropertiesComponent.class).get());
        selectableTiles = engine.getEntitiesFor(Family.all(SelectableComponent.class, TileComponent.class).get());
        visibleCharacters = engine.getEntitiesFor(Family.all(CharacterPropertiesComponent.class).get());
    }

    public void update(float deltaTime){ //Updates new map entities

        Console console = gameServer.getConsole();
        if (console.peekConsoleNewEntries() != null) {
            String nextEntry = console.peekConsoleNewEntries();

            if (nextEntry.substring(0, nextEntry.indexOf('.')).equals("client")) {
                nextEntry = nextEntry.substring(nextEntry.indexOf('.') + 1);
                if (nextEntry.substring(0, nextEntry.indexOf('.')).equals("move")) {
                    nextEntry = nextEntry.substring(nextEntry.indexOf('.') + 1);
                    int fromX = Integer.parseInt(nextEntry.substring(0, nextEntry.indexOf(',')));
                    nextEntry = nextEntry.substring(nextEntry.indexOf(',') + 1);
                    int fromY = Integer.parseInt(nextEntry.substring(0, nextEntry.indexOf('.')));
                    nextEntry = nextEntry.substring(nextEntry.indexOf('.') + 1);
                    nextEntry = nextEntry.substring(nextEntry.indexOf('.') + 1);
                    int toX = Integer.parseInt(nextEntry.substring(0, nextEntry.indexOf(',')));
                    nextEntry = nextEntry.substring(nextEntry.indexOf(',') + 1);
                    int toY = Integer.parseInt(nextEntry.substring(0, nextEntry.indexOf('.')));

                    map.getComponent(MapComponent.class).mapEntities[1][toX][toY] = map.getComponent(MapComponent.class).mapEntities[1][fromX][fromY];
                    map.getComponent(MapComponent.class).mapEntities[1][fromX][fromY] = null;
                    map.getComponent(MapComponent.class).mapEntities[1][toX][toY].add(new UpdateTileComponent());
                    map.getComponent(MapComponent.class).mapEntities[1][toX][toY].getComponent(TileComponent.class).tileX = toX;
                    map.getComponent(MapComponent.class).mapEntities[1][toX][toY].getComponent(TileComponent.class).tileY = toY;
                    console.pollConsoleNewEntries();
                }
            }
        }

        int[] moveTo = {-1, -1};

        for(Entity entity : selectableTiles){
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            if(entity.getComponent(CharacterPropertiesComponent.class) == null && selectableComponent.isSelected){
                moveTo[0] = tileComponent.tileX;
                moveTo[1] = tileComponent.tileY;
            }
        }

        for(Entity entity : selectableEntities){
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            if(selectableComponent.isSelected && moveTo[0] != -1){
                gameServer.getConsole().putConsoleNewEntries("client.move." + tileComponent.tileX + "," + tileComponent.tileY + ".to." + moveTo[0] + "," + moveTo[1] + ".");
                gameServer.getConsole().putConsoleNewEntries("server.move." + tileComponent.tileX + "," + tileComponent.tileY + ".to." + moveTo[0] + "," + moveTo[1] + ".");
            }
        }
        boolean[][] playerViewMap = gameServer.retrieveViewMap();
        for(int i = 0; i < playerViewMap.length; i++){
            for(int j = 0; j < playerViewMap[0].length; j++){
                if(fogOfWarComponent.rawViewMap[i][j] == 2){
                    fogOfWarComponent.rawViewMap[i][j] = 1;
                }
                if(playerViewMap[i][j]){
                    fogOfWarComponent.rawViewMap[i][j] = 2;
                }
            }
        }
        for(int i = 0; i < playerViewMap.length; i++){
            for(int j = 0; j < playerViewMap[0].length; j++){

                for(int k = 0; k <= 18; k++){
                    fogOfWarComponent.viewMap[i][j].remove(k);
                }

                fogOfWarComponent.viewMap[i][j].add(fogOfWarComponent.rawViewMap[i][j]);

                if(i > 0 && i < playerViewMap.length - 1 && j > 0 && j < playerViewMap[0].length - 1){
                    if(fogOfWarComponent.rawViewMap[i][j] != 0) {
                        if (fogOfWarComponent.rawViewMap[i - 1][j] == 0 && fogOfWarComponent.rawViewMap[i][j - 1] == 0) {
                            fogOfWarComponent.viewMap[i][j].add(7);
                        }
                        if (fogOfWarComponent.rawViewMap[i - 1][j] == 0 && fogOfWarComponent.rawViewMap[i][j + 1] == 0) {
                            fogOfWarComponent.viewMap[i][j].add(8);
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] == 0 && fogOfWarComponent.rawViewMap[i][j - 1] == 0) {
                            fogOfWarComponent.viewMap[i][j].add(10);
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] == 0 && fogOfWarComponent.rawViewMap[i][j + 1] == 0) {
                            fogOfWarComponent.viewMap[i][j].add(9);
                        }
                    }
                    if(fogOfWarComponent.rawViewMap[i][j] == 2) {
                        if (fogOfWarComponent.rawViewMap[i - 1][j] == 1 && fogOfWarComponent.rawViewMap[i][j - 1] == 1) {
                            fogOfWarComponent.viewMap[i][j].add(3);
                        }
                        if (fogOfWarComponent.rawViewMap[i - 1][j] == 1 && fogOfWarComponent.rawViewMap[i][j + 1] == 1) {
                            fogOfWarComponent.viewMap[i][j].add(4);
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] == 1 && fogOfWarComponent.rawViewMap[i][j - 1] == 1) {
                            fogOfWarComponent.viewMap[i][j].add(6);
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] == 1 && fogOfWarComponent.rawViewMap[i][j + 1] == 1) {
                            fogOfWarComponent.viewMap[i][j].add(5);
                        }
                    }
                    if(fogOfWarComponent.rawViewMap[i][j] == 0) {
                        boolean didMakeChange = false;
                        if (fogOfWarComponent.rawViewMap[i - 1][j] != 0 && fogOfWarComponent.rawViewMap[i][j - 1] != 0) {
                            fogOfWarComponent.viewMap[i][j].add(17);
                            fogOfWarComponent.viewMap[i][j].remove(0);
                            didMakeChange = true;
                        }
                        if (fogOfWarComponent.rawViewMap[i - 1][j] != 0 && fogOfWarComponent.rawViewMap[i][j + 1] != 0) {
                            fogOfWarComponent.viewMap[i][j].add(18);
                            fogOfWarComponent.viewMap[i][j].remove(0);
                            didMakeChange = true;
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] != 0 && fogOfWarComponent.rawViewMap[i][j - 1] != 0) {
                            fogOfWarComponent.viewMap[i][j].add(16);
                            fogOfWarComponent.viewMap[i][j].remove(0);
                            didMakeChange = true;
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] != 0 && fogOfWarComponent.rawViewMap[i][j + 1] != 0) {
                            fogOfWarComponent.viewMap[i][j].add(15);
                            fogOfWarComponent.viewMap[i][j].remove(0);
                            didMakeChange = true;
                        }
                        if(didMakeChange){
                            if (fogOfWarComponent.rawViewMap[i + 1][j] == 1) {
                                fogOfWarComponent.viewMap[i][j].add(1);
                            }
                            if (fogOfWarComponent.rawViewMap[i - 1][j] == 1) {
                                fogOfWarComponent.viewMap[i][j].add(1);
                            }
                            if (fogOfWarComponent.rawViewMap[i][j + 1] == 1) {
                                fogOfWarComponent.viewMap[i][j].add(1);
                            }
                            if (fogOfWarComponent.rawViewMap[i][j - 1] == 1) {
                                fogOfWarComponent.viewMap[i][j].add(1);
                            }
                        }
                    }
                    if(fogOfWarComponent.rawViewMap[i][j] == 1) {
                        if (fogOfWarComponent.rawViewMap[i - 1][j] == 2 && fogOfWarComponent.rawViewMap[i][j - 1] == 2) {
                            fogOfWarComponent.viewMap[i][j].add(13);
                            fogOfWarComponent.viewMap[i][j].remove(1);
                        }
                        if (fogOfWarComponent.rawViewMap[i - 1][j] == 2 && fogOfWarComponent.rawViewMap[i][j + 1] == 2) {
                            fogOfWarComponent.viewMap[i][j].add(14);
                            fogOfWarComponent.viewMap[i][j].remove(1);
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] == 2 && fogOfWarComponent.rawViewMap[i][j - 1] == 2) {
                            fogOfWarComponent.viewMap[i][j].add(12);
                            fogOfWarComponent.viewMap[i][j].remove(1);
                        }
                        if (fogOfWarComponent.rawViewMap[i + 1][j] == 2 && fogOfWarComponent.rawViewMap[i][j + 1] == 2) {
                            fogOfWarComponent.viewMap[i][j].add(11);
                            fogOfWarComponent.viewMap[i][j].remove(1);
                        }
                    }
                }
            }
        }

        for(Entity entity : visibleCharacters){
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            if(playerViewMap[tileComponent.tileX][tileComponent.tileY]){
                if(entity.getComponent(VisibleComponent.class) == null) {
                    entity.add(new VisibleComponent());
                    entity.getComponent(HighlightComponent.class).highlight.add(new VisibleComponent());
                }
            }
            if(!playerViewMap[tileComponent.tileX][tileComponent.tileY]){
                if(entity.getComponent(VisibleComponent.class) != null) {
                    entity.remove(VisibleComponent.class);
                    entity.getComponent(HighlightComponent.class).highlight.remove(VisibleComponent.class);
                }
            }
        }

        for(Entity entity : updateTileEntities){
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            PositionComponent position = positionComponentComponentMapper.get(entity);
            position.x = tileComponent.tileX * 16;
            position.y = tileComponent.tileY * 16;
            entity.remove(UpdateTileComponent.class);
        }

        for(Entity entity : selectableEntities){
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
                            if(tileCheck != null && map.getComponent(MapComponent.class).mapEntities[1][availableSpot[0]][availableSpot[1]] == null) {
                                int type = Integer.parseInt(tileCheck.getComponent(TileComponent.class).type);
                                if ((type >= 0 && type <= 11) ||
                                        (type >= 0 && type <= 11) ||
                                        (type >= 21 && type <= 26) ||
                                        (type >= 28 && type <= 32) ||
                                        (type >= 42 && type <= 52) ||
                                        (type >= 63 && type <= 70) ||
                                        (type >= 84 && type <= 89) ||
                                        (type == 91)) {
                                    possibleMoves1.add(availableSpot);
                                }
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

    public void dispose() {

    }
}
