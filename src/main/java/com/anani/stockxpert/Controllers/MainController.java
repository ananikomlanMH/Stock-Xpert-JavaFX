package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainController implements Initializable {

    @FXML
    private Button btnMenuAppro;

    @FXML
    private Button btnMenuCaisse;

    @FXML
    private Button btnMenuCommande;

    @FXML
    private Button btnMenuDashboard;

    @FXML
    private Button btnMenuDepenses;

    @FXML
    private Button btnMenuIventaires;

    @FXML
    private Button btnMenuParametre;

    @FXML
    private Button btnMenuStocks;

    @FXML
    private Button btnMenuVente;

    @FXML
    private StackPane contentArea;

    @FXML
    private Text labelStatus;

    @FXML
    private VBox sideBar;

    @FXML
    private Pane settingsSubMenu;

    @FXML
    private Button btnParametreArticles;

    @FXML
    private Button btnParametreCategorie;

    @FXML
    private Button btnParametreUtilisateur;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
//            Font.loadFont(Main.class.getResource("fonts/Poppins-Regular.ttf").toExternalForm(), 10);

            Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/dashboard.fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    @FXML
    private void sideBarClick(ActionEvent event) throws IOException {
        Object source = event.getSource();
        setSideBarBtnActive((Button) source);
        Boolean state = ((Button) source).getStyleClass().contains("activeSideBtn");
        if (btnMenuDashboard.equals(source)) {
            labelStatus.setText(btnMenuDashboard.getText());
            dashboardPage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuVente.equals(source)) {
            labelStatus.setText(btnMenuVente.getText());
            ventePage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuCaisse.equals(source)) {
            labelStatus.setText(btnMenuCaisse.getText());
            CaissePage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuStocks.equals(source)) {
            labelStatus.setText(btnMenuStocks.getText());
            StockPage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuIventaires.equals(source)) {
            labelStatus.setText(btnMenuIventaires.getText());
            InventairePage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuDepenses.equals(source)) {
            labelStatus.setText(btnMenuDepenses.getText());
            DepensePage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuAppro.equals(source)) {
            labelStatus.setText(btnMenuAppro.getText());
            approPage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuCommande.equals(source)) {
            labelStatus.setText(btnMenuCommande.getText());
            commandePage();
            settingsSubMenu.setVisible(false);
        } else if (btnMenuParametre.equals(source)) {
            settingsSubMenu.setVisible(true);
        } else if (btnParametreArticles.equals(source)) {
            labelStatus.setText(btnMenuParametre.getText() + " • " + btnParametreArticles.getText());
            settingsSubMenu.setVisible(true);
            ParametreArticlePage();
        } else if (btnParametreCategorie.equals(source)) {
            labelStatus.setText(btnMenuParametre.getText() + " • " + btnParametreCategorie.getText());
            settingsSubMenu.setVisible(true);
            ParametreCategoriePage();
        } else if (btnParametreUtilisateur.equals(source)) {
            labelStatus.setText(btnMenuParametre.getText() + " • " + btnParametreUtilisateur.getText());
            settingsSubMenu.setVisible(true);
            UserPage();
        } else {
            settingsSubMenu.setVisible(false);
        }
    }

    private void setSideBarBtnActive(Button source) {
        ObservableList<Node> nodes = sideBar.getChildren();

        String[] parematreSubMenu = new String[]{"btnParametreArticles",
                "btnParametreCategorie",
                "btnParametreUtilisateur"};

        List<String> list = Arrays.asList(parematreSubMenu);

        for (Node node : nodes) {
            if (node instanceof Button button && node.getStyleClass().contains("activeSideBtn")) {
                button.getStyleClass().remove("activeSideBtn");
            }
        }

        if (!list.contains(source.getId())) {
            source.getStyleClass().add("activeSideBtn");
        } else {
            btnMenuParametre.getStyleClass().add("activeSideBtn");
        }

    }

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

    private void dashboardPage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/dashboard.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void ventePage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/Vente/index.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void approPage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/approvisionnment.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void commandePage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/commande.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }


    private void ParametreArticlePage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/Settings/Article/index.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void ParametreCategoriePage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/Settings/Categorie/index.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void CaissePage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/caisse.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void StockPage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/Stock/index.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void InventairePage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/Inventaire/index.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void DepensePage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/Depense/index.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private void UserPage() throws IOException {
        Parent fxml = FXMLLoader.load(Main.class.getResource("fxml/Settings/User/index.fxml"));
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    private Stage showLoader() {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    Main.class.getResource("fxml/loader.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root));
        stage.setTitle("");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("images/fav.png")));
        stage.setResizable(false);

//        stage.initOwner(
//                ((Node) event.getSource()).getScene().getWindow());

        // Centrer la fenêtre modale par rapport à son parent
//        stage.centerOnScreen();


        // Récupère le bouton de fermeture depuis le modalStage
//        Button closeButton = (Button) stage.getScene().lookup("#closeButton");
//        closeButton.setOnAction(e -> stage.close());

        stage.show();
        return stage;
    }
}