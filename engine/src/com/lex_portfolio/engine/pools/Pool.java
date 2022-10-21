package com.lex_portfolio.engine.pools;

import java.util.ArrayList;

public abstract class Pool<T extends Poolable> {

    protected final ArrayList<T> activeObjects = new ArrayList<>();
    protected final ArrayList<T> freeObjects = new ArrayList<>();

    public T obtain() {
        T obj;
        if (freeObjects.isEmpty()) {
            obj = newObject();
        } else {
            obj = freeObjects.remove(freeObjects.size() - 1);
        }
        activeObjects.add(obj);
        obj.onObtainFromPool();
        debugLog();
        return obj;
    }

    protected abstract T newObject();

    public void free(T obj) {
        if (!activeObjects.remove(obj)) {
            throw new RuntimeException("Deleting not active obj: " + obj);
        }
        if (freeObjects.contains(obj)) {
            throw new RuntimeException("Duplicate object: " + obj);
        }
        freeObjects.add(obj);
        obj.onReturnToPool();
        debugLog();
    }

    public void freeAllActiveObjects() {
        for (int i = 0, cnt = activeObjects.size(); i < cnt; i++) {
            T obj = activeObjects.get(i);
            if (freeObjects.contains(obj)) {
                throw new RuntimeException("Duplicate object: " + obj);
            }
            freeObjects.add(obj);
            obj.onReturnToPool();
        }
        activeObjects.clear();
        debugLog();
    }

    @SuppressWarnings("unused")
    public void foreground(T obj) {
        if (!activeObjects.remove(obj)) throw new RuntimeException("Can't remove: " + obj);
        activeObjects.add(obj);
    }

    @SuppressWarnings("unused")
    public boolean isEmpty() {
        return activeObjects.isEmpty();
    }

    @SuppressWarnings("unused")
    public int size() {
        return activeObjects.size();
    }

    public ArrayList<T> getActiveObjects() {
        return activeObjects;
    }

    @SuppressWarnings("EmptyMethod")
    protected void debugLog() {
    }

    public void dispose() {
        freeObjects.clear();
        activeObjects.clear();
    }
}
