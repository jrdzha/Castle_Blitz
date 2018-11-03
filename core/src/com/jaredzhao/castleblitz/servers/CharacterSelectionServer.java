package com.jaredzhao.castleblitz.servers;

import com.jaredzhao.castleblitz.utils.Console;

import java.util.ArrayList;
import java.util.HashMap;

public class CharacterSelectionServer implements GameServer {

    private boolean running;
    private Console console;
    private String[][][] rawMap;
    private HashMap<String, boolean[][]> playerViewMaps;

    public void init() {
        running = true;
        playerViewMaps = new HashMap<String, boolean[][]>();
        console = new Console();
    }

    public void loadMap(String[][][] rawMap) {
        this.rawMap = rawMap;
        updateViewMap();
    }

    public String getTeam() {
        return "G";
    }

    public double viewRange() {
        return 6.5;
    }

    public void updateViewMap() {
        boolean[][] playerViewMap = new boolean[rawMap[0].length][rawMap[0][0].length];
        for (int j = 0; j < playerViewMap.length; j++) {
            for (int k = 0; k < playerViewMap[0].length; k++) {
                playerViewMap[j][k] = true;
            }
        }
        playerViewMaps.put("CLIENT", playerViewMap);
    }

    public int[][] retrieveTeamPositions() {
        ArrayList<int[]> teamPositionArrayList = new ArrayList<int[]>();
        for (int j = 0; j < rawMap[0].length; j++) {
            for (int k = 0; k < rawMap[0][0].length; k++) {
                if (rawMap[1][j][k].substring(1, 2).equals(getTeam())) {
                    int[] position = {j, k};
                    teamPositionArrayList.add(position);
                }
            }
        }
        int[][] teamPositionArray = new int[teamPositionArrayList.size()][2];
        for (int i = 0; i < teamPositionArrayList.size(); i++) {
            teamPositionArray[i] = teamPositionArrayList.get(i);
        }
        return teamPositionArray;
    }

    public boolean[][] retrieveViewMap() {
        return playerViewMaps.get("CLIENT"); //Only return what has been adjusted for fog of war
    }

    public Console getConsole() {
        return console;
    }

    public void killServer() {
        running = false;
    }

}