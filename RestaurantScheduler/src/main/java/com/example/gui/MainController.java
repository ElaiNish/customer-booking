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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import com.example.model.Table;
import com.example.logic.RestaurantFactory;
import java.util.List;
import java.util.List;
import com.example.model.Table;
import com.example.model.Reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




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
    private Restaurant restaurant = DummyData.createRestaurant();
    private final Timeline   timer      = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> tickAndRefresh()));
    private boolean playing = true;


    @FXML private Button startButton;
    private ObservableList<Table> userTables = FXCollections.observableArrayList();


    /* observable backing list for ListView */
    private final ObservableList<Group> waitingGroups =
            FXCollections.observableArrayList();

    // NEW: list for reservations
    @FXML private ListView<Reservation> reservationsList;
    private final ObservableList<Reservation> reservationsObs =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        Clock.setTime(Clock.dateTimeToUnix(java.time.LocalDateTime.of(2025, 3, 20, 18, 0)));

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
        startButton.setDisable(true);

        /* hook reservations list */
        reservationsList.setItems(reservationsObs);
        reservationsList.setCellFactory(lv -> new ListCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            @Override protected void updateItem(Reservation r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setText(null); return; }
                String from = fmt.format(Clock.unixToDateTime(r.getStartTime()));
                String tbl  = (r.getTable() == null ? " – " : " T"+r.getTable().getTableID());
                setText("⏰ " + from + tbl + " • G" + r.getGroup().getGroupID());
            }
        });
        refreshReservations();
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
        refreshReservations();
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

        refreshWaitingList();
        tableCanvas.redraw();
    }
    @FXML
    private void onOpenSetup() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SetupDialog.fxml"));
        DialogPane pane = loader.load();
        SetupDialogController dctrl = loader.getController();

        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Restaurant Setup");
        dlg.setDialogPane(pane);

        dlg.showAndWait().ifPresent(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                userTables = dctrl.getTables();
                startButton.setDisable(userTables.isEmpty());
            }
        });
    }

    @FXML
    private void onStartSimulation() {

        if (userTables.isEmpty()) return;   // nothing to add

        /* 1️⃣  combine current (DummyData) tables with new ones */
        List<Table> combined = new java.util.ArrayList<>();
        // existing singles (DummyData) — clusters are built on demand
        java.util.Collections.addAll(combined, restaurant.getBaseTablesArray());
        // user-defined tables
        combined.addAll(userTables);

        /* 2️⃣  keep any current reservations (there may be the demo reservation) */
        List<Reservation> existing = restaurant.getReservations();

        /* 3️⃣  build a fresh Restaurant from the merged list */
        restaurant = RestaurantFactory.build(combined, existing);

        /* 4️⃣  update UI, enable controls */
        tableCanvas.setRestaurant(restaurant);
        tableCanvas.redraw();

        startButton.setDisable(true);
        playPauseButton.setDisable(false);
        timer.play();                       // (re)start clock on new layout
    }



    /* =================================================================
       Helpers
       ================================================================ */
    private void refreshWaitingList() {
        waitingGroups.setAll(restaurant.getGroups());
    }
    private void refreshReservations() {
        reservationsObs.setAll(restaurant.getReservations());
    }

    private void updateClock() {
        clockLabel.setText("Time: " + Clock.getDateTime());
    }
}
