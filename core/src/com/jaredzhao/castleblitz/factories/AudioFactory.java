package com.jaredzhao.castleblitz.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioFactory {

    public AudioFactory(){

    }

    public Sound loadSound(String location){ //Load assets
        return Gdx.audio.newSound(Gdx.files.internal(location));
    }

    public String[] loadMusicTitles(){ //List of available tracks
        String[] songs = {"Vindsvept - Vindsvept Complete - 03 Let the Embers Burn.mp3",
                "Vindsvept - Vindsvept Complete - 04 Weaving the Skies.mp3",
                "Vindsvept - Vindsvept Complete - 05 In Honour of the king, part one.mp3",
                "Vindsvept - Vindsvept Complete - 06 Spirit of the Wind.mp3",
                "Vindsvept - Vindsvept Complete - 07 Hiding in Solitude.mp3",
                "Vindsvept - Vindsvept Complete - 08 Spellbound.mp3",
                "Vindsvept - Vindsvept Complete - 09 Alone.mp3",
                "Vindsvept - Vindsvept Complete - 12 Through The Fog.mp3",
                "Vindsvept - Vindsvept Complete - 16 The Mad Harvester.mp3",
                "Vindsvept - Vindsvept Complete - 17 Wanderer, part two.mp3",
                "Vindsvept - Vindsvept Complete - 18 New Hope.mp3",
                "Vindsvept - Vindsvept Complete - 19 Rain.mp3",
                "Vindsvept - Vindsvept Complete - 21 Lost but not Forgotten.mp3",
                "Vindsvept - Vindsvept Complete - 22 Shapeshifter.mp3",
                "Vindsvept - Vindsvept Complete - 23 Sleeper.mp3",
                "Vindsvept - Vindsvept Complete - 24 Forsaken.mp3",
                "Vindsvept - Vindsvept Complete - 25 Norrsken (Feat. Merrigan).mp3",
                "Vindsvept - Vindsvept Complete - 26 Hugin's Flight.mp3",
                "Vindsvept - Vindsvept Complete - 27 Munin's Return.mp3",
                "Vindsvept - Vindsvept Complete - 29 Farseer.mp3",
                "Vindsvept - Vindsvept Complete - 30 Shattered Sun.mp3",
                "Vindsvept - Vindsvept Complete - 31 Distant.mp3",
                "Vindsvept - Vindsvept Complete - 34 Never to Return.mp3",
                "Vindsvept - Vindsvept Complete - 36 Heart of Thunder.mp3",
                "Vindsvept - Vindsvept Complete - 37 Shimmering in the Shallows.mp3",
                "Vindsvept - Vindsvept Complete - 38 Fall of the Leaf.mp3",
                "Vindsvept - Vindsvept Complete - 42 Skymning.mp3",
                "Vindsvept - Vindsvept Complete - 43 Into the Depths (Feat. Merrigan).mp3",
                "Vindsvept - Vindsvept Complete - 44 Mourning.mp3",
                "Vindsvept - Vindsvept Complete - 47 Last Light.mp3",
                "Vindsvept - Vindsvept Complete - 48 On the Other Side.mp3",
                "Vindsvept - Vindsvept Complete - 49 Illuminate.mp3",
                "Vindsvept - Vindsvept Complete - 50 Deliverance.mp3",
                "Vindsvept - Vindsvept Complete - 51 The Siren's Cadence.mp3",
                "Vindsvept - Vindsvept Complete - 53 Until Sunset.mp3",
                "Vindsvept - Vindsvept Complete - 57 Diverging Realms.mp3",
                "Vindsvept - Vindsvept Complete - 58 Lycanthropy.mp3",
                "Vindsvept - Vindsvept Complete - 59 Chasing Shadows.mp3",
                "Vindsvept - Vindsvept Complete - 60 The Forgotten Forest.mp3",
                "Vindsvept - Vindsvept Complete - 61 Winter's Tale.mp3",
                "Vindsvept - Vindsvept Complete - 62 The Journey Home.mp3",
                "Vindsvept - Vindsvept Complete - 65 Hollow.mp3",
                "Vindsvept - Vindsvept Complete - 66 Light the Bonfire.mp3",
                "Vindsvept - Vindsvept Complete - 67 Into the Mind's Eye, part one.mp3",
                "Vindsvept - Vindsvept Complete - 68 The Oracle's Prophecy.mp3",
                "Vindsvept - Vindsvept Complete - 69 Seven Flowers.mp3",
                "Vindsvept - Vindsvept Complete - 70 Into the Mind's Eye, part two.mp3",
                "Vindsvept - Vindsvept Complete - 72 Over the Mountain.mp3",
                "Vindsvept - Vindsvept Complete - 73 A World Divided.mp3",
                "Vindsvept - Vindsvept Complete - 74 At the Edge of the World.mp3",
                "Vindsvept - Vindsvept Complete - 75 Bringer of Rain.mp3",
                "Vindsvept - Vindsvept Complete - 76 Untamed.mp3",
                "Vindsvept - Vindsvept Complete - 77 Wherever the Path May Lead.mp3",
                "Vindsvept - Vindsvept Complete - 78 In Winter’s Grasp.mp3",
                "Vindsvept - Vindsvept Complete - 81 Winter's Morning.mp3",
                "Vindsvept - Vindsvept Complete - 82 On the Twilight Strand.mp3",
                "Vindsvept - Vindsvept Complete - 83 Guardian.mp3",
                "Vindsvept - Vindsvept Complete - 84 What Lies Beyond.mp3",
                "Vindsvept - Vindsvept Complete - 85 Storm.mp3",
                "Vindsvept - Vindsvept Complete - 86 The Fae.mp3",
                "Vindsvept - Vindsvept Complete - 87 A Night at the Eolian.mp3",
                "Vindsvept - Vindsvept Complete - 89 Sombre.mp3",
                "Vindsvept - Vindsvept Complete - 90 Clarity.mp3",
                "Vindsvept - Vindsvept Complete - 91 Last Stand.mp3"};
        return songs;
    }

    public Music loadMusic(String location){ //Load music from disk
        return Gdx.audio.newMusic(Gdx.files.internal("audio/ost/" + location));
    }

}
