package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.Article;
import com.anani.stockxpert.Model.FactureArticle;
import com.anani.stockxpert.Model.Stock;
import com.anani.stockxpert.Repository.ArticleRepository;
import com.anani.stockxpert.Repository.StockRepository;
import com.anani.stockxpert.Service.ArticleService;
import com.anani.stockxpert.Service.StockService;
import com.anani.stockxpert.Util.AlertDialog;
import com.anani.stockxpert.Util.ConversionNombreEnLettre;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    public void printStock(ActionEvent event) throws IOException {
        String userHomeDir = System.getProperty("user.home");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.format("%d_%d_%d", month, day, year);

        String DEST = userHomeDir + "/Desktop/StockXpert/Stock/situation_stock" + date + "_" + timestamp.getTime() + ".pdf";

        String repertoireStockXpert = userHomeDir + "/Desktop/StockXpert/Stock";
        File repertoire = new File(repertoireStockXpert);
        if (!repertoire.exists()) {
            boolean creationRepertoire = repertoire.mkdirs();
        }

        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        Document document = new Document(pdf);

        String imFile = Main.class.getResource("images/header.png").getFile();
        ImageData data = ImageDataFactory.create(imFile);

        // Creating an Image object
        com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(data);

        // Adding image to the document
        document.add(image);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String line = "Situation de stock au:" + formatter.format(new Date());

        // Creating a table
        Table table = new Table(2).setMarginTop(10).setWidthPercent(100);

        // Adding cells to the table
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(line).setBold().setTextAlignment(TextAlignment.LEFT).setUnderline().setFontColor(new DeviceRgb(6, 96, 58))).setBorder(Border.NO_BORDER));
        table.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));

        // Adding Table to document
        document.add(table);


        float [] pointColumnWidths = {1, 4, 3, 1, 1, 1};
        Table table_items = new Table(pointColumnWidths).setMarginTop(20).setWidthPercent(100);

        table_items.addCell(new com.itextpdf.layout.element.Cell().add("#").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Article").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Catégorie").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Prix").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Qte Alerte").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Qte Stock").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));

        List<Stock> items = stockService.getAll();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        int count = 1;
        int total = 0;
        for (Stock stock: items){
            Color color_row;
            if (stock.getQte_stock() <= stock.getQte_alerte()){
                color_row = new DeviceRgb(220, 53, 70);
            }else {
                color_row = new DeviceRgb(255, 255, 255);
            }
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(String.valueOf(count)).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(stock.getArticle().getLibelle()).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(stock.getArticle().getCategorie().getLibelle()).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(stock.getArticle().getPv())).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(stock.getQte_alerte())).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(stock.getQte_stock())).setBackgroundColor(color_row, (float) 0.3));
            count++;
            total += stock.getQte_stock();
        }

        table_items.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Total").setTextAlignment(TextAlignment.CENTER).setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(total)));

        document.add(table_items);

        String text = "*Veuillez conserver cet document dans vos archive.";
        Paragraph para = new Paragraph (text).setMarginTop(15).setItalic();
        document.add(para);

        document.close();

        File pdfFile = new File(DEST);
        if (pdfFile.exists()) {

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                AlertDialog.errorDialog("Awt Desktop is not supported!");
            }

        } else {
            AlertDialog.errorDialog("File is not exists!");
        }
    }
}
