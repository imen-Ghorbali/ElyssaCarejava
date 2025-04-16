package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.models.events;
import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceEvent;
import tn.esprit.services.ServiceSponsor;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class affichereventscontroller implements Initializable {

    @FXML
    private FlowPane eventCardsContainer;

    @FXML
    private ComboBox<String> triComboBox;

    @FXML
    private TextField filtreTextField;



    private final ServiceEvent serviceEvent = new ServiceEvent();
    private ObservableList<events> allEvents = FXCollections.observableArrayList();
    private FilteredList<events> filteredEvents;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser le tri
        triComboBox.getItems().addAll("Nom", "Date", "Popularité");
        triComboBox.setValue("Nom");
        triComboBox.setOnAction(event -> loadEvents());

        // Initialiser le filtrage
        filtreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredEvents != null) {
                filteredEvents.setPredicate(e -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lower = newValue.toLowerCase();
                    return (e.getTitle() != null && e.getTitle().toLowerCase().contains(lower))
                            || (e.getLieu() != null && e.getLieu().toLowerCase().contains(lower))
                            || (e.getSponsor() != null && e.getSponsor().getName() != null && e.getSponsor().getName().toLowerCase().contains(lower));
                });
                afficherCartes();  // Rafraîchir les cartes après filtrage
            }
        });

        loadEvents();
    }

    private void loadEvents() {
        eventCardsContainer.getChildren().clear();
        List<events> list = serviceEvent.getAll();
        allEvents.setAll(list);

        // Initialisation de FilteredList après avoir mis à jour allEvents
        filteredEvents = new FilteredList<>(allEvents, e -> true);

        // Appliquer le tri et afficher les cartes
        appliquerTri();
        afficherCartes();
    }

    private void appliquerTri() {
        String critereTri = triComboBox.getValue();
        if (critereTri == null) return;

        Comparator<events> comparator = switch (critereTri) {
            case "Nom" -> Comparator.comparing(events::getTitle);
            case "Date" -> Comparator.comparing(events::getDate);
            default -> null;
        };

        if (comparator != null)
            FXCollections.sort(allEvents, comparator);
    }

    private void afficherCartes() {
        eventCardsContainer.getChildren().clear();
        for (events e : filteredEvents) {
            VBox card = createEventCard(e);
            eventCardsContainer.getChildren().add(card);
        }
    }

    private VBox createEventCard(events e) {
        VBox card = new VBox(10);
        card.setPrefWidth(250);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 2, 2);");

        ImageView imageView = new ImageView();
        if (e.getImage() != null && !e.getImage().isEmpty()) {
            try {
                imageView.setImage(new Image("file:" + e.getImage()));
            } catch (Exception ignored) {}
        }
        imageView.setFitWidth(220);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Label titleLabel = new Label(e.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label dateLabel = new Label(e.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        Label lieuLabel = new Label("📍 " + e.getLieu());
        Label sponsorLabel = new Label(e.getSponsor() != null ? "🎗 Sponsor: " + e.getSponsor().getName() : "");
        sponsorLabel.setStyle("-fx-text-fill: #3498db; -fx-underline: true; -fx-cursor: hand;");

        sponsorLabel.setOnMouseClicked(ev -> {
            if (e.getSponsor() != null) {
                openSponsorDetails(e.getSponsor().getId());
            }
        });


        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnDetails = new Button("Détails");

        btnModifier.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnDetails.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

        btnModifier.setOnAction(ev -> openModifierForm(e));
        btnSupprimer.setOnAction(ev -> deleteEvent(e));
        btnDetails.setOnAction(ev -> openDetailsForm(e));

        HBox buttonsBox = new HBox(5, btnModifier, btnSupprimer, btnDetails);
        buttonsBox.setPadding(new Insets(5, 0, 0, 0));

        card.getChildren().addAll(imageView, titleLabel, lieuLabel, dateLabel, sponsorLabel, buttonsBox);
        return card;
    }
    private void openSponsorDetails(int sponsorId) {
        try {
            // Retrieve the sponsor object using the sponsorId
            ServiceSponsor serviceSponsor = new ServiceSponsor();
            sponsor sponsorDetails = serviceSponsor.getById(sponsorId);

            if (sponsorDetails != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailsponsor.fxml"));
                Parent root = loader.load();

                // Pass the sponsor object to the controller
                DetailSponsorController controller = loader.getController();
                controller.setSponsor(sponsorDetails);  // Pass the sponsor object

                Stage stage = new Stage();
                stage.setTitle("Détails du Sponsor");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.out.println("Sponsor not found!");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void deleteEvent(events e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer cet événement ?");
        confirm.setContentText("Êtes-vous sûr de vouloir le supprimer ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceEvent.delete(e.getId());
                loadEvents();  // Rafraîchir l'affichage après suppression
            }
        });
    }

    private void openModifierForm(events e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierevents.fxml"));
            Parent root = loader.load();
            modifiereventsController controller = loader.getController();
            controller.setEvent(e);
            Stage stage = (Stage) eventCardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Événement");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openDetailsForm(events e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailsevents.fxml"));
            Parent root = loader.load();
            detailseventsController controller = loader.getController();
            controller.setEventId(e.getId());
            Stage stage = (Stage) eventCardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'Événement");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ajouterEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterevents.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) eventCardsContainer.getScene().getWindow();
            stage.setTitle("Ajouter un Événement");
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshEvents() {
        loadEvents();  // Rafraîchir les événements
    }

}
