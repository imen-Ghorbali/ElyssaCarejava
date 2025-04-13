package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.Medicaments;
import tn.esprit.services.MedicamentService;

import java.io.File;

public class ModifierMedicaments {

    @FXML private TextField nomField;
    @FXML private TextField classeField;
    @FXML private TextField prixField;
    @FXML private TextField descriptionField;
    @FXML private TextField imagePathField;
    @FXML private ImageView imagePreview;
    @FXML private Button uploadButton;

    private Medicaments medicament;
    private final MedicamentService medicamentService = new MedicamentService();
    private String imagePath;

    public void setMedicament(Medicaments medicament) {
        this.medicament = medicament;
        nomField.setText(medicament.getNom());
        classeField.setText(medicament.getClasse());
        prixField.setText(String.valueOf(medicament.getPrix()));
        descriptionField.setText(medicament.getDescription());

        if (medicament.getImage() != null) {
            imagePath = medicament.getImage();
            imagePathField.setText(imagePath);
            imagePreview.setImage(new Image(new File(imagePath).toURI().toString()));
        }
    }

    @FXML
    private void handleSave() {
        if (nomField.getText().isEmpty() || classeField.getText().isEmpty()
                || prixField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis", Alert.AlertType.ERROR);
            return;
        }

        int prix;
        try {
            prix = Integer.parseInt(prixField.getText());
            if (prix <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide supérieur à 0", Alert.AlertType.ERROR);
            return;
        }

        medicament.setNom(nomField.getText());
        medicament.setClasse(classeField.getText());
        medicament.setPrix(prix);
        medicament.setDescription(descriptionField.getText());
        medicament.setImage(imagePath);

        medicamentService.modifier(medicament);
        showAlert("Succès", "Médicament modifié avec succès", Alert.AlertType.INFORMATION);
        ((Stage) nomField.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        ((Stage) nomField.getScene().getWindow()).close();
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(nomField.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            imagePathField.setText(imagePath);
            imagePreview.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
