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
import javafx.scene.control.Button;
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MaterielMedicalService;
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
        loadMateriels();
        configureListView();
        setupSelectionListener();
        setupSearchFilter();
    }

    private void loadMateriels() {
        allMateriels = FXCollections.observableArrayList(materielMedicalService.getAll());
        filteredList = new FilteredList<>(allMateriels, p -> true);
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
        ObservableList<MaterielMedical> pageItems = FXCollections.observableArrayList(filteredList.subList(start, end));
        affichageMateriel.setItems(pageItems);
        return new VBox(affichageMateriel);
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
                            // Essayer comme fichier
                            File file = new File(materiel.getImage());
                            if (file.exists()) {
                                imageView.setImage(new Image(file.toURI().toString()));
                            }
                            // Essayer comme ressource
                            else {
                                InputStream is = getClass().getResourceAsStream(
                                        materiel.getImage().startsWith("/") ?
                                                materiel.getImage() : "/" + materiel.getImage());
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

                    nomLabel.setText(materiel.getNom());
                    detailsLabel.setText(String.format("Classe: %s | Prix: %.2f TND", materiel.getNom(), materiel.getPrix()));

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
        affichageMateriel.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showMaterielDetailsDialog(newVal);
            }
        });
    }

    private void showMaterielDetailsDialog(MaterielMedical materiel) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("✨ Détails du Matériel Médical ✨");
        dialog.setHeaderText(null);

        ImageView imageView = new ImageView();
        try {
            if (materiel.getImage() != null && !materiel.getImage().isEmpty()) {
                File file = new File(materiel.getImage());
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                } else {
                    InputStream is = getClass().getResourceAsStream(
                            materiel.getImage().startsWith("/") ?
                                    materiel.getImage() : "/" + materiel.getImage());
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
        Label nom = new Label("Nom : " + materiel.getNom());
        Label prix = new Label("Prix : " + materiel.getPrix() + " TND");
        Label statut = new Label("Statut : " + materiel.getStatut());

        for (Label label : new Label[]{nom, prix, statut}) {
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a2a72; -fx-font-size: 14px;");
        }

        Label description = new Label("Description : " +
                (materiel.getDescription() != null ? materiel.getDescription() : "Non disponible"));
        description.setWrapText(true);
        description.setMaxWidth(300);
        description.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");

        VBox infoBox = new VBox(10, nom, prix, statut, description);
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
            filteredList.setPredicate(materiel -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return materiel.getNom().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(materiel.getPrix()).contains(newValue)
                        || (materiel.getDescription() != null &&
                        materiel.getDescription().toLowerCase().contains(lowerCaseFilter));
            });
            setupPagination();
        });
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        // Affichage de la fenêtre d'ajout
        tn.esprit.MainFx.showAjoutMateriel();

        // Recharger la liste des matériels
        MaterielMedical newMateriel = materielMedicalService.getLastAddedMateriel(); // Vous devez définir cette méthode dans votre service pour obtenir le dernier matériel ajouté.
        allMateriels.add(newMateriel);  // Ajout du nouvel élément à la liste
        filteredList = new FilteredList<>(allMateriels, p -> true); // Filtrer à nouveau
        setupPagination();  // Reconfigurer la pagination
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        MaterielMedical selected = affichageMateriel.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Alerte", "Veuillez sélectionner un matériel à modifier.");
            return;
        }

        // Appel à la méthode qui ouvre la fenêtre de modification
        tn.esprit.MainFx.showModifierMateriel(selected);
        loadMateriels(); // Recharge les matériels si nécessaire
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
            materielMedicalService.supprimer(selected.getId());
            loadMateriels();
            showAlert("Succès", "Matériel supprimé avec succès !");
        }
    }

    @FXML
    private void handleExportExcel (ActionEvent event) {
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
        File excelFile = new File("materiels_export_" + System.currentTimeMillis() + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Matériels Médicaux");

            // Créer les en-têtes
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nom", "Prix", "Statut", "Description"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Remplir avec les données de la liste filtrée
            int rowNum = 1;
            for (MaterielMedical materiel : filteredList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(materiel.getNom());
                row.createCell(1).setCellValue(materiel.getPrix());
                row.createCell(2).setCellValue(materiel.getStatut());
                row.createCell(3).setCellValue(
                        materiel.getDescription() != null ? materiel.getDescription() : ""
                );
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
}
