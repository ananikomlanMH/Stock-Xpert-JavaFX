package com.anani.stockxpert.Util;

import com.anani.stockxpert.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertDialog {

    public static boolean yesNoDialog(String text) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("StockXpert");
        alert.setContentText(text);
        ButtonType buttonTypeOne = new ButtonType("Oui");
        ButtonType buttonTypeCancel = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);


        // Get the Stage.
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image(Main.class.getResource("images/fav.png").toString()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            return true;
        } else {
            return false;
        }
    }

    public static void infoDialog(String text) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("StockXpert");
        alert.setHeaderText(null);
        alert.setContentText(text);

        // Get the Stage.
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image(Main.class.getResource("images/fav.png").toString()));
        alert.showAndWait();
    }
    public static void errorDialog(String text) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("StockXpert");
        alert.setHeaderText(null);
        alert.setContentText(text);

        // Get the Stage.
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image(Main.class.getResource("images/fav.png").toString()));
        alert.showAndWait();
    }
    public static void warningDialog(String text) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("StockXpert");
        alert.setHeaderText(null);
        alert.setContentText(text);

        // Get the Stage.
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image(Main.class.getResource("images/fav.png").toString()));
        alert.showAndWait();
    }
}
