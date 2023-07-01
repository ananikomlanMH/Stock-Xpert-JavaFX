package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.Article;
import com.anani.stockxpert.Model.Stock;
import com.anani.stockxpert.Repository.ArticleRepository;
import com.anani.stockxpert.Repository.StockRepository;
import com.anani.stockxpert.Service.ArticleService;
import com.anani.stockxpert.Service.StockService;
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
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class StockController implements Initializable {

    @FXML
    private TableColumn<Stock, Integer> colAlerte;

    @FXML
    private TableColumn<Stock, Article> colArticle;

    @FXML
    private TableColumn<Stock, Article> colCategorie;

    @FXML
    private TableColumn<Stock, Article> colPrix;

    @FXML
    private TableColumn<Stock, Integer> colStock;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Stock> tableView;

    private final TableColumn<Stock, Button> editButtonColumn = new TableColumn<>("");
    private final TableColumn<Stock, Button> deleteButtonColumn = new TableColumn<>("");


    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?\\d*")) { // Utilisation d'une expression régulière pour valider les entiers
            return change;
        }
        return null;
    };

    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    ObservableList<Article> articleObservableList = FXCollections.observableArrayList();
    ObservableList<Stock> allData = FXCollections.observableArrayList();

    private StockService stockService;
    private ArticleService articleService;

    public StockController() {
        this.articleService = new ArticleRepository();
        this.stockService = new StockRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (colArticle != null) {
            colArticle.setCellValueFactory(new PropertyValueFactory<Stock, Article>("article"));
            colCategorie.setCellValueFactory(new PropertyValueFactory<Stock, Article>("article"));
            colPrix.setCellValueFactory(new PropertyValueFactory<Stock, Article>("article"));
            colAlerte.setCellValueFactory(new PropertyValueFactory<>("qte_alerte"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("qte_stock"));

            colArticle.setCellFactory(column -> {
                TableCell<Stock, Article> tableCell = new TableCell<>() {
                    @Override
                    protected void updateItem(Article article, boolean empty) {
                        super.updateItem(article, empty);
                        if (article == null || empty) {
                            setText(null);
                        } else {
                            setText(article.getLibelle());
                        }
                    }
                };
                return tableCell;
            });

            colCategorie.setCellFactory(column -> {
                TableCell<Stock, Article> tableCell = new TableCell<>() {
                    @Override
                    protected void updateItem(Article article, boolean empty) {
                        super.updateItem(article, empty);
                        if (article == null || empty) {
                            setText(null);
                        } else {
                            setText(article.getCategorie().getLibelle());
                        }
                    }
                };
                return tableCell;
            });

            colPrix.setCellFactory(column -> {
                TableCell<Stock, Article> tableCell = new TableCell<>() {
                    @Override
                    protected void updateItem(Article article, boolean empty) {
                        super.updateItem(article, empty);
                        if (article == null || empty) {
                            setText(null);
                        } else {
                            setText(decimalFormat.format(article.getPv()));
                        }
                    }
                };
                return tableCell;
            });


            colAlerte.setCellFactory(column -> new TableCell<Stock, Integer>() {
                @Override
                protected void updateItem(Integer alerte, boolean empty) {
                    super.updateItem(alerte, empty);
                    if (alerte == null || empty) {
                        setText(null);
                    } else {
                        setText(decimalFormat.format(alerte));
                    }
                }
            });

            colStock.setCellFactory(column -> {
                return new TableCell<Stock, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        }else if (item != null && getTableRow() != null) {
                            Stock stock = getTableRow().getItem();
                            setText(decimalFormat.format(item));
                            if (stock != null && stock.getQte_stock() < stock.getQte_alerte()) {
                                // Si la quantité en stock est inférieure à la quantité d'alerte, appliquer le style CSS pour la couleur rouge
                                getStyleClass().add("low-stock");
                            } else {
                                // Sinon, enlever le style CSS pour la couleur rouge
                                getStyleClass().remove("low-stock");
                            }
                        }
                    }
                };
            });

            editButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createEditButton(param.getValue())));
            deleteButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createDeleteButton(param.getValue())));

            editButtonColumn.setCellFactory(param -> new TableCell<Stock, Button>() {
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

            deleteButtonColumn.setCellFactory(param -> new TableCell<Stock, Button>() {
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

    public void addStock(ActionEvent event) throws IOException {
        openModal(new Stock(), event);
    }

    private void refreshData() {
        allData.clear();
        allData.addAll(stockService.getAll());
    }

    public void searchData(KeyEvent keyEvent) {
        String value = searchTextField.getText();
        if (!value.isEmpty()){
            allData.clear();
            allData.addAll(stockService.getAllFromSearch(value));
        }else{
            refreshData();
        }
    }

    public void loadData() {
        refreshData();
        tableView.setItems(allData);
        TextFields.bindAutoCompletion(searchTextField, stockService.getAllArticle());

    }

    private Button createEditButton(Stock stock) {
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-text-fill: #fff !important");
        editButton.setOnAction(event -> {
            try {
                openModal(stock, event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return editButton;
    }

    private Button createDeleteButton(Stock stock) {
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: #fff !important");
        deleteButton.setOnAction(event -> {
            if (AlertDialog.yesNoDialog("Voulez-vous vraiment supprimer cette ressource ?")) {
                stockService.delete(stock.getId());
                refreshData();
                AlertDialog.infoDialog("La ressource a été supprimée avec succès");
            }
        });
        return deleteButton;
    }

    private void openModal(Stock stock, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Stock/form.fxml"));
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

        ComboBox<String> articleComboBox = (ComboBox) root.lookup("#articleComboBox");

        articleObservableList.clear();
        articleObservableList.addAll(articleService.getAll());

        articleComboBox.getItems().clear();
        for (Article article : articleObservableList) {
            articleComboBox.getItems().add(article.getLibelle());
        }
        TextFields.bindAutoCompletion(articleComboBox.getEditor(), articleComboBox.getItems());

        TextFormatter<String> textFormatter = new TextFormatter<>(integerFilter);
        TextFormatter<String> textFormatter2 = new TextFormatter<>(integerFilter);

        TextField qte_alerte = (TextField) root.lookup("#qteAlerte");
        TextField qte_stock = (TextField) root.lookup("#qteStock");
        qte_stock.setTextFormatter(textFormatter2);
        qte_alerte.setTextFormatter(textFormatter);

        if (stock.getId() != null) {
            qte_alerte.setText(String.valueOf(stock.getQte_alerte()));
            qte_stock.setText(String.valueOf(stock.getQte_stock()));
            articleComboBox.getSelectionModel().select(stock.getArticle().getLibelle());
            articleComboBox.setEditable(false);
            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!qte_alerte.getText().isEmpty()) {
                        saveBtn.setDisable(true);
                        closeButton.setDisable(true);

                        stock.setQte_stock(Integer.valueOf(qte_stock.getText()));
                        stock.setQte_alerte(Integer.valueOf(qte_alerte.getText()));

                        stockService.edit(stock);
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

                    if (!qte_stock.getText().isEmpty() && !qte_alerte.getText().isEmpty() && !articleComboBox.getSelectionModel().isEmpty()) {
                        saveBtn.setDisable(true);
                        closeButton.setDisable(true);

                        stock.setQte_stock(Integer.valueOf(qte_stock.getText()));
                        stock.setQte_alerte(Integer.valueOf(qte_alerte.getText()));
                        stock.setArticle(articleService.getFromString(articleComboBox.getSelectionModel().getSelectedItem()));
                        stockService.save(stock);

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
