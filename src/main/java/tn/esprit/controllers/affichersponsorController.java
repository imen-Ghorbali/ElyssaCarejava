package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceSponsor;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class affichersponsorController implements Initializable {

    @FXML
    private TableView<sponsor> sponsorTable;
    @FXML
    private VBox eventCardsContainer;
    @FXML
    private TableColumn<sponsor, Integer> idColumn;

    @FXML
    private TableColumn<sponsor, String> nameColumn;

    @FXML
    private TableColumn<sponsor, String> descriptionColumn;

    @FXML
    private TableColumn<sponsor, String> typeColumn;

    @FXML
    private TableColumn<sponsor, Integer> prixColumn;

    @FXML
    private TableColumn<sponsor, Void> actionColumn;

    private final ServiceSponsor serviceSponsor = new ServiceSponsor();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des colonnes
        idColumn.setVisible(false);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Ajouter boutons Modifier / Supprimer
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnModifier.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5;");
                btnSupprimer.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5;");

                btnModifier.setOnAction(e -> {
                    sponsor s = getTableView().getItems().get(getIndex());
                    openModifierForm(s);
                });

                btnSupprimer.setOnAction(e -> {
                    sponsor s = getTableView().getItems().get(getIndex());
                    deleteSponsor(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10, btnModifier, btnSupprimer);
                    setGraphic(hBox);
                }
            }
        });

        // Charger les sponsors depuis la BDD
        List<sponsor> sponsors = serviceSponsor.getAll();
        sponsorTable.getItems().addAll(sponsors);
    }

    private void openModifierForm(sponsor s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiersponsor.fxml"));
            Parent root = loader.load();

            modifierSponsorController controller = loader.getController();
            controller.setSponsor(s);

            Stage stage = (Stage) sponsorTable.getScene().getWindow();
            stage.setTitle("Modifier Sponsor");
            stage.setScene(new Scene(root));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSponsor(sponsor s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer ce sponsor ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce sponsor ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceSponsor.delete(s.getId());
                sponsorTable.getItems().remove(s);
            }
        });
    }
    @FXML
    private void AjouterSponsor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutersponsor.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) sponsorTable.getScene().getWindow();
            stage.setTitle("Ajouter un sponsor");
            stage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
