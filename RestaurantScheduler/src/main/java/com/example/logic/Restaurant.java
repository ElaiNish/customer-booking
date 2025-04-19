package com.example.logic;

import com.example.model.Group;
import com.example.model.Reservation;
import com.example.model.Table;
import com.example.model.TableCluster;
import com.example.util.AdjacencyMatrix;
import com.example.util.Clock;
import com.example.util.ComparableQueue;
import com.example.util.HeapMax;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    public static final long RESERVATION_DURATION_SECONDS = 60 * 60 + (long)(60 * 30); // 1.5 hours

    private AdjacencyMatrix<Table> tables;
    private List<Reservation> reservations;
    private ComparableQueue<Group>[] queues;
    private HeapMax<ComparableQueue<Group>> queuesHeap;
    private List<Table> fullTablesList;
    private int minSeatsPrecalculated;

    @SuppressWarnings("unchecked")
    public Restaurant(int maxGroupSize, AdjacencyMatrix<Table> tablesMatrix, List<Reservation> reservationsList) {
        this.tables = tablesMatrix;
        this.reservations = reservationsList;
        queues = new ComparableQueue[2 * maxGroupSize + 1];
        for (int i = 0; i < queues.length; i++) {
            queues[i] = new ComparableQueue<>();
        }
        queuesHeap = new HeapMax<>();
        for (int i = 0; i < queues.length; i++) {
            queuesHeap.add(queues[i]);
        }
        assignReservations();
        handleReservations();
    }

    public boolean addToQueue(Group g) {
        int index = 2 * g.getPeople() + (g.isHasBaby() ? 1 : 0);
        if (index >= queues.length || g.getPeople() == 0)
            return false;
        queues[index].add(g);
        // In a complete implementation, re-heapify if needed.
        return true;
    }

    public Table[] getBaseTablesArray() {
        return tables.getArray();
    }

    private void makeTableCombos(Table table, List<Table> currentList, List<Table> clusterList, int minSeats) {
        if (currentList.contains(table)) return;
        currentList.add(table);
        TableCluster cluster = currentList.size() > 1 ? new TableCluster(currentList.toArray(new Table[0])) : null;
        if (cluster != null && cluster.getSeats() >= minSeats) {
            boolean isDup = false;
            for (Table t : clusterList) {
                if (t.isCluster() && ((TableCluster)t).isEqual(cluster)) {
                    isDup = true;
                    break;
                }
            }
            if (!isDup)
                clusterList.add(cluster);
        }
        for (Table adjTbl : tables.getAdjacent(table)) {
            makeTableCombos(adjTbl, new ArrayList<>(currentList), clusterList, minSeats);
        }
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

    public Table findBestTableFromEnumerable(Group group, List<Table> allTables) {
        Table result = null;
        int maxScore = 0;
        int score;
        boolean isValid;
        for (Table table : allTables) {
            isValid = (table.getSeats() >= group.getPeople()) &&
                    !(group.isHasBaby() && !table.hasBabySeat()) &&
                    !table.isOccupied() &&
                    !table.isReserved(Clock.getTime());
            if (isValid) {
                score = (group.getPeople() == table.getSeats() ? 100 : 100 - (table.getSeats() - group.getPeople()))
                        + (table.isInside() == group.isPrefersInside() ? 1 : 0)
                        + (!group.isHasBaby() && table.hasBabySeat() ? -1 : 0)
                        + (table.isCluster() ? -3 : 0);
            } else {
                score = 0;
            }
            if (score > maxScore) {
                maxScore = score;
                result = table;
            }
        }
        return result;
    }

    public Table getBestTable(Group group) {
        Table result = findBestTableFromEnumerable(group, List.of(getBaseTablesArray()));
        if (result != null)
            return result;
        return findBestTableFromEnumerable(group, getTablesList(group.getPeople(), false));
    }

    public void handleQueues() {
        Table table = null;
        for (ComparableQueue<Group> q : queues) {
            if (!q.isEmpty()) {
                Group group = q.peek();
                table = getBestTable(group);
                if (table != null) {
                    table.occupy();
                    q.remove();
                    break;
                }
            }
        }
    }

    public List<Group> getGroups() {
        List<Group> allGroups = new ArrayList<>();
        for (ComparableQueue<Group> q : queues) {
            allGroups.addAll(q);
        }
        return allGroups;
    }

    private void assignReservations() {
        long originalTime = Clock.getTime();
        for (Reservation reservation : reservations) {
            if (reservation.getTable() == null) {
                int delayed = 0;
                Clock.setTime(reservation.getStartTime());
                Table tbl = getBestTable(reservation.getGroup());
                while (tbl == null) {
                    reservation.delay(1800);
                    delayed += 1800;
                    Clock.setTime(reservation.getStartTime());
                    tbl = getBestTable(reservation.getGroup());
                }
                if (delayed > 0) {
                    JOptionPane.showMessageDialog(null,
                            "This reservation has been delayed " + (delayed / 60) +
                                    " minutes\n" + reservation.toString());
                }
                reservation.reserveTable(tbl);
            }
        }
        Clock.setTime(originalTime);
    }

    public void handleReservations() {
        List<Reservation> reservationsToRemove = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getTable() != null && Clock.getTime() >= reservation.getStartTime()) {
                reservation.getTable().occupy();
                reservationsToRemove.add(reservation);
                reservation.getTable().unReserve(reservation);
            }
        }
        reservations.removeAll(reservationsToRemove);
    }
}
