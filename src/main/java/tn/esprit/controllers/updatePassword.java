package tn.esprit.controllers;
import tn.esprit.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import tn.esprit.services.service_consultation;
import tn.esprit.services.service_user;
import tn.esprit.models.session;
import java.sql.SQLException;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
public class updatePassword {
    @FXML
    private TextField ancien_password;

    @FXML
    private Button back;

    @FXML
    private TextField new_password;

    @FXML
    private Button update;

    private user currentUser;

    public void setUser(user user) {
        this.currentUser = user;
        // Tu peux aussi préremplir les champs ici si besoin
    }
    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateUser.fxml"));
            Parent root = loader.load();

            updateUser controller = loader.getController();
            controller.setUser(currentUser);

            back.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    void updatePassword(ActionEvent event) {
        String oldPwd = ancien_password.getText().trim(); // retire les espaces en début et fin
        String newPwd = new_password.getText();

        if (currentUser != null) {
            // Vérifie si le mot de passe en clair saisi correspond au mot de passe crypté dans la base
            if (!BCrypt.checkpw(oldPwd, currentUser.getPassword())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Ancien mot de passe incorrect.");
                return;
            }

            // Si le mot de passe est correct, on crypte le nouveau mot de passe
            String hashedNewPwd = BCrypt.hashpw(newPwd, BCrypt.gensalt());

            // Mise à jour du mot de passe dans l'objet utilisateur et dans la base de données
            currentUser.setPassword(hashedNewPwd);
            new service_user().update(currentUser); // MAJ en base de données

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe mis à jour avec succès !");

            // Retourner vers la page updateUser
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateUser.fxml"));
                Parent root = loader.load();

                updateUser controller = loader.getController();
                controller.setUser(currentUser);

                update.getScene().setRoot(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}