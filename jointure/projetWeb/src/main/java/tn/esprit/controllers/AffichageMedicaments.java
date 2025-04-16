package tn.esprit.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tn.esprit.models.Medicaments;
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MedicamentService;
import tn.esprit.services.MaterielMedicalService;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;

public class AffichageMedicaments {

    @FXML private ListView<Medicaments> affichageMed;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private Label filePathLabel;
    @FXML private ComboBox<MaterielMedical> materielCombo;

    @FXML private TableView<Medicaments> medicamentsTable;
    @FXML private TableColumn<Medicaments, String> colNom;
    @FXML private TableColumn<Medicaments, String> colClasse;
    @FXML private TableColumn<Medicaments, Integer> colPrix;
    @FXML private TableColumn<Medicaments, String> colMateriel;

    private final MedicamentService medicamentService = new MedicamentService();
    private final MaterielMedicalService materielService = new MaterielMedicalService();

    private final int itemsPerPage = 5;
    private ObservableList<Medicaments> allMedicaments;
    private FilteredList<Medicaments> filteredList;

    @FXML
    public void initialize() {
        try {
            setupData();
            setupUI();
            setupEventHandlers();
        } catch (Exception e) {
            showAlert("Erreur d'initialisation", "Une erreur est survenue lors du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupData() {
        allMedicaments = FXCollections.observableArrayList(medicamentService.getAllWithMateriel());
        filteredList = new FilteredList<>(allMedicaments);
        loadMateriels();
    }

    private void loadMateriels() {
        try {
            List<MaterielMedical> materiels = materielService.getAll();
            materielCombo.setItems(FXCollections.observableArrayList(materiels));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les matériels: " + e.getMessage());
        }
    }

    private void setupUI() {
        configureListView();
        configureTableView();
        setupPagination();
    }

    private void configureListView() {
        affichageMed.setCellFactory(param -> new ListCell<Medicaments>() {
            private final ImageView imageView = new ImageView();
            private final Label nomLabel = new Label();
            private final Label detailsLabel = new Label();
            private final Label materielLabel = new Label();
            private final HBox content = new HBox(10, imageView, new VBox(5, nomLabel, detailsLabel, materielLabel));

            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
                content.setAlignment(Pos.CENTER_LEFT);
                nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
                materielLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2a2a72;");
            }

            @Override
            protected void updateItem(Medicaments medicament, boolean empty) {
                super.updateItem(medicament, empty);
                if (empty || medicament == null) {
                    setGraphic(null);
                } else {
                    loadMedicamentImage(medicament);
                    setMedicamentInfo(medicament);
                    setGraphic(content);
                }
            }

            private void loadMedicamentImage(Medicaments medicament) {
                try {
                    if (medicament.getImage() != null && !medicament.getImage().isEmpty()) {
                        Image image = new Image(new File(medicament.getImage()).toURI().toString());
                        imageView.setImage(image);
                    } else {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                    }
                } catch (Exception e) {
                    imageView.setImage(null);
                }
            }

            private void setMedicamentInfo(Medicaments medicament) {
                nomLabel.setText(medicament.getNom());
                detailsLabel.setText(String.format("%s | %d TND", medicament.getClasse(), medicament.getPrix()));
                materielLabel.setText(medicament.getMaterielRequis() != null ?
                        medicament.getMaterielRequis().getNom() : "Aucun matériel");
            }
        });
    }

    private void configureTableView() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colMateriel.setCellValueFactory(cellData -> {
            MaterielMedical mat = cellData.getValue().getMaterielRequis();
            return new SimpleStringProperty(mat != null ? mat.getNom() : "Aucun");
        });
        medicamentsTable.setItems(filteredList);
    }

    private void setupPagination() {
        pagination.setPageCount((int) Math.ceil((double) filteredList.size() / itemsPerPage));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredList.size());
            affichageMed.setItems(FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex)));
            return new VBox(affichageMed);
        });
    }

    private void setupEventHandlers() {
        setupSelectionListener();
        setupSearchFilter();
    }

    private void setupSelectionListener() {
        affichageMed.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filePathLabel.setText("Sélectionné: " + newVal.getNom());
                materielCombo.getSelectionModel().select(newVal.getMaterielRequis());
            }
        });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(medicament -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lowerCaseFilter = newVal.toLowerCase();
                return containsIgnoreCase(medicament.getNom(), lowerCaseFilter) ||
                        containsIgnoreCase(medicament.getClasse(), lowerCaseFilter) ||
                        String.valueOf(medicament.getPrix()).contains(newVal) ||
                        (medicament.getMaterielRequis() != null &&
                                containsIgnoreCase(medicament.getMaterielRequis().getNom(), lowerCaseFilter));
            });
            pagination.setCurrentPageIndex(0);
            setupPagination();
        });
    }

    private boolean containsIgnoreCase(String str, String search) {
        return str != null && str.toLowerCase().contains(search);
    }

    @FXML
    private void handleAssignMateriel(ActionEvent event) {
        Medicaments selected = affichageMed.getSelectionModel().getSelectedItem();
        MaterielMedical materiel = materielCombo.getValue();

        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un médicament");
            return;
        }

        selected.setMaterielRequis(materiel);
        medicamentService.modifier(selected);
        refreshData();
        showAlert("Succès", "Matériel associé avec succès");
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        tn.esprit.MainFx.showAjoutMedicament();
        refreshData();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Medicaments selected = affichageMed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un médicament");
            return;
        }
        tn.esprit.MainFx.showModifierMedicament(selected);
        refreshData();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Medicaments selected = affichageMed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un médicament");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer " + selected.getNom() + "?");
        confirm.setContentText("Cette action est irréversible");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            medicamentService.supprimer(selected.getId());
            refreshData();
            showAlert("Succès", "Médicament supprimé");
        }
    }

    @FXML
    private void handleExportExcel(ActionEvent event) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Médicaments");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nom");
            headerRow.createCell(1).setCellValue("Classe");
            headerRow.createCell(2).setCellValue("Prix");
            headerRow.createCell(3).setCellValue("Matériel");

            int rowNum = 1;
            for (Medicaments m : filteredList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getNom());
                row.createCell(1).setCellValue(m.getClasse());
                row.createCell(2).setCellValue(m.getPrix());
                row.createCell(3).setCellValue(m.getMaterielRequis() != null ?
                        m.getMaterielRequis().getNom() : "");
            }

            File file = new File("medicaments_export.xlsx");
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'export: " + e.getMessage());
        }
    }

    private void refreshData() {
        allMedicaments.setAll(medicamentService.getAllWithMateriel());
        setupPagination();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}