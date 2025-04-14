package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.events;
import tn.esprit.services.ServiceEvent;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AjouterEventsController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField description;

    @FXML
    private TextField lieu;

    @FXML
    private DatePicker date;

    @FXML
    private ImageView previewImage;

    @FXML
    private TextField image;

    private final ServiceEvent serviceEvent = new ServiceEvent();

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
            Image img = new Image(selectedFile.toURI().toString());
            previewImage.setImage(img);
        }
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        String titre = titleField.getText().trim();
        String descriptionField = description.getText().trim();
        String lieuField = lieu.getText().trim();
        String imagePathValue = image.getText().trim();
        LocalDate dateValue = date.getValue();

        if (titre.isEmpty()) {
            showErrorAlert("Erreur", "Le titre de l'événement est obligatoire");
            return;
        }
        if (descriptionField.isEmpty()) {
            showErrorAlert("Erreur", "La description est obligatoire");
            return;
        }
        if (lieuField.isEmpty()) {
            showErrorAlert("Erreur", "Le lieu est obligatoire");
            return;
        }
        if (dateValue == null) {
            showErrorAlert("Erreur", "La date est obligatoire");
            return;
        }
        if (imagePathValue.isEmpty()) {
            showErrorAlert("Erreur", "Veuillez sélectionner une image");
            return;
        }

        try {
            LocalDateTime dateTime = dateValue.atStartOfDay();

            events newEvent = new events();
            newEvent.setTitle(titre);
            newEvent.setDescription(descriptionField);
            newEvent.setLieu(lieuField);
            newEvent.setDate(dateTime);
            newEvent.setImage(imagePathValue);

            serviceEvent.add(newEvent);

            showSuccessAlert("Succès", "Événement ajouté avec succès");
            navigateToAfficherEvents();

        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur technique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToAfficherEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherevents.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        // Initialisation future si nécessaire
    }
}
