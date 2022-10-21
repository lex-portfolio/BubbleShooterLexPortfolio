package com.lex_portfolio.engine.pools;

public abstract class UpdatablePool<T extends Poolable & Destroyable & Updatable> extends DestroyablePool<T> {

    public void updateActiveObjects(float deltaTime) {
        for (int i = 0, cnt = activeObjects.size(); i < cnt; i++) {
            T obj = activeObjects.get(i);
            if (obj.isDestroyed()) throw new RuntimeException("Update destroyed obj");
            obj.update(deltaTime);
        }
    }

    public void updateActiveObjectsAndFreeAllDestroyed(float deltaTime) {
        updateActiveObjects(deltaTime);
        freeAllDestroyedActiveObjects();
    }
}
