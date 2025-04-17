package tn.esprit.controllers;

import tn.esprit.models.user;
import tn.esprit.utils.DataBase;
import tn.esprit.models.session;
import tn.esprit.models.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import tn.esprit.services.service_user;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import tn.esprit.services.service_user;
import javafx.scene.Node;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class connexion {

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Button btn_login;

    service_user su = new service_user();

   /* @FXML
    public void handleLogin(ActionEvent event) {
        String email = tf_email.getText();
        String password = tf_password.getText();

        if (verifierLogin(email, password)) {
            try {
                // Récupération de l'utilisateur connecté
                user utilisateurConnecte = su.getByEmail(email); // Crée cette méthode si elle n'existe pas

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
                Parent root = loader.load();

                afficherUser controller = loader.getController();
                controller.setUser(utilisateurConnecte); // Envoi de l'utilisateur au contrôleur

                // Optionnel : enregistrer la session
                session.setCurrentUser(utilisateurConnecte);

                // Afficher la nouvelle scène
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect.");
        }
    }*/
   @FXML
   public void handleLogin(ActionEvent event) {
       String email = tf_email.getText();
       String password = tf_password.getText();

       if (verifierLogin(email, password)) {
           try {
               user utilisateurConnecte = su.getByEmail(email);

               FXMLLoader loader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
               Parent root = loader.load();

               // ✅ Récupérer le bon contrôleur
               NavbarController controller = loader.getController();
               controller.setUser(utilisateurConnecte);
               controller.setMainLayout((BorderPane) root); // Important pour attacher les actions

               session.setCurrentUser(utilisateurConnecte);

               Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
               stage.setScene(new Scene(root));
               stage.show();

           } catch (IOException e) {
               e.printStackTrace();
           }
       } else {
           showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect.");
       }
   }




    private boolean verifierLogin(String email, String password) {
        Connection cnx = DataBase.getInstance().getCnx();
        String query = "SELECT password FROM user WHERE email = ?";

        try {
            PreparedStatement stmt = cnx.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String motDePasseHache = rs.getString("password");
                // Utilisation de BCrypt pour vérifier le mot de passe
                return BCrypt.checkpw(password, motDePasseHache);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscription.fxml"));
            Parent root = loader.load();
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
