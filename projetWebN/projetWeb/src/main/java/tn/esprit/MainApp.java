package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Gestion de Blog");
        showAddBlogView();
    }

    public static void showAddBlogView() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/addBlog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showListBlogView() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/listBlog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}