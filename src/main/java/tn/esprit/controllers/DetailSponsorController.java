package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceSponsor;
import tn.esprit.utils.QRCodeGenerator;

import java.io.IOException;

public class DetailSponsorController {
    @FXML
    private Button btnAfficher;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private Label nomLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private ImageView sponsorImageView;

    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnRetour;

    private final ServiceSponsor serviceSponsor = new ServiceSponsor();
    private sponsor currentSponsor;

    public void setSponsor(sponsor sponsor) {
        this.currentSponsor = sponsor;
        nomLabel.setText(sponsor.getName());
        descriptionLabel.setText(sponsor.getDescription());
        typeLabel.setText(sponsor.getType());
        prixLabel.setText("Prix: " + sponsor.getPrix());

        // Générer et afficher le QR code pour le sponsor
        try {
            String sponsorInfo = "Nom: " + sponsor.getName() + "\n" +
                    "Type: " + sponsor.getType() + "\n" +
                    "Prix: " + sponsor.getPrix();
            Image qrCodeImage = QRCodeGenerator.generateQRCodeImage(sponsorInfo, 150, 150);
            qrCodeImageView.setImage(qrCodeImage); // Afficher l'image du QR code dans l'ImageView
        } catch (Exception e) {
            e.printStackTrace();
            // Si erreur, afficher un message d'erreur
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur de génération du QR code");
            errorAlert.setHeaderText("Erreur lors de la création du QR code");
            errorAlert.setContentText("Impossible de générer le QR code du sponsor.");
            errorAlert.showAndWait();
        }
    }


    @FXML
    private void modifierSponsor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiersponsor.fxml"));
            Parent root = loader.load();
            modifierSponsorController controller = loader.getController();
            controller.setSponsor(currentSponsor);  // Passer le sponsor actuel pour modification

            Stage stage = new Stage();
            stage.setTitle("Modifier Sponsor");
            stage.setScene(new Scene(root));
            stage.show();
            // Fermer la fenêtre actuelle si nécessaire
            Stage currentStage = (Stage) btnModifier.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isInSponsorsListView() {
        // Exemple simple pour vérifier si on est dans la liste des sponsors
        // Vous pouvez l'améliorer en fonction de votre logique de navigation
        return getClass().getResource("/affichersponsor.fxml") != null;
    }
    @FXML
    private void supprimerSponsor() {
        // Vérifier si l'on est dans la vue des sponsors
        if (isInSponsorsListView()) {
            // Confirmation de la suppression
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation de suppression");
            confirm.setHeaderText("Êtes-vous sûr de vouloir supprimer ce sponsor ?");
            confirm.setContentText("Cette action est irréversible.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Appeler la méthode de suppression
                        serviceSponsor.delete(currentSponsor.getId());

                        // Confirmation de la suppression réussie
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Suppression réussie");
                        successAlert.setHeaderText("Le sponsor a été supprimé avec succès.");
                        successAlert.showAndWait();

                        // Fermer la fenêtre actuelle
                        Stage currentStage = (Stage) btnSupprimer.getScene().getWindow();
                        currentStage.close();

                        // Charger et afficher la liste des sponsors après la suppression
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichersponsor.fxml"));
                            Parent root = loader.load();
                            Stage stage = new Stage();
                            stage.setTitle("Liste des Sponsors");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Si une exception survient lors de la suppression, afficher un message d'erreur
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Erreur");
                        errorAlert.setHeaderText("Erreur de suppression");
                        errorAlert.setContentText("Une erreur est survenue lors de la suppression du sponsor.");
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            // Si on est dans la vue des événements, ne pas supprimer
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Action invalide");
            infoAlert.setHeaderText("Suppression non autorisée");
            infoAlert.setContentText("La suppression d'un sponsor ne peut pas être effectuée dans cette vue.");
            infoAlert.showAndWait();
        }
    }


    @FXML
    private void retourListe() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
        // Charger la liste des sponsors
        // Utiliser FXMLLoader pour ouvrir la fenêtre de la liste des sponsors
    }

    @FXML
    private void ajouterSponsor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutersponsor.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Sponsor");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void afficherTousLesSponsors() {
        try {
            // Charger la vue des sponsors
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichersponsor.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle fenêtre pour afficher la liste
            Stage stage = new Stage();
            stage.setTitle("Tous les Sponsors");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
