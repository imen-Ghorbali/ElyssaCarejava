package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceCommentaire;
import javafx.scene.Scene;

import java.io.IOException;

public class EditCommentaireController {
    @FXML private TextField nomField;
    @FXML private TextArea contenuField;
    @FXML private Label likeLabel;
    @FXML private Button likeButton;

    private Commentaire commentaire;
    private final ServiceCommentaire commentaireService = new ServiceCommentaire();

    // Méthode pour initialiser les données du commentaire
    public void setCommentaireData(Commentaire commentaire) {
        this.commentaire = commentaire;
        nomField.setText(commentaire.getNom_utilisateur());
        contenuField.setText(commentaire.getContenu());
        likeLabel.setText(String.valueOf(commentaire.getNombre_like()));
    }

    @FXML
    private void initialize() {
        // Action pour augmenter le nombre de likes
        likeButton.setOnAction(event -> {
            int likes = Integer.parseInt(likeLabel.getText()) + 1;
            likeLabel.setText(String.valueOf(likes));
        });
    }
    private Runnable successCallback;

    public void setSuccessCallback(Runnable callback) {
        this.successCallback = callback;
    }


    @FXML
    private void handleUpdate() {
        // Mettre à jour le commentaire avec les nouvelles valeurs
        commentaire.setNom_utilisateur(nomField.getText());
        commentaire.setContenu(contenuField.getText());
        commentaire.setNombre_like(Integer.parseInt(likeLabel.getText()));

        // Si la mise à jour réussie, afficher un message de succès
        if (commentaireService.update(commentaire)) {
            showAlert("Succès", "Commentaire modifié", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            // Si la mise à jour échoue, afficher un message d'erreur
            showAlert("Erreur", "Échec de la modification", Alert.AlertType.ERROR);
        }
        redirectToListCommentaire();
    }

    @FXML
    private void handleCancel() {
        // Annuler et revenir à la liste des commentaires
        redirectToListCommentaire();
    }

    private void closeWindow() {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Utilisation de null ici pour header
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void redirectToListCommentaire() {
        try {
            // Charger la vue de la liste des commentaires
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listCommentaire.fxml"));
            Parent root = loader.load(); // Charger la vue FXML

            // Obtenir la fenêtre actuelle (Stage) et changer la scène
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root)); // Mettre à jour la scène avec la nouvelle vue
            stage.show(); // Afficher la nouvelle scène

        } catch (IOException e) {
            // Gérer l'exception si le chargement échoue
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page d'affichage.", Alert.AlertType.ERROR);
        }
    }
}
