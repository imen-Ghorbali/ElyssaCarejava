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

import java.io.IOException;

public class NavbarController {
    @FXML
    private BorderPane mainLayout;
    @FXML
    private Button btnUser,btnAccueil, btnEvenements, btnMenu,btnConsultation;

    @FXML
    private VBox menuDrawer;



    @FXML
    private ImageView menuIcon;  // Pour changer l'icône du menu hamburger

    private user currentUser; // Pour stocker l'utilisateur connecté

    public void setUser(user u) {
        this.currentUser = u;
        System.out.println("Utilisateur connecté : " + u.getName());
        // Tu peux ici afficher l'utilisateur dans la navbar, etc.
    }

    public void setMainLayout(BorderPane layout) {
        this.mainLayout = layout;
        btnUser.setOnAction(e -> {
            chargerVue("/afficherUser.fxml");
            fermerMenu();
        });

        btnAccueil.setOnAction(e -> {
            chargerVue("/accueil.fxml");
            fermerMenu();
        });
        btnEvenements.setOnAction(e -> {
            chargerVue("/afficherevents.fxml");
            fermerMenu();
        });
        btnConsultation.setOnAction(e -> {
            chargerVue("/addConsultation.fxml");
            fermerMenu();
        });





        btnMenu.setOnAction(e -> toggleMenu());
    }

    private void chargerVue(String cheminFxml) {
        try {
            Parent vue = FXMLLoader.load(getClass().getResource(cheminFxml));
            mainLayout.setCenter(vue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   /* private void chargerVue(String cheminFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));
            Parent vue = loader.load();

            // Si on charge la vue de profil, on passe l'utilisateur connecté
            if (cheminFxml.equals("/afficherUser.fxml")) {
                afficherUser controller = loader.getController();
                controller.setUser(currentUser);
            }

            mainLayout.setCenter(vue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    // Fonction pour ouvrir/fermer le menu
    private void toggleMenu() {
        boolean isVisible = menuDrawer.isVisible();
        menuDrawer.setVisible(!isVisible);
        menuDrawer.setManaged(!isVisible);

        // Change l'icône du menu hamburger
        String iconPath = "/icones/menu.png";  // Assure-toi que le fichier est à cet emplacement
        Image menuImage = new Image(getClass().getResourceAsStream(iconPath));

        if (menuImage.isError()) {
            System.err.println("Erreur lors du chargement de l'icône : " + iconPath);
        }

        if (isVisible) {
            // Remplacer par l'icône hamburger ouverte si nécessaire
            menuIcon.setImage(menuImage);
        } else {
            // Réinitialiser l'icône du menu hamburger
            menuIcon.setImage(menuImage);
        }

    }

    // Fonction pour fermer le menu
    private void fermerMenu() {
        menuDrawer.setVisible(false);
        menuDrawer.setManaged(false);

        // Assurez-vous que l'icône du menu est réinitialisée en hamburger
        menuIcon.setImage(new Image(getClass().getResourceAsStream("/icones/menu.png")));
    }
}
