package com.lex_portfolio.bubble_shooter.path_tracker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class PathTracker {

    public interface CollisionChecker {
        boolean isPathTrackerBallCollision(Vector2 pos);
        boolean isPathTrackerOutOfGrid(Vector2 pos);
    }

    public enum Type {MIRROR, THROUGH}

    private final Sprite[][] trackers;
    private final CollisionChecker collisionChecker;

    private float leftWall;
    private float rightWall;
    private float interval;
    private int precision;

    private int activeTrackersCount;
    private int limitTrackersCount;

    public PathTracker(TextureRegion[] regions, CollisionChecker collisionChecker, int maxTrackersCount) {
        trackers = new Sprite[regions.length][maxTrackersCount + 1];
        for (int i = 0; i < trackers.length; i++) {
            for (int j = 0; j < trackers[i].length; j++) {
                trackers[i][j] = new Sprite(regions[i]);
            }
        }
        this.collisionChecker = collisionChecker;
        setLimitTrackersCount(maxTrackersCount);
    }

    private boolean arm;
    private Vector2 src;
    private final Vector2 dst = new Vector2();
    private int index;
    private Type type;

    private final Vector2 s = new Vector2();

    public void engage(Vector2 src, Vector2 dst, int index, Type type) {
        this.src = src;
        this.dst.set(dst);
        this.index = index;
        this.type = type;
        engage();
    }

    private void engage() {
//        System.out.println("engage");
        arm = false;
        final float stepLength = interval / precision;
        s.set(dst).sub(src).nor().scl(stepLength);
        activeTrackersCount = 0;
        trackers[index][0].getPos().set(src);
        for (int i = 1; i <= limitTrackersCount; i++) {
            activeTrackersCount++;
            Sprite tracker = trackers[index][i];
            tracker.getPos().set(trackers[index][i - 1].getPos());
            for (int j = 0; j < precision; j++) {
                tracker.getPos().add(s);
                switch (type) {
                    case MIRROR:
                        if (engageMirror(tracker.getPos())) return;
                        break;
                    case THROUGH:
                        if (engageThrough(tracker.getPos())) return;
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        }
    }

    private boolean engageMirror(Vector2 trackerPos) {
        if (trackerPos.x < leftWall) {
            trackerPos.x = leftWall;
            s.x = -s.x;
        }
        if (trackerPos.x > rightWall) {
            trackerPos.x = rightWall;
            s.x = -s.x;
        }
        return collisionChecker.isPathTrackerBallCollision(trackerPos);
    }

    private boolean engageThrough(Vector2 trackerPos) {
        return collisionChecker.isPathTrackerOutOfGrid(trackerPos);
    }

    public void engageIfArm() {
        if (arm) engage(src, dst, index, type);
    }

    public void arm(Vector2 src, Vector2 dst, int index, Type type) {
//        System.out.println("arm");
        this.src = src;
        this.dst.set(dst);
        this.index = index;
        this.type = type;
        arm = true;
    }

    public void disengage() {
//        System.out.println("disengage");
        activeTrackersCount = 0;
        arm = false;
    }

    public void setLimitTrackersCount(int limitTrackersCount) {
        if (limitTrackersCount > trackers[0].length - 1)
            throw new RuntimeException("Лимит трекеров > их максимального количества limit = " + limitTrackersCount);
        this.limitTrackersCount = limitTrackersCount;
    }

    public void setLeftWall(float leftWall) {
        this.leftWall = leftWall;
    }

    public void setRightWall(float rightWall) {
        this.rightWall = rightWall;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setTrackerHeight(float height) {
        for (int i = 0; i < trackers.length; i++) {
            for (int j = 0; j < trackers[i].length; j++) {
                trackers[i][j].setHeightProportion(height);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (int i = 1; i <= activeTrackersCount; i++) {
            trackers[index][i].draw(batch);
        }
    }
}