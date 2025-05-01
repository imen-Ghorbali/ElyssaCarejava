package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceCommentaire;
import tn.esprit.models.session;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddCommentaireController {

    @FXML private TextArea contenuField;
    @FXML private Label likeLabel;
    @FXML private Button likeButton;

    private int blogId;
    private int likeCount = 0;
    private final ServiceCommentaire commentaireService = new ServiceCommentaire();

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    @FXML
    private void initialize() {
        likeLabel.setText(String.valueOf(likeCount));

        likeButton.setOnAction(event -> {
            // Vérifie si l'utilisateur a déjà liké ce commentaire dans cette session
            if (session.hasLiked(blogId)) {
                showAlert("Info", null, "Vous avez déjà liké ce commentaire.", Alert.AlertType.INFORMATION);
            } else {
                likeCount++;
                likeLabel.setText(String.valueOf(likeCount));
                session.addLikedComment(blogId); // Ajoute le blog/commentaire liké à la session
                likeButton.setDisable(true); // Désactive le bouton
            }
        });
    }

    @FXML
    private void handleSubmit() {
        if (!validateFields()) {
            return;
        }

        String nom = session.getCurrentUser().getName();
        // récupère le nom automatiquement
        String contenu = contenuField.getText().trim();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Commentaire commentaire = new Commentaire(
                blogId,
                likeCount,
                nom,
                date,
                contenu
        );

        if (commentaireService.add(commentaire) > 0) {
            showAlert("Succès", null, "Commentaire ajouté avec succès", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showAlert("Erreur", null, "Échec de l'ajout du commentaire", Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (contenuField.getText() == null || contenuField.getText().trim().isEmpty()) {
            errors.append("- Le contenu du commentaire est requis.\n");
        } else if (contenuField.getText().trim().length() < 5) {
            errors.append("- Le commentaire doit contenir au moins 5 caractères.\n");
        }

        if (errors.length() > 0) {
            showAlert("Champs invalides", "Veuillez corriger les erreurs suivantes :", errors.toString(), Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        contenuField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
