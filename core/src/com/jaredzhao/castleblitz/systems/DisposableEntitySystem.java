package com.jaredzhao.castleblitz.systems;

import com.badlogic.ashley.core.EntitySystem;

public abstract class DisposableEntitySystem extends EntitySystem{
    public abstract void dispose();
}
