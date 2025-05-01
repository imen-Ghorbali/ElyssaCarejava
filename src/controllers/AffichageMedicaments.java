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
import javafx.scene.layout.VBox;
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

    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private Label filePathLabel;
    @FXML private ComboBox<MaterielMedical> materielCombo;
    @FXML private TableView<Medicaments> medicamentsTable;
    @FXML private TableColumn<Medicaments, String> colNom;
    @FXML private TableColumn<Medicaments, String> colClasse;
    @FXML private TableColumn<Medicaments, Integer> colPrix;
    @FXML private TableColumn<Medicaments, String> colMateriel;
    @FXML private VBox detailsContainer;
    @FXML private Label detailNom;
    @FXML private Label detailClasse;
    @FXML private Label detailPrix;
    @FXML private Label detailMateriel;
    @FXML private Label detailDescription;
    @FXML private ImageView detailImage;

    private final MedicamentService medicamentService = new MedicamentService();
    private final MaterielMedicalService materielService = new MaterielMedicalService();
    private final int itemsPerPage = 10;
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
        configureTableView();
        setupPagination();
    }

    private void configureTableView() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colMateriel.setCellValueFactory(cellData -> {
            MaterielMedical mat = cellData.getValue().getMaterielRequis();
            return new SimpleStringProperty(mat != null ? mat.getNom() : "Aucun matériel");
        });
        medicamentsTable.setItems(filteredList);
    }

    private void setupPagination() {
        pagination.setPageCount((int) Math.ceil((double) filteredList.size() / itemsPerPage));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredList.size());
            medicamentsTable.setItems(FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex)));
            return new VBox(medicamentsTable);
        });
    }

    private void setupEventHandlers() {
        setupSelectionListener();
        setupSearchFilter();
    }

    private void setupSelectionListener() {
        medicamentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showMedicamentDetails(newVal);
                filePathLabel.setText("Sélectionné: " + newVal.getNom());
                materielCombo.getSelectionModel().select(newVal.getMaterielRequis());
            }
        });
    }

    private void showMedicamentDetails(Medicaments medicament) {
        detailsContainer.setVisible(true);
        detailNom.setText(medicament.getNom());
        detailClasse.setText("Classe: " + medicament.getClasse());
        detailPrix.setText("Prix: " + medicament.getPrix() + " TND");
        detailMateriel.setText("Matériel requis: " + (medicament.getMaterielRequis() != null ?
                medicament.getMaterielRequis().getNom() : "Aucun matériel"));
        detailDescription.setText("Description: " + (medicament.getDescription() != null ?
                medicament.getDescription() : "Non disponible"));

        try {
            if (medicament.getImage() != null && !medicament.getImage().isEmpty()) {
                Image image = new Image(new File(medicament.getImage()).toURI().toString());
                detailImage.setImage(image);
            } else {
                detailImage.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } catch (Exception e) {
            detailImage.setImage(null);
        }
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
        Medicaments selected = medicamentsTable.getSelectionModel().getSelectedItem();
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
        tn.esprit.mainFX.showAjoutMedicament();
        refreshData();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Medicaments selected = medicamentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un médicament");
            return;
        }
        tn.esprit.mainFX.showModifierMedicament(selected);
        refreshData();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Medicaments selected = medicamentsTable.getSelectionModel().getSelectedItem();
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

            // En-têtes
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nom");
            headerRow.createCell(1).setCellValue("Classe");
            headerRow.createCell(2).setCellValue("Prix (TND)");
            headerRow.createCell(3).setCellValue("Matériel");

            // Données
            int rowNum = 1;
            for (Medicaments m : filteredList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getNom());
                row.createCell(1).setCellValue(m.getClasse());
                row.createCell(2).setCellValue(m.getPrix());
                row.createCell(3).setCellValue(m.getMaterielRequis() != null ? m.getMaterielRequis().getNom() : "");
            }

            // Enregistrement
            File file = new File("medicaments_export.xlsx");
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }

            // Ouverture automatique
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

            showAlert("Succès", "Export Excel réalisé avec succès");
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'export: " + e.getMessage());
        }
    }

    private void refreshData() {
        allMedicaments.setAll(medicamentService.getAllWithMateriel());
        setupPagination();
        detailsContainer.setVisible(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}