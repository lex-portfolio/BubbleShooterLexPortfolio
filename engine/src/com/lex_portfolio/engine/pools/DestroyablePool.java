package com.lex_portfolio.engine.pools;

public abstract class DestroyablePool<T extends Poolable & Destroyable> extends Pool<T> {

    public DestroyablePool() {}

    public void freeAllDestroyedActiveObjects() {
        for (int i = 0; i < activeObjects.size(); i++) {
            T obj = activeObjects.get(i);
            if(obj.isDestroyed()) {
                if (!activeObjects.remove(obj)) {
                    throw new RuntimeException("Deleting not active obj: " + obj);
                }
                if (freeObjects.contains(obj)) {
                    throw new RuntimeException("Duplicate object: " + obj);
                }
                freeObjects.add(obj);
                obj.onReturnToPool();
                debugLog();
                i--;
            }
        }
    }
}
