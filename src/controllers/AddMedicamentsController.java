
package tn.esprit.controllers;
import tn.esprit.mainFX;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import tn.esprit.models.Medicaments;
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MedicamentService;
import tn.esprit.services.MaterielMedicalService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javafx.event.ActionEvent;

public class AddMedicamentsController {

    @FXML private TextField classeCombo;
    @FXML private TextField descriptionField;
    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private TextField imagePathField;
    @FXML private Button uploadButton;
    @FXML private ImageView imagePreview;
    @FXML private ComboBox<MaterielMedical> materielCombo;

    private final MedicamentService medicamentService = new MedicamentService();
    private final MaterielMedicalService materielService = new MaterielMedicalService();
    private File selectedImageFile;
    private final String IMAGE_DIRECTORY = "src/main/resources/images/medicaments/";

    @FXML
    void initialize() {
        new File(IMAGE_DIRECTORY).mkdirs();
        uploadButton.setVisible(true);
        materielCombo.getItems().setAll(materielService.getAll());
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
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
                imagePreview.setFitWidth(150);
                imagePreview.setFitHeight(150);
                imagePreview.setPreserveRatio(true);
                uploadButton.setVisible(false);
            } catch (Exception e) {
                showErrorAlert("Erreur", "Impossible de charger l'image: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String classe = classeCombo.getText().trim();
        String prixText = prixField.getText().trim();
        MaterielMedical selectedMateriel = materielCombo.getValue();

        if (nom.isEmpty() || description.isEmpty() || classe.isEmpty() || prixText.isEmpty()) {
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

            Medicaments medicament = new Medicaments();
            medicament.setNom(nom);
            medicament.setDescription(description);
            medicament.setClasse(classe);
            medicament.setPrix(prix);
            medicament.setMaterielRequis(selectedMateriel);

            if (selectedImageFile != null) {
                String imagePath = saveImageToDirectory(selectedImageFile, nom);
                medicament.setImage(imagePath);
            }

            medicamentService.ajouter(medicament);
            showSuccessAlert("Succès", "Médicament ajouté avec succès");
            clearFields();
            mainFX.showAffichageMedicaments();

        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Veuillez entrer un prix valide");
        } catch (IOException e) {
            showErrorAlert("Erreur", "Erreur lors de l'enregistrement de l'image");
        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur technique: " + e.getMessage());
        }
    }

    private String saveImageToDirectory(File imageFile, String medicamentName) throws IOException {
        String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
        String newFileName = medicamentName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + extension;
        Path destination = Path.of(IMAGE_DIRECTORY + newFileName);
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return "images/medicaments/" + newFileName;
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
        uploadButton.setVisible(true);
    }
}

