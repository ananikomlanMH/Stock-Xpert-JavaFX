package com.anani.stockxpert.Controllers;

import com.anani.stockxpert.Main;
import com.anani.stockxpert.Model.Article;
import com.anani.stockxpert.Model.Categorie;
import com.anani.stockxpert.Repository.ArticleRepository;
import com.anani.stockxpert.Repository.CategorieRepository;
import com.anani.stockxpert.Service.ArticleService;
import com.anani.stockxpert.Service.CategorieService;
import com.anani.stockxpert.Util.AlertDialog;
import com.anani.stockxpert.Util.HibernateUtil;
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.controlsfx.control.textfield.TextFields;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class ArticleController implements Initializable {

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private Button addBtn;

    @FXML
    private TableColumn<Article, Categorie> colCategorie;

    @FXML
    private TableColumn<?, ?> colLibelle;

    @FXML
    private TableColumn<Article, Integer> colPrixVente;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Article> tableViewArticle;

    @FXML
    private ChoiceBox<?> article;

    @FXML
    private Button closeButton;

    @FXML
    private TextField libelle;

    @FXML
    private TextField pv;

    private final TableColumn<Article, Button> editButtonColumn = new TableColumn<>("");
    private final TableColumn<Article, Button> deleteButtonColumn = new TableColumn<>("");

    ObservableList<Categorie> categorieObservableList = FXCollections.observableArrayList();
    ObservableList<Article> dataArticleList = FXCollections.observableArrayList();

    private ArticleService articleService;
    private CategorieService categorieService;

    public ArticleController() {
        this.articleService = new ArticleRepository();
        this.categorieService = new CategorieRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (colId != null){
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
            colCategorie.setCellValueFactory(new PropertyValueFactory<Article, Categorie>("categorie"));

            colCategorie.setCellFactory(column -> {
                TableCell<Article, Categorie> tableCell = new TableCell<>() {
                    @Override
                    protected void updateItem(Categorie categorie, boolean empty) {
                        super.updateItem(categorie, empty);
                        if (categorie == null || empty) {
                            setText(null);
                        } else {
//                            Session session = HibernateUtil.getSessionFactory().openSession();
//                            try {
//                                session.refresh(categorie);
//                                setText(categorie.getLibelle());
//                            } catch (HibernateException e) {
//                                e.printStackTrace();
//                                setText("Inconnue");
//                            } finally {
//                                session.close();
//                            }

                            setText(categorie.getLibelle());
                        }
                    }
                };
                return tableCell;
            });


            colPrixVente.setCellValueFactory(new PropertyValueFactory<>("pv"));
            colPrixVente.setCellFactory(column -> new TableCell<Article, Integer>() {
                private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

                @Override
                protected void updateItem(Integer prixVente, boolean empty) {
                    super.updateItem(prixVente, empty);
                    if (prixVente == null || empty) {
                        setText(null);
                    } else {
                        setText(decimalFormat.format(prixVente));
                    }
                }
            });

            editButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createEditButton(param.getValue())));
            deleteButtonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(createDeleteButton(param.getValue())));

            editButtonColumn.setCellFactory(param -> new TableCell<Article, Button>() {
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

            deleteButtonColumn.setCellFactory(param -> new TableCell<Article, Button>() {
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

            tableViewArticle.getColumns().addAll(editButtonColumn, deleteButtonColumn);

            loadData();
        }
    }

    public void addArticle(ActionEvent event) throws IOException {
        openModal(new Article(), event);
    }


    private void refreshData() {
        dataArticleList.clear();
        dataArticleList.addAll(articleService.getAll());
    }

    public void loadData() {
        refreshData();
        tableViewArticle.setItems(dataArticleList);

    }

    private Button createEditButton(Article article) {
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-text-fill: #fff !important");
        editButton.setOnAction(event -> {
            try {
                openModal(article, event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return editButton;
    }

    private Button createDeleteButton(Article article) {
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: #fff !important");
        deleteButton.setOnAction(event -> {
            if (AlertDialog.yesNoDialog("Voulez-vous vraiment supprimer cette ressource ?")) {
                articleService.delete(article.getId());
                refreshData();
                AlertDialog.infoDialog("La ressource a été supprimée avec succès");
            }
        });
        return deleteButton;
    }

    private void openModal(Article article, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Settings/Article/form.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Article");
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

        ComboBox<String> categorieComboBox = (ComboBox) root.lookup("#categorieComboBox");

        categorieObservableList.clear();
        categorieObservableList.addAll(categorieService.getAll());

        categorieComboBox.getItems().clear();
        for (Categorie categorie : categorieObservableList) {
            categorieComboBox.getItems().add(categorie.getLibelle());
        }
        TextFields.bindAutoCompletion(categorieComboBox.getEditor(), categorieComboBox.getItems());

        if (article.getId() != null){
            TextField libelle = (TextField) root.lookup("#libelle");
            TextField pv = (TextField) root.lookup("#pv");
            libelle.setText(article.getLibelle());
            pv.setText(String.valueOf(article.getPv()));
            categorieComboBox.getSelectionModel().select(article.getCategorie().getLibelle());
            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!libelle.getText().isEmpty()) {
                        saveBtn.setDisable(true);
                        libelle.setDisable(true);
                        closeButton.setDisable(true);

                        article.setLibelle(libelle.getText());
                        article.setPv(Integer.valueOf(pv.getText()));
                        String categorieValue = categorieComboBox.getSelectionModel().getSelectedItem() != null ? categorieComboBox.getSelectionModel().getSelectedItem() : categorieComboBox.getEditor().getText();

                        article.setCategorie(categorieService.getFromString(categorieValue));

                        articleService.edit(article);
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
            TextFields.bindAutoCompletion(categorieComboBox.getEditor(), categorieComboBox.getItems());
            saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TextField libelle = (TextField) root.lookup("#libelle");
                    TextField pv = (TextField) root.lookup("#pv");

                    if (!pv.getText().isEmpty() && !libelle.getText().isEmpty() && !categorieComboBox.getSelectionModel().isEmpty()) {
                        saveBtn.setDisable(true);
                        libelle.setDisable(true);
                        closeButton.setDisable(true);
                        article.setLibelle(libelle.getText());
                        article.setPv(Integer.valueOf(pv.getText()));
                        article.setCategorie(categorieService.getFromString(categorieComboBox.getSelectionModel().getSelectedItem()));
                        articleService.save(article);
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
