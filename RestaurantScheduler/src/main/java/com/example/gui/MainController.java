package com.example.gui;

import com.example.logic.Restaurant;
import com.example.model.Group;
import com.example.util.Clock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import com.example.gui.DummyData;


/**
 * Main JavaFX controller.
 * Adds a walk-in-group input strip + live waiting-list pane.
 */
public class MainController {

    /* ─── existing controls ────────────────────────────────────────── */
    @FXML private Label       clockLabel;
    @FXML private TableCanvas tableCanvas;
    @FXML private Button      playPauseButton;

    /* ─── NEW controls for walk-in groups ─────────────────────────── */
    @FXML private Spinner<Integer> sizeSpinner;
    @FXML private CheckBox         babyCheck;
    @FXML private ChoiceBox<String> locationChoice;
    @FXML private ListView<Group>   waitingList;

    /* ─── model & timer ───────────────────────────────────────────── */
    private final Restaurant restaurant = DummyData.createRestaurant();
    private final Timeline   timer      = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> tickAndRefresh()));
    private boolean playing = true;

    /* observable backing list for ListView */
    private final ObservableList<Group> waitingGroups =
            FXCollections.observableArrayList();

    /* =================================================================
       FXML life-cycle
       ================================================================ */
    @FXML
    private void initialize() {

        /* timer starts immediately */
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        /* hook model into canvas & first paint */
        tableCanvas.setRestaurant(restaurant);
        tableCanvas.redraw();
        updateClock();

        /* ---------- NEW widget bootstrap --------------------------- */
        sizeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 2));

        locationChoice.setItems(
                FXCollections.observableArrayList("Inside", "Outside"));
        locationChoice.getSelectionModel().selectFirst();

        waitingList.setItems(waitingGroups);
        waitingList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Group g, boolean empty) {
                super.updateItem(g, empty);
                if (empty || g == null) { setText(null); return; }
                String txt = "G" + g.getGroupID() +
                        " • " + g.getPeople() +
                        (g.hasBaby() ? " 👶" : "") +
                        (g.isPrefersInside() ? " • in" : " • out");
                setText(txt);
            }
        });
    }

    /* =================================================================
       Simulation controls
       ================================================================ */
    @FXML private void onTick() { tickAndRefresh(); }

    @FXML
    private void onPlayPause() {
        playing = !playing;
        playPauseButton.setText(playing ? "Pause" : "Play");
        if (playing) timer.play(); else timer.stop();
    }

    private void tickAndRefresh() {
        Clock.tick();
        restaurant.handleQueues();
        restaurant.handleReservations();
        refreshWaitingList();
        tableCanvas.redraw();
        updateClock();
    }

    /* =================================================================
       NEW  : Add walk-in group
       ================================================================ */
    @FXML
    private void onAddGroup(ActionEvent e) {

        int size = sizeSpinner.getValue();
        boolean baby = babyCheck.isSelected();
        boolean insidePref =
                "Inside".equals(locationChoice.getSelectionModel().getSelectedItem());

        Group g = new Group((byte) size, insidePref, baby, Clock.getTime());

        if (!restaurant.addGroupToQueue(g)) {
            new Alert(Alert.AlertType.ERROR,
                    "Could not queue group – size out of range?",
                    ButtonType.OK).show();
            return;
        }

        waitingGroups.add(g);                 // visual
        restaurant.handleQueues();            // try seat immediately
        refreshWaitingList();
        tableCanvas.redraw();
    }

    /* =================================================================
       Helpers
       ================================================================ */
    private void refreshWaitingList() {
        waitingGroups.setAll(restaurant.getGroups());
    }

    private void updateClock() {
        clockLabel.setText("Time: " + Clock.getDateTime());
    }
}
