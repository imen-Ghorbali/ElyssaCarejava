package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceSponsor;

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
}

