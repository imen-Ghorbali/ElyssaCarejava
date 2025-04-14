package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceSponsor;

import java.io.IOException;

public class modifierSponsorController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField prixField;

    @FXML
    private TextField typeField;

    private sponsor currentSponsor;

    private final ServiceSponsor serviceSponsor = new ServiceSponsor();

    // Appelé pour remplir les champs avec le sponsor à modifier
    public void setSponsor(sponsor s) {
        this.currentSponsor = s;

        nomField.setText(s.getName());
        descriptionField.setText(s.getDescription());
        prixField.setText(String.valueOf(s.getPrix()));
        typeField.setText(s.getType());
    }

    @FXML
    private void handleModifier() {
        if (currentSponsor == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun sponsor à modifier !");
            return;
        }

        try {
            int prix = Integer.parseInt(prixField.getText());

            currentSponsor.setName(nomField.getText());
            currentSponsor.setDescription(descriptionField.getText());
            currentSponsor.setPrix(prix);
            currentSponsor.setType(typeField.getText());

            serviceSponsor.edit(currentSponsor);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Sponsor modifié avec succès !");
            navigateTosponsorList();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un entier valide.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void navigateTosponsorList() {
        try {
            // Charger la vue de la liste des événements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichersponsor.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) nomField.getScene().getWindow();

            // Remplacer la scène actuelle par la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Rafraîchir la liste des événements dans le contrôleur de la liste
            affichersponsorController affichersponsorController = loader.getController();
            // Appel de la méthode pour rafraîchir la liste

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer vers la liste des événements.");
        }
    }
}

