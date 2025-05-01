package tn.esprit.controllers;
import javafx.scene.control.*;
import tn.esprit.models.Role;
import tn.esprit.models.user;
import tn.esprit.models.session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;  // Assure-toi d'ajouter cette ligne
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.services.service_user;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;


public class updateUser {
    @FXML
    private Button delete;

    @FXML
    private TextField email2;

    @FXML
    private TextField name2;

    @FXML
    private TextField password2;

    @FXML
    private Button back;

    @FXML
    private Button updatePassword;

    @FXML
    private Button update;
    private user currentUser;
    private final service_user userService = new service_user();




    public void setUser(user user) {
        this.currentUser = user;
        name2.setText(user.getName());
        email2.setText(user.getEmail());
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void updateUser(ActionEvent event) {
        if (currentUser != null) {
            currentUser.setName(name2.getText());
            currentUser.setEmail(email2.getText());

            userService.update(currentUser);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour avec succès !");

            try {
                // Charger la page navbar.fxml
                FXMLLoader navbarLoader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
                Parent navbarRoot = navbarLoader.load();

                // Récupérer le contrôleur de la navbar
                NavbarController navbarController = navbarLoader.getController();
                navbarController.setUser(currentUser); // Transmettre l'utilisateur modifié

                // Charger la vue afficherUser.fxml dans le BorderPane de navbar
                FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/afficherUser.fxml"));
                Parent userView = userLoader.load();
                afficherUser userController = userLoader.getController();
                userController.setUser(currentUser);

                // Insérer afficherUser dans le BorderPane de navbar
                navbarController.setMainLayout((BorderPane) navbarRoot);
                navbarController.setUser(currentUser);
                ((BorderPane) navbarRoot).setCenter(userView);

                // Remplacer la scène actuelle par la scène navbar
                update.getScene().setRoot(navbarRoot);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Charger la page navbar
            FXMLLoader navbarLoader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
            Parent navbarRoot = navbarLoader.load();

            // Récupérer le contrôleur de la navbar
            NavbarController navbarController = navbarLoader.getController();
            navbarController.setUser(currentUser); // transmet l'utilisateur connecté

            // Charger la vue afficherUser
            FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/afficherUser.fxml"));
            Parent userView = userLoader.load();
            afficherUser userController = userLoader.getController();
            userController.setUser(currentUser);

            // Insérer afficherUser dans le BorderPane central
            navbarController.setMainLayout((BorderPane) navbarRoot); // important pour setupButtonActions
            navbarController.setUser(currentUser);
            navbarController.setMainLayout((BorderPane) navbarRoot); // Pour que le loadView fonctionne
            ((BorderPane) navbarRoot).setCenter(userView);

            // Remplacer la scène actuelle par la scène navbar
            back.getScene().setRoot(navbarRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public void deleteUser(ActionEvent event) {
        if (currentUser != null) {
            boolean deleted = userService.delete(currentUser);

            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Supprimé", "Utilisateur supprimé avec succès !");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/connexion.fxml"));
                    Parent root = loader.load();
                    delete.getScene().setRoot(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'utilisateur.");
            }
        }
    }
    @FXML
    private void handleUpdatePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updatePassword.fxml"));
            Parent root = loader.load();

            // ✅ Renommer ici pour éviter le conflit avec le bouton
            tn.esprit.controllers.updatePassword controller = loader.getController();
            controller.setUser(currentUser);

            updatePassword.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /*private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }*/
}