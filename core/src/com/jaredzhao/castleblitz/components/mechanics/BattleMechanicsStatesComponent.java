package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

public class BattleMechanicsStatesComponent implements Component {
    //Character selected
    public boolean characterSelected= false;

    //For character movement
    public boolean move = false;
    public boolean attack = false;
}