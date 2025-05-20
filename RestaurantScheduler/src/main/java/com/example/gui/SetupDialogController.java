package com.example.gui;

import com.example.model.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SetupDialogController {

    @FXML private Spinner<Integer> seatsSpinner;
    @FXML private CheckBox babyCheck;
    @FXML private ChoiceBox<String> locChoice;
    @FXML private ListView<Table> tablesList;

    private final ObservableList<Table> tables = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        seatsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 4));
        locChoice.setItems(FXCollections.observableArrayList("Inside", "Outside"));
        locChoice.getSelectionModel().selectFirst();
        tablesList.setItems(tables);
    }

    @FXML
    private void onAddTable() {
        int seats      = seatsSpinner.getValue();
        boolean baby   = babyCheck.isSelected();
        boolean inside = "Inside".equals(locChoice.getSelectionModel().getSelectedItem());
        tables.add(new Table(inside, baby, (byte) seats));
    }

    /** exposed to MainController */
    public ObservableList<Table> getTables() { return tables; }
}
