package com.example.model;

public class Reservation {
    private Group group;
    private Table table;
    private long startTime;
    private long endTime;

    public Reservation(Group group, long startTime, long endTime) {
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
        this.table = null;
    }

    public Group getGroup() {
        return group;
    }

    public Table getTable() {
        return table;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return group.toString() + "\nfrom " + startTime + " to " + endTime;
    }

    public void reserveTable(Table tbl) {
        this.table = tbl;
        tbl.reserve(this);
    }

    public void delay(long interval) {
        startTime += interval;
        endTime += interval;
    }
}
