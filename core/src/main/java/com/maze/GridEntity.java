package com.maze;

//Fix issues here and implement
public final class GridEntity {
    private int row;
    private  int column;

    public GridEntity(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }

    public boolean occupies(int row, int column) {
        if(this.column == column && this.row == row){
            return true;
        }
        return false;
    }

    public void moveTo(int row, int column) {
        this.row = row;
        this.column = column;
    }
}
