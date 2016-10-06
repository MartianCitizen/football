package com.martiancitizen.football.model;

/**
 * Created by johnchamberlain on 10/5/16.
 */
public enum RowType {

    CONFERENCE(2), DIVISION(2), TEAM(4), PLAYER(8);

    private int numRequiredDataCells;

    RowType(int numRequiredDataCells) {
        this.numRequiredDataCells = numRequiredDataCells;
    }

    public int getNumRequiredDataCells() {
        return this.numRequiredDataCells;
    }
}
