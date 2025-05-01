package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class AccueilController {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private AnchorPane contentPane;

    public void initialize() {
        // Charger l'image dynamiquement si nécessaire
        Image image = new Image(getClass().getResourceAsStream("/icones/hopital.png"));
        backgroundImage.setImage(image);

        // Optionnel : Ajouter des effets à l'image ou modifier la transparence
        backgroundImage.setOpacity(0.5); // rendre l'image semi-transparente
    }
}
