package com.jaredzhao.castleblitz.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.jaredzhao.castleblitz.components.graphics.HighlightComponent;
import com.jaredzhao.castleblitz.components.map.MapComponent;

public class MapFactory {

    private Engine ashleyEngine;
    private EntityFactory entityFactory;

    private String[][][] rawMap;
    private Entity map;

    public MapFactory(Engine ashleyEngine, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.ashleyEngine = ashleyEngine;
    }

    public Entity loadMap(String[][][] rawMap) { //Create map entity and load level data

        Entity map = entityFactory.createMap(); //Create map entity

        Entity[][][] mapEntities = new Entity[rawMap.length][rawMap[0].length][rawMap[0][0].length]; //Create appropriate size 3d entity array

        for (int i = 0; i < mapEntities.length; i++) {
            for (int j = 0; j < mapEntities[i].length; j++) {
                for (int k = 0; k < mapEntities[i][j].length; k++) {

                    String entityType = rawMap[i][j][k];
                    String entityTypeFirstCharacter = entityType.substring(0, 1);
                    String entityTypeSecondCharacter = entityType.substring(1, 2);


                    if (entityType.contains("TO")) {
                        mapEntities[i][j][k] = entityFactory.createTorch(j, k);
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    } else if (entityType.contains("CH") || entityType.contains("BV") || entityType.contains("BH") || entityType.contains("BA") || entityType.contains("SC") || entityType.contains("LC")) {
                        mapEntities[i][j][k] = entityFactory.createProp(entityType, j, k);
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    } else if (entityType.contains("CA")) {
                        mapEntities[i][j][k] = entityFactory.createCastle(j, k);
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                        for (int x = j - 1; x <= j + 1; x++) {
                            for (int y = k; y <= k + 1; y++) {
                                if (mapEntities[i][x][y] == null) {
                                    mapEntities[i][x][y] = entityFactory.createInhibitor(x, y);
                                    ashleyEngine.addEntity(mapEntities[i][x][y]);
                                }
                            }
                        }
                    } else if (entityTypeFirstCharacter.contains("R") || entityTypeFirstCharacter.contains("G") || entityTypeFirstCharacter.contains("B") || entityTypeFirstCharacter.contains("D") || entityTypeFirstCharacter.contains("K")) {
                        mapEntities[i][j][k] = entityFactory.createCharacter(entityTypeFirstCharacter, j, k, entityTypeSecondCharacter);
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    } else if (!entityType.contains("--")) {
                        mapEntities[i][j][k] = entityFactory.createTile(j, k, Integer.parseInt(entityType), i); //Create tile entity
                        ashleyEngine.addEntity(mapEntities[i][j][k]); //Add new tile entity to ashelyEngine
                    }
                }
            }
        }
        map.getComponent(MapComponent.class).mapEntities = mapEntities; //Store completed 2d entity array in map tiles

        this.rawMap = rawMap;
        this.map = map;

        return map;
    }

