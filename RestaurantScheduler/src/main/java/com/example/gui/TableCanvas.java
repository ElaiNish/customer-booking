package com.example.gui;

import com.example.logic.Restaurant;
import com.example.model.Table;
import com.example.model.TableCluster;
import com.example.util.Clock;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Draws the restaurant layout with richer cues:
 * – Fill colour  : light-green free, orange reserved window, red occupied.
 * – Outline      : BLACK = inside  •  DODGERBLUE = outside.
 * – Baby-seat    : shows small “👶” badge top-right.
 * – Cluster      : thicker outline (3 px).
 * Also paints a small legend strip at the bottom of the canvas.
 */
public class TableCanvas extends Canvas {

    private static final double PAD   = 10;
    private static final double W     = 140;
    private static final double H     = 70;

    private Restaurant restaurant;

    public TableCanvas() {
        setWidth(820);
        setHeight(560);
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        redraw();
    }

    public void redraw() {
        if (restaurant == null) return;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        double x = PAD, y = PAD;
        gc.setFont(Font.font("System", 11));

        /* ─── draw every table / cluster ─────────────────────────── */
        for (Table t : restaurant.getBaseTablesArray()) {

            /* fill depends on run-time status */
            Color fill = Color.LIGHTGREEN;
            if (t.isReserved(Clock.getTime())) fill = Color.ORANGE;
            if (t.isOccupied())                fill = Color.RED;

            /* outline colour by location */
            Color stroke = t.isInside() ? Color.BLACK : Color.DODGERBLUE;
            double strokeWidth = (t.isCluster() ? 3.0 : 1.0);

            gc.setFill(fill);
            gc.fillRect(x, y, W, H);

            gc.setLineWidth(strokeWidth);
            gc.setStroke(stroke);
            gc.strokeRect(x, y, W, H);

            /* text label */
            gc.setFill(Color.BLACK);
            gc.fillText("T" + t.getTableID() + " (" + t.getSeats() + ")", x + 4, y + 14);

            /* baby-seat badge */
            if (t.hasBabySeat()) {
                gc.setFill(Color.DARKMAGENTA);
                gc.setFont(Font.font("System", FontWeight.BOLD, 14));
                gc.fillText("👶", x + W - 18, y + 18);
                gc.setFont(Font.font("System", 11));       // restore
            }

            /* next position */
            x += W + PAD;
            if (x + W > getWidth()) {
                x = PAD;
                y += H + PAD;
            }
        }

        drawLegend(gc);
    }

    /* Simple colour / outline legend at bottom-left */
    private void drawLegend(GraphicsContext gc) {
        double baseY = getHeight() - 60;
        double lx = PAD;

        gc.setFont(Font.font("System", 11));

        legendBox(gc, Color.LIGHTGREEN, lx, baseY, "Free");
        lx += 80;
        legendBox(gc, Color.ORANGE, lx, baseY, "Reserved window");
        lx += 120;
        legendBox(gc, Color.RED, lx, baseY, "Occupied");
        lx += 80;

        /* outline samples */
        gc.setStroke(Color.BLACK);
        gc.strokeRect(lx, baseY, 20, 14);
        gc.fillText("Inside", lx + 26, baseY + 12);
        lx += 70;

        gc.setStroke(Color.DODGERBLUE);
        gc.strokeRect(lx, baseY, 20, 14);
        gc.fillText("Outside", lx + 26, baseY + 12);
        lx += 85;

        /* cluster sample */
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(lx, baseY, 20, 14);
        gc.setLineWidth(1);
        gc.fillText("Cluster", lx + 28, baseY + 12);

        /* baby badge */
        gc.setFill(Color.DARKMAGENTA);
        gc.fillText("👶 baby-seat", lx + 100, baseY + 12);
        gc.setFill(Color.BLACK);
    }

    private void legendBox(GraphicsContext gc, Color fill, double x,
                           double y, String label) {
        gc.setFill(fill);
        gc.setStroke(Color.BLACK);
        gc.fillRect(x, y, 20, 14);
        gc.strokeRect(x, y, 20, 14);
        gc.setFill(Color.BLACK);
        gc.fillText(label, x + 26, y + 12);
    }

    /* keep resizable-canvas behaviour */
    @Override public boolean isResizable() { return true; }
    @Override public double prefWidth(double h)  { return getWidth(); }
    @Override public double prefHeight(double w) { return getHeight(); }
}
