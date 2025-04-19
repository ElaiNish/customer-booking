package com.example.model;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Group {
    private byte people;
    private boolean prefersInside;
    private boolean hasBaby;
    private long arrivalTime; // seconds since Unix epoch
    private static int idCounter = 0;
    private int groupID;

    public Group(byte people, boolean prefersInside, boolean hasBaby, long arrivalTime) {
        this.people = people;
        this.prefersInside = prefersInside;
        this.hasBaby = hasBaby;
        this.arrivalTime = arrivalTime;
        this.groupID = idCounter++;
    }

    public byte getPeople() {
        return this.people;
    }

    public boolean isPrefersInside() {
        return this.prefersInside;
    }

    public boolean isHasBaby() {
        return this.hasBaby;
    }

    public long getArrivalTime() {
        return this.arrivalTime;
    }

    public int getGroupID() {
        return this.groupID;
    }

    @Override
    public String toString() {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(arrivalTime), ZoneId.systemDefault());
        return "Group " + this.groupID + "\nPeople: " + this.people + "\nArrival Time: " + time +
                "\n" + (this.prefersInside ? "INSIDE" : "OUTSIDE") +
                " | " + (this.hasBaby ? "BABY" : "NOBABY");
    }
}
