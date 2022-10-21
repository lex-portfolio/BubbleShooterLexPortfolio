package com.lex_portfolio.engine.math;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Rect {

    protected final Vector2 pos = new Vector2();
    protected float halfWidth;
    protected float halfHeight;

    public Rect() {
    }

    @SuppressWarnings("unused")
    public Rect(Rect from) {
        set(from);
    }

    public Rect(float x, float y, float halfWidth, float halfHeight) {
        pos.set(x, y);
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    public float getAspect() {
        return halfWidth / halfHeight;
    }

    public float getLeft() {
        return pos.x - halfWidth;
    }

    public float getTop() {
        return pos.y + halfHeight;
    }

    public float getRight() {
        return pos.x + halfWidth;
    }

    public float getBottom() {
        return pos.y - halfHeight;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public float getWidth() {
        return halfWidth * 2f;
    }

    public float getHeight() {
        return halfHeight * 2f;
    }

    public Vector2 getPos() {
        return pos;
    }

    public boolean isMe(Vector2 touch) {
        return touch.x >= getLeft() && touch.x <= getRight() && touch.y >= getBottom() && touch.y <= getTop();
    }

    @SuppressWarnings("unused")
    public boolean isOutside(Rect other) {
        return getLeft() > other.getRight() || getRight() < other.getLeft() || getBottom() > other.getTop() || getTop() < other.getBottom();
    }

    public void set(Rect from) {
        pos.set(from.pos);
        halfWidth = from.halfWidth;
        halfHeight = from.halfHeight;
    }

    public void setLeft(float left) {
        pos.x = left + halfWidth;
    }

    public void setTop(float top) {
        pos.y = top - halfHeight;
    }

    public void setRight(float right) {
        pos.x = right - halfWidth;
    }

    public void setBottom(float bottom) {
        pos.y = bottom + halfHeight;
    }

    @SuppressWarnings("unused")
    public void setHalfWidth(float halfWidth) {
        this.halfWidth = halfWidth;
    }

    @SuppressWarnings("unused")
    public void setHalfHeight(float halfHeight) {
        this.halfHeight = halfHeight;
    }

    public void setWidth(float width) {
        this.halfWidth = 0.5f * width;
    }

    public void setHeight(float height) {
        this.halfHeight = 0.5f * height;
    }

    public void setWidthProportion(float width, float aspect) {
        setWidth(width);
        setHeight(width / aspect);
    }

    public void setHeightProportion(float height, float aspect) {
        setHeight(height);
        setWidth(height * aspect);
    }

    public void setSize(float width, float height) {
        halfWidth = 0.5f * width;
        halfHeight = 0.5f * height;
    }

    public void setSize(float size) {
        halfWidth = 0.5f * size;
        halfHeight = 0.5f * size;
    }

    @SuppressWarnings("unused")
    public boolean isLandscape() {
        return halfWidth >= halfHeight;
    }

    @SuppressWarnings("unused")
    public boolean isPortrait() {
        return halfHeight > halfWidth;
    }

    @Override
    public String toString() {
        return "Rect: pos" + pos + " size(" + getWidth() + ", " + getHeight() + ")";
    }

    @SuppressWarnings("unused")
    public static void lineUpVertical(Rect container, List<? extends Rect> units) {
        int unitCnt = units.size();
        float dy = container.getHeight() / unitCnt;
        float x = container.pos.x;
        float y = container.getBottom() + 0.5f * dy;
        for (int i = 0; i < unitCnt; i++, y += dy) units.get(i).pos.set(x, y);
    }
}