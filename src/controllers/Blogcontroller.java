package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tn.esprit.mainFX;
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

    private File selectedImageFile;
    private String savedImagePath;
    private final ServiceBlog serviceBlog = new ServiceBlog();
    private Blog blogToEdit;
    private boolean isEditMode = false;

    private final String IMAGE_DIRECTORY = "src/main/resources/images/blog/";

    @FXML
    private void initialize() {
        new File(IMAGE_DIRECTORY).mkdirs();

        imagePreview.setPreserveRatio(true);
        imagePreview.setFitWidth(150);
        imagePreview.setFitHeight(150);
        uploadButton.setVisible(true);
    }

    @FXML
    private void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            try {
                selectedImageFile = file;
                imagePathField.setText(file.getAbsolutePath());
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
                uploadButton.setVisible(false);
            } catch (Exception e) {
                showErrorAlert("Erreur", "Impossible de charger l'image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        if (!validateFields()) return;

        if (isEditMode) {
            blogToEdit.setTitre(titreField.getText());
            blogToEdit.setContenu(contenuArea.getText());
            blogToEdit.setAuteur(auteurField.getText());
            blogToEdit.setDate_publication(LocalDate.now().toString());
            blogToEdit.setImage(savedImagePath);

            try {
                serviceBlog.update(blogToEdit);
                showSuccessAlert("Succès", "Blog modifié avec succès");
                mainFX.showListBlogView();
            } catch (Exception e) {
                showErrorAlert("Erreur", "Erreur lors de la modification du blog: " + e.getMessage());
            }
        } else {
            Blog blog = new Blog();
            blog.setTitre(titreField.getText());
            blog.setContenu(contenuArea.getText());
            blog.setAuteur(auteurField.getText());
            blog.setDate_publication(LocalDate.now().toString());

            try {
                if (selectedImageFile != null) {
                    String imagePath = saveImageToDirectory(selectedImageFile, blog.getTitre());
                    blog.setImage(imagePath);  // Stocker le chemin relatif
                    savedImagePath = imagePath;
                }

                serviceBlog.add(blog);
                showSuccessAlert("Succès", "Blog ajouté avec succès");
                clearFields();
                mainFX.showListBlogView();
            } catch (Exception e) {
                showErrorAlert("Erreur", "Erreur lors de l'ajout du blog: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleShowList(ActionEvent event) {
        mainFX.showListBlogView();
    }

    public void setEditMode(Blog blog) {
        this.blogToEdit = blog;
        this.isEditMode = true;

        titreField.setText(blog.getTitre());
        contenuArea.setText(blog.getContenu());
        auteurField.setText(blog.getAuteur());
        imagePathField.setText(blog.getImage());

        if (blog.getImage() != null && !blog.getImage().isEmpty()) {
            try {
                String absolutePath = new File("src/main/resources/" + blog.getImage()).getAbsolutePath();
                Image image = new Image("file:" + absolutePath);
                imagePreview.setImage(image);
                savedImagePath = blog.getImage();
                uploadButton.setVisible(false);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }

    private String saveImageToDirectory(File imageFile, String blogTitle) throws IOException {
        String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
        String newFileName = blogTitle.replaceAll("[^a-zA-Z0-9]", "") + "_" + System.currentTimeMillis() + extension;
        Path destination = Path.of(IMAGE_DIRECTORY + newFileName);
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Retourner un chemin RELATIF par rapport à src/main/resources
        return "images/blog/" + newFileName;
    }

    private boolean validateFields() {
        StringBuilder errorMsg = new StringBuilder();

        if (titreField.getText().trim().isEmpty()) {
            errorMsg.append("- Le titre est requis.\n");
        }
        if (contenuArea.getText().trim().isEmpty()) {
            errorMsg.append("- Le contenu est requis.\n");
        } else if (contenuArea.getText().length() < 10) {
            errorMsg.append("- Le contenu doit contenir au moins 10 caractères.\n");
        }
        if (auteurField.getText().trim().isEmpty()) {
            errorMsg.append("- L'auteur est requis.\n");
        }

        if (errorMsg.length() > 0) {
            showErrorAlert("Champs invalides", errorMsg.toString());
            return false;
        }

        return true;
    }

    private void clearFields() {
        titreField.clear();
        contenuArea.clear();
        auteurField.clear();
        imagePathField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;
        uploadButton.setVisible(true);
        isEditMode = false;
        blogToEdit = null;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
