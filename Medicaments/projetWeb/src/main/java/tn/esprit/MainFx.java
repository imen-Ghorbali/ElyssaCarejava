package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.models.Medicaments;
import tn.esprit.controllers.ModifierMedicaments;

import java.io.IOException;

public class MainFx extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showAffichageMedicaments();
    }

    public static void showAffichageMedicaments() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/affichageMedicaments.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Gestion des Médicaments");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showModifierMedicament(Medicaments medicament) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/modifierMedicaments.fxml"));
            Parent root = loader.load();

            ModifierMedicaments controller = loader.getController();
            controller.setMedicament(medicament);

            primaryStage.setTitle("Modifier Médicament");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void showAjoutMedicament() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/AddMedicaments.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Ajouter Médicament");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
