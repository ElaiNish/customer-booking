package com.example.model;

import java.util.Arrays;

public class TableCluster extends Table {
    private Table[] tables;

    public TableCluster(Table[] tables) {
        // For the cluster, we inherit:
        // - isInside: from the first table,
        // - hasBabySeat: true if any table has one,
        // - seats: the sum of all table seats.
        super(tables[0].isInside(),
                Arrays.stream(tables).anyMatch(Table::hasBabySeat),
                (byte) Arrays.stream(tables).mapToInt(Table::getSeats).sum()
        );
        this.tables = tables;
    }

    @Override
    public boolean occupy() {
        if (isOccupied()) return false;
        for (Table tbl : tables)
            tbl.occupy();
        super.occupy();
        return true;
    }

    @Override
    public boolean isOccupied() {
        if (super.isOccupied()) return true;
        for (Table tbl : tables)
            if (tbl.isOccupied()) return true;
        return false;
    }

    public boolean contains(Table tbl) {
        return Arrays.stream(tables).anyMatch(t -> t.getTableID() == tbl.getTableID());
    }

    public boolean isContainedIn(TableCluster other) {
        if (other == this) return true;
        return Arrays.stream(tables).allMatch(other::contains);
    }

    public boolean isEqual(TableCluster other) {
        return isContainedIn(other) && other.isContainedIn(this);
    }

    @Override
    public boolean isReserved(long time) {
        if (super.isReserved(time)) return true;
        for (Table tbl : tables)
            if (tbl.isReserved(time)) return true;
        return false;
    }

    @Override
    public void unReserve(Reservation reservation) {
        super.unReserve(reservation);
        for (Table tbl : tables)
            tbl.unReserve(reservation);
    }

    @Override
    public void reserve(Reservation reservation) {
        super.reserve(reservation);
        for (Table tbl : tables)
            tbl.reserve(reservation);
    }

    @Override
    public boolean isCluster() {
        return true;
    }
}

