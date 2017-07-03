package com.jaredzhao.castleblitz.components.mechanics;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class CharacterPropertiesComponent implements Component{
    public int team = 0;
    public int movementRange = 3;
    public ArrayList<int[]> possibleMoves;
}
