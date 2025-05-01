package tn.esprit.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import tn.esprit.models.Commentaire;
import tn.esprit.models.session;

public class CommentaireListCell extends ListCell<Commentaire> {
    private final VBox container = new VBox();
    private final HBox header = new HBox();
    private final StackPane avatarContainer = new StackPane();
    private final Label userLabel = new Label();
    private final Label dateLabel = new Label();
    private final Label likesLabel = new Label();
    private final TextFlow contentFlow = new TextFlow();
    private final Text contentText = new Text();
    private final HBox buttonBox = new HBox(5);
    private final Button editButton = new Button("✏️");
    private final Button deleteButton = new Button("🗑️");
    private final Button likeButton = new Button("👍");
    private final Button dislikeButton = new Button("👎");
    private final Region spacer = new Region();

    private ListCommentaireController listController;

    public CommentaireListCell() {
        super();
        setupUI();
        setupEvents();
    }

    private void setupUI() {
        container.setPadding(new Insets(10));
        container.setSpacing(5);
        container.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        Circle avatar = new Circle(15, Color.web("#6c757d"));
        Label avatarInitial = new Label();
        avatarInitial.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        avatarInitial.setTextFill(Color.WHITE);
        avatarContainer.getChildren().addAll(avatar, avatarInitial);
        avatarContainer.setAlignment(Pos.CENTER);

        header.setSpacing(8);
        header.setAlignment(Pos.CENTER_LEFT);
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dateLabel.setFont(Font.font("Arial", 10));
        dateLabel.setTextFill(Color.GRAY);
        likesLabel.setFont(Font.font("Arial", 10));
        likesLabel.setTextFill(Color.web("#6c757d"));

        contentText.setFont(Font.font("Arial", 12));
        contentText.setWrappingWidth(300);
        contentFlow.getChildren().add(contentText);

        buttonBox.setSpacing(5);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().addAll(likeButton, dislikeButton, spacer, editButton, deleteButton);

        likeButton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");
        dislikeButton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");
        editButton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");

        header.getChildren().addAll(avatarContainer, userLabel, dateLabel, likesLabel);
        container.getChildren().addAll(header, contentFlow, buttonBox);
    }

    private void setupEvents() {
        likeButton.setOnAction(e -> {
            if (listController != null && getItem() != null) {
                listController.handleLike(getItem());
            }
        });

        dislikeButton.setOnAction(e -> {
            if (listController != null && getItem() != null) {
                listController.handleDislike(getItem());
            }
        });

        editButton.setOnAction(e -> {
            if (listController != null && getItem() != null) {
                listController.handleEdit(getItem());
            }
        });

        deleteButton.setOnAction(e -> {
            if (listController != null && getItem() != null) {
                listController.handleDelete(getItem());
            }
        });
    }

    public void setListController(ListCommentaireController controller) {
        this.listController = controller;
    }

    @Override
    protected void updateItem(Commentaire comment, boolean empty) {
        super.updateItem(comment, empty);

        if (empty || comment == null) {
            setGraphic(null);
        } else {
            String username = comment.getNom_utilisateur();
            String initial = username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase();
            ((Label) avatarContainer.getChildren().get(1)).setText(initial);

            userLabel.setText(username);
            dateLabel.setText(comment.getDate());
            likesLabel.setText(comment.getNombre_like() + " votes");
            contentText.setText(comment.getContenu());

            boolean isCurrentUser = session.getCurrentUser() != null &&
                    username.equals(session.getCurrentUser().getName());
            editButton.setVisible(isCurrentUser);
            deleteButton.setVisible(isCurrentUser);

            // Désactiver les boutons si déjà liké/disliké
            likeButton.setDisable(session.hasLiked(comment.getId()));
            dislikeButton.setDisable(session.hasDisliked(comment.getId()));

            setGraphic(container);
        }
    }
}