package tn.esprit.controllers;

import tn.esprit.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
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

public class    updateConsultation {

    @FXML
    private Button back;

    @FXML
    private ChoiceBox<user> tf_doctor;

    @FXML
    private ChoiceBox<type_specialite> tf_specialite;

    @FXML
    private TextArea tf_status;

    @FXML
    private DatePicker tf_time;

    @FXML
    private Button update;

    private user currentUser;
    private consultation currentConsultation;

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherConsultation.fxml"));
            Parent root = loader.load();

            afficherConsultation controller = loader.getController();
            controller.setConsultationData(currentConsultation, currentUser); // remettre les données actuelles

            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir en arrière.");
        }
    }


    @FXML
     void handleUpdate(ActionEvent event) {
        try {
            // Récupérer les données du formulaire
            String status = tf_status.getText().trim();
            user selectedDoctor = tf_doctor.getSelectionModel().getSelectedItem();
            type_specialite specialite = tf_specialite.getSelectionModel().getSelectedItem();
            LocalDateTime dateTime = LocalDateTime.of(tf_time.getValue(), LocalTime.now());

            // Vérifier que tous les champs sont remplis
            if (status.isEmpty() || selectedDoctor == null || specialite == null || tf_time.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
                return;
            }

            // Créer une nouvelle instance de consultation avec les données modifiées
            consultation updatedConsultation = new consultation(dateTime, status, session.getCurrentUser(), selectedDoctor, specialite);
            updatedConsultation.setId(currentConsultation.getId());  // On s'assure d'utiliser le bon ID pour la mise à jour

            // Appeler la méthode de mise à jour du service
            service_consultation serviceConsultation = new service_consultation();
            serviceConsultation.update(updatedConsultation);

            // Afficher une alerte de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Consultation mise à jour avec succès !");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherConsultation.fxml"));
            Parent root = loader.load();

            afficherConsultation controller = loader.getController();
            controller.setConsultationData(updatedConsultation, currentUser); // Réinjecter la consultation modifiée

            Stage stage = (Stage) update.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setConsultation(consultation consultation) {
        this.currentConsultation = consultation;
        // Initialiser les champs avec les données de la consultation actuelle
        tf_status.setText(consultation.getStatus());
        tf_doctor.setValue(consultation.getDoctor());
        tf_specialite.setValue(consultation.getSpecialite());
        tf_time.setValue(consultation.getDate().toLocalDate());

    }

    public void setCurrentUser(user currentUser) {
        this.currentUser = currentUser;
    }

    // Méthode pour remplir la liste des docteurs dans la choiceBox
    public void loadDoctors() {
        service_user userService = new service_user();
        List<user> doctors = userService.getAllDoctors();
        ObservableList<user> doctorList = FXCollections.observableArrayList(doctors);
        tf_doctor.setItems(doctorList);
    }

    // Méthode pour remplir la liste des spécialités dans la choiceBox
    public void loadSpecialties() {
        ObservableList<type_specialite> specialties = FXCollections.observableArrayList(type_specialite.values());
        tf_specialite.setItems(specialties);
    }

    @FXML
    public void initialize() {
        // Charger les données des docteurs et des spécialités lorsque l'écran est initialisé
        loadDoctors();
        loadSpecialties();

        // Ajouter des écouteurs d'événements pour les choix de docteurs et spécialités
        tf_doctor.setOnAction(event -> {
            user selectedDoctor = tf_doctor.getSelectionModel().getSelectedItem();
            if (selectedDoctor != null) {
                // Vous pouvez mettre à jour d'autres éléments en fonction du docteur sélectionné
            }
        });

        tf_specialite.setOnAction(event -> {
            type_specialite selectedSpecialty = tf_specialite.getSelectionModel().getSelectedItem();
            if (selectedSpecialty != null) {
                // Vous pouvez mettre à jour d'autres éléments en fonction de la spécialité sélectionnée
            }
        });
    }
}
