package com.example.model;

import com.example.logic.Restaurant;
import com.example.util.Clock;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private static int idCounter = 0;

    private final int tableID;
    private final boolean isInside;
    private final boolean hasBabySeat;
    private final byte seats;
    private long availabilityTime; // free if current time >= availabilityTime
    private final List<Reservation> reservations;

    public Table(boolean isInside, boolean hasBabySeat, byte seats) {
        this.isInside = isInside;
        this.hasBabySeat = hasBabySeat;
        this.seats = seats;
        this.availabilityTime = 0;
        this.tableID = idCounter++;
        this.reservations = new ArrayList<>();
    }

    public boolean isOccupied() {
        return this.availabilityTime > Clock.getTime();
    }

    public boolean occupy() {
        if (isOccupied()) return false;
        this.availabilityTime = Clock.getTime() + Restaurant.RES_DURATION_SEC;
        return true;
    }

    public boolean isReserved(long time) {
        if (reservations.isEmpty()) return false;

        // the walk-in (or cluster) would occupy [time , time+RES_DURATION]
        long newEnd = time + Restaurant.RES_DURATION_SEC;

        for (Reservation r : reservations) {
            long resStart = r.getStartTime();
            long resEnd   = r.getEndTime();          // already start+RES_DURATION

            // intervals overlap unless one ends before the other starts
            if (!(newEnd <= resStart || resEnd <= time))
                return true;
        }
        return false;
    }

    public void unReserve(Reservation reservation) {
        reservations.remove(reservation);
    }

    public void reserve(Reservation reservation) {
        reservations.add(reservation);
    }

    public boolean isCluster() {
        return false;
    }

    public int getTableID() {
        return tableID;
    }

    public boolean isInside() {
        return isInside;
    }

    public boolean hasBabySeat() {
        return hasBabySeat;
    }

    public byte getSeats() {
        return seats;
    }

    public long getAvailabilityTime() {
        return availabilityTime;
    }

    public void setAvailabilityTime(long availabilityTime) {
        this.availabilityTime = availabilityTime;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    @Override
    public String toString() {
        return "Table " + tableID +
                " (Seats: " + seats + ", " +
                (isInside ? "INSIDE" : "OUTSIDE") +
                ", " + (hasBabySeat ? "BABY SEAT" : "NO BABY SEAT") +
                ") - " + (isOccupied() ? "Occupied" : "Available");
    }

}
