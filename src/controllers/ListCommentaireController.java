package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceCommentaire;
import tn.esprit.mainFX;
import tn.esprit.models.session;

public class ListCommentaireController {

    @FXML private ListView<Commentaire> commentListView;

    private final ServiceCommentaire commentaireService = new ServiceCommentaire();
    private int blogId;
    private final ObservableList<Commentaire> comments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        commentListView.setItems(comments);
        commentListView.setCellFactory(param -> {
            CommentaireListCell cell = new CommentaireListCell();
            cell.setListController(this);
            return cell;
        });
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
        loadComments();
    }

    private void loadComments() {
        comments.clear();
        comments.addAll(commentaireService.getByBlogId(blogId));
    }

    @FXML
    private void handleReturn() {
        commentListView.getScene().getWindow().hide();
    }

    @FXML
    private void handleAdd() {
        mainFX.showAddCommentaireView(blogId);
        loadComments();
    }

    public void handleEdit(Commentaire commentaire) {
        mainFX.showEditCommentaireView(commentaire);
        loadComments();
    }

    public void handleDelete(Commentaire commentaire) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le commentaire");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (commentaireService.delete(commentaire.getId())) {
                comments.remove(commentaire);
            }
        }
    }

    public void handleLike(Commentaire commentaire) {
        if (commentaireService.canVote(commentaire.getId())) {
            if (commentaireService.incrementLike(commentaire.getId())) {
                commentaire.incrementLikes();
                commentListView.refresh();
            }
        }
    }

    public void handleDislike(Commentaire commentaire) {
        if (commentaireService.canVote(commentaire.getId())) {
            if (commentaireService.incrementDislike(commentaire.getId())) {
                commentaire.incrementLikes(-1); // Décrémente les likes (simule un dislike)
                commentListView.refresh();
            }
        }
    }

    // Méthode pour annuler un vote (optionnelle)
    public void handleCancelVote(Commentaire commentaire, boolean wasLike) {
        if (commentaireService.cancelVote(commentaire.getId(), wasLike)) {
            commentaire.incrementLikes(wasLike ? -1 : 1); // Annule le vote précédent
            commentListView.refresh();
        }
    }
}