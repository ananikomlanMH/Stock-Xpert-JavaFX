package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.Categorie;
import com.anani.stockxpert.Repository.CategorieRepository;
import com.anani.stockxpert.Service.CategorieService;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CategorieController implements Initializable {

    @FXML
    private Button closeButton;

    @FXML
    private Button addCategorieBtn;

    @FXML
    private TextField searchcategorieField;

    @FXML
    private TableView<Categorie> tableViewCategorie;

    @FXML
    private TableColumn<Categorie, Integer> colId;

    @FXML
    private TableColumn<Categorie, String> colLibelle;

    private final TableColumn<Categorie, Button> editButtonColumn = new TableColumn<>("");
    private final TableColumn<Categorie, Button> deleteButtonColumn = new TableColumn<>("");

    @FXML
    private TextField libelle;

    @FXML
    private Button saveBtn;

    ObservableList<Categorie> dataCategorieList = FXCollections.observableArrayList();

    private CategorieService categorieService;

    public CategorieController() {
        this.categorieService = new CategorieRepository();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (colId != null){
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));

            editButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createEditButton(param.getValue())));
            deleteButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createDeleteButton(param.getValue())));

            editButtonColumn.setCellFactory(param -> new TableCell<Categorie, Button>() {
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

            deleteButtonColumn.setCellFactory(param -> new TableCell<Categorie, Button>() {
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

            tableViewCategorie.getColumns().addAll(editButtonColumn, deleteButtonColumn);

            loadData();
        }
    }

    public void showAddModal(ActionEvent event) throws IOException {
        openModal(new Categorie(), event);
    }

    private void refreshData() {
        dataCategorieList.clear();
        dataCategorieList.addAll(categorieService.getAll());
    }

    public void loadData() {
        refreshData();
        tableViewCategorie.setItems(dataCategorieList);

    }

    private Button createEditButton(Categorie categorie) {
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-text-fill: #fff !important");
        editButton.setOnAction(event -> {
            try {
                openModal(categorie, event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return editButton;
    }

    private Button createDeleteButton(Categorie categorie) {
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: #fff !important");
        deleteButton.setOnAction(event -> {
            if (AlertDialog.yesNoDialog("Voulez-vous vraiment supprimer cette ressource ?")) {
                categorieService.delete(categorie.getId());
                refreshData();
                AlertDialog.infoDialog("La ressource a été supprimée avec succès");
            }
        });
        return deleteButton;
    }

    private void openModal(Categorie categorie, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Settings/Categorie/form.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Catégorie");
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
        if (categorie.getId() != null){
            TextField libelle = (TextField) root.lookup("#libelle");
            libelle.setText(categorie.getLibelle());

            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!libelle.getText().isEmpty()) {
                        saveBtn.setDisable(true);
                        libelle.setDisable(true);
                        closeButton.setDisable(true);
                        categorie.setLibelle(libelle.getText());
                        categorieService.edit(categorie);
                        closeButton.setDisable(false);
                        closeButton.fire();
                        AlertDialog.infoDialog("Modification réussi !");
                        refreshData();

                    } else {
                        AlertDialog.errorDialog("Veuillez remplir tous les champs.");
                    }
                }
            });
        }else{
            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TextField libelle = (TextField) root.lookup("#libelle");
                    if (!libelle.getText().isEmpty()) {
                        saveBtn.setDisable(true);
                        libelle.setDisable(true);
                        closeButton.setDisable(true);

                        categorieService.save(new Categorie(libelle.getText()));
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
