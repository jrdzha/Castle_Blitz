package com.jaredzhao.castleblitz.utils;

import java.util.ArrayList;

/**
 * DisposableEntitySystem-wide console for exchanging commands between various systems and servers
 */
public class Console {

    private ArrayList<String> consoleHistory;
    private ArrayList<String> consoleNewEntries;

    /**
     * Initializes console
     */
    public Console(){
        consoleHistory = new ArrayList<String>();
        consoleNewEntries = new ArrayList<String>();
    }

    /**
     * Polls next console entry
     *
     * @return  Next console entry
     */
    public String pollConsoleNewEntries(){
        if(consoleNewEntries.size() == 0 ){
            return null;
        }
        String nextEntry = consoleNewEntries.get(0);
        consoleHistory.add(nextEntry);
        consoleNewEntries.remove(0);
        return nextEntry;
    }

    /**
     * Peeks next console entry
     *
     * @return  Next console entry
     */
    public String peekConsoleNewEntries(){
        if(consoleNewEntries.size() == 0 ){
            return null;
        }
        String nextEntry = consoleNewEntries.get(0);
        return nextEntry;
    }

    /**
     * Put new console entry
     *
     * @param nextEntry     New console entry
     */
    public void putConsoleNewEntries(String nextEntry){
        System.out.println(nextEntry);
        consoleNewEntries.add(nextEntry);
    }

    /**
     * Retrieve console history
     *
     * @return      Console history
     */
    public ArrayList<String> getConsoleHistory(){
        return consoleHistory;
    }

    /**
     * Retrieve full console stack
     *
     * @return      Full console stack
     */
    public ArrayList<String> getConsoleNewEntries(){
        return consoleNewEntries;
    }

    /**
     * Retrieve console history size
     *
     * @return      Size of history console
     */
    public int getConsoleHistorySize(){
        return consoleHistory.size();
    }

    /**
     * Retrieve console size
     *
     * @return      Size of console
     */
    public int getConsoleNewEntriesSize(){
        return consoleNewEntries.size();
    }
}
