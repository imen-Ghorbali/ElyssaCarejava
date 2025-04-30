package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceSponsor;

import java.io.IOException;

public class AjouterSponsorController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField prixField;

    private final ServiceSponsor serviceSponsor = new ServiceSponsor();

    @FXML
    void handleSubmit(ActionEvent event) {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String type = typeField.getText();
        String prixText = prixField.getText();

        if (!isValidField(name)) {
            showErrorAlert("Erreur", "Le nom est obligatoire (au moins 4 caractères, sans espace au début)");
            return;
        }
        if (!isValidField(description)) {
            showErrorAlert("Erreur", "La description est obligatoire (au moins 4 caractères, sans espace au début)");
            return;
        }
        if (!isValidField(type)) {
            showErrorAlert("Erreur", "Le type est obligatoire (au moins 4 caractères, sans espace au début)");
            return;
        }
        if (prixText.isEmpty()) {
            showErrorAlert("Erreur", "Le prix est obligatoire");
            return;
        }

        try {
            int prix = Integer.parseInt(prixText);
            if (prix <= 0) {
                showErrorAlert("Erreur", "Le prix doit être strictement positif");
                return;
            }

            sponsor newSponsor = new sponsor();
            newSponsor.setName(name.trim());
            newSponsor.setDescription(description.trim());
            newSponsor.setType(type.trim());
            newSponsor.setPrix(prix);

            serviceSponsor.add(newSponsor);

            showSuccessAlert("Succès", "Sponsor ajouté avec succès");
            navigateToAfficherSponsors();

        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Le prix doit être un nombre entier");
        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur technique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidField(String value) {
        return value != null && value.trim().length() >= 4 && !value.startsWith(" ");
    }

    private void navigateToAfficherSponsors() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichersponsor.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nameField.getScene().getWindow();
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
        // Initialisation future si besoin
    }
}
