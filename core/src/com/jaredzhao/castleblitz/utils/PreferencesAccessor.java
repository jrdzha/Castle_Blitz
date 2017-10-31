package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Utility for accessing local preferences
 */
public class PreferencesAccessor {

    Preferences preferences;

    /**
     * Initialize preference access at correct location
     */
    public void init(){
        preferences = Gdx.app.getPreferences("local");
    }

    /**
     * Load persistent game settings
     *
     * @return boolean array of settings
     */
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

    /**
     * Save String data
     *
     * @param key   Key for data
     * @param val   String
     */
    public void putString(String key, String val){
        preferences.putString(key, val);
        preferences.flush();
    }

    /**
     * Save Boolean data
     *
     * @param key   Key for data
     * @param val   Boolean
     */
    public void putBoolean(String key, boolean val){
        preferences.putBoolean(key, val);
        preferences.flush();
    }

    /**
     * Save Int data
     *
     * @param key   Key for data
     * @param val   Int
     */
    public void putInt(String key, int val){
        preferences.putInteger(key, val);
        preferences.flush();
    }

    /**
     * Save Float data
     *
     * @param key   Key for data
     * @param val   Float
     */
    public void putFloat(String key, float val){
        preferences.putFloat(key, val);
        preferences.flush();
    }

    /**
     * Retrieve String data
     *
     * @param key   Key for data
     * @return      String
     */
    public String getString(String key){
        return preferences.getString(key);
    }

    /**
     * Retrieve Boolean data
     *
     * @param key   Key for data
     * @return      Boolean
     */
    public boolean getBoolean(String key){
        return preferences.getBoolean(key);
    }

    /**
     * Retrieve Int data
     * @param key   Key for data
     * @return      Int
     */
    public int getInt(String key){
        return preferences.getInteger(key);
    }

    /**
     * Retrieve Float data
     *
     * @param key   Key for data
     * @return      Float
     */
    public float getFloat(String key){
        return preferences.getFloat(key);
    }

}
