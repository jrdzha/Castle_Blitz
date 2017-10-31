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

    /**
     * Initializes SocketAccessor and attempts to connect to server
     */
    public void init(){
        lastPing = -1;

        outputQueue = new ArrayList<String>();
        inputQueue = new ArrayList<String>();

        connectToServer();
    }

    /**
     * Attempt to connect to server
     */
    public void connectToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Attempting to connect");
                    socket = Gdx.net.newClientSocket(Protocol.TCP, "localhost", 4000, new SocketHints());
                    System.out.println("Connection successful");
                    isConnected = true;
                } catch (GdxRuntimeException e) {
                    System.out.println("Failed to connect");
                    isConnected = false;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    connectToServer();
                }
            }
        }).start();
    }

    /**
     * Multi-threaded send / receive data from server
     */
    public void update(){
        //Ping

        if(lastPing != (int) GameEngine.lifetime && (int) GameEngine.lifetime % 10 == 0){
            outputQueue.add("ping\n");
            lastPing = (int) GameEngine.lifetime;
        }

        //Send
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (outputQueue.size() != 0 && socket != null) {
                    try {
                        socket.getOutputStream().write(outputQueue.get(0).getBytes());
                        socket.getOutputStream().flush();
                        outputQueue.remove(0);
                    } catch (IOException e) {
                        System.out.println("Disconnected from server");
                        isConnected = false;
                        connectToServer();
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //Receive
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null && socket.getInputStream().available() > 0) {
                        inputQueue.add(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
                        System.out.println(inputQueue.get(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
