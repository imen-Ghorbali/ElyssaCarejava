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
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MaterielMedicalService;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.Optional;
import java.awt.Desktop;
import java.util.List;
import tn.esprit.MainFx;
import javafx.stage.FileChooser;
import javafx.stage.Window;
public class AffichageMaterielsController {

    @FXML
    private ListView<MaterielMedical> affichageMateriel;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private Label filePathLabel;

    private final MaterielMedicalService materielMedicalService = new MaterielMedicalService();
    private final int itemsPerPage = 5;
    private ObservableList<MaterielMedical> allMateriels;
    private FilteredList<MaterielMedical> filteredList;

    @FXML
    public void initialize() {
        try {
            // Initialisation des données
            allMateriels = FXCollections.observableArrayList(materielMedicalService.getAll());
            filteredList = new FilteredList<>(allMateriels, p -> true);

            // Configuration de l'interface
            configureListView();
            setupPagination();
            setupSearchFilter();

            // Sélection par défaut
            if (!allMateriels.isEmpty()) {
                affichageMateriel.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureListView() {
        affichageMateriel.setCellFactory(param -> new ListCell<MaterielMedical>() {
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
            protected void updateItem(MaterielMedical materiel, boolean empty) {
                super.updateItem(materiel, empty);
                if (empty || materiel == null) {
                    setGraphic(null);
                } else {
                    try {
                        // Chargement de l'image
                        if (materiel.getImage() != null && !materiel.getImage().isEmpty()) {
                            try {
                                // Essayer comme fichier
                                File file = new File(materiel.getImage());
                                if (file.exists()) {
                                    imageView.setImage(new Image(file.toURI().toString()));
                                } else {
                                    // Essayer comme ressource
                                    InputStream is = getClass().getResourceAsStream(
                                            materiel.getImage().startsWith("/") ?
                                                    materiel.getImage() : "/" + materiel.getImage());
                                    imageView.setImage(is != null ? new Image(is) : loadDefaultImage());
                                }
                            } catch (Exception e) {
                                imageView.setImage(loadDefaultImage());
                            }
                        } else {
                            imageView.setImage(loadDefaultImage());
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur chargement image: " + e.getMessage());
                        imageView.setImage(loadDefaultImage());
                    }

                    nomLabel.setText(materiel.getNom());
                    detailsLabel.setText(String.format("Prix: %.2f TND | Statut: %s",
                            materiel.getPrix(), materiel.getStatut()));

                    setGraphic(content);
                }
            }
        });
    }

    private Image loadDefaultImage() {
        try (InputStream is = getClass().getResourceAsStream("/images/default.png")) {
            return is != null ? new Image(is) : null;
        } catch (IOException e) {
            return null;
        }
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) filteredList.size() / itemsPerPage);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredList.size());
            affichageMateriel.setItems(FXCollections.observableArrayList(
                    filteredList.subList(fromIndex, toIndex)));
            return new VBox(affichageMateriel);
        });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(materiel -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return materiel.getNom().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(materiel.getPrix()).contains(newValue)
                        || materiel.getStatut().toLowerCase().contains(lowerCaseFilter)
                        || (materiel.getDescription() != null &&
                        materiel.getDescription().toLowerCase().contains(lowerCaseFilter));
            });
            pagination.setCurrentPageIndex(0);
            setupPagination();
        });
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        MainFx.showAjoutMateriel();
        refreshData();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        MaterielMedical selected = affichageMateriel.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Alerte", "Veuillez sélectionner un matériel à modifier.");
            return;
        }
        MainFx.showModifierMateriel(selected);
        refreshData();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        MaterielMedical selected = affichageMateriel.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Alerte", "Aucun matériel sélectionné pour suppression.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Voulez-vous supprimer \"" + selected.getNom() + "\" ?");
        confirmation.setContentText("Cette action est irréversible. Êtes-vous sûr ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                materielMedicalService.supprimer(selected.getId());
                refreshData();
                showAlert("Succès", "Matériel supprimé avec succès !");
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportExcel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter vers Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        fileChooser.setInitialFileName("materiels_medical_" + System.currentTimeMillis() + ".xlsx");

        Window window = affichageMateriel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            try {
                exportToExcel(file);
                filePathLabel.setText("Exporté vers: " + file.getAbsolutePath());
                showAlert("Succès", "Export Excel réussi !");

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Échec de l'export: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void exportToExcel(File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Matériels Médicaux");

            // Style pour les en-têtes
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // En-têtes
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nom", "Description", "Prix", "Statut", "Image Path"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i); // Nom complet
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données
            int rowNum = 1;
            for (MaterielMedical materiel : filteredList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(materiel.getId());
                row.createCell(1).setCellValue(materiel.getNom());
                row.createCell(2).setCellValue(materiel.getDescription());
                row.createCell(3).setCellValue(materiel.getPrix());
                row.createCell(4).setCellValue(materiel.getStatut());
                row.createCell(5).setCellValue(materiel.getImage());
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sauvegarder
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
    }

    private void refreshData() {
        allMateriels.setAll(materielMedicalService.getAll());
        filteredList.setPredicate(p -> true); // Réinitialiser le filtre
        pagination.setCurrentPageIndex(0);
        setupPagination();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}