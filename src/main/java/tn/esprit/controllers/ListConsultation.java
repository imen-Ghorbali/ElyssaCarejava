package tn.esprit.controllers;
import tn.esprit.utils.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.models.consultation;
import tn.esprit.services.service_consultation;
import javafx.application.Platform;

public class ListConsultation {

    @FXML
    private TableView<consultation> consultationTable;
    @FXML
    private TableColumn<consultation, String> colDate;
    @FXML
    private TableColumn<consultation, String> colStatus;
    @FXML
    private TableColumn<consultation, String> colPatient;
    @FXML
    private TableColumn<consultation, String> colSpecialite;
    @FXML
    private TableColumn<consultation, Void> colActions;
    @FXML
    private Button openAgendaButton;

    private final service_consultation service = new service_consultation();
    private int doctorId = 0; // Initialisé à 0 par défaut

    @FXML
    public void initialize() {
        try {
            // Configuration des colonnes
            configureColumns();

            // Initialisation des données
            refreshTable();

            // Configuration du bouton
            openAgendaButton.setOnAction(event -> openAgenda());

        } catch (Exception e) {
            showAlert("Erreur d'initialisation",
                    "Erreur lors du chargement des consultations",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureColumns() {
        colDate.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getDate().toString());
            } catch (NullPointerException e) {
                return new SimpleStringProperty("N/A");
            }
        });

        colStatus.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getStatus());
            } catch (NullPointerException e) {
                return new SimpleStringProperty("N/A");
            }
        });

        colPatient.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getPatient().getName());
            } catch (NullPointerException e) {
                return new SimpleStringProperty("N/A");
            }
        });

        colSpecialite.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getSpecialite().getDisplayName());
            } catch (NullPointerException e) {
                return new SimpleStringProperty("N/A");
            }
        });


        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnConfirmer = new Button("Confirmer");
            private final Button btnAnnuler = new Button("Annuler");
            private final HBox hbox = new HBox(5, btnConfirmer, btnAnnuler);
            private final EmailService emailService = new EmailService();

            {
                btnConfirmer.setOnAction(e -> {
                    consultation c = getTableView().getItems().get(getIndex());
                    c.setStatus("Confirmé");
                    service.update(c);

                    // Envoi de notification par email
                    sendConfirmationEmail(c, "confirmée");

                    refreshTable();
                });

                btnAnnuler.setOnAction(e -> {
                    consultation c = getTableView().getItems().get(getIndex());
                    c.setStatus("Annulé");
                    service.update(c);

                    // Envoi de notification par email
                    sendConfirmationEmail(c, "annulée");

                    refreshTable();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }

            private void sendConfirmationEmail(consultation consultation, String status) {
                try {
                    String patientEmail = consultation.getPatient().getEmail();
                    String subject = "Statut de votre consultation";
                    String body = String.format(
                            "Bonjour %s,\n\n" +
                                    "Votre consultation prévue le %s a été %s par le Dr. %s.\n\n" +
                                    "Cordialement,\n" +
                                    "L'équipe médicale",
                            consultation.getPatient().getName(),
                            consultation.getDate().toString(),
                            status,
                            consultation.getDoctor().getName()
                    );

                    emailService.sendEmail(patientEmail, subject, body);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
                    showAlert("Erreur Email",
                            "Le statut a été mis à jour mais l'email n'a pas pu être envoyé",
                            e.getMessage());
                }
            }
        });
    }
    private void showEmailErrorAlert(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème d'envoi d'email");
            alert.setHeaderText("Le statut a été mis à jour mais l'email n'a pas pu être envoyé");
            alert.setContentText("Détail: " + e.getMessage());
            alert.showAndWait();
        });
    }

    private void refreshTable() {
        try {
            ObservableList<consultation> consultations = FXCollections.observableArrayList(
                    service.getAll().stream()
                            .filter(c -> {
                                if (c.getDoctor() == null) {
                                    System.out.println("Consultation sans médecin: " + c.getId());
                                    return false;
                                }
                                return c.getDoctor().getId() == doctorId;
                            })
                            .toList()
            );

            System.out.println("Nombre de consultations trouvées: " + consultations.size());
            consultationTable.setItems(consultations);

        } catch (Exception e) {
            showAlert("Erreur",
                    "Erreur lors du chargement des données",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
        System.out.println("Médecin ID défini: " + doctorId);
        refreshTable();
    }

   /* @FXML
    private void openAgenda() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AgendaConsultation.fxml"));
            Parent root = loader.load();

            AgendaConsultation controller = loader.getController();
            controller.setDoctorId(this.doctorId); // Passage de l'ID du médecin

            Stage stage = new Stage();
            stage.setTitle("Agenda des consultations - Médecin ID: " + this.doctorId);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur",
                    "Impossible d'ouvrir l'agenda",
                    "Assurez-vous que:\n" +
                            "1. Le fichier AgendaConsultation.fxml existe\n" +
                            "2. La bibliothèque CalendarFX est correctement configurée\n" +
                            "Erreur technique: " + e.getMessage());
        }
    }*/
   @FXML
   private void openAgenda() {
       try {
           // 1. Charger le fichier FXML
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/AgendaConsultation.fxml"));
           Parent root = loader.load();

           // 2. Configurer le contrôleur
           AgendaConsultation controller = loader.getController();
           controller.setDoctorId(this.doctorId); // Passage de l'ID du médecin

           // 3. Créer et configurer la fenêtre
           Stage stage = new Stage();
           stage.setTitle("Agenda des Consultations - Dr. ID: " + this.doctorId);
           stage.setScene(new Scene(root, 1000, 700)); // Taille optimale

           // 4. Empêcher la fermeture accidentelle
           stage.setOnCloseRequest(event -> {
               event.consume(); // Intercepte la fermeture
               confirmExit(stage); // Méthode de confirmation
           });

           // 5. Afficher la fenêtre
           stage.show();

       } catch (NullPointerException e) {
           showAlert("Erreur Critique",
                   "Fichier manquant",
                   "Le fichier AgendaConsultation.fxml est introuvable.\n" +
                           "Vérifiez qu'il est dans le dossier resources.");
           e.printStackTrace();
       } catch (Exception e) {
           showAlert("Erreur de Chargement",
                   "Problème de lecture",
                   "Impossible de charger l'agenda.\n" +
                           "Erreur: " + e.getMessage());
           e.printStackTrace();
       }
   }

    // Méthode utilitaire pour confirmer la fermeture
    private void confirmExit(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Fermer l'agenda ?");
        alert.setContentText("Voulez-vous vraiment quitter l'agenda ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                stage.close();
            }
        });
    }



    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}