package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MaterielMedicalService;

import java.io.File;
import java.io.IOException;

public class ModifierMaterielController {

    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField statutField;
    @FXML private TextField imagePathField;
    @FXML private ImageView imagePreview;
    @FXML private Button uploadButton;

    private MaterielMedical materiel;
    private final MaterielMedicalService materielMedicalService = new MaterielMedicalService();
    private String imagePath;

    // Méthode pour initialiser les champs avec les données du matériel
    public void setMateriel(MaterielMedical materiel) {
        this.materiel = materiel;
        nomField.setText(materiel.getNom());
        descriptionField.setText(materiel.getDescription());
        prixField.setText(String.valueOf(materiel.getPrix()));
        statutField.setText(materiel.getStatut());

        // Chargement de l'image si elle existe
        if (materiel.getImage() != null && !materiel.getImage().isEmpty()) {
            imagePath = materiel.getImage();
            imagePathField.setText(imagePath);
            imagePreview.setImage(new Image(new File(imagePath).toURI().toString()));
        }
    }

    @FXML
    private void handleSave() {
        // Vérification des champs vides
        if (nomField.getText().isEmpty() || descriptionField.getText().isEmpty()
                || prixField.getText().isEmpty() || statutField.getText().isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis", Alert.AlertType.ERROR);
            return;
        }

        // Validation du prix
        double prix;
        try {
            prix = Integer.parseInt(prixField.getText());
            if (prix <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide supérieur à 0", Alert.AlertType.ERROR);
            return;
        }

        // Mise à jour du matériel
        materiel.setNom(nomField.getText());
        materiel.setDescription(descriptionField.getText());
        materiel.setPrix(prix);
        materiel.setStatut(statutField.getText());
        materiel.setImage(imagePath);

        // Enregistrement du matériel modifié dans le service
        materielMedicalService.modifier(materiel);
        showAlert("Succès", "Matériel médical modifié avec succès", Alert.AlertType.INFORMATION);
        redirectToAffichage();
    }

    @FXML
    private void handleCancel() {
        redirectToAffichage();
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToAffichage() {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageMateriels.fxml"));
            Parent root = loader.load();

            // Récupérer l'objet Stage de la fenêtre actuelle
            Stage stage = (Stage) nomField.getScene().getWindow();

            // Créer une nouvelle scène
            stage.setScene(new javafx.scene.Scene(root));

            // Afficher la scène
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page d'affichage.", Alert.AlertType.ERROR);
        }
    }
}


