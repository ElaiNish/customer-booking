package com.example.gui;


import com.example.logic.Restaurant;
import com.example.util.Clock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MainController {

    @FXML private Label clockLabel;
    @FXML private TableCanvas tableCanvas;
    @FXML private Button playPauseButton;

    private final Restaurant restaurant = DummyData.createRestaurant();
    private final Timeline timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> tickAndRefresh()));
    private boolean playing = true;

    @FXML
    private void initialize() {
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        tableCanvas.setRestaurant(restaurant);
        updateClock();
    }

    @FXML private void onTick() { tickAndRefresh(); }

    @FXML
    private void onPlayPause() {
        playing = !playing;
        playPauseButton.setText(playing ? "Pause" : "Play");
        if (playing) timer.play(); else timer.stop();
    }

    /* --- helpers --- */
    private void tickAndRefresh() {
        Clock.tick();
        restaurant.handleQueues();
        restaurant.handleReservations();
        updateClock();
        tableCanvas.redraw();
    }

    private void updateClock() {
        clockLabel.setText("Time: " + Clock.getDateTime());
    }
}

