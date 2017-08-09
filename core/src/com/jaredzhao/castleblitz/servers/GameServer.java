package com.jaredzhao.castleblitz.servers;

import com.jaredzhao.castleblitz.utils.Console;

public interface GameServer {
    void init();
    void loadMap(String[][][] rawMap);
    void killServer();
    Console getConsole();
    boolean[][] retrieveViewMap();
    String getTeam();
}
