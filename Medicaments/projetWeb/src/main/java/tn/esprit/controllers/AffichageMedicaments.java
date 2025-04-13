package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.models.Medicaments;
import tn.esprit.services.MedicamentService;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import java.util.Optional;

public class AffichageMedicaments {

    @FXML
    private ListView<Medicaments> Affichagemed;
    @FXML
    private TextField searchField;

    private MedicamentService medicamentService = new MedicamentService();
    private FilteredList<Medicaments> filteredList;

    @FXML
    public void initialize() {
        loadMedicaments();
        configureListView();
        setupSelectionListener();
        setupSearchFilter();
    }

    private void loadMedicaments() {
        ObservableList<Medicaments> medicaments = FXCollections.observableArrayList(medicamentService.getAll());
        filteredList = new FilteredList<>(medicaments, p -> true);
        Affichagemed.setItems(filteredList);
    }

    private void configureListView() {
        Affichagemed.setCellFactory(param -> new ListCell<Medicaments>() {
            private final ImageView imageView = new ImageView();
            private final Label nomLabel = new Label();
            private final Label detailsLabel = new Label();
            private final HBox content = new HBox(15, imageView, new VBox(5, nomLabel, detailsLabel));

            {
                imageView.setFitHeight(60);
                imageView.setFitWidth(60);
                imageView.setPreserveRatio(true);
                content.setAlignment(Pos.CENTER_LEFT);
                nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            }

            @Override
            protected void updateItem(Medicaments medicament, boolean empty) {
                super.updateItem(medicament, empty);

                if (empty || medicament == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    try {
                        if (medicament.getImage() != null && !medicament.getImage().isEmpty()) {
                            Image image = new Image("file:" + medicament.getImage());
                            imageView.setImage(image);
                        } else {
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur de chargement d'image: " + e.getMessage());
                        imageView.setImage(null);
                    }

                    nomLabel.setText(medicament.getNom());
                    detailsLabel.setText(String.format("Classe: %s | Prix: %d TND",
                            medicament.getClasse(), medicament.getPrix()));

                    setGraphic(content);
                    setText(null);
                }
            }
        });
    }

    private void setupSelectionListener() {
        Affichagemed.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showMedicamentDetailsDialog(newVal);
            }
        });
    }

    private void showMedicamentDetailsDialog(Medicaments medicament) {
        // Création de la boîte de dialogue
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails du médicament");
        dialog.setHeaderText(medicament.getNom());

        // Configuration du contenu
        ImageView imageView = new ImageView();
        try {
            if (medicament.getImage() != null && !medicament.getImage().isEmpty()) {
                Image image = new Image("file:" + medicament.getImage());
                imageView.setImage(image);
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement d'image: " + e.getMessage());
            imageView.setImage(null);
        }
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);

        Label classeLabel = new Label("Classe: " + medicament.getClasse());
        Label prixLabel = new Label("Prix: " + medicament.getPrix() + " TND");
        Label descriptionLabel = new Label("Description: " +
                (medicament.getDescription() != null ? medicament.getDescription() : "Non disponible"));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(300);

        VBox content = new VBox(10,
                new HBox(20, imageView, new VBox(5, classeLabel, prixLabel)),
                descriptionLabel);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().setPrefSize(400, 250);

        // Afficher la boîte de dialogue
        dialog.showAndWait();
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(medicament -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return medicament.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        medicament.getClasse().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(medicament.getPrix()).contains(newValue) ||
                        (medicament.getDescription() != null &&
                                medicament.getDescription().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        tn.esprit.MainFx.showAjoutMedicament();
        loadMedicaments();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Medicaments selectedMedicament = Affichagemed.getSelectionModel().getSelectedItem();
        if (selectedMedicament == null) {
            showAlert("Erreur", "Veuillez sélectionner un médicament à modifier.");
            return;
        }
        tn.esprit.MainFx.showModifierMedicament(selectedMedicament);
        loadMedicaments();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Medicaments selected = Affichagemed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Aucun médicament sélectionné");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNom() + " ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            medicamentService.supprimer(selected.getId());
            loadMedicaments();
            showAlert("Succès", "Médicament supprimé avec succès.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}