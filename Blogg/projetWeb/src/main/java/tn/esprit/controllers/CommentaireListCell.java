package tn.esprit.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import tn.esprit.models.Commentaire;

public class CommentaireListCell extends ListCell<Commentaire> {

    private final VBox container = new VBox(5);
    private final Label userLabel = new Label();
    private final Label dateLabel = new Label();
    private final Label likesLabel = new Label();
    private final Text content = new Text();
    private final Button editButton = new Button("Modifier");
    private final Button deleteButton = new Button("Supprimer");
    private final Button likeButton = new Button("Like");

    private ListCommentaireController listController;

    public CommentaireListCell() {
        super();

        container.setStyle("-fx-padding: 10; -fx-background-color: #f8f8f8; -fx-border-color: #ddd; -fx-border-radius: 5;");
        content.setWrappingWidth(750);

        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        likeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        HBox buttons = new HBox(10, editButton, deleteButton, likeButton);
        HBox header = new HBox(10,
                new Label("Par:"), userLabel,
                new Label("Le:"), dateLabel,
                new Label("Likes:"), likesLabel
        );

        container.getChildren().addAll(header, content, buttons);

        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
        likeButton.setOnAction(event -> handleLike());
    }

    public void setListController(ListCommentaireController controller) {
        this.listController = controller;
    }

    private void handleEdit() {
        if (listController != null) {
            listController.handleEdit(getItem());
        }
    }

    private void handleDelete() {
        if (listController != null) {
            listController.handleDelete(getItem());
        }
    }

    private void handleLike() {
        if (listController != null) {
            listController.handleLike(getItem());
        }
    }

    @Override
    protected void updateItem(Commentaire comment, boolean empty) {
        super.updateItem(comment, empty);
        if (empty || comment == null) {
            setGraphic(null);
        } else {
            userLabel.setText(comment.getNom_utilisateur());
            dateLabel.setText(comment.getDate());
            likesLabel.setText(String.valueOf(comment.getNombre_like()));
            content.setText(comment.getContenu());
            setGraphic(container);
        }
    }
}
