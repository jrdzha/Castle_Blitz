package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jaredzhao.castleblitz.GameEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SocketAccessor {

    private Socket socket;
    public ArrayList<String> outputQueue, inputQueue;
    private int lastPing;
    private boolean isConnected = false;
    public String host;
    BufferedReader bufferedReader;

    public SocketAccessor(String host){
        this.host = host;
    }

    /**
     * Initializes SocketAccessor and attempts to connect to server
     */
    public void init(){
        lastPing = -1;

        outputQueue = new ArrayList<String>();
        inputQueue = new ArrayList<String>();

        connectToServer();
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Attempt to connect to server
     */
    public void connectToServer(){
        try {
            System.out.println("CONNECTING");
            socket = Gdx.net.newClientSocket(Protocol.TCP, host, 4000, new SocketHints());
            System.out.println("CONNECTION SUCCESSFUL");
            isConnected = true;
        } catch (GdxRuntimeException e) {
            System.out.println("FAILED TO CONNECT");
            isConnected = false;
            connectToServer();
        }
    }

    /**
     * Multi-threaded send / receive data from server
     */
    public void update(){
        //Ping

        if(lastPing != (int) GameEngine.lifetime && (int) GameEngine.lifetime % 10 == 0){
            outputQueue.add("PING");
            lastPing = (int) GameEngine.lifetime;
        }

        //Send
        if (outputQueue.size() != 0 && socket != null) {
            try {
                socket.getOutputStream().write((outputQueue.get(0) + "\n").getBytes());
                socket.getOutputStream().flush();
                System.out.println("OUT " + outputQueue.get(0));
                outputQueue.remove(0);
            } catch (IOException e) {
                System.out.println("DISCONNECTED");
                isConnected = false;
                connectToServer();
            }
        }

        //Receive
        try {
            while (socket != null && bufferedReader.ready()) {
                String input = bufferedReader.readLine();
                inputQueue.add(input);
                System.out.println("IN  " + input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
