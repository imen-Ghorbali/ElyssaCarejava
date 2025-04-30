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

        List<sponsor> sponsors = serviceSponsor.getAll();
        comboBoxSponsor.setItems(FXCollections.observableArrayList(sponsors));

        comboBoxSponsor.setCellFactory(cell -> new ListCell<sponsor>() {
            @Override
            protected void updateItem(sponsor item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null && !empty) ? item.getName() : null);
            }
        });

        comboBoxSponsor.setButtonCell(new ListCell<sponsor>() {
            @Override
            protected void updateItem(sponsor item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null && !empty) ? item.getName() : null);
            }
        });

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

        if (!isValidField(titleField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre est obligatoire, doit contenir au moins 4 caractères, et ne doit pas commencer par un espace.");
            return;
        }

        if (!isValidField(description.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La description est obligatoire, doit contenir au moins 4 caractères, et ne doit pas commencer par un espace.");
            return;
        }

        if (!isValidField(lieu.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le lieu est obligatoire, doit contenir au moins 4 caractères, et ne doit pas commencer par un espace.");
            return;
        }

        if (image.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'image est obligatoire.");
            return;
        }

        currentEvent.setTitle(titleField.getText().trim());
        currentEvent.setDescription(description.getText().trim());
        currentEvent.setLieu(lieu.getText().trim());
        currentEvent.setDate(date.getValue().atStartOfDay());
        currentEvent.setImage(image.getText().trim());

        sponsor selectedSponsor = comboBoxSponsor.getValue();
        if (selectedSponsor != null) {
            currentEvent.setSponsor(selectedSponsor);
            System.out.println("Sponsor sélectionné : " + selectedSponsor.getName());
        } else {
            System.out.println("Aucun sponsor sélectionné.");
        }

        serviceEvent.edit(currentEvent);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement modifié avec succès !");
        navigateToEventList();
    }

    private boolean isValidField(String value) {
        return value != null && value.trim().length() >= 4 && !value.startsWith(" ");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherevents.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer vers la liste des événements.");
        }
    }
}
