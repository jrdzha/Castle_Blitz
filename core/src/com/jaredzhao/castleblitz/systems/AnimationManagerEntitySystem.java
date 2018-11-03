package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jaredzhao.castleblitz.GameEngine;
import com.jaredzhao.castleblitz.components.graphics.*;
import com.jaredzhao.castleblitz.components.mechanics.SelectableComponent;
import com.jaredzhao.castleblitz.components.mechanics.SettingsComponent;
import com.jaredzhao.castleblitz.components.mechanics.UIComponent;

import java.util.ArrayList;

public class AnimationManagerEntitySystem extends DisposableEntitySystem {

    private ImmutableArray<Entity> selectableHighlights, selectableUI, pointLights;

    private ComponentMapper<HighlightComponent> highlightComponentComponentMapper = ComponentMapper.getFor(HighlightComponent.class);
    private ComponentMapper<SelectableComponent> selectableComponentComponentMapper = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<AnimationComponent> animationComponentComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SpriteComponent> spriteComponentComponentMapper = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<LightComponent> lightComponentMapper = ComponentMapper.getFor(LightComponent.class);

    private SettingsComponent settingsComponent;

    public AnimationManagerEntitySystem(Entity settings) {
        this.settingsComponent = settings.getComponent(SettingsComponent.class);
    }

    public void addedToEngine(Engine engine) {
        selectableHighlights = engine.getEntitiesFor(Family.all(SelectableComponent.class, HighlightComponent.class).get());
        selectableUI = engine.getEntitiesFor(Family.all(SelectableComponent.class, AnimationComponent.class).get());
        pointLights = engine.getEntitiesFor(Family.all(LightComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : selectableHighlights) {
            HighlightComponent highlightComponent = highlightComponentComponentMapper.get(entity);
            SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
            AnimationComponent animationComponent = animationComponentComponentMapper.get(highlightComponent.highlight);
            if (selectableComponent.isSelected) {
                animationComponent.currentFrame = 0;
                animationComponent.currentTrack = 1;
                animationComponent.framesDisplayed = 0;
            } else {
                animationComponent.currentFrame = 0;
                animationComponent.currentTrack = 0;
                animationComponent.framesDisplayed = 0;
            }
        }

        for (Entity entity : pointLights) {
            LightComponent lightComponent = lightComponentMapper.get(entity);
            lightComponent.intensity = 13f + (float) (Math.sin(GameEngine.lifetime) * 1.5f);
        }

        for (Entity entity : selectableUI) {
            if (entity.getComponent(UIComponent.class) != null) {
                SelectableComponent selectableComponent = selectableComponentComponentMapper.get(entity);
                SpriteComponent spriteComponent = spriteComponentComponentMapper.get(entity);
                AnimationComponent animationComponent = animationComponentComponentMapper.get(entity);

                Sprite currentFrame = spriteComponent.spriteList.get(animationComponent.currentTrack).get(animationComponent.currentFrame);

                if (selectableComponent.touchDown && selectableComponent.canShrink) {
                    if (currentFrame.getScaleX() > .93f) {
                        currentFrame.scale(-.03f);
                    }
                } else {
                    if (currentFrame.getScaleX() < 1f) {
                        currentFrame.scale(.03f);
                    }
                }

                for (ArrayList<Sprite> track : spriteComponent.spriteList) {
                    for (Sprite frame : track) {
                        frame.setScale(currentFrame.getScaleX());
                    }
                }

                if (selectableComponent.name.equals("sound")) {
                    if (settingsComponent.soundOn) {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 0;
                        animationComponent.framesDisplayed = 0;
                    } else {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 1;
                        animationComponent.framesDisplayed = 0;
                    }
                }

                if (selectableComponent.name.equals("sfx")) {
                    if (settingsComponent.sfxOn) {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 0;
                        animationComponent.framesDisplayed = 0;
                    } else {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 1;
                        animationComponent.framesDisplayed = 0;
                    }
                }

                if (selectableComponent.name.equals("homeCastle")) {
                    if (settingsComponent.homeScreen.equals("homeCastle")) {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 1;
                        animationComponent.framesDisplayed = 0;
                    } else {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 0;
                        animationComponent.framesDisplayed = 0;
                    }
                }

                if (selectableComponent.name.equals("homePotions")) {
                    if (settingsComponent.homeScreen.equals("homePotions")) {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 1;
                        animationComponent.framesDisplayed = 0;
                    } else {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 0;
                        animationComponent.framesDisplayed = 0;
                    }
                }

                if (selectableComponent.name.equals("homeShop")) {
                    if (settingsComponent.homeScreen.equals("homeShop")) {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 1;
                        animationComponent.framesDisplayed = 0;
                    } else {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 0;
                        animationComponent.framesDisplayed = 0;
                    }
                }

                if (selectableComponent.name.equals("homeArmory")) {
                    if (settingsComponent.homeScreen.equals("homeArmory")) {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 1;
                        animationComponent.framesDisplayed = 0;
                    } else {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 0;
                        animationComponent.framesDisplayed = 0;
                    }
                }

                if (selectableComponent.name.equals("homeRanking")) {
                    if (settingsComponent.homeScreen.equals("homeRanking")) {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 1;
                        animationComponent.framesDisplayed = 0;
                    } else {
                        animationComponent.currentFrame = 0;
                        animationComponent.currentTrack = 0;
                        animationComponent.framesDisplayed = 0;
                    }
                }

                if (selectableComponent.name.equals("battle")) {
                    if (settingsComponent.homeScreen.equals("homeCastle")) {
                        if (entity.getComponent(VisibleComponent.class) == null) {
                            entity.add(new VisibleComponent());
                        }
                    } else {
                        if (entity.getComponent(VisibleComponent.class) != null) {
                            entity.remove(VisibleComponent.class);
                        }
                    }
                }

                if (selectableComponent.name.equals("debug")
                        || selectableComponent.name.equals("home")
                        || selectableComponent.name.equals("sound")
                        || selectableComponent.name.equals("sfx")
                        || selectableComponent.name.equals("fastforward")) {
                    if (settingsComponent.isPaused && GameEngine.currentScene == GameEngine.singlePlayerGameScene.IDENTIFIER) {
                        if (entity.getComponent(VisibleComponent.class) == null) {
                            entity.add(new VisibleComponent());
                        }
                    } else {
                        entity.remove(VisibleComponent.class);
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {

    }
}