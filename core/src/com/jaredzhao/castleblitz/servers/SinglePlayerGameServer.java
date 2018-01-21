package com.jaredzhao.castleblitz.servers;

import com.jaredzhao.castleblitz.utils.Console;

import java.util.ArrayList;
import java.util.HashMap;

public class SinglePlayerGameServer implements GameServer {

    private boolean running;
    private Console console;
    private String[][][] rawMap;
    private HashMap<String, boolean[][]> playerViewMaps;

    public void init(){
        running = true;
        playerViewMaps = new HashMap<String, boolean[][]>();

        console = new Console();
        //initialization sequence
        console.putConsoleNewEntries("CLIENT.TURN");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (console.peekConsoleNewEntries() != null) {
                        String[] nextEntry = console.peekConsoleNewEntries().split("\\.");

                        if (nextEntry[0].equals("SERVER")) {
                            if (nextEntry[1].equals("MOVE")) {
                                int fromX = Integer.parseInt(nextEntry[2].split(",")[0]);
                                int fromY = Integer.parseInt(nextEntry[2].split(",")[1]);
                                int toX = Integer.parseInt(nextEntry[4].split(",")[0]);
                                int toY = Integer.parseInt(nextEntry[4].split(",")[1]);

                                rawMap[1][toX][toY] = rawMap[1][fromX][fromY];
                                rawMap[1][fromX][fromY] = "--";
                                //console.putConsoleNewEntries("CLIENT.MOVE." + fromX + "," + fromY + ".TO." + toX + "," + toY);
                                console.pollConsoleNewEntries();
                                updateViewMap();
                            }
                        }
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void loadMap(String[][][] rawMap){
        this.rawMap = rawMap;
        updateViewMap();
    }

    public String getTeam(){
        return "G";
    }

    public double viewRange(){
        return 6.5;
    }

    public void updateViewMap() {
        boolean[][] playerViewMap = new boolean[rawMap[0].length][rawMap[0][0].length];
        for (int j = 0; j < playerViewMap.length; j++) {
            for (int k = 0; k < playerViewMap[0].length; k++) {
                if (rawMap[1][j][k].substring(1, 2).equals(getTeam())) {
                    for (double x = viewRange() * -1; x <= viewRange(); x++) {
                        for (double y = viewRange() * -1; y <= viewRange(); y++) {
                            if ((Math.pow(Math.pow(x, 2) + Math.pow(y, 2), .5)) <= viewRange()) {
                                playerViewMap[(int) x + j][(int) y + k] = true;
                            }
                        }
                    }
                }
            }
        }
        playerViewMaps.put("CLIENT", playerViewMap);
    }

    public int[][] retrieveTeamPositions(){
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
        for(int i = 0; i < teamPositionArrayList.size(); i++){
            teamPositionArray[i] = teamPositionArrayList.get(i);
        }
        return teamPositionArray;
    }

    public boolean[][] retrieveViewMap(){
        return playerViewMaps.get("CLIENT"); //Only return what has been adjusted for fog of war
    }

    public Console getConsole(){
        return console;
    }

    public void killServer(){
        running = false;
    }

}