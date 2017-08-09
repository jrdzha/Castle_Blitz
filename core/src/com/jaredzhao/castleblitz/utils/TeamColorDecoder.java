package com.jaredzhao.castleblitz.utils;

public class TeamColorDecoder {
    public static float[] decodeColor(String color){
        float r = 0, g = 0, b = 0;
        if(color.equals("R")){
            r = .953f;
            g = .208f;
            b = .208f;
        } else if(color.equals("O")){
            r = .953f;
            g = .416f;
            b = .258f;
        } else if(color.equals("Y")){
            r = .953f;
            g = .953f;
            b = .258f;
        } else if(color.equals("G")){
            r = .258f;
            g = .953f;
            b = .258f;
        } else if(color.equals("C")){
            r = .258f;
            g = .953f;
            b = .953f;
        } else if(color.equals("P")){
            r = .641f;
            g = .258f;
            b = .953f;
        }
        float[] decodedColor = {r, g, b};
        return decodedColor;
    }
}
