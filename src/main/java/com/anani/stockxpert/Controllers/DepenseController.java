package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.Depense;
import com.anani.stockxpert.Repository.DepenseRepository;
import com.anani.stockxpert.Service.DepenseService;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class DepenseController implements Initializable {


    @FXML
    private Button addBtn;

    @FXML
    private Text somDepense;

    @FXML
    private TableColumn<Depense, Date> colDate;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<Depense, Integer> colMontant;

    @FXML
    private TableColumn<?, ?> colMotif;

    @FXML
    private TableView<Depense> tableView;

    private final TableColumn<Depense, Button> editButtonColumn = new TableColumn<>("");
    private final TableColumn<Depense, Button> deleteButtonColumn = new TableColumn<>("");

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?\\d*")) { // Utilisation d'une expression régulière pour valider les entiers
            return change;
        }
        return null;
    };

    ObservableList<Depense> allData = FXCollections.observableArrayList();

    private DepenseService depenseService;

    public DepenseController() {
        this.depenseService = new DepenseRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
            colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

            colDate.setCellFactory(column -> new TableCell<Depense, Date>() {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");


                @Override
                protected void updateItem(Date date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date == null || empty) {
                        setText(null);
                    } else {
                        setText(formatter.format(date));
                    }
                }
            });

            colMontant.setCellFactory(column -> new TableCell<Depense, Integer>() {
                private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

                @Override
                protected void updateItem(Integer montant, boolean empty) {
                    super.updateItem(montant, empty);
                    if (montant == null || empty) {
                        setText(null);
                    } else {
                        setText(decimalFormat.format(montant));
                    }
                }
            });

            editButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createEditButton(param.getValue())));
            deleteButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createDeleteButton(param.getValue())));

            editButtonColumn.setCellFactory(param -> new TableCell<Depense, Button>() {
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

            deleteButtonColumn.setCellFactory(param -> new TableCell<Depense, Button>() {
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
        allData.addAll(depenseService.getAll());

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        somDepense.setText(String.valueOf(decimalFormat.format(depenseService.getSum())) + " FCFA");
    }

    public void loadData() {
        refreshData();
        tableView.setItems(allData);
    }

    @FXML
    void addDepense(ActionEvent event) throws IOException {
        openModal(new Depense(), event);
    }

    private Button createEditButton(Depense depense) {
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-text-fill: #fff !important");
        editButton.setOnAction(event -> {
            try {
                openModal(depense, event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return editButton;
    }

    private Button createDeleteButton(Depense depense) {
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: #fff !important");
        deleteButton.setOnAction(event -> {
            if (AlertDialog.yesNoDialog("Voulez-vous vraiment supprimer cette ressource ?")) {
                depenseService.delete(depense.getId());
                refreshData();
                AlertDialog.infoDialog("La ressource a été supprimée avec succès");
            }
        });
        return deleteButton;
    }

    private void openModal(Depense depense, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Depense/form.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Depense");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("images/fav.png")));
        stage.setResizable(false);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());

        // Centrer la fenêtre modale par rapport à son parent
        stage.centerOnScreen();

        // Récupère le bouton de fermeture depuis le modalStage
        Button closeButton = (Button) root.lookup("#closeButton");
        closeButton.setOnAction(e -> stage.close());

        TextField motif = (TextField) root.lookup("#motif");
        TextField montant = (TextField) root.lookup("#montant");
        DatePicker date = (DatePicker) root.lookup("#date");

        TextFormatter<String> textFormatter = new TextFormatter<>(integerFilter);
        montant.setTextFormatter(textFormatter);

        Button saveBtn = (Button) root.lookup("#saveBtn");
        if (depense.getId() != null) {

            motif.setText(depense.getMotif());
            montant.setText(String.valueOf(depense.getMontant()));
            date.setValue(depense.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!motif.getText().isEmpty() && !montant.getText().isEmpty() && date.getValue() != null) {
                        saveBtn.setDisable(true);
                        closeButton.setDisable(true);


                        depenseService.edit(depense);
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
                    if (!motif.getText().isEmpty() && !motif.getText().isEmpty() && date.getValue() != null) {
                        saveBtn.setDisable(true);
                        closeButton.setDisable(true);

                        Date javaDate = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

                        depenseService.save(new Depense(null, motif.getText(), Integer.valueOf(montant.getText()), javaDate));
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
