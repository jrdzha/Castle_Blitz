package com.jaredzhao.castleblitz.utils;

import java.util.ArrayList;

public class Console {

    private ArrayList<String> consoleHistory;
    private ArrayList<String> consoleNewEntries;

    public Console(){
        consoleHistory = new ArrayList<String>();
        consoleNewEntries = new ArrayList<String>();
    }

    public String pollConsoleNewEntries(){
        if(consoleNewEntries.size() == 0 ){
            return null;
        }
        String nextEntry = consoleNewEntries.get(0);
        consoleHistory.add(nextEntry);
        consoleNewEntries.remove(0);
        return nextEntry;
    }

    public String peekConsoleNewEntries(){
        if(consoleNewEntries.size() == 0 ){
            return null;
        }
        String nextEntry = consoleNewEntries.get(0);
        return nextEntry;
    }

    public void putConsoleNewEntries(String nextEntry){
        consoleNewEntries.add(nextEntry);
    }

    public ArrayList<String> getConsoleHistory(){
        return consoleHistory;
    }

    public ArrayList<String> getConsoleNewEntries(){
        return consoleNewEntries;
    }

    public int getConsoleHistorySize(){
        return consoleHistory.size();
    }

    public int getConsoleNewEntriesSize(){
        return consoleNewEntries.size();
    }
}
