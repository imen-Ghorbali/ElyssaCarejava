package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.models.user;
import tn.esprit.models.consultation;
import tn.esprit.services.service_consultation;
import javafx.event.ActionEvent;
public class historiqueConsultation {
    @FXML
    private TableView<consultation> tableViewConsultations;

    @FXML
    private TableColumn<consultation, String> colDate;

    @FXML
    private TableColumn<consultation, String> colDoctor;

    @FXML
    private TableColumn<consultation, String> colSpecialite;

    @FXML
    private TableColumn<consultation, String> colStatus;

    private user currentUser;
    private final service_consultation consultation_service = new service_consultation();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colSpecialite.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Charger les données si currentUser est déjà défini
        if (currentUser != null) {
            loadConsultations();
        }
    }

    public void setUser(user user) {
        this.currentUser = user;
        loadConsultations();
    }

    private void loadConsultations() {
        if (currentUser == null) {
            System.out.println("Aucun utilisateur connecté");
            return;
        }

        ObservableList<consultation> consultations = FXCollections.observableArrayList(
                consultation_service.getConsultationsByUser(currentUser.getId())
        );

        tableViewConsultations.setItems(consultations);
        System.out.println("Nombre de consultations chargées: " + consultations.size());
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) tableViewConsultations.getScene().getWindow();
        stage.close();
    }
}