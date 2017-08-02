package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

public class SettingsComponent implements Component {
    public boolean isPaused = false;
    public boolean fastForward = false;
    public boolean debug = false;
    public boolean facebookLogin = false;
    public boolean soundOn = true;
    public boolean sfxOn = true;
    public String homeScreen = "homeCastle";
    public boolean battle = false;
}