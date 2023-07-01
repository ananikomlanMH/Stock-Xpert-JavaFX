package com.anani.stockxpert.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class LoginController {

    public void quitApp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("StockXpert");
//        alert.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText("Voulez-vous vraiment quitter StockXpert ?");
        ButtonType buttonTypeOne = new ButtonType("Oui");
        ButtonType buttonTypeCancel = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            System.exit(0);
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
}
