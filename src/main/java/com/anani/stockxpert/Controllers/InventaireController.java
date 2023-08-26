package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.*;
import com.anani.stockxpert.Repository.InventaireRepository;
import com.anani.stockxpert.Repository.StockRepository;
import com.anani.stockxpert.Service.InventaireService;
import com.anani.stockxpert.Service.StockService;
import com.anani.stockxpert.Util.AlertDialog;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.UnaryOperator;

public class InventaireController implements Initializable {

    @FXML
    private TableColumn<Inventaire, Integer> colArticles;

    @FXML
    private TableColumn<Inventaire, Date> colDate;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<Inventaire, Utilisateur> colInventoriste;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Inventaire> tableView;

    private final TableColumn<Inventaire, Button> printButtonColumn = new TableColumn<>("");

    ObservableList<Inventaire> allData = FXCollections.observableArrayList();

    private StockService stockService;
    private InventaireService inventaireService;

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?\\d*")) { // Utilisation d'une expression régulière pour valider les entiers
            return change;
        }
        return null;
    };

    public InventaireController() {
        this.stockService = new StockRepository();
        this.inventaireService = new InventaireRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("num"));
            colDate.setCellValueFactory(new PropertyValueFactory<Inventaire, Date>("date"));
            colInventoriste.setCellValueFactory(new PropertyValueFactory<Inventaire, Utilisateur>("utilisateur"));
            colArticles.setCellValueFactory(new PropertyValueFactory<Inventaire, Integer>("articles"));

            colArticles.setCellFactory(column -> new TableCell<Inventaire, Integer>() {
                private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

                @Override
                protected void updateItem(Integer value, boolean empty) {
                    super.updateItem(value, empty);
                    if (value == null || empty) {
                        setText(null);
                    } else {
                        setText(decimalFormat.format(value));
                    }
                }
            });


            colInventoriste.setCellFactory(column -> {
                return new TableCell<Inventaire, Utilisateur>() {
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
            });

            colDate.setCellFactory(column -> new TableCell<Inventaire, Date>() {
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

            printButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createPrintButton(param.getValue())));
            printButtonColumn.setCellFactory(param -> new TableCell<Inventaire, Button>() {
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
            tableView.getColumns().addAll(printButtonColumn);

            loadData();
        }
    }

    private void refreshData() {
        allData.clear();
        allData.addAll(inventaireService.getAll());
    }

    public void loadData() {
        refreshData();
        tableView.setItems(allData);
    }

    private Button createPrintButton(Inventaire inventaire) {
        Button printButton = new Button("Imprimer");
        printButton.setStyle("-fx-text-fill: #fff !important");
        printButton.setOnAction(event -> {
            if (AlertDialog.yesNoDialog("Voulez-vous vraiment imprimer l'inventaire " + inventaire.getNum() + " ?")) {
                try {
                    printPdf(inventaire);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return printButton;
    }


    private void printPdf(Inventaire inventaire) throws IOException {
        String userHomeDir = System.getProperty("user.home");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.format("%d_%d_%d", month, day, year);

        String DEST = userHomeDir + "/Desktop/StockXpert/Inventaire/inventaire" + date + "_" + timestamp.getTime() + ".pdf";

        String repertoireStockXpert = userHomeDir + "/Desktop/StockXpert/Inventaire";
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
        String line = "Inventaire N° " + inventaire.getNum() +" au:" + formatter.format(inventaire.getDate());

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
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Qte Virtuel").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Qte Reel").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));

        List<InventaireArticle> items = inventaireService.getAllInventaireItems(inventaire.getId());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        int count = 1;
        for (InventaireArticle inventaireArticle: items){

            Color color_row;
            if (!Objects.equals(inventaireArticle.getQte(), inventaireArticle.getQte_reel())){
                color_row = new DeviceRgb(220, 53, 70);
            }else {
                color_row = new DeviceRgb(255, 255, 255);
            }

            table_items.addCell(new com.itextpdf.layout.element.Cell().add(String.valueOf(count)).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(inventaireArticle.getArticle().getLibelle()).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(inventaireArticle.getArticle().getCategorie().getLibelle()).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(String.valueOf(inventaireArticle.getArticle().getPv())).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(inventaireArticle.getQte_reel())).setBackgroundColor(color_row, (float) 0.3));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(inventaireArticle.getQte())).setBackgroundColor(color_row, (float) 0.3));
            count++;
        }

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
        for (Stock stock : data) {
            TempInventaireItem item = new TempInventaireItem(stock.getId(), stock.getArticle().getLibelle(), 0);
            table.getItems().add(item);
        }

        saveBtn.setOnAction(event1 -> {
            saveBtn.setDisable(true);

            Inventaire iv = inventaireService.save(table.getItems());

            saveBtn.setDisable(false);
            closeButton.fire();

            refreshData();
            AlertDialog.infoDialog("Ajout réussi !");
            try {
                printPdf(iv);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        stage.show();
    }

}
