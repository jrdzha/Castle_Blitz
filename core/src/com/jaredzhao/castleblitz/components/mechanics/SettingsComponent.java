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
    public String username = "";
    public String password = "";
    public String confirmPassword = "";
    public boolean battle = false;
    public boolean editUsername = false;
    public boolean editPassword = false;
    public boolean editConfirmPassword = false;
    public boolean signUp = false;
    public boolean login = false;
    public boolean goHome = false;
    public boolean back = false;
    public String signUpLoginError = "";
}