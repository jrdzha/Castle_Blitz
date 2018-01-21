package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jaredzhao.castleblitz.components.graphics.FogOfWarComponent;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.graphics.VisibleComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;
import com.jaredzhao.castleblitz.components.map.TileComponent;
import com.jaredzhao.castleblitz.components.map.UpdateTileComponent;
import com.jaredzhao.castleblitz.components.mechanics.BattleMechanicsStatesComponent;
import com.jaredzhao.castleblitz.components.mechanics.CharacterPropertiesComponent;
import com.jaredzhao.castleblitz.components.mechanics.PositionComponent;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;
import com.jaredzhao.castleblitz.servers.GameServer;
import com.jaredzhao.castleblitz.utils.Console;

import java.util.ArrayList;

public class MapEntitySystem extends DisposableEntitySystem {

    private ImmutableArray<Entity> updateTileEntities, selectableCharacters, selectableTiles, visibleCharacters;

    private ComponentMapper<TileComponent> tileComponentComponentMapper = ComponentMapper.getFor(TileComponent.class);
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CharacterPropertiesComponent> characterPropertiesComponentComponentMapper = ComponentMapper.getFor(CharacterPropertiesComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);

    private Entity map;
    private FogOfWarComponent fogOfWarComponent;
    private BattleMechanicsStatesComponent battleMechanicsStatesComponent;

    private GameServer gameServer;

    /**
     * Initialize MapEntitySystem
     *
     * @param gameServer    Server used to provide gameplay data
     * @param map           Map to be used in gameplay
     * @param fogOfWar      Entity used to store fog of war data
     */
    public MapEntitySystem(GameServer gameServer, Entity map, Entity fogOfWar, Entity battleMechanics){
        this.gameServer = gameServer;
        this.map = map;
        this.fogOfWarComponent = fogOfWar.getComponent(FogOfWarComponent.class);
        this.battleMechanicsStatesComponent = battleMechanics.getComponent(BattleMechanicsStatesComponent.class);
    }

    /**
     * Load entities as they are added to the Ashley Engine
     *
     * @param engine
     */
    public void addedToEngine(Engine engine){
        updateTileEntities = engine.getEntitiesFor(Family.all(TileComponent.class, PositionComponent.class, UpdateTileComponent.class).get());
        selectableCharacters = engine.getEntitiesFor(Family.all(SelectableComponent.class, TileComponent.class, CharacterPropertiesComponent.class).get());
        selectableTiles = engine.getEntitiesFor(Family.all(SelectableComponent.class, TileComponent.class).get());
        visibleCharacters = engine.getEntitiesFor(Family.all(CharacterPropertiesComponent.class).get());
    }

