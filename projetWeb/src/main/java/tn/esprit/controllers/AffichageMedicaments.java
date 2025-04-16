/*package tn.esprit.controllers;

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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.Optional;
import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AffichageMedicaments {

    @FXML private ListView<Medicaments> affichageMed;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private Label filePathLabel;

    private final MedicamentService medicamentService = new MedicamentService();
    private final int itemsPerPage = 5;
    private ObservableList<Medicaments> allMedicaments;
    private FilteredList<Medicaments> filteredList;

    @FXML
    public void initialize() {
        loadMedicaments();
        configureListView();
        setupSelectionListener();
        setupSearchFilter();
    }

    private void loadMedicaments() {
        allMedicaments = FXCollections.observableArrayList(medicamentService.getAll());
        filteredList = new FilteredList<>(allMedicaments, p -> true);
        setupPagination();
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) filteredList.size() / itemsPerPage);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredList.size());
        ObservableList<Medicaments> pageItems = FXCollections.observableArrayList(filteredList.subList(start, end));
        affichageMed.setItems(pageItems);
        return new VBox(affichageMed);
    }

    private void configureListView() {
        affichageMed.setCellFactory(param -> new ListCell<Medicaments>() {
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
                } else {
                    try {
                        // Chargement de l'image
                        if (medicament.getImage() != null && !medicament.getImage().isEmpty()) {
                            // Essayer comme fichier
                            File file = new File(medicament.getImage());
                            if (file.exists()) {
                                imageView.setImage(new Image(file.toURI().toString()));
                            }
                            // Essayer comme ressource
                            else {
                                InputStream is = getClass().getResourceAsStream(
                                        medicament.getImage().startsWith("/") ?
                                                medicament.getImage() : "/" + medicament.getImage());
                                if (is != null) {
                                    imageView.setImage(new Image(is));
                                } else {
                                    loadDefaultImage();
                                }
                            }
                        } else {
                            loadDefaultImage();
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur chargement image: " + e.getMessage());
                        loadDefaultImage();
                    }

                    nomLabel.setText(medicament.getNom());
                    detailsLabel.setText(String.format("Classe: %s | Prix: %d TND",
                            medicament.getClasse(), medicament.getPrix()));
                    setGraphic(content);
                }
            }

            private void loadDefaultImage() {
                try (InputStream is = getClass().getResourceAsStream("/images/default.png")) {
                    imageView.setImage(is != null ? new Image(is) : null);
                } catch (IOException e) {
                    imageView.setImage(null);
                }
            }
        });
    }

    private void setupSelectionListener() {
        affichageMed.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showMedicamentDetailsDialog(newVal);
            }
        });
    }

    private void showMedicamentDetailsDialog(Medicaments medicament) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("✨ Détails du Médicament ✨");
        dialog.setHeaderText(null);

        ImageView imageView = new ImageView();
        try {
            if (medicament.getImage() != null && !medicament.getImage().isEmpty()) {
                File file = new File(medicament.getImage());
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                } else {
                    InputStream is = getClass().getResourceAsStream(
                            medicament.getImage().startsWith("/") ?
                                    medicament.getImage() : "/" + medicament.getImage());
                    if (is != null) {
                        imageView.setImage(new Image(is));
                    } else {
                        loadDefaultImage(imageView);
                    }
                }
            } else {
                loadDefaultImage(imageView);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image: " + e.getMessage());
            loadDefaultImage(imageView);
        }

        imageView.setFitHeight(180);
        imageView.setFitWidth(180);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0.3, 4, 4);");

        // Titres stylisés
        Label nom = new Label("Nom : " + medicament.getNom());
        Label classe = new Label("Classe : " + medicament.getClasse());
        Label prix = new Label("Prix : " + medicament.getPrix() + " TND");

        for (Label label : new Label[]{nom, classe, prix}) {
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a2a72; -fx-font-size: 14px;");
        }

        Label description = new Label("Description : " +
                (medicament.getDescription() != null ? medicament.getDescription() : "Non disponible"));
        description.setWrapText(true);
        description.setMaxWidth(300);
        description.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");

        VBox infoBox = new VBox(10, nom, classe, prix, description);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox contentBox = new HBox(25, imageView, infoBox);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: #f5f7fa;");

        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().setPrefSize(500, 300);
        dialog.showAndWait();
    }




    private void loadDefaultImage(ImageView imageView) {
        try (InputStream is = getClass().getResourceAsStream("/images/default.png")) {
            imageView.setImage(is != null ? new Image(is) : null);
        } catch (IOException e) {
            imageView.setImage(null);
        }
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(medicament -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return medicament.getNom().toLowerCase().contains(lowerCaseFilter)
                        || medicament.getClasse().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(medicament.getPrix()).contains(newValue)
                        || (medicament.getDescription() != null &&
                        medicament.getDescription().toLowerCase().contains(lowerCaseFilter));
            });
            setupPagination();
        });
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        tn.esprit.MainFx.showAjoutMedicament();
        loadMedicaments();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Medicaments selected = affichageMed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Alerte", "Veuillez sélectionner un médicament à modifier.");
            return;
        }
        tn.esprit.MainFx.showModifierMedicament(selected);
        loadMedicaments();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Medicaments selected = affichageMed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Alerte", "Aucun médicament sélectionné pour suppression.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Voulez-vous supprimer \"" + selected.getNom() + "\" ?");
        confirmation.setContentText("Cette action est irréversible. Êtes-vous sûr ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            medicamentService.supprimer(selected.getId());
            loadMedicaments();
            showAlert("Succès", "Médicament supprimé avec succès !");
        }
    }

    @FXML
    private void handleSaveAndShowExcel(ActionEvent event) {
        try {
            File excelFile = createExcelFile();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(excelFile);
            } else {
                showAlert("Erreur", "Impossible d'ouvrir le fichier Excel.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'export Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private File createExcelFile() throws IOException {
        File excelFile = new File("medicaments_export_" + System.currentTimeMillis() + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Médicaments");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nom", "Classe", "Prix", "Description"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Medicaments med : filteredList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(med.getNom());
                row.createCell(1).setCellValue(med.getClasse());
                row.createCell(2).setCellValue(med.getPrix());
                row.createCell(3).setCellValue(med.getDescription() != null ? med.getDescription() : "");
            }

            try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
                workbook.write(outputStream);
            }
        }

        return excelFile;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}*/