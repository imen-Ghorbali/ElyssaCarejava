package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tn.esprit.MainFx;
import tn.esprit.models.Blog;
import tn.esprit.services.ServiceBlog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class Blogcontroller {

    @FXML private TextField titreField;
    @FXML private TextArea contenuArea;
    @FXML private TextField auteurField;
    @FXML private TextField imagePathField;
    @FXML private Button uploadButton;
    @FXML private ImageView imagePreview;
    @FXML private Button submitButton;
    @FXML private Button listButton;

    private String savedImagePath;
    private final ServiceBlog serviceBlog = new ServiceBlog();
    private Blog blogToEdit;
    private boolean isEditMode = false;

    @FXML
    private void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Path uploadDir = Path.of(System.getProperty("user.dir"), "uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destination = uploadDir.resolve(uniqueFileName);

                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                savedImagePath = destination.toString();
                imagePathField.setText(savedImagePath);
                imagePreview.setImage(new Image(destination.toUri().toString()));
            } catch (IOException e) {
                showAlert("Erreur", "Échec du téléchargement",
                        "Impossible de sauvegarder l'image: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        if (!validateFields()) {
            showAlert("Validation", "Champs manquants",
                    "Veuillez remplir tous les champs obligatoires (Titre, Contenu et Auteur)",
                    Alert.AlertType.WARNING);
            return;
        }

        if (isEditMode) {
            // Mise à jour du blog existant
            blogToEdit.setTitre(titreField.getText());
            blogToEdit.setContenu(contenuArea.getText());
            blogToEdit.setAuteur(auteurField.getText());
            blogToEdit.setDate_publication(LocalDate.now().toString());
            blogToEdit.setImage(savedImagePath);

            try {
                serviceBlog.update(blogToEdit);
                showAlert("Succès", "Modification réussie",
                        "Le blog a été modifié avec succès", Alert.AlertType.INFORMATION);
                MainFx.showListBlogView();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la modification",
                        "Erreur lors de la modification du blog: " + e.getMessage(),
                        Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            // Création d'un nouveau blog
            Blog blog = new Blog();
            blog.setTitre(titreField.getText());
            blog.setContenu(contenuArea.getText());
            blog.setAuteur(auteurField.getText());
            blog.setDate_publication(LocalDate.now().toString());
            blog.setImage(savedImagePath);

            try {
                serviceBlog.add(blog);
                showAlert("Succès", "Enregistrement réussi",
                        "Le blog a été ajouté avec succès", Alert.AlertType.INFORMATION);
                clearFields();
                MainFx.showListBlogView();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de l'enregistrement",
                        "Erreur lors de l'ajout du blog: " + e.getMessage(),
                        Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleShowList(ActionEvent event) {
        MainFx.showListBlogView();
    }

    public void setEditMode(Blog blog) {
        this.blogToEdit = blog;
        this.isEditMode = true;

        // Pré-remplir les champs
        titreField.setText(blog.getTitre());
        contenuArea.setText(blog.getContenu());
        auteurField.setText(blog.getAuteur());
        imagePathField.setText(blog.getImage());

        if (blog.getImage() != null && !blog.getImage().isEmpty()) {
            try {
                imagePreview.setImage(new Image("file:" + blog.getImage()));
                savedImagePath = blog.getImage();
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        return titreField.getText() != null && !titreField.getText().isEmpty() &&
                contenuArea.getText() != null && !contenuArea.getText().isEmpty() &&
                auteurField.getText() != null && !auteurField.getText().isEmpty();
    }

    private void clearFields() {
        titreField.clear();
        contenuArea.clear();
        auteurField.clear();
        imagePathField.clear();
        imagePreview.setImage(null);
        savedImagePath = null;
        isEditMode = false;
        blogToEdit = null;
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        imagePreview.setPreserveRatio(true);
        imagePreview.setFitWidth(200);
        imagePreview.setFitHeight(150);
    }
}