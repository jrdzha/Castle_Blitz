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
        console.putConsoleNewEntries("client.turn.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (console.peekConsoleNewEntries() != null) {
                        String nextEntry = console.peekConsoleNewEntries();

                        if (nextEntry.substring(0, nextEntry.indexOf('.')).equals("server")) {
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
                                rawMap[1][toX][toY] = rawMap[1][fromX][fromY];
                                rawMap[1][fromX][fromY] = "--";
                                //console.putConsoleNewEntries("client.move." + fromX + "," + fromY + ".to." + toX + "," + toY + ".");
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
        return "R";
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
        playerViewMaps.put("client", playerViewMap);
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
        return playerViewMaps.get("client"); //Only return what has been adjusted for fog of war
    }

    public Console getConsole(){
        return console;
    }

    public void killServer(){
        running = false;
    }

}
