package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.*;
import com.anani.stockxpert.Repository.StockRepository;
import com.anani.stockxpert.Service.StockService;
import com.anani.stockxpert.Util.AlertDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class InventaireController implements Initializable {

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<?> tableView;

    private StockService stockService;

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?\\d*")) { // Utilisation d'une expression régulière pour valider les entiers
            return change;
        }
        return null;
    };

    public InventaireController() {
        this.stockService = new StockRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void addInventaire(ActionEvent event) throws IOException {
        openModal(new Inventaire(), event);
    }

    private void openModal(Inventaire inventaire, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Inventaire/form.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Inventaire");
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

        Text dateInventaire = (Text) root.lookup("#dateInventaire");

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        dateInventaire.setText(formatter.format(new Date()));

        TableView<TempInventaireItem> table = (TableView) root.lookup("#table");

        TableColumn<TempInventaireItem, String> idColumn = new TableColumn<>("#");
        TableColumn<TempInventaireItem, String> articleColumn = new TableColumn<>("ARTICLE");
        TableColumn<TempInventaireItem, Integer> qteColumn = new TableColumn<>("QTE");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("article"));
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("qte"));

        idColumn.setPrefWidth(43);
        articleColumn.setPrefWidth(748);
        qteColumn.setPrefWidth(100);

        qteColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        qteColumn.setOnEditCommit(e -> {
            try {
                int newValue = Integer.parseInt(String.valueOf(e.getNewValue()));
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setQte(newValue);
            } catch (NumberFormatException ex) {
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setQte(0);
                AlertDialog.errorDialog("valeur incorrect");
            }
        });

        TextFormatter<String> textFormatter = new TextFormatter<>(integerFilter);

        table.setEditable(true);

        table.getColumns().addAll(idColumn, articleColumn, qteColumn);

        List<Stock> data = stockService.getAll();
        for (Stock stock : data){
            TempInventaireItem item = new TempInventaireItem(stock.getId(), stock.getArticle().getLibelle(), 0);
            table.getItems().add(item);
        }

        stage.show();
    }

}
