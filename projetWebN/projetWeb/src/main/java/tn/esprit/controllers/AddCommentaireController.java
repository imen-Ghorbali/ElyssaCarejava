package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceCommentaire;
import tn.esprit.MainFx;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddCommentaireController {
    @FXML private TextField nomUtilisateurField;
    @FXML private TextArea contenuField;
    @FXML private Label likeCountLabel;
    @FXML private Button likeButton;

    private int blogId;
    private int likeCount = 0;
    private final ServiceCommentaire commentaireService = new ServiceCommentaire();

    @FXML
    public void initialize() {
        likeCountLabel.setText(String.valueOf(likeCount));
    }

    @FXML
    private void handleSubmit() {
        if (!validateFields()) {
            return;
        }

        try {
            Commentaire commentaire = new Commentaire(
                    blogId,
                    likeCount,
                    nomUtilisateurField.getText(),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    contenuField.getText()
            );

            if (commentaireService.add(commentaire) > 0) { // Modifiez votre ServiceCommentaire pour retourner l'ID généré
                showSuccessAlert("Commentaire ajouté avec succès");
                closeWindow();
            } else {
                showErrorAlert("Échec de l'ajout du commentaire");
            }
        } catch (Exception e) {
            showErrorAlert("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLike() {
        likeCount++;
        likeCountLabel.setText(String.valueOf(likeCount));
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateFields() {
        if (nomUtilisateurField.getText().isEmpty() || contenuField.getText().isEmpty()) {
            showAlert("Validation", "Champs manquants",
                    "Veuillez remplir tous les champs obligatoires",
                    Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void closeWindow() {
        nomUtilisateurField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        showAlert("Succès", null, message, Alert.AlertType.INFORMATION);
    }

    private void showErrorAlert(String message) {
        showAlert("Erreur", null, message, Alert.AlertType.ERROR);
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }
}