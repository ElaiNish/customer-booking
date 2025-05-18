package com.example.logic;

import com.example.model.Group;
import com.example.model.Reservation;
import com.example.model.Table;
import com.example.util.AdjacencyMatrix;
import com.example.util.Clock;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        // יצירת מבנה פשוט: 2 שולחנות עם 2 מושבים כל אחד, מחוברים
        var tables = List.of(
                new Table(true,false,(byte)2),
                new Table(true,false,(byte)2)
        );
        var matrix = new AdjacencyMatrix<>(tables);
        matrix.makeAdjacent(tables.get(0), tables.get(1));

        // הזמנה אחת שמתאימה בדיוק לשולחן
        var group = new Group((byte)2,true,false, (int)Clock.getTime());
        var res   = new Reservation(group, (int)Clock.getTime(), (int)Clock.getTime());

        restaurant = new Restaurant(2, matrix, List.of(res));
    }

    @Test
    void testInitialReservationAssignment() {
        // ברגע האתחול, ההזמנה צריכה להתקבל לשולחן
        assertTrue(restaurant.getReservations().isEmpty());
    }

    @Test
    void testAddGroupToQueue() {
        Clock.setTime(1000);
        var g = new Group((byte)1, true, false, 1000);
        assertTrue(restaurant.addGroupToQueue(g));
        assertFalse(restaurant.getGroups().isEmpty());
    }
}
