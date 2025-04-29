package tn.esprit.controllers;

import tn.esprit.models.events;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.services.PdfService;

public class PdfController {

    @FXML
    private TableView<events> tableEvents;

    @FXML
    private TableColumn<events, String> colTitre;

    @FXML
    private TableColumn<events, String> colLieu;

    @FXML
    private TableColumn<events, String> colDate;

    @FXML
    private TableColumn<events, Void> colPdf;

    private final PdfService pdfService = new PdfService();

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        colTitre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        colLieu.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLieu()));
        colDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate().toString()));

        // Ajouter la colonne PDF
        ajouterColonnePDF();
    }

    private void ajouterColonnePDF() {
        // Créer la colonne pour générer un PDF
        colPdf.setCellFactory(param -> new TableCell<events, Void>() {
            private final Button btn = new Button("PDF");

            {
                btn.setOnAction(event -> {
                    events selectedEvent = getTableView().getItems().get(getIndex());

                    // Création du fichier PDF
                    String nomFichier = selectedEvent.getTitle().replaceAll("\\s+", "_") + ".pdf";
                    String contenu = "Titre : " + selectedEvent.getTitle() + "\n" +
                            "Lieu : " + selectedEvent.getLieu() + "\n" +
                            "Date : " + selectedEvent.getDate() + "\n" +
                            "Description : " + selectedEvent.getDescription();

                    // Récupération du chemin de l'image de l'événement (ajuster selon votre structure)
                    String eventImagePath = selectedEvent.getImage(); // Assurez-vous que cette méthode existe dans la classe `events`

                    pdfService.genererPdf(nomFichier, contenu, eventImagePath);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    public void setTableData(TableView<events> tableView) {
        // Méthode pour lier la table à des données
        tableEvents.setItems(tableView.getItems());
    }
}
