package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.controllers.Blogcontroller;
import tn.esprit.controllers.ListBlogController;
import tn.esprit.models.Blog;

public class MainFx extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Gestion Blog");
        showAddBlogView();
    }

    public static void showAddBlogView() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/addBlog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de addBlog.fxml:");
            e.printStackTrace();
        }
    }

    public static void showListBlogView() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/listBlog.fxml"));
            Parent root = loader.load();

            ListBlogController controller = loader.getController();
            controller.refreshBlogList();

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de listBlog.fxml:");
            e.printStackTrace();
        }
    }

    public static void showEditBlogView(Blog blog) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/addBlog.fxml"));
            Parent root = loader.load();

            Blogcontroller controller = loader.getController();
            controller.setEditMode(blog); // Configurer le contrôleur en mode édition

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de addBlog.fxml pour l'édition:");
            e.printStackTrace();
        }
    }
}