package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tn.esprit.controllers.*;
import tn.esprit.models.MaterielMedical;
import tn.esprit.models.Medicaments;
import tn.esprit.models.Blog;
import tn.esprit.models.Commentaire;
import java.io.IOException;

public class mainFX extends Application {

    private static Stage primaryStage;

    // ⚠️ À adapter selon le bon chemin vers ton fichier CSS
    private static final String CSS_PATH = "/css/styles.css";

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Afficher directement la navbar au démarrage
        showConnexion();
    }

    // ---------------- Configuration de la fenêtre ----------------
    private static void configureStage(String title, Parent root) {
        Scene scene = new Scene(root);

        try {
            String css = mainFX.class.getResource(CSS_PATH).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Fichier CSS non trouvé: " + CSS_PATH);
            e.printStackTrace(); // Ajout du printStackTrace()
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
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/modifierMedicaments.fxml"));
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
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/modifierMateriel.fxml"));
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

    // ---------------- Gestion des Blogs et Commentaires ----------------
    public static void showAddBlogView() {
        try {
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/addBlog.fxml"));
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
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/listBlog.fxml"));
            Parent root = loader.load();

            ListBlogController controller = loader.getController();
            controller.refreshBlogList();

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setTitle("blogs medicales");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de listBlog.fxml:");
            e.printStackTrace();
        }
    }

    public static void showEditBlogView(Blog blog) {
        try {
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/addBlog.fxml"));
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
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/addCommentaire.fxml"));
            Parent root = loader.load();

            AddCommentaireController controller = loader.getController();
            controller.setBlogId(blogId);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un commentaire");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur addCommentaire.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showListCommentaireView(int blogId) {
        try {
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/listCommentaire.fxml"));
            Parent root = loader.load();

            ListCommentaireController controller = loader.getController();
            controller.setBlogId(blogId);

            Stage stage = new Stage();
            stage.setTitle("Commentaires du Blog #" + blogId);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur listCommentaire.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showEditCommentaireView(Commentaire commentaire) {
        try {
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/editCommentaire.fxml"));
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

    // ---------------- Connexion / Navbar ----------------
    public static void showConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/connexion.fxml"));
            Parent root = loader.load();
            configureStage("Connexion", root);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public static void showNavbar() {
        try {
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource("/navbar.fxml"));
            Parent root = loader.load();
            NavbarController controller = loader.getController();
            BorderPane layout = (BorderPane) root;
            controller.setMainLayout(layout);
            configureStage("Application avec Navbar", root);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    // ---------------- Méthodes utilitaires ----------------
    private static void loadFXML(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(mainFX.class.getResource(fxmlPath));
            Parent root = loader.load();
            configureStage(title, root);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private static void handleIOException(IOException e) {
        System.err.println("Erreur de chargement FXML: " + e.getMessage());
        e.printStackTrace();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}