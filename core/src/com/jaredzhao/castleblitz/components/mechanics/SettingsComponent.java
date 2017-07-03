package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

public class SettingsComponent implements Component {
    public boolean isPaused = false;
    public boolean fastForward = false;
    public boolean debug = false;
}