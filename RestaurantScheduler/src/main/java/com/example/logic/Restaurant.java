package com.example.logic;

import com.example.model.*;
import com.example.util.*;

import javax.swing.JOptionPane;
import java.util.*;


public class Restaurant {

    private final AdjacencyMatrix<Table> tables;
    private final List<Reservation> reservations;
    private final ComparableQueue<Group>[] queues;
    private final HeapMax<ComparableQueue<Group>> queuesHeap;

    private List<Table> fullTablesList = null;
    private int minSeatsPrecalculated = 0;

    /** 2 hours in seconds */
    public static final long RES_DURATION_SEC = (long)(60 * 60 * 1.5);

    public Restaurant(int maxGroupSize,
                      AdjacencyMatrix<Table> tablesMatrix,
                      List<Reservation> reservationsList) {

        this.tables = tablesMatrix;
        this.reservations  = new ArrayList<>(reservationsList);

        // build queue array
        int queuesNumber = 2 * maxGroupSize + 1;
        queues = new ComparableQueue[queuesNumber];
        for (int i = 0; i < queuesNumber; i++) queues[i] = new ComparableQueue<>();

        // build maxHeap and add queues
        queuesHeap = new HeapMax<>();
        for (ComparableQueue<Group> q : queues) queuesHeap.add(q);

        assignReservations();
        handleReservations();
    }

    /** Adds a walk-in group to the correct queue bucket. */
    public boolean addGroupToQueue(Group group) {
        int index = 2 * group.getPeople() + (group.hasBaby() ? 1 : 0);
        if (index >= queues.length || group.getPeople() == 0) return false;
        queues[index].add(group);
        queuesHeap.heapifyUp(queues[index]);
        return true;
    }

    public void handleQueues() {
        Table assigned = null;
        Stack<ComparableQueue<Group>> stack = new Stack<>();

        // till we have a queue to handle
        while (assigned == null && !queuesHeap.isEmpty()) {
            ComparableQueue<Group> q = queuesHeap.popMax();
            stack.push(q);
            if (!q.isEmpty()) {
                Group next = q.peek();
                assigned = getBestTable(next);
                if (assigned != null) {
                    q.remove();  // seat it
                }
            }
        }
        // occupy and return queues to heap
        if (assigned != null) {
            assigned.occupy();
        }
        while (!stack.isEmpty()) {
            queuesHeap.add(stack.pop());
        }
    }

    /** Second pass: when time arrives, occupy reserved tables and remove past reservations. */
    public void handleReservations() {
        List<Reservation> toRemove = new ArrayList<>();
        long now = Clock.getTime();
        for (Reservation r : reservations) {
            if (r.getTable() != null && now >= r.getStartTime()) {
                r.getTable().occupy();
                r.getTable().unReserve(r);
                toRemove.add(r);
            }
        }
        reservations.removeAll(toRemove);
    }


    public Table getBestTable(Group group) {
        Table t = findBestTableFromIterable(group, List.of(getBaseTablesArray()));
        if (t != null) return t;
        return findBestTableFromIterable(group, getTablesList(group.getPeople(), false));
    }


    /** Returns the base (single) tables array. */
    public Table[] getBaseTablesArray() {
        return tables.getArray();
    }
    /** Returns a flat list of all groups currently in all queues. */
    public List<Group> getGroups() {
        List<Group> all = new ArrayList<>();
        for (ComparableQueue<Group> q : queues) {
            all.addAll(q);
        }
        return all;
    }

    /**
     * Finds the highest-scoring table from the given iterable.
     * Scoring discourages wasted seats, wrong location, unneeded baby-seats, and clusters.
     */
    public Table findBestTableFromIterable(Group group, Iterable<Table> allTables) {
        Table best = null;
        int    maxScore = Integer.MIN_VALUE;

        for (Table t : allTables) {
            boolean valid = t.getSeats() >= group.getPeople()
                    && (!group.hasBaby() || t.hasBabySeat())
                    && !t.isOccupied()
                    && !t.isReserved(Clock.getTime());

            int score = 0;
            if (valid) {
                score = (group.getPeople() == t.getSeats() ? 100
                        : 100 - (t.getSeats() - group.getPeople()))
                        + (t.isInside() == group.isPrefersInside() ? 1 : 0)
                        + (!group.hasBaby() && t.hasBabySeat() ? -1 : 0)
                        + (t.isCluster() ? -3 : 0);
            }
            if (score > maxScore) {
                maxScore = score;
                best     = t;
            }
        }
        return best;
    }

    /** First pass: try to seat reservations at their requested time, delaying if needed. */
    private void assignReservations() {
        long originalTime = Clock.getTime();

        for (Reservation r : reservations) {
            if (r.getTable() == null) {
                int delayedSeconds = 0;

                // try seating at the requested time, then retry every 20m
                while (r.getTable() == null) {
                    Clock.setTime(r.getStartTime());
                    Table t = getBestTable(r.getGroup());
                    if (t != null) {
                        r.reserveTable(t);
                    } else {
                        r.delay(1200);
                        delayedSeconds +=1200;
                    }
                }

                // if we ever delayed, notify the user
//                if (delayedSeconds > 0) {
//                    int delayedMinutes = delayedSeconds / 60;
//                    Platform.runLater(() -> {
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setHeaderText("Reservation delayed by " + delayedMinutes + " minutes");
//                        alert.setContentText(r.toString());
//                        alert.show();
//                    });
//                }
            }
        }

        // restore clock
        Clock.setTime(originalTime);
    }

    public List<Table> getTablesList(int minSeatsForGroups, boolean forceRecalculation) {
        if (!forceRecalculation && fullTablesList != null && minSeatsForGroups >= minSeatsPrecalculated)
            return fullTablesList;
        List<Table> result = new ArrayList<>();
        for (Table table : getBaseTablesArray()) {
            makeTableCombos(table, new ArrayList<>(), result, minSeatsForGroups);
        }
        fullTablesList = result;
        minSeatsPrecalculated = minSeatsForGroups;
        return result;
    }


    /** Expose current reservations list for UI binding. */
    public List<Reservation> getReservations() {
        return reservations;
    }

    /** Recursively build all table-cluster combinations of size ≥ minSeats. */
    private void makeTableCombos(Table table,
                                 List<Table> currentList,
                                 List<Table> clusterList,
                                 int minSeats) {
        if (currentList.contains(table)) return;

        currentList.add(table);
        if (currentList.size() > 1) {
            TableCluster cluster = new TableCluster(currentList.toArray(new Table[0]));
            if (cluster.getSeats() >= minSeats) {
                boolean isDup = clusterList.stream()
                        .filter(TableCluster.class::isInstance)
                        .map(TableCluster.class::cast)
                        .anyMatch(existing -> existing.isEqual(cluster));
                if (!isDup) {
                    clusterList.add(cluster);
                }
            }
        }
        // recurse on neighbors
        for (Table adj : tables.getAdjacent(table)) {
            makeTableCombos(adj, new ArrayList<>(currentList), clusterList, minSeats);
        }
    }
}
