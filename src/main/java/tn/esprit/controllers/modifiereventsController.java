package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import tn.esprit.models.events;
import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceEvent;
import tn.esprit.services.ServiceSponsor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class modifiereventsController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField description;

    @FXML
    private TextField lieu;

    @FXML
    private DatePicker date;

    @FXML
    private TextField image;

    @FXML
    private ImageView previewImage;

    @FXML
    private ComboBox<sponsor> comboBoxSponsor;

    private events currentEvent;

    private final ServiceEvent serviceEvent = new ServiceEvent();
    private final ServiceSponsor serviceSponsor = new ServiceSponsor();

    public void setEvent(events e) {
        this.currentEvent = e;

        titleField.setText(e.getTitle());
        description.setText(e.getDescription());
        lieu.setText(e.getLieu());
        date.setValue(e.getDate().toLocalDate());
        image.setText(e.getImage());

        try {
            Image img = new Image("file:" + e.getImage());
            previewImage.setImage(img);
        } catch (Exception ex) {
            System.out.println("Erreur chargement image : " + ex.getMessage());
        }

        // Remplir le ComboBox avec tous les sponsors
        List<sponsor> sponsors = serviceSponsor.getAll();
        comboBoxSponsor.setItems(FXCollections.observableArrayList(sponsors));

        // Personnaliser l'affichage dans le ComboBox
        comboBoxSponsor.setCellFactory(cell -> new ListCell<sponsor>() {
            @Override
            protected void updateItem(sponsor item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });

        comboBoxSponsor.setButtonCell(new ListCell<sponsor>() {
            @Override
            protected void updateItem(sponsor item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });

        // Pré-sélectionner le sponsor de l’événement s’il existe
        if (e.getSponsor() != null) {
            for (sponsor s : sponsors) {
                if (s.getId() == e.getSponsor().getId()) {
                    comboBoxSponsor.setValue(s);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleImageBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            image.setText(selectedFile.getAbsolutePath());
            previewImage.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleModifier() {
        if (currentEvent == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement à modifier !");
            return;
        }

        currentEvent.setTitle(titleField.getText());
        currentEvent.setDescription(description.getText());
        currentEvent.setLieu(lieu.getText());
        currentEvent.setDate(date.getValue().atStartOfDay());
        currentEvent.setImage(image.getText());

        sponsor selectedSponsor = comboBoxSponsor.getValue();
        if (selectedSponsor != null) {
            currentEvent.setSponsor(selectedSponsor);
            System.out.println("Sponsor sélectionné : " + selectedSponsor.getName());
        } else {
            System.out.println("Aucun sponsor sélectionné.");
        }

        // Modifier l'événement dans la base de données
        serviceEvent.edit(currentEvent);

        // Afficher une alerte de succès
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement modifié avec succès !");

        // Naviguer vers la liste des événements
        navigateToEventList();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToEventList() {
        try {
            // Charger la vue de la liste des événements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherevents.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) titleField.getScene().getWindow();

            // Remplacer la scène actuelle par la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Rafraîchir la liste des événements dans le contrôleur de la liste
            affichereventscontroller eventListController = loader.getController();
             // Appel de la méthode pour rafraîchir la liste

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer vers la liste des événements.");
        }
    }

}
