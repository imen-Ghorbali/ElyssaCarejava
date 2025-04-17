package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import tn.esprit.controllers.NavbarController;

public class mainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger navbar.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
        BorderPane root = loader.load();

        // Récupérer le contrôleur et appeler setMainLayout
        NavbarController controller = loader.getController();
        controller.setMainLayout(root);

        // Créer la scène et afficher
        Scene scene = new Scene(root);
        primaryStage.setTitle("Ajouter un événement");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
