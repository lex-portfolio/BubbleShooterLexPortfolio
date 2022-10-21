package com.lex_portfolio.engine.math.grid.hex_grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.math.grid.Cell;

public class GridView {

    private final NonRegularHexGrid model;
    private final Color selectedColor = Color.RED;
    private final Color borderColor = new Color(0f, 2f, 0f, 0.3f);
    private final Color defaultColor = new Color(1f, 1f, 1f, 1f);
    private Cell selectedCell;

    public GridView(NonRegularHexGrid model) {
        this.model = model;
    }

    public void drawHexGrid(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(defaultColor);
        renderHexGrid(shapeRenderer);
        if (selectedCell != null) {
            shapeRenderer.setColor(selectedColor);
            renderHex(shapeRenderer, selectedCell);
        }
        renderBorder(shapeRenderer);
    }

    public void drawLines(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(defaultColor);
        renderLines(shapeRenderer);
        renderBorder(shapeRenderer);
    }

    private void renderHexGrid(ShapeRenderer shapeRenderer) {
        for (int i = 0; i < model.m(); i++) {
            final int cnt = model.getRowLength(i);
            for (int j = 0; j < cnt; j++) {
                renderHex(shapeRenderer, model.getCell(i, j));
            }
        }
    }

    private void renderBorder(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(borderColor);
        shapeRenderer.line(model.getLeft(), model.getBottom(), model.getLeft(), model.getTop());
        shapeRenderer.line(model.getLeft(), model.getTop(), model.getRight(), model.getTop());
        shapeRenderer.line(model.getRight(), model.getTop(), model.getRight(), model.getBottom());
        shapeRenderer.line(model.getRight(), model.getBottom(), model.getLeft(), model.getBottom());
    }

    private void renderHex(ShapeRenderer shapeRenderer, Cell cell) {
        Vector2 center = cell.getPos();
        float r = model.r();
        float R = model.R();
        float hR = 0.5f * R;

        float x1 = center.x;
        float y1 = center.y - R;
        float x2 = center.x - r;
        float y2 = center.y - hR;
        shapeRenderer.line(x1, y1, x2, y2);

        x1 = x2;
        y1 = y2;
        x2 = center.x - r;
        y2 = center.y + hR;
        shapeRenderer.line(x1, y1, x2, y2);

        x1 = x2;
        y1 = y2;
        x2 = center.x;
        y2 = center.y + R;
        shapeRenderer.line(x1, y1, x2, y2);

        x1 = x2;
        y1 = y2;
        x2 = center.x + r;
        y2 = center.y + hR;
        shapeRenderer.line(x1, y1, x2, y2);

        x1 = x2;
        y1 = y2;
        x2 = center.x + r;
        y2 = center.y - hR;
        shapeRenderer.line(x1, y1, x2, y2);

        x1 = x2;
        y1 = y2;
        x2 = center.x;
        y2 = center.y - R;
        shapeRenderer.line(x1, y1, x2, y2);
    }

    private void renderLines(ShapeRenderer shapeRenderer) {
        final float left = model.getLeft();
        final float right = model.getRight();
        final float bottom = model.getBottom();
        final float top = model.getTop();
        final float r = model.r;
        final float R = model.R;
        for (int i = 0, cnt = model.n; i < cnt; i++) {
            shapeRenderer.setColor(1f, 0f, 0f, 1f);
            float x = left + 2f * r * (i + 1);
            shapeRenderer.line(x, bottom, x, top);
        }

        for (int i = 0, cnt = model.m; i < cnt; i++) {
            shapeRenderer.setColor(1f, 0f, 0f, 1f);
            float y = bottom + 1.5f * R * (i + 1);
            shapeRenderer.line(left, y, right, y);
        }
    }

    public void select(Cell selectedCell) {
        this.selectedCell = selectedCell;
    }
}
