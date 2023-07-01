package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Model.Caisse;
import com.anani.stockxpert.Model.Utilisateur;
import com.anani.stockxpert.Repository.CaisseRepository;
import com.anani.stockxpert.Service.CaisseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class CaisseController implements Initializable {

    @FXML
    private TableColumn<Caisse, Utilisateur> ColVendeur;

    @FXML
    private TableColumn<Caisse, Date> colDate;

    @FXML
    private TableColumn<?, ?> colFacture;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<Caisse, Integer> colMontant;

    @FXML
    private Text somCaisse;

    @FXML
    private TableView<Caisse> tableView;

    ObservableList<Caisse> allData = FXCollections.observableArrayList();

    private CaisseService caisseService;
    public CaisseController() {
        this.caisseService = new CaisseRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFacture.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        ColVendeur.setCellValueFactory(new PropertyValueFactory<Caisse, Utilisateur>("utilisateur"));

        ColVendeur.setCellFactory(column -> {
            TableCell<Caisse, Utilisateur> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(Utilisateur utilisateur, boolean empty) {
                    super.updateItem(utilisateur, empty);
                    if (utilisateur == null || empty) {
                        setText(null);
                    } else {
                        setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
                    }
                }
            };
            return tableCell;
        });

        colDate.setCellFactory(column -> new TableCell<Caisse, Date>() {
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

        colMontant.setCellFactory(column -> new TableCell<Caisse, Integer>() {
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

        loadData();
    }

    private void refreshData() {
        allData.clear();
        allData.addAll(caisseService.getAll());

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        somCaisse.setText(String.valueOf(decimalFormat.format(caisseService.getSum())) + " FCFA");
    }

    public void loadData() {
        refreshData();
        tableView.setItems(allData);
    }
}
