package com.lex_portfolio.engine.math.grid;

public interface CellObject {
    void onAddedToCell(Cell cell);
    void onChangeCell(Cell cell);
    @SuppressWarnings({"EmptyMethod", "unused"})
    void onRemovedFromCell(Cell cell);
}
