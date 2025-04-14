package tn.esprit.controllers;

import javafx.scene.control.ListCell;
import tn.esprit.models.Blog;

public class BlogCell extends ListCell<Blog> {
    @Override
    protected void updateItem(Blog blog, boolean empty) {
        super.updateItem(blog, empty);

        if (empty || blog == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(String.format(
                    "Titre: %s\nAuteur: %s\nDate: %s\n%s...",
                    blog.getTitre(),
                    blog.getAuteur(),
                    blog.getDate_publication(),
                    blog.getContenu().substring(0, Math.min(blog.getContenu().length(), 50))
            ));
        }
    }
}