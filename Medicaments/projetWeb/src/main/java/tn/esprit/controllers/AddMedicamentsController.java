package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tn.esprit.models.Medicaments;
import tn.esprit.services.MedicamentService;

import java.io.File;

public class AddMedicamentsController {

    @FXML
    private TextField classeCombo;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prixField;

    @FXML
    private TextField imagePathField;
    @FXML
    private Button uploadButton;
    @FXML
    private ImageView imagePreview;

    private final MedicamentService medicamentService = new MedicamentService();
    private File selectedImageFile; // pour stocker l'image sélectionnée

    @FXML
    void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imagePathField.setText(file.getAbsolutePath());

            // Afficher l'image dans l'ImageView
            Image image = new Image(file.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String classe = classeCombo.getText().trim();
        String prixText = prixField.getText().trim();

        if (nom.isEmpty()) {
            showErrorAlert("Erreur", "Le nom du médicament est obligatoire");
            return;
        }

        if (description.isEmpty()) {
            showErrorAlert("Erreur", "La description est obligatoire");
            return;
        }

        if (classe.isEmpty()) {
            showErrorAlert("Erreur", "La classe thérapeutique est obligatoire");
            return;
        }

        if (prixText.isEmpty()) {
            showErrorAlert("Erreur", "Le prix est obligatoire");
            return;
        }

        try {
            double prixDecimal = Double.parseDouble(prixText);
            if (prixDecimal <= 0) {
                showErrorAlert("Erreur", "Le prix doit être supérieur à 0");
                return;
            }

            int prix = (int) Math.round(prixDecimal);

            Medicaments medicament = new Medicaments();
            medicament.setNom(nom);
            medicament.setDescription(description);
            medicament.setClasse(classe);
            medicament.setPrix(prix);
            // ⚠️ Tu peux aussi ici enregistrer le chemin de l’image dans ton objet si besoin
            // medicament.setImagePath(selectedImageFile.getAbsolutePath());

            medicamentService.ajouter(medicament);

            showSuccessAlert("Succès", "Médicament ajouté avec succès");
            clearFields();

        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Veuillez entrer un prix valide (ex: 15 ou 19.99)");
        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur technique: " + e.getMessage());
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

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        classeCombo.clear();
        prixField.clear();
        imagePathField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;
    }

    @FXML
    void initialize() {
        // rien de spécial pour le moment
    }
}
