package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceCommentaire;
import tn.esprit.MainFx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddCommentaireController {
    @FXML private TextField nomField;
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
            likeCount++;
            likeLabel.setText(String.valueOf(likeCount));
        });
    }

    @FXML
    private void handleSubmit() {
        if (nomField.getText().isEmpty() || contenuField.getText().isEmpty()) {
            showAlert("Erreur", "Champs manquants", "Veuillez remplir tous les champs", Alert.AlertType.WARNING);
            return;
        }

        Commentaire commentaire = new Commentaire(
                blogId,
                likeCount,
                nomField.getText(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                contenuField.getText()
        );

        if (commentaireService.add(commentaire) > 0) {
            showAlert("Succès", null, "Commentaire ajouté avec succès", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showAlert("Erreur", null, "Échec de l'ajout", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        nomField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}