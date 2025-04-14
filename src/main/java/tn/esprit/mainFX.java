package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML pour ajouter un événement
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutersponsor.fxml"));
        Parent root = loader.load();

        // Créer la scène et définir le titre de la fenêtre
        Scene scene = new Scene(root);
        primaryStage.setTitle("Ajouter un événement");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

