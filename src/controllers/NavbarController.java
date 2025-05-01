package tn.esprit.controllers;

import tn.esprit.models.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import java.net.URL;
import java.io.IOException;

public class NavbarController {
    @FXML
    private BorderPane mainLayout;
    @FXML
    private Button btnUser, btnAccueil, btnEvenements, btnMenu, btnConsultation, btnMedicaments, btnMateriel, btnblog;
    @FXML
    private VBox menuDrawer;
    @FXML
    private ImageView menuIcon;

    private user currentUser;

    public void setUser(user u) {
        this.currentUser = u;
        System.out.println("Utilisateur connecté : " + u.getName());
    }

    public void setMainLayout(BorderPane layout) {
        this.mainLayout = layout;
        setupButtonActions();
    }

    private void setupButtonActions() {
        btnUser.setOnAction(e -> loadView("/afficherUser.fxml"));
        btnAccueil.setOnAction(e -> loadView("/accueil.fxml"));
        btnEvenements.setOnAction(e -> loadView("/afficherevents.fxml"));
        btnConsultation.setOnAction(e -> loadView("/addConsultation.fxml"));
        btnMedicaments.setOnAction(e -> loadView("/AffichageMedicaments.fxml"));
        btnMateriel.setOnAction(e -> loadView("/AffichageMateriels.fxml"));
        btnblog.setOnAction(e -> loadView("listBlog.fxml"));
        btnMenu.setOnAction(e -> toggleMenu());
    }

    private void loadView(String fxmlPath) {
        try {
            System.out.println("Tentative de chargement: " + fxmlPath);

            // Normaliser le chemin pour le débogage
            String normalizedPath = fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;
            URL resourceUrl = getClass().getResource(normalizedPath);

            if (resourceUrl == null) {
                throw new IOException("Fichier non trouvé: " + normalizedPath);
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent vue = loader.load();
            System.out.println("Chargement réussi: " + normalizedPath);

            // Passage de l'utilisateur si nécessaire
            if ("/afficherUser.fxml".equals(normalizedPath)) {
                Object controller = loader.getController();
                if (controller instanceof afficherUser) {
                    ((afficherUser) controller).setUser(currentUser);
                }
            }

            mainLayout.setCenter(vue);
            fermerMenu();
        } catch (IOException e) {
            System.err.println("Échec du chargement: " + fxmlPath);
            e.printStackTrace();

            showAlert("Erreur",
                    "Impossible de charger la page",
                    "Le fichier " + fxmlPath + " est introuvable ou corrompu.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void toggleMenu() {
        boolean isVisible = menuDrawer.isVisible();
        menuDrawer.setVisible(!isVisible);
        menuDrawer.setManaged(!isVisible);

        try {
            Image menuImage = new Image(getClass().getResourceAsStream("/icones/menu.png"));
            menuIcon.setImage(menuImage);
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'icône");
        }
    }

    private void fermerMenu() {
        menuDrawer.setVisible(false);
        menuDrawer.setManaged(false);
    }
}