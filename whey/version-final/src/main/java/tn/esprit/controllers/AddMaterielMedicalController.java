package tn.esprit.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import tn.esprit.MainFx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MaterielMedicalService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AddMaterielMedicalController {

    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField statutField;
    @FXML private TextField imagePathField;
    @FXML private Button uploadButton;
    @FXML private ImageView imagePreview;

    private final MaterielMedicalService materielMedicalService = new MaterielMedicalService();
    private File selectedImageFile;
    private final String IMAGE_DIRECTORY = "src/main/resources/images/materiel/";

    @FXML
    void initialize() {
        // Créer le dossier s'il n'existe pas
        new File(IMAGE_DIRECTORY).mkdirs();

        // Afficher le bouton "Parcourir" au démarrage
        uploadButton.setVisible(true);
    }

    @FXML
    void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            try {
                selectedImageFile = file;
                imagePathField.setText(file.getAbsolutePath());

                // Affichage de l'image dans l'aperçu
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
                imagePreview.setFitWidth(150);
                imagePreview.setFitHeight(150);
                imagePreview.setPreserveRatio(true);

                // Cacher le bouton "Parcourir" après sélection d'une image
                uploadButton.setVisible(false);

            } catch (Exception e) {
                showErrorAlert("Erreur", "Impossible de charger l'image: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String statut = statutField.getText().trim();
        String prixText = prixField.getText().trim();

        // Validation des champs
        if (nom.isEmpty() || description.isEmpty() || statut.isEmpty() || prixText.isEmpty()) {
            showErrorAlert("Erreur", "Tous les champs sont obligatoires");
            return;
        }

        try {
            double prixDecimal = Double.parseDouble(prixText);
            if (prixDecimal <= 0) {
                showErrorAlert("Erreur", "Le prix doit être supérieur à 0");
                return;
            }

            int prix = (int) Math.round(prixDecimal);

            // Création du matériel médical
            MaterielMedical materiel = new MaterielMedical();
            materiel.setNom(nom);
            materiel.setDescription(description);
            materiel.setStatut(statut);
            materiel.setPrix(prix);

            // Gestion de l'image
            if (selectedImageFile != null) {
                String imagePath = saveImageToDirectory(selectedImageFile, nom);
                materiel.setImage(imagePath);
            }

            // Ajout en base de données
            materielMedicalService.ajouter(materiel);

            showSuccessAlert("Succès", "Matériel médical ajouté avec succès");
            clearFields();
            MainFx.showAffichageMateriel();

        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Veuillez entrer un prix valide");
        } catch (IOException e) {
            showErrorAlert("Erreur", "Erreur lors de l'enregistrement de l'image");
        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur technique: " + e.getMessage());
        }
    }

    private String saveImageToDirectory(File imageFile, String materielName) throws IOException {
        // Créer un nom de fichier unique
        String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
        String newFileName = materielName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + extension;

        Path destination = Path.of(IMAGE_DIRECTORY + newFileName);
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Retourner le chemin relatif pour le stockage en base
        return "images/materiel/" + newFileName;
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
        statutField.clear();
        prixField.clear();
        imagePathField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;

        // Réafficher le bouton "Parcourir"
        uploadButton.setVisible(true);
    }

    @FXML
    void handleCancel(ActionEvent event) {
        // Option pour annuler l'ajout et revenir à la page précédente ou fermer la fenêtre
        clearFields();
    }
}
