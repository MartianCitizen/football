package com.martiancitizen.football.model;

/**
 * Created by johnchamberlain on 10/5/16.
 */
public enum RowType {

    CONFERENCE(3), DIVISION(3), TEAM(5), PLAYER(9);

    private int numRequiredDataCells;

    RowType(int numRequiredDataCells) {
        this.numRequiredDataCells = numRequiredDataCells;
    }

    public int getNumRequiredDataCells() {
        return this.numRequiredDataCells;
    }
}
