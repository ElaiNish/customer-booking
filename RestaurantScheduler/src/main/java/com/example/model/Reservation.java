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
        return this.group;
    }

    public Table getTable() {
        return this.table;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public String toString() {
        return this.group.toString() + "\nfrom " + this.startTime + " to " + this.endTime;
    }

    public void reserveTable(Table tbl) {
        this.table = tbl;
        table.reserve(this);
    }

    public void delay(long interval) {
        this.startTime += interval;
        this.endTime += interval;
    }
}
