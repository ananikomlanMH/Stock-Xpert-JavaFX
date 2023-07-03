package com.anani.stockxpert;

import com.anani.stockxpert.Repository.UtilisateurRepository;
import com.anani.stockxpert.Util.AlertDialog;
import com.anani.stockxpert.Util.SessionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    double x, y;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader1 = new FXMLLoader(Main.class.getResource("fxml/loader.fxml"));
        Parent root = fxmlLoader1.load();
        Scene scene0 = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.getIcons().add(new Image(Main.class.getResourceAsStream("images/icon.png")));
        ProgressBar progressBar = (ProgressBar) root.lookup("#progressBar");
        Text progressPourcentage = (Text) root.lookup("#progressPourcentage");
        Text progressText = (Text) root.lookup("#progressText");

        stage.setTitle("StockXpert");
        stage.setScene(scene0);
        stage.show();

        new Thread(() -> {
            for (double progress = 0.0; progress <= 1.0; progress += 0.01) {
                final double updateProgress = progress;
                Platform.runLater(() -> progressBar.setProgress(updateProgress));

                try {
                    Thread.sleep(25); // Attendre pendant 50 millisecondes
                    progressPourcentage.setText(String.valueOf((int) Math.round(updateProgress * 100)) + "%");

                    if (updateProgress <= 0.1) {
                        progressText.setText("Initialisation");
                    } else if (updateProgress <= 0.2) {
                        progressText.setText("Chargement des modules");
                    } else if (updateProgress <= 0.3) {
                        progressText.setText("Traitement des données");
                    } else if (updateProgress <= 0.4) {
                        progressText.setText("Vérification des paramètres");
                    } else if (updateProgress <= 0.5) {
                        progressText.setText("Configuration en cours");
                    } else if (updateProgress <= 0.6) {
                        progressText.setText("Chargement des ressources");
                    } else if (updateProgress <= 0.7) {
                        progressText.setText("Analyse des données");
                    }

                    if (updateProgress >= 0.75) {
                        progressText.setText("Connexion");
                        Platform.runLater(() -> {
                            try {
                                FXMLLoader fxmlLoaderLogin = new FXMLLoader(Main.class.getResource("fxml/login.fxml"));
                                Scene scene_login = new Scene(fxmlLoaderLogin.load());
//                                stage.initStyle(StageStyle.UNDECORATED);

//                                stage.getIcons().add(new Image(Main.class.getResourceAsStream("images/icon.png")));

                                Screen screen = Screen.getPrimary();
                                double screenWidth = screen.getBounds().getWidth();
                                double screenHeight = screen.getBounds().getHeight();

                                stage.setTitle("StockXpert");
                                stage.setScene(scene_login);
                                stage.centerOnScreen();

                                Button loginBtn = (Button) scene_login.lookup("#loginBtn");
                                TextField login = (TextField) scene_login.lookup("#login");
                                TextField password = (TextField) scene_login.lookup("#password");
                                loginBtn.setOnAction(event -> {
                                    loginBtn.setDisable(true);
                                    loginBtn.setDisable(true);
                                    if (!login.getText().isEmpty() && !password.getText().isEmpty()) {
                                        if (new UtilisateurRepository().login(login.getText(), password.getText())) {
//                                            SessionManager.getInstance().login(user);

                                            FXMLLoader fxmlLoader0 = new FXMLLoader(Main.class.getResource("fxml/loader.fxml"));
                                            try {
                                                Scene scene1 = new Scene(fxmlLoader0.load());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            stage.setTitle("StockXpert");
                                            stage.setScene(scene0);
                                            stage.show();

                                            new Thread(() -> {
                                                for (double progress2 = 0.75; progress2 <= 1.0; progress2 += 0.01) {
                                                    final double updateProgress2 = progress2;
                                                    Platform.runLater(() -> progressBar.setProgress(updateProgress2));

                                                    try {
                                                        Thread.sleep(25); // Attendre pendant 50 millisecondes
                                                        progressPourcentage.setText(String.valueOf((int) Math.round(updateProgress2 * 100)) + "%");

                                                        if (updateProgress2 <= 0.8) {
                                                            progressText.setText("Validation des données");
                                                        } else if (updateProgress2 <= 0.9) {
                                                            progressText.setText("Finalisation");
                                                        }

                                                        if (updateProgress2 >= 0.99) {
                                                            progressText.setText("Ouverture");
                                                            Platform.runLater(() -> {
                                                                try {
                                                                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/main.fxml"));
                                                                    Scene scene = new Scene(fxmlLoader.load());

                                                                    scene.setOnMousePressed(mouseEvent -> {
                                                                        x = mouseEvent.getSceneX();
                                                                        y = mouseEvent.getSceneY();
                                                                    });

                                                                    scene.setOnMouseDragged(mouseEvent -> {
                                                                        stage.setX(mouseEvent.getScreenX() - x);
                                                                        stage.setY(mouseEvent.getScreenY() - y);
                                                                    });

                                                                    stage.setTitle("StockXpert");
                                                                    stage.setScene(scene);
                                                                    stage.centerOnScreen();

                                                                    stage.show();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            });
                                                        }
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                        } else {
                                            loginBtn.setDisable(false);
                                            AlertDialog.warningDialog("Login ou password invalide!");
                                        }
                                    } else {
                                        loginBtn.setDisable(false);
                                        AlertDialog.warningDialog("Login ou password invalide!");
                                    }
                                });

                                stage.show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public static void main(String[] args) {
        launch();
    }
}
