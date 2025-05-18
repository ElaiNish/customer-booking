package com.example.gui;

import com.example.logic.Restaurant;
import com.example.model.Table;
import com.example.util.Clock;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TableCanvas extends Canvas {

    private Restaurant restaurant;

    public TableCanvas() {  // default size
        setWidth(800);
        setHeight(500);
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        redraw();
    }

    public void redraw() {
        if (restaurant == null) return;
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0,0,getWidth(),getHeight());

        Table[] tables = restaurant.getBaseTablesArray();
        double x = 10, y = 10, w = 140, h = 70, pad = 10;
        gc.setFont(new Font(11));

        for (Table t : tables) {
            Color fill = Color.LIGHTGREEN;
            if (t.isReserved(Clock.getTime())) fill = Color.ORANGE;
            if (t.isOccupied())               fill = Color.RED;
            gc.setFill(fill);
            gc.fillRect(x, y, w, h);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, w, h);
            gc.setFill(Color.BLACK);
            gc.fillText("T" + t.getTableID() + " (" + t.getSeats() + ")", x+4, y+14);

            x += w + pad;
            if (x + w > getWidth()) {
                x = 10;
                y += h + pad;
            }
        }
    }

    @Override public boolean isResizable() { return true; }
    @Override public double prefWidth(double h)  { return getWidth(); }
    @Override public double prefHeight(double w) { return getHeight(); }
}
