package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.models.events;
import tn.esprit.services.ServiceEvent;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class detailseventsController {
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
    private ImageView imageView; // Correction: Utilisation de javafx.scene.image.ImageView

    private final ServiceEvent serviceEvent = new ServiceEvent();
    private int eventId;

    public void setEventId(int eventId) {
        this.eventId = eventId;
        loadEventDetails();
    }

    private void loadEventDetails() {
        events event = serviceEvent.getById(eventId);
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

            // Correction pour l'ImageView
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
                // Option: Charger une image par défaut
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