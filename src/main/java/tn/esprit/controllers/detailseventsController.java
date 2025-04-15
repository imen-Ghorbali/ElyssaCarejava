package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.models.events;
import tn.esprit.services.ServiceEvent;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class detailseventsController {
    @FXML
    private Button btnRetourListe;
    @FXML
    private Label sponsorLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label lieuLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private ImageView imageView; // Utilisation de javafx.scene.image.ImageView

    private final ServiceEvent serviceEvent = new ServiceEvent();
    private int eventId;

    // Méthode pour définir l'ID de l'événement et charger ses détails
    public void setEventId(int eventId) {
        this.eventId = eventId;
        loadEventDetails();
    }
    @FXML
    public void handleRetourListe(ActionEvent event) {
        // Ici, tu peux charger la vue de la liste des événements
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficherevents.fxml")); // Remplace par ton fichier FXML de la liste
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetourListe.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour charger les détails de l'événement
    private void loadEventDetails() {
        events event = serviceEvent.getById(eventId); // Récupérer l'événement par son ID
        if (event != null) {
            titleLabel.setText("Titre: " + event.getTitle());
            descriptionLabel.setText("Description: " + event.getDescription());
            lieuLabel.setText("Lieu: " + event.getLieu());

            if (event.getDate() != null) {
                dateLabel.setText("Date: " + event.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            } else {
                dateLabel.setText("Date: N/A");
            }

            if (event.getSponsor() != null) {
                sponsorLabel.setText("Sponsor: " + event.getSponsor().getName());
            } else {
                sponsorLabel.setText("Sponsor: Aucun");
            }

            // Gestion de l'image
            try {
                String imagePath = event.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    if (imagePath.startsWith("http")) {
                        // Si c'est une URL
                        imageView.setImage(new Image(imagePath));
                    } else {
                        // Si c'est un chemin de fichier local
                        File file = new File(imagePath);
                        if (file.exists()) {
                            imageView.setImage(new Image(file.toURI().toString()));
                        } else {
                            // Si le fichier n'existe pas, essayer de le charger depuis les ressources
                            imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                // Option: Charger une image par défaut si erreur
                // imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } else {
            titleLabel.setText("Événement non trouvé");
            descriptionLabel.setText("");
            lieuLabel.setText("");
            dateLabel.setText("");
            sponsorLabel.setText("");
            imageView.setImage(null);
        }
    }
}
