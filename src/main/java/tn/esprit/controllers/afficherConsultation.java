package tn.esprit.controllers;

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

   /* public void setConsultationData(consultation c) {
        doctor.setText(c.getDoctor().getName()); // ou getNom(), selon ton modèle
        //specialite.setText(c.getSpecialite().toString());
        if (c.getSpecialite() != null) {
            specialite.setText(c.getSpecialite().toString());
        } else {
            specialite.setText("Specialite non défini"); // ou une valeur par défaut
        }
        status.setText(c.getStatus());
        time.setText(c.getDate().toString());
    }*/
   /*public void setConsultationData(consultation consultation, user currentUser) {
       this.c = consultation;
       this.currentUser = currentUser; // Sauvegarder l'utilisateur courant

       doctor.setText(consultation.getDoctor().getName());
       status.setText(consultation.getStatus());
       time.setText(consultation.getDate().toString());

       if (consultation.getSpecialite() != null) {
           specialite.setText(consultation.getSpecialite().toString());
       } else {
           specialite.setText("Spécialité non définie");
       }
       System.out.println("Consultation sélectionnée : ID = " + consultation.getId());

   }*/
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

   /* @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/afficherUser.fxml"));
            Parent root = loader.load();

            afficherUser controller = loader.getController();
            controller.setUser(currentUser); // ✅ réinjecte les données

            back.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    @FXML
    void handleDelete(ActionEvent event) {
        try {
            consultation_service.delete(c);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Consultation supprimée avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherUser.fxml"));
            Parent root = loader.load();

            afficherUser controller = loader.getController();
            controller.setUser(currentUser); // ✅ réinjecte l'utilisateur

            delete.getScene().setRoot(root);

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

    /*@FXML
     void handleBack(ActionEvent event) {
        try {
            // Chargement de la page afficherConsultation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/afficherUser.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et réinjecter les données mises à jour
            afficherConsultation controller = loader.getController();
            controller.setConsultationData(c, currentUser); // Mettre à jour avec la consultation mise à jour

            back.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    @FXML
    void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage) back.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherUser.fxml"));
            Parent root = loader.load();

            afficherUser controller = loader.getController();
            controller.setUser(currentUser); // réinjecter l'utilisateur

            // Change la scène dans le même stage
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la navigation.");
        }
    }





}
