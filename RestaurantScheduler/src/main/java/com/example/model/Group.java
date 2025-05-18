package com.example.model;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Group {
    private final byte people;
    private final boolean prefersInside;
    private final boolean hasBaby;
    private final long arrivalTime; // seconds since Unix epoch
    private final int groupID;
    private static int idCounter = 0;

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

    public boolean hasBaby() {
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
