package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class CharacterPropertiesComponent implements Component{
    public String team = "";
    public int movementRange = 5;
    public ArrayList<int[]> possibleMoves;
}