    /**
     * Update map for new game console entries, update console for selected tiles and entities, update fog of war
     *
     * @param deltaTime
     */
    public void update(float deltaTime){ //Updates new map entities

        Console console = gameServer.getConsole();
        if (console.peekConsoleNewEntries() != null) {
            String[] nextEntry = console.peekConsoleNewEntries().split("\\.");

            if (nextEntry[0].equals("CLIENT")) {
                if (nextEntry[1].equals("MOVE")) {
                    int fromX = Integer.parseInt(nextEntry[2].split(",")[0]);
                    int fromY = Integer.parseInt(nextEntry[2].split(",")[1]);
                    int toX = Integer.parseInt(nextEntry[4].split(",")[0]);
                    int toY = Integer.parseInt(nextEntry[4].split(",")[1]);

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
            if(!battleMechanicsStatesComponent.attack && entity.getComponent(CharacterPropertiesComponent.class) == null && selectableComponent.isSelected){
                moveTo[0] = tileComponent.tileX;
                moveTo[1] = tileComponent.tileY;
            }
        }

        for(Entity entity : selectableCharacters){
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            if(selectableComponent.isSelected && moveTo[0] != -1){
                gameServer.getConsole().putConsoleNewEntries("CLIENT.MOVE." + tileComponent.tileX + "," + tileComponent.tileY + ".TO." + moveTo[0] + "," + moveTo[1]);
                gameServer.getConsole().putConsoleNewEntries("SERVER.MOVE." + tileComponent.tileX + "," + tileComponent.tileY + ".TO." + moveTo[0] + "," + moveTo[1]);
            }
        }

        boolean[][] playerViewMap = gameServer.retrieveViewMap();
        int[][] playerPositions = gameServer.retrieveTeamPositions();
        for(int i = 0; i < playerViewMap.length; i++){
            for(int j = 0; j < playerViewMap[0].length; j++){
                if(fogOfWarComponent.rawViewMap[i][j] == 2){
                    fogOfWarComponent.rawViewMap[i][j] = 1;
                }
                if(playerViewMap[i][j]){
                    fogOfWarComponent.rawViewMap[i][j] = 2;
                }
                // 0 for black, 1 for gray, 2 for visible
            }
        }

        for(int i = 0; i < playerViewMap.length; i++){
            for(int j = 0; j < playerViewMap[0].length; j++){
                if(fogOfWarComponent.rawViewMap[i][j] == 0){
                    fogOfWarComponent.viewMap[i][j] = 0;
                } else if(fogOfWarComponent.rawViewMap[i][j] == 1){
                    fogOfWarComponent.viewMap[i][j] = 1;
                } else if(fogOfWarComponent.rawViewMap[i][j] == 2){
                    double scale = 0;
                    for(int k = 0; k < playerPositions.length; k++){
                        scale += (1.5 / Math.pow(Math.pow((playerPositions[k][0] - i), 2) + Math.pow((playerPositions[k][1] - j), 2), 0.5));
                        fogOfWarComponent.viewMap[i][j] = Math.max(Math.min((int)(scale * 15.0 / 6.0), 4), 1);
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
            } else {
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

        for(Entity entity : selectableCharacters){
            TileComponent tileComponent = tileComponentComponentMapper.get(entity);
            CharacterPropertiesComponent characteristics = characterPropertiesComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
            ArrayList<int[]> possibleAttacks = new ArrayList<int[]>();
            //Find appropriate range
            if(selectableComponent.isSelected) {
                for (int x = characteristics.movementRange * -1; x <= characteristics.movementRange; x++) {
                    for (int y = characteristics.movementRange * -1; y <= characteristics.movementRange; y++) {
                        if ((Math.pow(Math.pow(x, 2) + Math.pow(y, 2), .5)) <= characteristics.movementRange) {
                            int[] availableSpot = {x + tileComponent.tileX, y + tileComponent.tileY};
                            Entity tileCheck = map.getComponent(MapComponent.class).mapEntities[0][availableSpot[0]][availableSpot[1]];
                            if(tileCheck != null) {
                                int type = Integer.parseInt(tileCheck.getComponent(TileComponent.class).type);
                                if ((type >= 0 && type <= 11) ||
                                        (type >= 0 && type <= 11) ||
                                        (type >= 21 && type <= 26) ||
                                        (type >= 28 && type <= 32) ||
                                        (type >= 42 && type <= 52) ||
                                        (type >= 63 && type <= 70) ||
                                        (type >= 84 && type <= 89) ||
                                        (type == 91)) {
                                    if(map.getComponent(MapComponent.class).mapEntities[1][availableSpot[0]][availableSpot[1]] == null) {
                                        possibleMoves.add(availableSpot);
                                    } else {
                                        CharacterPropertiesComponent characterPropertiesComponent = map.getComponent(MapComponent.class).mapEntities[1][availableSpot[0]][availableSpot[1]].getComponent(CharacterPropertiesComponent.class);
                                        if(characterPropertiesComponent != null && !characterPropertiesComponent.team.equals(characteristics.team)){
                                            possibleAttacks.add(availableSpot);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int[] origin = {tileComponent.tileX, tileComponent.tileY};

                characteristics.possibleMoves = filterPath(origin , characteristics.movementRange, possibleMoves);
                characteristics.possibleAttacks = filterRange(origin , characteristics.movementRange, possibleAttacks);
            }
        }
    }

    public ArrayList<int[]> filterPath(int[] origin, int range, ArrayList<int[]> possibleRange) {
        ArrayList<int[]> path = new ArrayList<int[]>();
        ArrayList<int[]> addList = new ArrayList<int[]>();
        path.add(origin);

        for (int j = 0; j < range; j++) {
            addList.removeAll(addList);
            for (int[] branch : path) {
                for (int[] check : possibleRange) {
                    if (branch[0] == check[0] && Math.abs(branch[1] - check[1]) == 1) {
                        addList.add(check);
                    } else if (branch[1] == check[1] && Math.abs(branch[0] - check[0]) == 1) {
                        addList.add(check);
                    }
                }
            }
            path.addAll(addList);
            possibleRange.removeAll(addList);
        }
        path.remove(origin);
        return path;
    }

    public ArrayList<int[]> filterRange(int[] origin, int range, ArrayList<int[]> possibleRange) {
        ArrayList<int[]> removeList = new ArrayList<int[]>();
        for(int[] location : possibleRange){
            if(location[0] != origin[0] && location[1] != origin[1]){
                removeList.add(location);
            }
            if(Math.abs(location[0] - origin[0]) > range || Math.abs(location[1] - origin[1]) > range){
                removeList.add(location);
            }
        }

        possibleRange.removeAll(removeList);

        ArrayList<int[]> rangeX = new ArrayList<int[]>();
        ArrayList<int[]> rangeY = new ArrayList<int[]>();

        for(int[] location : possibleRange){
            if(location[0] == origin[0]){
                rangeY.add(location);
            } else if(location[1] == origin[1]){
                rangeX.add(location);
            }
        }

        ArrayList<int[]> filteredRange = new ArrayList<int[]>();

        if(rangeX.size() != 0) {
            if (origin[0] - rangeX.get(0)[0] < 0) {
                filteredRange.add(rangeX.get(0));
            }
            if (origin[0] - rangeX.get(rangeX.size() - 1)[0] > 0) {
                filteredRange.add(rangeX.get(rangeX.size() - 1));
            }
        }
        if(rangeY.size() != 0) {
            if (origin[1] - rangeY.get(0)[1] < 0) {
                filteredRange.add(rangeY.get(0));
            }
            if (origin[1] - rangeY.get(rangeY.size() - 1)[1] > 0) {
                filteredRange.add(rangeY.get(rangeY.size() - 1));
            }
        }

        for(int i = 0; i < rangeX.size() - 1; i++){
            if(origin[0] - rangeX.get(i)[0] > 0 && origin[0] - rangeX.get(i + 1)[0] < 0) {
                filteredRange.add(rangeX.get(i));
                filteredRange.add(rangeX.get(i + 1));
            }
        }

        for(int i = 0; i < rangeY.size() - 1; i++){
            if(origin[1] - rangeY.get(i)[1] > 0 && origin[1] - rangeY.get(i + 1)[1] < 0) {
                filteredRange.add(rangeY.get(i));
                filteredRange.add(rangeY.get(i + 1));
            }
        }

        return filteredRange;
    }

    @Override
    public void dispose() {

    }
}
