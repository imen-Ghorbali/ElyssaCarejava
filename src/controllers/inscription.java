package tn.esprit.controllers;

import javafx.scene.control.*;
import tn.esprit.models.user;
import tn.esprit.models.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import tn.esprit.services.service_user;
import java.io.IOException;
import javafx.scene.layout.BorderPane;

public class inscription {

    private final service_user user_service = new service_user();

    @FXML
    private TextField tf_email;

    @FXML
    private Button btn_login;

    @FXML
    private TextField tf_name;

    @FXML
    private PasswordField tf_password;

    @FXML
    private ChoiceBox<Role> tf_role;

    @FXML
    public void initialize() {
        tf_role.getItems().addAll(Role.values()); // PATIENT, MEDECIN, ADMIN
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void ajouter(ActionEvent event) {
        String name = tf_name.getText();
        String email = tf_email.getText();
        String password = tf_password.getText(); // 🔴 Mot de passe en clair
        Role role = tf_role.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

        if (!name.matches("^[a-zA-Z\\s]{2,}$")) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom doit contenir au moins 2 lettres.");
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez entrer une adresse e-mail valide.");
            return;
        }

        if (password.length() < 6 || !password.matches(".*[0-9].*") || !password.matches(".*[A-Z].*")) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe faible", "Le mot de passe doit contenir au moins 6 caractères, un chiffre et une lettre majuscule.");
            return;
        }

        // 🔴 Ici on stocke le mot de passe tel quel (sans hash)
        user u = new user(0, name, email, role, password);

        try {
            user_service.add(u);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "✅ Utilisateur ajouté avec succès !");

            // Charger navbar.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
            Parent root = loader.load();

            NavbarController navbarController = loader.getController();
            navbarController.setUser(u);  // Passer l'utilisateur à la navbar
            navbarController.setMainLayout((BorderPane) root); // Lier le BorderPane

            tf_email.getScene().setRoot(root);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout", e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/connexion.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btn_login.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Page de Connexion");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

