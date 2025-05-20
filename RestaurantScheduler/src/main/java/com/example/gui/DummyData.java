package com.example.gui;

import com.example.logic.Restaurant;
import com.example.model.Group;
import com.example.model.Reservation;
import com.example.model.Table;
import com.example.util.AdjacencyMatrix;
import com.example.util.Clock;
import java.util.ArrayList;
import java.util.List;


public class DummyData {
    public static Restaurant createRestaurant() {
        List<Table> tableList = new ArrayList<>();
        tableList.add(new Table(true, false, (byte)4));
        tableList.add(new Table(false, true, (byte)2));
        tableList.add(new Table(true, false, (byte)6));
        tableList.add(new Table(false, false, (byte)4));

        AdjacencyMatrix<Table> matrix = new AdjacencyMatrix<>(tableList);

        /* 1️⃣  Start the simulation clock at  20-Mar-2025 18:00  */
        Clock.setTime(Clock.dateTimeToUnix(java.time.LocalDateTime.of(2025, 3, 20, 18, 0)));

        for (int i = 0; i < tableList.size(); i++) {
            for (int j = 0; j < tableList.size(); j++) {
                if (i != j) {
                    matrix.makeAdjacent(tableList.get(i), tableList.get(j));
                }
            }
        }

        List<Reservation> reservations = new ArrayList<>();
        long currentTime = Clock.getTime();


        reservations.add(new Reservation(
                new Group((byte) 3,  true,  false, currentTime),
                currentTime + 30  * 60L,
                currentTime + 30  * 60L + Restaurant.RES_DURATION_SEC));

        reservations.add(new Reservation(
                new Group((byte) 2, false,  true,  currentTime),
                currentTime + 60  * 60L,
                currentTime + 60  * 60L + Restaurant.RES_DURATION_SEC));

        reservations.add(new Reservation(
                new Group((byte) 6,  true,  false, currentTime),
                currentTime + 360  * 60L,
                currentTime + 360  * 60L + Restaurant.RES_DURATION_SEC));

//        reservations.add(new Reservation(
//                new Group((byte) 7,  true,  false, currentTime),
//                currentTime + 120 * 60L,
//                currentTime + 120 * 60L + Restaurant.RES_DURATION_SEC));

        return new Restaurant(6, matrix, reservations);
    }
}
