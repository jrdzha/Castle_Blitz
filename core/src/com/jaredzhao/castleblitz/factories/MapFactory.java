package com.jaredzhao.castleblitz.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.jaredzhao.castleblitz.components.map.MapComponent;

public class MapFactory {

    Engine ashleyEngine;

    EntityFactory entityFactory;

    public MapFactory(Engine ashleyEngine, EntityFactory entityFactory){
        this.entityFactory = entityFactory;
        this.ashleyEngine = ashleyEngine;
    }

    public Object[] loadMap(FileHandle level) { //Create map entity and load level data
        Entity map = entityFactory.createMap(); //Create map entity

        String levelInString = level.readString(); //Read level data
        levelInString = levelInString.replaceAll("\\r", "");
        levelInString = levelInString.replaceAll("\\n", "");

        int numLevels = Integer.parseInt(levelInString.substring(0, levelInString.indexOf('x'))); //Find size of map
        levelInString = levelInString.substring(levelInString.indexOf('x') + 1);
        int levelX = Integer.parseInt(levelInString.substring(0, levelInString.indexOf('x')));
        levelInString = levelInString.substring(levelInString.indexOf('x') + 1);
        int levelY = Integer.parseInt(levelInString.substring(0, levelInString.indexOf(';')));
        levelInString = levelInString.substring(levelInString.indexOf(';') + 1);
        String availableTracksString = levelInString.substring(0, levelInString.indexOf(';'));
        levelInString = levelInString.substring(levelInString.indexOf(';') + 1);
        String[] availableTracksArray = availableTracksString.split(","); //Array used to retrieve entity type data

        Entity[][][] mapEntities = new Entity[numLevels][levelX][levelY]; //Create appropriate size 2d entity array
        String[] levelInStringArray = levelInString.split(","); //Array used to retrieve entity type data

        for(int i = 0; i < mapEntities.length; i++) {
            for (int j = 0; j < mapEntities[i].length; j++) {
                for (int k = 0; k < mapEntities[i][j].length; k++) {

                    String entityType = levelInStringArray[j + ((mapEntities[i][j].length - k - 1) * mapEntities[i].length) + (i * mapEntities[i].length * mapEntities[i][j].length)]; //Entity type data retrieved from level data

                    if((entityType.contains("R") || entityType.contains("G") || entityType.contains("B") || entityType.contains("D") || entityType.contains("K")) && entityType.matches(".*\\d+.*")){
                        mapEntities[i][j][k] = entityFactory.createCharacter(entityType.substring(0, 1), j, k, Integer.parseInt(entityType.substring(1, 2))); //The team needs to be encoded somehow
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    } else if(entityType.contains("TO")){
                        mapEntities[i][j][k] = entityFactory.createTorch(j, k);
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    } else if(entityType.contains("CH") || entityType.contains("BV") || entityType.contains("BH") || entityType.contains("BA") || entityType.contains("SB") || entityType.contains("BB")){
                        mapEntities[i][j][k] = entityFactory.createProp(entityType, j, k);
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    } else if(entityType.contains("CA")){
                        mapEntities[i][j][k] = entityFactory.createCastle(j, k);
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                        for(int x = j - 1; x <= j + 1; x++){
                            for(int y = k; y <= k + 1; y++){
                                if(mapEntities[i][x][y] == null){
                                    mapEntities[i][x][y] = entityFactory.createInhibitor(x, y);
                                    ashleyEngine.addEntity(mapEntities[i][x][y]);
                                }
                            }
                        }
                    } else if(!entityType.contains("--")){
                        mapEntities[i][j][k] = entityFactory.createTile(j, k, Integer.parseInt(entityType), i); //Create tile entity
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    }
                }
            }
        }
        map.getComponent(MapComponent.class).mapEntities = mapEntities; //Store completed 2d entity array in map tiles

        Object[] levelData = new Object[2];
        levelData[0] = map;
        levelData[1] = availableTracksArray;
        return levelData;
    }
}