    public void updateMap(String[][][] rawMap) { //Create map entity and load level data

        Entity[][][] mapEntities = map.getComponent(MapComponent.class).mapEntities; //Create appropriate size 3d entity array

        for (int j = 0; j < mapEntities[0].length; j++) {
            for (int k = 0; k < mapEntities[0][0].length; k++) {

                if (this.rawMap[0][j][k].equals("--") && !rawMap[0][j][k].equals("--")) {
                    this.rawMap[0][j][k] = rawMap[0][j][k];
                    mapEntities[0][j][k] = entityFactory.createTile(j, k, Integer.parseInt(rawMap[0][j][k]), 0); //Create tile entity
                    ashleyEngine.addEntity(mapEntities[0][j][k]); //Add new tile entity to ashelyEngine
                }

                if (rawMap[1][j][k].equals("--") && !this.rawMap[1][j][k].equals("--")) {
                    ashleyEngine.removeEntity(mapEntities[1][j][k].getComponent(HighlightComponent.class).highlight);
                    ashleyEngine.removeEntity(mapEntities[1][j][k]);
                    mapEntities[1][j][k] = null;
                    this.rawMap[1][j][k] = "--";
                }

                if (!this.rawMap[1][j][k].equals(rawMap[1][j][k])) {

                    String entityType = rawMap[1][j][k];
                    String entityTypeFirstCharacter = entityType.substring(0, 1);
                    String entityTypeSecondCharacter = entityType.substring(1, 2);

                    if (!rawMap[1][j][k].equals("--") && this.rawMap[1][j][k].equals("--")) {
                        if (entityType.contains("TO")) {
                            mapEntities[1][j][k] = entityFactory.createTorch(j, k);
                            ashleyEngine.addEntity(mapEntities[1][j][k]); //Add new tile entity to ashelyEngine
                        } else if (entityType.contains("CH") || entityType.contains("BV") || entityType.contains("BH") || entityType.contains("BA") || entityType.contains("SC") || entityType.contains("LC")) {
                            mapEntities[1][j][k] = entityFactory.createProp(entityType, j, k);
                            ashleyEngine.addEntity(mapEntities[1][j][k]); //Add new tile entity to ashelyEngine
                        } else if (entityType.contains("CA")) {
                            mapEntities[1][j][k] = entityFactory.createCastle(j, k);
                            ashleyEngine.addEntity(mapEntities[1][j][k]); //Add new tile entity to ashelyEngine
                            for (int x = j - 1; x <= j + 1; x++) {
                                for (int y = k; y <= k + 1; y++) {
                                    if (mapEntities[1][x][y] == null) {
                                        mapEntities[1][x][y] = entityFactory.createInhibitor(x, y);
                                        ashleyEngine.addEntity(mapEntities[1][x][y]);
                                    }
                                }
                            }
                        } else if (entityTypeFirstCharacter.contains("R") || entityTypeFirstCharacter.contains("G") || entityTypeFirstCharacter.contains("B") || entityTypeFirstCharacter.contains("D") || entityTypeFirstCharacter.contains("K")) {
                            mapEntities[1][j][k] = entityFactory.createCharacter(entityTypeFirstCharacter, j, k, entityTypeSecondCharacter);
                            ashleyEngine.addEntity(mapEntities[1][j][k]); //Add new tile entity to ashelyEngine
                        } else if (!entityType.contains("--")) {
                            mapEntities[1][j][k] = entityFactory.createTile(j, k, Integer.parseInt(entityType), 1); //Create tile entity
                            ashleyEngine.addEntity(mapEntities[1][j][k]); //Add new tile entity to ashelyEngine
                        }
                        this.rawMap[1][j][k] = rawMap[1][j][k];
                    }
                }
            }
        }
        map.getComponent(MapComponent.class).mapEntities = mapEntities; //Store completed 2d entity array in map tiles
    }

    public String[][][] loadRawMap(FileHandle level) { //Load raw map data

        String levelInString = level.readString(); //Read level data
        levelInString = levelInString.replaceAll("\\r", "");
        levelInString = levelInString.replaceAll("\\n", "");

        int numLevels = Integer.parseInt(levelInString.substring(0, levelInString.indexOf('x'))); //Find size of map
        levelInString = levelInString.substring(levelInString.indexOf('x') + 1);
        int levelX = Integer.parseInt(levelInString.substring(0, levelInString.indexOf('x')));
        levelInString = levelInString.substring(levelInString.indexOf('x') + 1);
        int levelY = Integer.parseInt(levelInString.substring(0, levelInString.indexOf(';')));
        levelInString = levelInString.substring(levelInString.indexOf(';') + 1);
        levelInString = levelInString.substring(levelInString.indexOf(';') + 1);

        String[][][] rawMap = new String[numLevels][levelX][levelY]; //Create appropriate size 3d entity array
        String[] levelInStringArray = levelInString.split(","); //Array used to retrieve entity type data

        for (int i = 0; i < rawMap.length; i++) {
            for (int j = 0; j < rawMap[i].length; j++) {
                for (int k = 0; k < rawMap[i][j].length; k++) {

                    String entityType = levelInStringArray[j + ((rawMap[i][j].length - k - 1) * rawMap[i].length) + (i * rawMap[i].length * rawMap[i][j].length)]; //Entity type data retrieved from level data
                    rawMap[i][j][k] = entityType;

                }
            }
        }
        return rawMap;
    }

    public String[] loadAvailableTracks(FileHandle level) { //Load raw map data

        String levelInString = level.readString(); //Read level data
        levelInString = levelInString.replaceAll("\\r", "");
        levelInString = levelInString.replaceAll("\\n", "");
        levelInString = levelInString.substring(levelInString.indexOf('x') + 1);
        levelInString = levelInString.substring(levelInString.indexOf('x') + 1);
        levelInString = levelInString.substring(levelInString.indexOf(';') + 1);
        String availableTracksString = levelInString.substring(0, levelInString.indexOf(';'));
        String[] availableTracksArray = availableTracksString.split(","); //Array used to retrieve entity type data

        return availableTracksArray;
    }
}