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
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import tn.esprit.services.service_consultation;
import tn.esprit.services.service_user;
import tn.esprit.models.session;
import java.sql.SQLException;
import java.util.List;
public class addConsultation {
    @FXML
    private Button btn_add;

    @FXML
    private ChoiceBox<user> tf_doctor;

    @FXML
    private ChoiceBox<type_specialite> tf_specialite;

    @FXML
    private TextArea tf_status;
    @FXML
    private Button back;
    @FXML
    private DatePicker tf_time;
    private service_user serviceUser = new service_user();
    private service_consultation serviceConsultation = new service_consultation();


    public void initialize() {
        // Initialisation du service
        service_user su = new service_user();

        // Récupération et affichage des docteurs dans le ChoiceBox
        List<user> doctors = su.getAllDoctors(); // Assure-toi que cette méthode retourne une liste de docteurs
        tf_doctor.setItems(FXCollections.observableArrayList(doctors));

        // Convertir l'objet user en nom lisible dans le ChoiceBox
        tf_doctor.setConverter(new StringConverter<user>() {
            @Override
            public String toString(user user) {
                return user != null ? user.getName() : "";
            }

            @Override
            public user fromString(String string) {
                return null; // Pas nécessaire ici
            }
        });

        // Sélectionner un docteur par défaut s'il y en a
        if (!doctors.isEmpty()) {
            tf_doctor.setValue(doctors.get(0));
        }

        // Initialisation des spécialités depuis l'enum type_specialite
        ObservableList<type_specialite> specialites = FXCollections.observableArrayList(type_specialite.values());
        tf_specialite.setItems(specialites);

        // Convertir type_specialite en nom lisible dans le ChoiceBox
        tf_specialite.setConverter(new StringConverter<type_specialite>() {
            @Override
            public String toString(type_specialite specialite) {
                return specialite != null ? specialite.name() : "";
            }

            @Override
            public type_specialite fromString(String string) {
                return type_specialite.valueOf(string);
            }
        });

        // Sélectionner une spécialité par défaut s'il y en a
        if (!specialites.isEmpty()) {
            tf_specialite.setValue(specialites.get(0));
        }
    }

    @FXML
    void addConsultation(ActionEvent event) {
        try {
            String status = tf_status.getText().trim();
            user selectedDoctor = tf_doctor.getSelectionModel().getSelectedItem();

            type_specialite specialite = tf_specialite.getSelectionModel().getSelectedItem();;

            if (status.isEmpty()|| selectedDoctor == null || specialite == null || tf_time.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
                return;
            }

            LocalDateTime dateTime = LocalDateTime.of(tf_time.getValue(), LocalTime.now());
            consultation c = new consultation(dateTime, status, session.getCurrentUser(), selectedDoctor, specialite);

            serviceConsultation.add(c);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Consultation ajoutée avec succès !");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherConsultation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer la consultation
            afficherConsultation controller = loader.getController();
            controller.setConsultationData(c, session.getCurrentUser()); // ✅ transmettre consultation et user
            // Passer l'objet consultation

            // Ouvrir la nouvelle scène
            Stage stage = (Stage) btn_add.getScene().getWindow();
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
    public void setUser(user u) {
        this.currentUser = u;
    }

    private user currentUser;
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherUser.fxml"));
            Parent root = loader.load();

            afficherUser controller = loader.getController();
            controller.setUser(currentUser); // ✅ réinjecte les données

            back.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


