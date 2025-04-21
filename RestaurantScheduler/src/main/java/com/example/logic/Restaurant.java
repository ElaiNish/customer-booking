package com.example.logic;

import com.example.model.*;
import com.example.util.*;

import javax.swing.JOptionPane;
import java.util.*;


public class Restaurant {

    public static final long RES_DURATION_SEC = 60 * 90;    // 1.5 hours is the reservation time

    /* ───────── state ───────── */
    private final AdjacencyMatrix<Table> tables;
    private final List<Reservation> reservations;

    /** 2‑dim index: 0..2*maxGroupSize (even=no baby, odd=baby) */
    private final ComparableQueue<Group>[] queues;
    private final HeapMax<ComparableQueue<Group>> queuesHeap;

    /* memoized data */
    private List<Table> fullTableList = null;
    private int minSeatsPrecalculated = 0;

    @SuppressWarnings("unchecked")
    public Restaurant(int maxGroupSize,
                      AdjacencyMatrix<Table> tablesMatrix,
                      List<Reservation> reservationsList) {

        this.tables = tablesMatrix;
        this.reservations  = new ArrayList<>(reservationsList);

        // build queue array
        int buckets = 2 * maxGroupSize + 1;
        queues = new ComparableQueue[buckets];
        for (int i = 0; i < buckets; i++) queues[i] = new ComparableQueue<>();

        // build maxHeap and add queues
        queuesHeap = new HeapMax<>();
        for (ComparableQueue<Group> q : queues) queuesHeap.add(q);

        // pre‑assign any reservations
        assignReservations();
        handleReservations();   // seat those that start immediately
    }

    public boolean addGroupToQueue(Group group) {
        int index = 2 * group.getPeople() + (group.hasBaby() ? 1 : 0);
        if (index >= queues.length || group.getPeople() == 0) return false;
        queues[index].add(group);
        queuesHeap.heapifyUp(queues[index]);
        return true;
    }

    /* ===============================================================
       MAIN TICK HANDLERS
       ============================================================== */

    public void handleQueues() {
        if (queuesHeap.isEmpty()) return;

        Table table = null;
        Stack<ComparableQueue<Group>> popped = new Stack<>();

        while (table == null && !queuesHeap.isEmpty()) {
            ComparableQueue<Group> q = queuesHeap.DeleteMax();
            popped.push(q);

            if (!q.isEmpty()) {
                Group g = q.peek();
                table = getBestTable(g);
                if (table != null) {
                    table.occupy();
                    q.poll();                       // dequeue
                }
            }
        }
        while (!popped.isEmpty()) queuesHeap.Add(popped.pop());
    }

    public void handleReservations() {
        if (reservations.isEmpty()) return;

        List<Reservation> done = new ArrayList<>();
        for (Reservation r : reservations) {
            if (Clock.getTime() >= r.startTime() && r.table() != null) {
                r.table().occupy();
                r.table().unReserve(r);
                done.add(r);
            }
        }
        reservations.removeAll(done);
    }

    /* ===============================================================
       INTERNAL TABLE SEARCH
       ============================================================== */

    private Table[] baseTables() { return tables.getArray(); }

    private List<Table> allTableCombos(int minSeats) {
        if (fullTableList != null && minSeats >= minSeatsPrecalculated)
            return fullTableList;

        List<Table> combos = new ArrayList<>();
        for (Table t : baseTables()) dfsCombos(t, new ArrayList<>(), combos, minSeats);
        fullTableList = combos;
        minSeatsPrecalculated = minSeats;
        return combos;
    }

    private void dfsCombos(Table t, List<Table> current,
                           List<Table> out, int minSeats) {

        if (current.contains(t)) return;
        current.add(t);

        if (current.size() > 1) {
            TableCluster cluster = new TableCluster(current.toArray(new Table[0]));
            if (cluster.seats() >= minSeats && !duplicate(out, cluster)) out.add(cluster);
        }
        for (Table adj : tables.getAdjacent(t))
            dfsCombos(adj, new ArrayList<>(current), out, minSeats);
    }

    private boolean duplicate(List<Table> list, TableCluster c) {
        for (Table tbl : list)
            if (tbl instanceof TableCluster tc && tc.isEqual(c)) return true;
        return false;
    }

    private Table getBestTable(Group g) {
        Table t = scoreBest(g, Arrays.asList(baseTables()));
        if (t != null) return t;
        return scoreBest(g, allTableCombos(g.people()));
    }

    private Table scoreBest(Group g, Iterable<Table> candidates) {
        int best = 0;
        Table res = null;
        for (Table t : candidates) {
            if (!fits(g, t)) continue;
            int score = computeScore(g, t);
            if (score > best) {
                best = score;
                res  = t;
            }
        }
        return res;
    }

    /* score & fit helpers */
    private boolean fits(Group g, Table t) {
        return t.seats() >= g.people()
                && !(g.hasBaby() && !t.hasBabySeat())
                && !t.isOccupied()
                && !t.isReserved(Clock.getTime());
    }
    private int computeScore(Group g, Table t) {
        int waste = t.seats() - g.people();
        return 100 - waste
                + (t.isInside() == g.prefersInside() ? 1 : 0)
                - (!g.hasBaby() && t.hasBabySeat() ? 1 : 0)
                - (t instanceof TableCluster ? 3 : 0);
    }

    /* ===============================================================
       RESERVATION ASSIGNER
       ============================================================== */

    private void assignReservations() {
        for (Reservation r : reservations) assignSingleReservation(r);
    }

    private void assignSingleReservation(Reservation r) {
        long original = Clock.getTime();
        int   delayed = 0;

        while (r.table() == null) {
            Clock.setTime(r.startTime());
            Table t = getBestTable(r.group());
            if (t != null) {
                r.reserveTable(t);
                break;
            }
            r.delay(1800);           // push 30 min
            delayed += 1800;
        }
        if (delayed > 0) {
            JOptionPane.showMessageDialog(null,
                    "Reservation delayed by " + delayed/60 + " minutes\n" + r);
        }
        Clock.setTime(original);
    }
    public void addReservation(Reservation r) {
        reservations.add(r);
        assignSingleReservation(r);
    }
}
