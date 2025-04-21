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
        this.availabilityTime = Clock.getTime() + Restaurant.RESERVATION_DURATION_SECONDS;
        return true;
    }

    public boolean isReserved(long time) {
        if (reservations.isEmpty()) return false;
        long endTime;
        for (Reservation reservation : reservations) {
            endTime = reservation.getStartTime() + Restaurant.RESERVATION_DURATION_SECONDS;
            if (!(endTime <= reservation.getStartTime() || reservation.getEndTime() <= time))
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
