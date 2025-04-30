package tn.esprit.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.esprit.models.events;
import tn.esprit.models.sponsor;
import tn.esprit.services.MailService;
import tn.esprit.services.OpenAIService;

import tn.esprit.services.ServiceEvent;
import tn.esprit.services.ServiceSponsor;
import javafx.geometry.Pos;   // For positioning inside StackPane
import javafx.scene.layout.StackPane; // For StackPane usage
import javafx.scene.layout.Pane;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class AjouterEventsController {

    @FXML private TextField titleField;
    @FXML private TextField description;
    @FXML private TextField lieu;
    @FXML private DatePicker date;
    public void setSelectedDate(LocalDate selectedDate) {
        if (selectedDate != null) {
            date.setValue(selectedDate);
        }
    }
    @FXML private ImageView previewImage;
    @FXML private TextField image;
    @FXML private ComboBox<sponsor> comboBoxSponsor;
    @FXML private Button mapButton;

    private Double latitude;
    private Double longitude;

    private final ServiceEvent serviceEvent = new ServiceEvent();
    private final ServiceSponsor serviceSponsor = new ServiceSponsor();

    @FXML
    void initialize() {
        // Limiter le DatePicker à la date d'aujourd'hui et aux dates futures
        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now())); // Désactive les dates avant aujourd'hui
            }
        });

        // Initialisation du ComboBox des sponsors
        List<sponsor> sponsors = serviceSponsor.getAll();
        ObservableList<sponsor> sponsorList = FXCollections.observableArrayList(sponsors);
        comboBoxSponsor.setItems(sponsorList);

        // Afficher uniquement le nom du sponsor dans le ComboBox
        comboBoxSponsor.setCellFactory(param -> new ListCell<sponsor>() {
            @Override
            protected void updateItem(sponsor item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName()); // Affiche uniquement le nom du sponsor
                }
            }
        });
        comboBoxSponsor.setButtonCell(new ListCell<sponsor>() {
            @Override
            protected void updateItem(sponsor item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName()); // Affiche uniquement le nom du sponsor dans le bouton
                }
            }
        });
    }

    @FXML
    void handleImageBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(titleField.getScene().getWindow());
        if (selectedFile != null) {
            image.setText(selectedFile.getAbsolutePath());
            previewImage.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    void openMapp(ActionEvent event) {
        Stage mapStage = new Stage();
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        JavaBridge bridge = new JavaBridge();

        // ❗ CORRECT BRIDGE INJECTION ICI
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", bridge);
            }
        });

        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Sélection de lieu</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
            <style>
                body { margin: 0; padding: 0; }
                #map { height: 100vh; width: 100vw; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            <script>
                var map = L.map('map').setView([36.8, 10.2], 7);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; OpenStreetMap'
                }).addTo(map);
                
                var marker = null;

                map.on('click', function(e) {
                    if (marker) {
                        map.removeLayer(marker);
                    }
                    marker = L.marker(e.latlng).addTo(map);

                    fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${e.latlng.lat}&lon=${e.latlng.lng}`)
                        .then(response => response.json())
                        .then(data => {
                            const placeName = data.display_name || "Lieu inconnu";
                            if (window.javaBridge) {
                                window.javaBridge.updateLocation(
                                    e.latlng.lat.toFixed(6),
                                    e.latlng.lng.toFixed(6),
                                    placeName
                                );
                            }
                        })
                        .catch(error => {
                            console.error("Erreur:", error);
                            if (window.javaBridge) {
                                window.javaBridge.updateLocation(
                                    e.latlng.lat.toFixed(6),
                                    e.latlng.lng.toFixed(6),
                                    "Coordonnées: " + e.latlng.lat.toFixed(4) + ", " + e.latlng.lng.toFixed(4)
                                );
                            }
                        });
                });
            </script>
        </body>
        </html>
        """;

        webEngine.loadContent(htmlContent);

        mapStage.setScene(new Scene(webView, 800, 600));
        mapStage.setTitle("Sélectionnez un lieu sur la carte");
        mapStage.initModality(Modality.APPLICATION_MODAL);
        mapStage.show();
    }

    public class JavaBridge {
        public void updateLocation(String lat, String lng, String place) {
            Platform.runLater(() -> {
                try {
                    latitude = Double.parseDouble(lat);
                    longitude = Double.parseDouble(lng);
                    lieu.setText(place);
                    System.out.println("Lieu sélectionné: " + place + " (" + lat + ", " + lng + ")");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la mise à jour de l'adresse: " + e.getMessage());
                }
            });
        }
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        if (!validateForm()) return;

        try {
            events newEvent = new events();
            newEvent.setTitle(titleField.getText().trim());
            newEvent.setDescription(description.getText().trim());
            newEvent.setLieu(lieu.getText().trim());
            newEvent.setDate(date.getValue().atStartOfDay());
            newEvent.setImage(image.getText().trim());
            newEvent.setSponsor(comboBoxSponsor.getValue());

            serviceEvent.addEventWithCoordinates(newEvent, latitude, longitude);
            sponsor selectedSponsor = comboBoxSponsor.getValue();
            if (selectedSponsor != null) {
                int sponsorId = selectedSponsor.getId(); // Obtient l'ID du sponsor
                String sponsorEmail = serviceSponsor.getSponsorEmail(sponsorId); // Utilise le service pour récupérer l'email
                String sponsorName = selectedSponsor.getName();
                String eventTitle = newEvent.getTitle();
                String eventDate = newEvent.getDate().toLocalDate().toString();
                String eventLocation = newEvent.getLieu();
                MailService mailService = new MailService();

                // Envoie l'email au sponsor
                mailService.sendSponsorNotification(sponsorEmail, sponsorName, eventTitle, eventDate, eventLocation);
            }
            showAlert("Succès", "Événement ajouté avec succès", Alert.AlertType.INFORMATION);
            redirectToEventsView();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (titleField.getText().trim().length() < 8) {
            showAlert("Erreur", "Le titre doit contenir au moins 8 caractères", Alert.AlertType.ERROR);
            return false;
        }
        if (lieu.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un lieu sur la carte", Alert.AlertType.ERROR);
            return false;
        }
        if (date.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une date", Alert.AlertType.ERROR);
            return false;
        }
        if (image.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une image", Alert.AlertType.ERROR);
            return false;
        }
        if (comboBoxSponsor.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un sponsor", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToEventsView() throws IOException {
        Stage stage = (Stage) titleField.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("/AfficherEvents.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    private void goToList(ActionEvent event) {
        try {
            // Ferme la fenêtre actuelle
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Ouvre la fenêtre de liste des événements
            Stage stage = new Stage();
            BorderPane root = FXMLLoader.load(getClass().getResource("/AfficherEvents.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des événements");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste", Alert.AlertType.ERROR);
        }
    }
    private final OpenAIService openAIService = new OpenAIService();
 //ia generate
 @FXML
 private void handleGenerateDescription(ActionEvent event) {
     String titleText = titleField.getText().trim();
     if (titleText.length() < 5) {
         showAlert("Erreur", "Le titre doit contenir au moins 5 caractères", Alert.AlertType.WARNING);
         return;
     }

     // Indicateur de progression
     ProgressIndicator progress = new ProgressIndicator();
     progress.setMaxSize(50, 50);

     // Overlay semi-transparent
     StackPane overlay = new StackPane(progress);
     overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
     overlay.setAlignment(Pos.CENTER);

     // Ajout à la scène
     Platform.runLater(() -> {
         Scene scene = titleField.getScene();
         if (scene != null && scene.getRoot() instanceof Pane root) {
             if (!root.getChildrenUnmodifiable().contains(overlay)) {
                 ((Pane) root).getChildren().add(overlay);
             }
         }
     });

     // Thread pour appel OpenAI
     new Thread(() -> {
         try {
             String generated = openAIService.generateDescription(titleText);
             Platform.runLater(() -> {
                 // Supprime overlay et remplit la description
                 Scene scene = titleField.getScene();
                 if (scene != null && scene.getRoot() instanceof Pane root) {
                     root.getChildren().remove(overlay);
                 }
                 description.setText(generated);
             });
         } catch (IOException e) {
             e.printStackTrace(); // Pour debug console
             System.err.println("Erreur OpenAI: " + e.getMessage());

             Platform.runLater(() -> {
                 Scene scene = titleField.getScene();
                 if (scene != null && scene.getRoot() instanceof Pane root) {
                     root.getChildren().remove(overlay);
                 }
                 showAlert("Erreur", "Une erreur est survenue lors de la génération.", Alert.AlertType.ERROR);
             });
         }
     }).start();
 }



}
