package com.jaredzhao.castleblitz.servers;

import com.jaredzhao.castleblitz.utils.Console;

public class EmptyServer implements GameServer {
    @Override
    public void init() {

    }

    @Override
    public void loadMap(String[][][] rawMap) {

    }

    @Override
    public void killServer() {

    }

    @Override
    public Console getConsole() {
        return null;
    }

    @Override
    public boolean[][] retrieveViewMap() {
        return new boolean[0][];
    }

    @Override
    public int[][] retrieveTeamPositions() {
        return new int[0][];
    }

    @Override
    public String getTeam() {
        return null;
    }
}
