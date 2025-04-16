package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.models.Medicaments;
import tn.esprit.models.MaterielMedical;
import tn.esprit.controllers.ModifierMedicaments;
import tn.esprit.controllers.ModifierMaterielController;

import java.io.IOException;

public class MainFx extends Application {

    private static Stage primaryStage;
    private static final String CSS_PATH = "/styles/styles.css"; // Chemin vers votre fichier CSS

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        showAffichageMedicaments();
    }

    // ---------------- Méthodes utilitaires communes ----------------

    private static void configureStage(String title, Parent root) {
        Scene scene = new Scene(root);

        // Chargement du CSS si disponible
        try {
            String css = MainFx.class.getResource(CSS_PATH).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Fichier CSS non trouvé: " + CSS_PATH);
        }

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // ---------------- Gestion des Médicaments ----------------

    public static void showAffichageMedicaments() {
        loadFXML("/affichageMedicaments.fxml", "Gestion des Médicaments");
    }

    public static void showModifierMedicament(Medicaments medicament) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/modifierMedicaments.fxml"));
            Parent root = loader.load();

            ModifierMedicaments controller = loader.getController();
            controller.setMedicament(medicament);

            configureStage("Modifier Médicament", root);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public static void showAjoutMedicament() {
        loadFXML("/AddMedicaments.fxml", "Ajouter Médicament");
    }

    // ---------------- Gestion des Matériels Médicaux ----------------

    public static void showAffichageMateriel() {
        loadFXML("/affichageMateriels.fxml", "Gestion des Matériels Médicaux");
    }

    public static void showModifierMateriel(MaterielMedical materiel) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/modifierMateriel.fxml"));
            Parent root = loader.load();

            ModifierMaterielController controller = loader.getController();
            controller.setMateriel(materiel);

            configureStage("Modifier Matériel Médical", root);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public static void showAjoutMateriel() {
        loadFXML("/AddMateriel.fxml", "Ajouter Matériel Médical");
    }

    // ---------------- Méthodes privées utilitaires ----------------

    private static void loadFXML(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource(fxmlPath));
            Parent root = loader.load();
            configureStage(title, root);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private static void handleIOException(IOException e) {
        System.err.println("Erreur de chargement FXML: " + e.getMessage());
        e.printStackTrace();
        // Vous pourriez ajouter ici une alerte à l'utilisateur
    }

    // ---------------- Getters utiles ----------------

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}