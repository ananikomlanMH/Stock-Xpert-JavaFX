package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.Article;
import com.anani.stockxpert.Model.Stock;
import com.anani.stockxpert.Model.Utilisateur;
import com.anani.stockxpert.Repository.UtilisateurRepository;
import com.anani.stockxpert.Service.UtilisateurService;
import com.anani.stockxpert.Util.AlertDialog;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UtilisateurController implements Initializable {

    @FXML
    private TableColumn<?, ?> colAdresse;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colLogin;

    @FXML
    private TableColumn<?, ?> colNom;

    @FXML
    private TableColumn<?, ?> colPrenom;

    @FXML
    private TableColumn<?, ?> colTel;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    private TextField searchcategorieField;

    @FXML
    private TableView<Utilisateur> tableView;

    private final TableColumn<Utilisateur, Button> editButtonColumn = new TableColumn<>("");
    private final TableColumn<Utilisateur, Button> deleteButtonColumn = new TableColumn<>("");

    ObservableList<Utilisateur> allData = FXCollections.observableArrayList();

    private UtilisateurService utilisateurService;

    public UtilisateurController() {
        this.utilisateurService = new UtilisateurRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
            colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));

            editButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createEditButton(param.getValue())));
            deleteButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createDeleteButton(param.getValue())));

            editButtonColumn.setCellFactory(param -> new TableCell<Utilisateur, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                    }
                }
            });

            deleteButtonColumn.setCellFactory(param -> new TableCell<Utilisateur, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                    }
                }
            });

            tableView.getColumns().addAll(editButtonColumn, deleteButtonColumn);

            loadData();
        }
    }

    private void refreshData() {
        allData.clear();
        allData.addAll(utilisateurService.getAll());
    }

    public void loadData() {
        refreshData();
        tableView.setItems(allData);
    }

    private Button createEditButton(Utilisateur utilisateur) {
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-text-fill: #fff !important");
        editButton.setOnAction(event -> {
            try {
                openModal(utilisateur, event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return editButton;
    }

    private Button createDeleteButton(Utilisateur utilisateur) {
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: #fff !important");
        deleteButton.setOnAction(event -> {
            if (AlertDialog.yesNoDialog("Voulez-vous vraiment supprimer cette ressource ?")) {
                utilisateurService.delete(utilisateur.getId());
                refreshData();
                AlertDialog.infoDialog("La ressource a été supprimée avec succès");
            }
        });
        return deleteButton;
    }

    @FXML
    void addModal(ActionEvent event) throws IOException {
        openModal(new Utilisateur(), event);
    }

    private void openModal(Utilisateur utilisateur, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Settings/User/form.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Stock");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("images/fav.png")));
        stage.setResizable(false);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());

        // Centrer la fenêtre modale par rapport à son parent
        stage.centerOnScreen();

        // Récupère le bouton de fermeture depuis le modalStage
        Button closeButton = (Button) root.lookup("#closeButton");
        closeButton.setOnAction(e -> stage.close());

        Button saveBtn = (Button) root.lookup("#saveBtn");

        ComboBox<String> type = (ComboBox) root.lookup("#type");

        type.getItems().clear();
        type.getItems().add("Caissier");
        type.getItems().add("Comptable");
        type.getItems().add("Administrateur");

        TextField nom = (TextField) root.lookup("#nom");
        TextField prenom = (TextField) root.lookup("#prenom");
        TextField telephone = (TextField) root.lookup("#telephone");
        TextField adresse = (TextField) root.lookup("#adresse");
        TextField login = (TextField) root.lookup("#login");
        TextField password = (TextField) root.lookup("#password");


        if (utilisateur.getId() != null) {
            nom.setText(utilisateur.getNom());
            prenom.setText(utilisateur.getPrenom());
            telephone.setText(utilisateur.getTelephone());
            adresse.setText(utilisateur.getAdresse());
            login.setText(utilisateur.getLogin());
            type.getSelectionModel().select(utilisateur.getType());

            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!login.getText().isEmpty() && !password.getText().isEmpty() && !type.getValue().isEmpty()) {
                        saveBtn.setDisable(true);
                        closeButton.setDisable(true);

                        String plainPassword = password.getText();
                        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

//                        String enteredPassword = password.getText();
//                        boolean passwordMatch = BCrypt.checkpw(enteredPassword, hashedPassword);

                        utilisateur.setNom(nom.getText());
                        utilisateur.setPrenom(prenom.getText());
                        utilisateur.setTelephone(telephone.getText());
                        utilisateur.setAdresse(adresse.getText());
                        utilisateur.setLogin(login.getText());
                        utilisateur.setPassword(hashedPassword);
                        utilisateur.setType(type.getValue());
                        utilisateurService.edit(utilisateur);

                        closeButton.setDisable(false);
                        closeButton.fire();
                        AlertDialog.infoDialog("Modification réussi !");
                        refreshData();

                    } else {
                        AlertDialog.errorDialog("Veuillez remplir tous les champs.");
                    }
                }
            });
        } else {

            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    if (!login.getText().isEmpty() && !password.getText().isEmpty() && !type.getValue().isEmpty()) {
                        saveBtn.setDisable(true);
                        closeButton.setDisable(true);

                        String plainPassword = password.getText();
                        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

                        utilisateur.setNom(nom.getText());
                        utilisateur.setPrenom(prenom.getText());
                        utilisateur.setTelephone(telephone.getText());
                        utilisateur.setAdresse(adresse.getText());
                        utilisateur.setLogin(login.getText());
                        utilisateur.setPassword(hashedPassword);
                        utilisateur.setType(type.getValue());
                        utilisateurService.save(utilisateur);

                        closeButton.setDisable(false);
                        closeButton.fire();
                        AlertDialog.infoDialog("Ajout réussi !");
                        refreshData();

                    } else {
                        AlertDialog.errorDialog("Veuillez remplir tous les champs.");
                    }
                }
            });
        }
        stage.show();
    }
}
