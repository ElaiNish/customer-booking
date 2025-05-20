package com.example.logic;

import com.example.model.Table;
import com.example.model.Reservation;
import com.example.util.AdjacencyMatrix;

import java.util.List;

public class RestaurantFactory {

    public static Restaurant build(List<Table> tables,
                                   List<Reservation> reservations) {
        AdjacencyMatrix<Table> mat = new AdjacencyMatrix<>(tables);
        for (int i = 0; i < tables.size(); i++)
            for (int j = i + 1; j < tables.size(); j++)
                mat.makeAdjacent(tables.get(i), tables.get(j));

        int maxGroup = tables.stream().mapToInt(Table::getSeats).max().orElse(4);
        return new Restaurant(maxGroup, mat, reservations);
    }
}
