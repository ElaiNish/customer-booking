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
        for (int i = 0; i < tableList.size(); i++) {
            for (int j = 0; j < tableList.size(); j++) {
                if (i != j) {
                    matrix.makeAdjacent(tableList.get(i), tableList.get(j));
                }
            }
        }

        List<Reservation> reservations = new ArrayList<>();
        long currentTime = Clock.getTime();
        Group group1 = new Group((byte)3, true, false, currentTime);
        reservations.add(new Reservation(group1, currentTime + 120, currentTime + 120 + Restaurant.RESERVATION_DURATION_SECONDS));

        return new Restaurant(6, matrix, reservations);
    }
}
