package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import tn.esprit.models.Blog;

public class BlogEdit {
    @FXML private TextField titreField;
    @FXML private TextArea contenuArea;
    @FXML private TextField dateField;
    @FXML private TextField auteurField;
    @FXML private TextField imageField;

    private Blog blog;

    public void setBlog(Blog blog) {
        this.blog = blog;
        // Initialiser les champs avec les valeurs du blog
        titreField.setText(blog.getTitre());
        contenuArea.setText(blog.getContenu());
        dateField.setText(blog.getDate_publication());
        auteurField.setText(blog.getAuteur());
        imageField.setText(blog.getImage());
    }

    public Blog getUpdatedBlog() {
        // Mettre à jour le blog avec les nouvelles valeurs
        blog.setTitre(titreField.getText());
        blog.setContenu(contenuArea.getText());
        blog.setDate_publication(dateField.getText());
        blog.setAuteur(auteurField.getText());
        blog.setImage(imageField.getText());

        return blog;
    }
}