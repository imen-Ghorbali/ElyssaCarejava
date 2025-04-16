package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceCommentaire;
import tn.esprit.MainFx;

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
            cell.setListController(this); // 🔥 Important !
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
        MainFx.showAddCommentaireView(blogId);
        commentListView.getScene().getWindow().hide();
    }

    public void handleEdit(Commentaire commentaire) {
        MainFx.showEditCommentaireView(commentaire);
        commentListView.getScene().getWindow().hide();
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
        if (commentaireService.incrementLike(commentaire.getId())) {
            commentaire.incrementLikes();
            commentListView.refresh();
        }
    }
}
