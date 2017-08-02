package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesAccessor {

    Preferences preferences;

    public void init(){
        preferences = Gdx.app.getPreferences("local");
    }

    public boolean[] loadLocalSettings(){
        boolean[] localSettings = new boolean[2];
        if(!getBoolean("loadedSettingsPreviously")){
            putBoolean("soundOn", true);
            putBoolean("sfxOn", true);
            putBoolean("loadedSettingsPreviously", true);
        }
        localSettings[0] = getBoolean("soundOn");
        localSettings[1] = getBoolean("sfxOn");
        return localSettings;
    }

    public void putString(String key, String val){
        preferences.putString(key, val);
        preferences.flush();
    }

    public void putBoolean(String key, boolean val){
        preferences.putBoolean(key, val);
        preferences.flush();
    }

    public void putInteger(String key, int val){
        preferences.putInteger(key, val);
        preferences.flush();
    }

    public void putFloat(String key, float val){
        preferences.putFloat(key, val);
        preferences.flush();
    }

    public String getString(String key){
        return preferences.getString(key);
    }

    public boolean getBoolean(String key){
        return preferences.getBoolean(key);
    }

    public int getInt(String key){
        return preferences.getInteger(key);
    }

    public float getFloat(String key){
        return preferences.getFloat(key);
    }

}
