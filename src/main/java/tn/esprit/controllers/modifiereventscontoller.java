package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tn.esprit.models.events;
import tn.esprit.services.ServiceEvent;

import java.io.File;

public class modifiereventscontoller {

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

    private events currentEvent;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    // Appelé pour préremplir les champs avec l'événement à modifier
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

        serviceEvent.edit(currentEvent);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement modifié avec succès !");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
