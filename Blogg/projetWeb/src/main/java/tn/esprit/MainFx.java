package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.controllers.*;
import tn.esprit.models.Blog;
import tn.esprit.models.Commentaire;


public class MainFx extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

   @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Gestion Blog");
        showListBlogView(); // Commencer directement par la liste des blogs
    }

     /*@Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Liste des Commentaires");

        int blogId = 1; // Remplace par un ID de blog existant dans ta base de données
        showListCommentaireView(blogId);
    }*/

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
            controller.setEditMode(blog);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de addBlog.fxml pour l'édition:");
            e.printStackTrace();
        }
    }

    public static void showAddCommentaireView(int blogId) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/addCommentaire.fxml"));
            Parent root = loader.load();

            AddCommentaireController controller = loader.getController();
            controller.setBlogId(blogId);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un commentaire");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur addCommentaire.fxml: " + e.getMessage());
        }
    }

    public static void showListCommentaireView(int blogId) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/listCommentaire.fxml"));
            Parent root = loader.load();

            ListCommentaireController controller = loader.getController();
            controller.setBlogId(blogId);

            Stage stage = new Stage();
            stage.setTitle("Commentaires du Blog #" + blogId);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur listCommentaire.fxml: " + e.getMessage());
        }
    }

    public static void showEditCommentaireView(Commentaire commentaire) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/editCommentaire.fxml"));
            Parent root = loader.load();

            EditCommentaireController controller = loader.getController();
            controller.setCommentaireData(commentaire);

            Stage stage = new Stage();
            stage.setTitle("Modifier commentaire");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de editCommentaire.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}