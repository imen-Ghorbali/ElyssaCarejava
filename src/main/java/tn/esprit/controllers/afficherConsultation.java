package tn.esprit.controllers;

import javafx.scene.layout.BorderPane;
import tn.esprit.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;  // Assure-toi d'ajouter cette ligne
import java.time.LocalTime;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;  // Assure-toi d'ajouter cette ligne
import javafx.scene.Parent;
import tn.esprit.services.service_consultation;
import javafx.scene.control.ListView;

public class afficherConsultation {
    private final service_consultation consultation_service = new service_consultation();

    @FXML
    private Button back;

    @FXML
    private Button delete;

    @FXML
    private Label doctor;

    @FXML
    private Label specialite;

    @FXML
    private Label status;

    @FXML
    private Label time;

    @FXML
    private Button update;
    private consultation c;


   public void setConsultationData(consultation consultation, user currentUser) {
       this.c = consultation;
       this.currentUser = currentUser;

       doctor.setText(consultation.getDoctor().getName());
       status.setText(consultation.getStatus());
       time.setText(consultation.getDate().toString());

       if (consultation.getSpecialite() != null) {
           specialite.setText(consultation.getSpecialite().toString());
       } else {
           specialite.setText("Spécialité non définie");
       }
   }


    private user currentUser;

;
    @FXML
    void handleDelete(ActionEvent event) {
        try {
            // Supprimer la consultation
            consultation_service.delete(c);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Consultation supprimée avec succès !");

            // Charger la page navbar
            FXMLLoader navbarLoader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
            Parent navbarRoot = navbarLoader.load();

            // Récupérer le contrôleur de la navbar
            NavbarController navbarController = navbarLoader.getController();
            navbarController.setUser(currentUser);

            // Charger la vue accueil
            FXMLLoader accueilLoader = new FXMLLoader(getClass().getResource("/accueil.fxml"));
            Parent accueilView = accueilLoader.load();

            // Si ton accueil.fxml a un contrôleur et a besoin du currentUser :
            // accueilController accueilController = accueilLoader.getController();
            // accueilController.setUser(currentUser);

            // Injecter accueil dans le BorderPane central
            navbarController.setMainLayout((BorderPane) navbarRoot);
            ((BorderPane) navbarRoot).setCenter(accueilView);

            // Remplacer la scène actuelle
            delete.getScene().setRoot(navbarRoot);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la suppression.");
        }
    }



    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleHistorique(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/historiqueConsultation.fxml"));
            Parent root = loader.load();

            historiqueConsultation controller = loader.getController();
            controller.setUser(session.getCurrentUser()); // Utilisation de la session

            Stage stage = new Stage();
            stage.setTitle("Historique des consultations de " + session.getCurrentUser().getName());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de l'historique: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateConsultation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page de modification
            updateConsultation controller = loader.getController();

            // Passer la consultation à la page de modification
            controller.setConsultation(c);  // Passer la consultation actuelle à la nouvelle page

            // Passer l'utilisateur courant (si nécessaire)
            controller.setCurrentUser(currentUser);

            // Ouvrir la scène de modification
            Stage stage = (Stage) update.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du chargement de la page de modification.");
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

            // Charger la vue accueil
            FXMLLoader accueilLoader = new FXMLLoader(getClass().getResource("/accueil.fxml"));
            Parent accueilView = accueilLoader.load();

            // Si tu as un contrôleur pour accueil.fxml, tu peux le récupérer ici :
            // accueilController accueilController = accueilLoader.getController();
            // accueilController.setUser(currentUser); // si besoin

            // Insérer accueil dans le BorderPane central
            navbarController.setMainLayout((BorderPane) navbarRoot);
            ((BorderPane) navbarRoot).setCenter(accueilView);

            // Remplacer la scène actuelle par la scène navbar
            back.getScene().setRoot(navbarRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
