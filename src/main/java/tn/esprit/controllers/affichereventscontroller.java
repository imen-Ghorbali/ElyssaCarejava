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
import tn.esprit.services.PdfService;
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
    private HBox paginationContainer;  // Conteneur pour les numéros de page
    private int totalPages;
    @FXML
    private ComboBox<String> triComboBox;

    @FXML
    private TextField filtreTextField;

    @FXML
    private AnchorPane contentPane;

    private final ServiceEvent serviceEvent = new ServiceEvent();
    private ObservableList<events> allEvents = FXCollections.observableArrayList();
    private FilteredList<events> filteredEvents;
    private static final int ITEMS_PER_PAGE = 6; // Nombre d'éléments par page
    private int currentPage = 0; // Page actuelle
    private ObservableList<events> paginatedEvents = FXCollections.observableArrayList(); // Liste filtrée pour la pagination
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        triComboBox.getItems().addAll("Nom", "Date", "Popularité");
        triComboBox.setValue("Nom");
        triComboBox.setOnAction(event -> loadEvents());

        filtreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredEvents != null) {
                filteredEvents.setPredicate(e -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lower = newValue.toLowerCase();
                    return (e.getTitle() != null && e.getTitle().toLowerCase().contains(lower))
                            || (e.getLieu() != null && e.getLieu().toLowerCase().contains(lower))
                            || (e.getSponsor() != null && e.getSponsor().getName() != null && e.getSponsor().getName().toLowerCase().contains(lower));
                });
                updatePagination();
                afficherCartes();
            }
        });

        loadEvents();
    }

    private void loadEvents() {
        eventCardsContainer.getChildren().clear();
        List<events> list = serviceEvent.getAll();
        allEvents.setAll(list);
        filteredEvents = new FilteredList<>(allEvents, e -> true);
        appliquerTri();
        updatePagination();          // <-- Ajoute ceci
        updatePaginatedEvents();
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
        if (comparator != null) FXCollections.sort(allEvents, comparator);
    }

    private void afficherCartes() {
        eventCardsContainer.getChildren().clear();
        for (events e : paginatedEvents) {
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
        Label lieuLabel = new Label("\uD83D\uDCCD " + e.getLieu());
        Label sponsorLabel = new Label("\uD83D\uDDFD Sponsor: " + (e.getSponsor() != null ? e.getSponsor().getName() : ""));
        sponsorLabel.setStyle("-fx-text-fill: #3498db; -fx-underline: true; -fx-cursor: hand;");

        sponsorLabel.setOnMouseClicked(ev -> {
            if (e.getSponsor() != null) openSponsorDetails(e.getSponsor().getId());
        });

        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnDetails = new Button("Détails");
        Button btnPdf = new Button("Générer PDF");

        btnModifier.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnDetails.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        btnPdf.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;");

        btnModifier.setOnAction(ev -> openModifierForm(e));
        btnSupprimer.setOnAction(ev -> deleteEvent(e));
        btnDetails.setOnAction(ev -> openDetailsForm(e));
        btnPdf.setOnAction(ev -> generatePdf(e));

        HBox buttonsBox = new HBox(5, btnModifier, btnSupprimer, btnDetails, btnPdf);
        buttonsBox.setPadding(new Insets(5, 0, 0, 0));

        card.getChildren().addAll(imageView, titleLabel, lieuLabel, dateLabel, sponsorLabel, buttonsBox);
        return card;
    }

    private void generatePdf(events e) {
        PdfService pdfService = new PdfService();
        String contenuPdf = "Titre : " + e.getTitle() + "\n" +
                "Lieu : " + e.getLieu() + "\n" +
                "Date : " + e.getDate() + "\n" +
                "Description : " + e.getDescription() + "\n" +
                "Sponsor : " + (e.getSponsor() != null ? e.getSponsor().getName() : "Aucun");
        String nomFichierPdf = e.getTitle().replaceAll("\\s+", "_") + "_details.pdf";
        pdfService.genererPdf(nomFichierPdf, contenuPdf);
    }

    private void openSponsorDetails(int sponsorId) {
        try {
            ServiceSponsor serviceSponsor = new ServiceSponsor();
            sponsor sponsorDetails = serviceSponsor.getById(sponsorId);

            if (sponsorDetails != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailsponsor.fxml"));
                Parent root = loader.load();
                DetailSponsorController controller = loader.getController();
                controller.setSponsor(sponsorDetails);

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
                loadEvents();
            }
        });
    }
    private void updatePagination() {
        paginationContainer.getChildren().clear();
        totalPages = (int) Math.ceil((double) filteredEvents.size() / ITEMS_PER_PAGE);

        for (int i = 0; i < totalPages; i++) {
            final int pageIndex = i;
            Button pageButton = new Button(String.valueOf(i + 1));
            pageButton.getStyleClass().add("pagination-button");

            if (i == currentPage) {
                pageButton.getStyleClass().add("selected");
            }

            pageButton.setOnAction(event -> {
                onPageButtonClicked(pageIndex);
                updatePagination(); // Pour mettre à jour la classe "selected"
            });

            paginationContainer.getChildren().add(pageButton);
        }
    }


    @FXML
    private void onPageButtonClicked(int pageNumber) {
        currentPage = pageNumber;  // Mettre à jour la page actuelle
        updatePaginatedEvents();   // Mettre à jour les événements à afficher
        afficherCartes();          // Mettre à jour l'affichage des cartes
    }

    private void updatePaginatedEvents() {
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredEvents.size());
        paginatedEvents.setAll(filteredEvents.subList(startIndex, endIndex));
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
    public void showCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/calendar.fxml"));
            AnchorPane calendarView = loader.load();
            contentPane.getChildren().setAll(calendarView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshEvents() {
        loadEvents();
    }
}