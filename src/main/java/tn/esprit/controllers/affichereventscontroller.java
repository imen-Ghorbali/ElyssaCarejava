package tn.esprit.controllers;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.events;
import tn.esprit.services.ServiceEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class affichereventscontroller implements Initializable {

    @FXML
    private TableView<events> eventsTable;

    @FXML
    private TableColumn<events, Integer> idColumn;

    @FXML
    private TableColumn<events, String> titleColumn;

    @FXML
    private TableColumn<events, String> descriptionColumn;

    @FXML
    private TableColumn<events, String> lieuColumn;

    @FXML
    private TableColumn<events, String> dateColumn;

    @FXML
    private TableColumn<events, String> imageColumn;

    @FXML
    private TableColumn<events, Void> actionColumn;
    @FXML
    private TableColumn<events, String> sponsorColumn;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ServiceEvent serviceEvent = new ServiceEvent();
        List<events> eventList = serviceEvent.getAll();

        // Créer un ObservableList et remplir la TableView
        ObservableList<events> observableEvents = FXCollections.observableArrayList(eventList);
        eventsTable.setItems(observableEvents);

        // Initialiser la colonne sponsor pour afficher le nom du sponsor
        sponsorColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSponsor() != null ?
                        cellData.getValue().getSponsor().getName() : ""));  // Afficher le nom du sponsor


        // Lier les colonnes aux propriétés
        idColumn.setVisible(false);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));

        // Affichage de l'image
        imageColumn.setCellFactory(column -> new javafx.scene.control.TableCell<events, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(100);
                imageView.setFitHeight(70);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image("file:" + imagePath);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Format personnalisé pour LocalDateTime
        dateColumn.setCellValueFactory(cellData -> {
            events ev = cellData.getValue();
            String formattedDate = ev.getDate() != null
                    ? ev.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                    : "N/A";
            return new SimpleStringProperty(formattedDate);
        });

        // Ajouter des boutons Modifier et Supprimer à chaque ligne
        actionColumn.setCellFactory(param -> new javafx.scene.control.TableCell<events, Void>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final Button btnVoirDetails = new Button("Voir Détails");



            {
                btnModifier.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5;");
                btnSupprimer.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5;");
                btnVoirDetails.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5;");


                // Action pour le bouton Modifier
                btnModifier.setOnAction(event -> {
                    events e = getTableView().getItems().get(getIndex());
                    openModifierForm(e);
                });

                // Action pour le bouton Supprimer
                btnSupprimer.setOnAction(event -> {
                    events e = getTableView().getItems().get(getIndex());
                    deleteEvent(e);
                });

                btnVoirDetails.setOnAction(event -> {
                    events e = getTableView().getItems().get(getIndex());
                    openDetailsForm(e);  // Ouvre le formulaire de détails
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10, btnModifier, btnSupprimer,btnVoirDetails);
                    setGraphic(hbox);
                }
            }
        });

        // Charger les données depuis la BDD
        List<events> allEvents = serviceEvent.getAll();
        eventsTable.getItems().addAll(allEvents);
    }

    // Méthode pour ouvrir le formulaire de modification
    private void openModifierForm(events e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierevents.fxml"));
            Parent root = loader.load();

            // Récupère le contrôleur de la vue de modification
            modifiereventsController controller = loader.getController();
            controller.setEvent(e);  // Pré-remplir les champs avec les données de l'événement

            Stage stage = new Stage();
            stage.setTitle("Modifier Événement");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour supprimer un événement avec confirmation
    private void deleteEvent(events e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer cet événement ?");
        confirm.setContentText("Êtes-vous sûr de vouloir le supprimer ?");

        // Action en fonction de la réponse
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceEvent.delete(e.getId());  // Appel de la méthode delete dans ServiceEvent
                eventsTable.getItems().remove(e);  // Supprime l'événement de la TableView
            }
        });
    }
    private void openDetailsForm(events e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailsevents.fxml"));
            Parent root = loader.load();

            // Récupère le contrôleur de la vue de détails
            detailseventsController controller = loader.getController();
            controller.setEventId(e.getId());  // Passer l'ID de l'événement pour charger ses détails

            Stage stage = new Stage();
            stage.setTitle("Détails de l'Événement");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    private void ajouterEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterevents.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Événement");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
