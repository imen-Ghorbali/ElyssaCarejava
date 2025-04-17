package tn.esprit.controllers;
import tn.esprit.models.Role;
import tn.esprit.models.user;
import tn.esprit.models.session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;  // Assure-toi d'ajouter cette ligne
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.services.service_user;

import java.sql.SQLException;
import java.util.List;
public class afficherUser {
    private final service_user user_service = new service_user();
    @FXML
    private Button back;
    @FXML
    private Button add_App;
    @FXML
    private Button delete;

    @FXML
    private Label email;

    @FXML
    private Label name;


    @FXML
    private Label role;

    @FXML
    private Button update;

    private user user;

    // Méthode qu'on va appeler après chargement
    public void setUser(user user) {
        this.user = user;
        name.setText(user.getName());
        email.setText(user.getEmail());
        if (user.getRole() != null) {
            role.setText(user.getRole().toString());
        } else {
            role.setText("Rôle non défini"); // ou une valeur par défaut
        }



    }

    @FXML
    private void deleteUser() {
        System.out.println("Suppression déclenchée");

        if (user != null) {
            System.out.println("ID de l'utilisateur à supprimer : " + user.getId());
            boolean deleted = user_service.delete(user);

            if (deleted) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suppression");
                alert.setContentText("✅ Utilisateur supprimé avec succès !");
                alert.showAndWait();

                // Nettoyer la session
                session.setCurrentUser(null);

                // Redirection vers connexion.fxml
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/connexion.fxml"));
                    Parent root = loader.load();
                    delete.getScene().setRoot(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("❌ Impossible de supprimer l'utilisateur !");
                alert.showAndWait();
            }
        } else {
            System.out.println("L'utilisateur est null !");
        }
    }
    @FXML
    private void handleUpdate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateUser.fxml"));
            Parent root = loader.load();

            updateUser controller = loader.getController();
            controller.setUser(user);  // Transmettre l'utilisateur sélectionné

            update.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Impossible de charger la page de mise à jour");
        }
    }




    @FXML
    void initialize() {
     /*   try {
            List<user> users = user_service.getAll();
            System.out.println(users);
            ObservableList<user> observableList = FXCollections.observableList(users);
            tableView.setItems(observableList);

            name.setCellValueFactory(new PropertyValueFactory<>("Name"));
            email.setCellValueFactory(new PropertyValueFactory<>("Email"));
            password.setCellValueFactory(new PropertyValueFactory<>("Password"));
            role.setCellValueFactory(new PropertyValueFactory<>("role"));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }*/}

        @FXML
        private void logoutAction() {
            // Nettoyer la session
            session.setCurrentUser(null);

            // Revenir à la page de connexion
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/connexion.fxml"));
                Parent root = loader.load();
                delete.getScene().setRoot(root);  // ou update.getScene().setRoot(root); selon le bouton
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    @FXML
    private void handleAddConsultation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addConsultation.fxml"));
            Parent root = loader.load();
            add_App.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d’ouvrir le formulaire d’ajout");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}

