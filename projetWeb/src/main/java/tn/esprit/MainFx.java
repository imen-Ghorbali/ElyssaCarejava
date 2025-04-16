package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.models.MaterielMedical;
import tn.esprit.controllers.ModifierMaterielController;  // Modifier le nom du contrôleur ici

import java.io.IOException;

public class MainFx extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showAffichageMateriel();
    }

    public static void showAffichageMateriel() {
        try {
            // Charger l'écran d'affichage des matériels
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/affichageMateriels.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Gestion des Matériels Médicaux");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showModifierMateriel(MaterielMedical materiel) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/modifierMateriel.fxml"));

            Parent root = loader.load();

            ModifierMaterielController controller = loader.getController();
            controller.setMateriel(materiel); // passe le matériel à modifier

            Stage stage = new Stage();
            stage.setTitle("Modifier Matériel");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void showAjoutMateriel() {
        try {
            // Charger l'écran d'ajout de matériel
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/AddMateriel.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Ajouter Matériel Médical");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
