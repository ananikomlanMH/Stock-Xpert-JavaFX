package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.*;
import com.anani.stockxpert.Repository.FactureRepository;
import com.anani.stockxpert.Repository.StockRepository;
import com.anani.stockxpert.Service.FactureService;
import com.anani.stockxpert.Service.StockService;
import com.anani.stockxpert.Util.AlertDialog;
import com.anani.stockxpert.Util.ConversionNombreEnLettre;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;


public class VenteController implements Initializable {

    @FXML
    private TableColumn<Facture, Integer> colArticle;

    @FXML
    private TableColumn<?, ?> colClient;

    @FXML
    private TableColumn<Facture, Date> colDate;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<Facture, Integer> colTotal;

    @FXML
    private TableColumn<Facture, Utilisateur> colVendeur;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Facture> tableView;

    private final TableColumn<Facture, Button> printButtonColumn = new TableColumn<>("");


    ObservableList<Stock> allDataStock = FXCollections.observableArrayList();
    ObservableList<Facture> allData = FXCollections.observableArrayList();

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?\\d*")) { // Utilisation d'une expression régulière pour valider les entiers
            return change;
        }
        return null;
    };

    private StockService stockService;
    private FactureService factureService;

    public VenteController() {
        this.stockService = new StockRepository();
        this.factureService = new FactureRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("num"));
            colDate.setCellValueFactory(new PropertyValueFactory<Facture, Date>("date"));
            colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
            colVendeur.setCellValueFactory(new PropertyValueFactory<Facture, Utilisateur>("utilisateur"));
            colArticle.setCellValueFactory(new PropertyValueFactory<Facture, Integer>("articles"));
            colTotal.setCellValueFactory(new PropertyValueFactory<Facture, Integer>("total"));

            colArticle.setCellFactory(column -> new TableCell<Facture, Integer>() {
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

            colTotal.setCellFactory(column -> new TableCell<Facture, Integer>() {
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

            colVendeur.setCellFactory(column -> {
                TableCell<Facture, Utilisateur> tableCell = new TableCell<>() {
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

            colDate.setCellFactory(column -> new TableCell<Facture, Date>() {
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
            printButtonColumn.setCellFactory(param -> new TableCell<Facture, Button>() {
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


    private Button createPrintButton(Facture facture) {
        Button printButton = new Button("Imprimer");
        printButton.setStyle("-fx-text-fill: #fff !important");
        printButton.setOnAction(event -> {
            if (AlertDialog.yesNoDialog("Voulez-vous vraiment imprimer la facture " + facture.getNum() + " ?")) {
                try {
                    printPdf(facture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return printButton;
    }

    private void printPdf(Facture facture) throws IOException {
        String userHomeDir = System.getProperty("user.home");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String DEST = userHomeDir + "/stock_xpert_doc/" + facture.getNum() + "_" + timestamp.getTime() + ".pdf";

        String repertoireStockXpert = userHomeDir + "/stock_xpert_doc";
        File repertoire = new File(repertoireStockXpert);
        if (!repertoire.exists()) {
            boolean creationRepertoire = repertoire.mkdirs();
//            if (creationRepertoire) {
//                System.out.println("Répertoire stock_xpert_doc créé avec succès !");
//            } else {
//                System.out.println("Échec de création du répertoire stock_xpert_doc.");
//            }
        }

        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        Document document = new Document(pdf);

        String imFile = Main.class.getResource("images/header.png").getFile();
        ImageData data = ImageDataFactory.create(imFile);

        // Creating an Image object
        com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(data);

        // Adding image to the document
        document.add(image);

        String line = "Facture #" + facture.getNum();

        // Creating a table
        Table table = new Table(2).setMarginTop(10).setWidthPercent(100);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        // Adding cells to the table
        table.addCell(new com.itextpdf.layout.element.Cell().add("Client: "+ facture.getClient()).setBorder(Border.NO_BORDER));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(line).setTextAlignment(TextAlignment.RIGHT).setUnderline().setBold().setFontColor(new DeviceRgb(6, 96, 58))).setBorder(Border.NO_BORDER));
        table.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table.addCell(new com.itextpdf.layout.element.Cell().add("Niamey, le "+ formatter.format(facture.getDate())).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        // Adding Table to document
        document.add(table);


        float [] pointColumnWidths = {1, 4, 1, 1, 1};
        Table table_items = new Table(pointColumnWidths).setMarginTop(20).setWidthPercent(100);

        table_items.addCell(new com.itextpdf.layout.element.Cell().add("#").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Article").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Pu").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Qte").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Total").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));

        List<FactureArticle> items = factureService.getAllFactureItems(facture.getId());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        int count = 1;
        for (FactureArticle factureArticle: items){
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(String.valueOf(count)));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(factureArticle.getArticle().getLibelle()));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(factureArticle.getPv())));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(factureArticle.getQte())));
            table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format((long) factureArticle.getQte() * factureArticle.getPv())));
            count++;
        }

        table_items.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add("Total").setBackgroundColor(new DeviceRgb(6,96,58)).setFontColor(Color.WHITE));
        table_items.addCell(new com.itextpdf.layout.element.Cell().add(decimalFormat.format(facture.getTotal())));

        document.add(table_items);

        String text = "Arrêté la présente facture à la somme de : " + ConversionNombreEnLettre.convertirEnLettre(facture.getTotal())+".";
        Paragraph para = new Paragraph (text).setMarginTop(15);
        document.add(para);

        // Creating a table
        float [] pointColumnWidths2 = {200F, 200F, 50F};
        Table table2 = new Table(pointColumnWidths2).setMarginTop(15).setWidthPercent(100);

        String imFile2 = Main.class.getResource("images/cachet.png").getFile();
        ImageData data2 = ImageDataFactory.create(imFile2);

        // Creating an Image object
        com.itextpdf.layout.element.Image image2 = new com.itextpdf.layout.element.Image(data2);

        // Adding cells to the table
        table2.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table2.addCell(new com.itextpdf.layout.element.Cell().add("").setBorder(Border.NO_BORDER));
        table2.addCell(new com.itextpdf.layout.element.Cell().add(image2.setTextAlignment(TextAlignment.RIGHT).setWidth(80)).setBorder(Border.NO_BORDER));

        document.add(table2);

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

    private void refreshData() {
        allData.clear();
        allData.addAll(factureService.getAll());
    }

    public void loadData() {
        refreshData();
        tableView.setItems(allData);
    }

    @FXML
    void addVente(ActionEvent event) throws IOException {
        openModal(new Facture(), event);
    }

    private void openModal(Facture facture, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Vente/form.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Facture");
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
        Button addItem = (Button) root.lookup("#addItem");

        TableView<TempFactureItem> table = (TableView) root.lookup("#table");

        Text totalTTC = (Text) root.lookup("#totalTTC");
        Text totalQte = (Text) root.lookup("#totalQte");

        // Créer les colonnes pour pu, qte et total
        TableColumn<TempFactureItem, String> idColumn = new TableColumn<>("#");
        TableColumn<TempFactureItem, String> articleColumn = new TableColumn<>("ARTICLE");
        TableColumn<TempFactureItem, Integer> puColumn = new TableColumn<>("PU");
        TableColumn<TempFactureItem, Integer> qteColumn = new TableColumn<>("QTE");
        TableColumn<TempFactureItem, Integer> totalColumn = new TableColumn<>("Total");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("article"));
        puColumn.setCellValueFactory(new PropertyValueFactory<>("pu"));
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("qte"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        TableColumn<TempFactureItem, Void> deleteColumn = new TableColumn<>("");

        idColumn.setPrefWidth(43);
        articleColumn.setPrefWidth(450);
        puColumn.setPrefWidth(100);
        qteColumn.setPrefWidth(100);
        totalColumn.setPrefWidth(100);
        deleteColumn.setPrefWidth(100);

        puColumn.setCellFactory(column -> new TableCell<TempFactureItem, Integer>() {
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

        qteColumn.setCellFactory(column -> new TableCell<>() {
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


        totalColumn.setCellFactory(column -> new TableCell<>() {
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

        deleteColumn.setCellFactory(param -> new TableCell<TempFactureItem, Void>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    TempFactureItem rowData = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(rowData);
                    calculerTotals(table, totalQte, totalTTC);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        table.getColumns().addAll(idColumn, articleColumn, puColumn, qteColumn, totalColumn, deleteColumn);

        TextField pu = (TextField) root.lookup("#pu");
        TextField qte = (TextField) root.lookup("#qte");
        TextField total = (TextField) root.lookup("#total");
        TextField client = (TextField) root.lookup("#client");

        TextFormatter<String> textFormatter = new TextFormatter<>(integerFilter);
        qte.setTextFormatter(textFormatter);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        // get all article

        ComboBox<String> articleComboBox = (ComboBox) root.lookup("#articleComboBox");

        allDataStock.clear();
        allDataStock.addAll(stockService.getAll());

        articleComboBox.getItems().clear();
        for (Stock stock : allDataStock) {
            articleComboBox.getItems().add(stock.getArticle().getLibelle());
        }
        TextFields.bindAutoCompletion(articleComboBox.getEditor(), articleComboBox.getItems());
        articleComboBox.setOnAction(event1 -> {
            Stock stock = stockService.getFromString(articleComboBox.getValue());
            if (stock != null) {
                pu.setText(decimalFormat.format(stock.getArticle().getPv()));
                qte.setText("0");
                total.setText("0");

                int valeurMaximale = stock.getQte_stock(); // Remplacez par la valeur maximale souhaitée

                for (TempFactureItem existingItem : table.getItems()) {
                    if (existingItem.getId() == stock.getId()) {
                        valeurMaximale -= existingItem.getQte();
                    }
                }

                Tooltip tooltip_qte = new Tooltip("Stock courant: " + valeurMaximale);
                qte.setTooltip(tooltip_qte);

                if (valeurMaximale > 0) {
                    qte.setText("1");
                    total.setText(decimalFormat.format(stock.getArticle().getPv()));
                }
                int finalValeurMaximale = valeurMaximale;
                UnaryOperator<TextFormatter.Change> filter = change -> {
                    String newText = change.getControlNewText();
                    if (newText.isEmpty()) {
                        return change;
                    }

                    try {
                        int newValue = Integer.parseInt(newText);
                        if (newValue > finalValeurMaximale) {
                            return null; // La modification est rejetée
                        }
                    } catch (NumberFormatException e) {
                        return null; // La modification est rejetée si la valeur n'est pas un entier valide
                    }

                    return change; // La modification est acceptée
                };

                TextFormatter<String> textFormatter_2 = new TextFormatter<>(filter);
                qte.setTextFormatter(textFormatter_2);
            }
        });

        qte.setOnKeyReleased(keyEvent -> {
            if (!pu.getText().isEmpty()) {
                String puText = pu.getText();
                String puWithoutSeparators = puText.replaceAll("[^0-9]", "");
                int puValue = Integer.parseInt(puWithoutSeparators);

                if (puValue <= 0) {
                    puValue = 1;
                    pu.setText("1");
                }

                int qteValue = 0;
                try {
                    qteValue = Integer.parseInt(qte.getText());
                } catch (NumberFormatException e) {
                    // Gérez l'exception en cas de format de texte non valide pour la quantité
                }

                int sous_total = qteValue * puValue;
                total.setText(decimalFormat.format(sous_total));
            }

        });

        addItem.setOnAction(event1 -> {
            if (!articleComboBox.getValue().isEmpty() && !pu.getText().isEmpty() && Integer.valueOf(qte.getText()) > 0) {
                Stock stock_article = stockService.getFromString(articleComboBox.getValue());
                int st = stock_article.getArticle().getPv() * Integer.valueOf(qte.getText());
                TempFactureItem newItem = new TempFactureItem(stock_article.getId(), stock_article.getArticle().getLibelle(), stock_article.getArticle().getPv(), Integer.valueOf(qte.getText()), st);

                // Vérifier si un élément avec le même ID existe déjà
                boolean itemExists = false;
                for (TempFactureItem existingItem : table.getItems()) {
                    if (existingItem.getId() == newItem.getId()) {
                        existingItem.setQte(existingItem.getQte() + newItem.getQte()); // Incrémenter la quantité existante
                        existingItem.setTotal(existingItem.getQte() * newItem.getPu()); // Incrémenter la quantité existante
                        itemExists = true;
                        break;
                    }
                }

                if (!itemExists) {
                    table.getItems().add(newItem);
                }

                articleComboBox.setValue(null);
                pu.clear();
                qte.clear();
                qte.setTooltip(null);
                total.clear();

                table.refresh(); // Actualiser la TableView

                calculerTotals(table, totalQte, totalTTC);
            } else {
                AlertDialog.errorDialog("Veuillez renseigner tous les champs obligatoires!");
            }
        });

        saveBtn.setOnAction(event1 -> {
            if (table.getItems().isEmpty()) {
                AlertDialog.errorDialog("Veuillez renseigner des articles dans la table");
            } else {
                saveBtn.setDisable(true);
                addItem.setDisable(true);

                Facture nf = factureService.save(table.getItems(), client.getText());

                saveBtn.setDisable(false);
                addItem.setDisable(false);
                closeButton.fire();
                refreshData();
                AlertDialog.infoDialog("Ajout réussi !");
                try {
                    printPdf(nf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stage.show();
    }

    public void calculerTotals(TableView<TempFactureItem> table, Text qte, Text total) {
        int sommeTotale = 0;
        int quantiteTotale = 0;

        for (TempFactureItem item : table.getItems()) {
            sommeTotale += item.getTotal();
            quantiteTotale += item.getQte();
        }

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        total.setText(String.valueOf(decimalFormat.format(sommeTotale)) + " FCFA");
        qte.setText(String.valueOf(decimalFormat.format(quantiteTotale)));
//        System.out.println("Somme totale : " + sommeTotale);
//        System.out.println("Quantité totale : " + quantiteTotale);
    }
}
