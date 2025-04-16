package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.models.Blog;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceBlog;
import tn.esprit.services.ServiceCommentaire;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.List;
import java.util.Optional; // ✅ Import ajouté
import java.util.ResourceBundle;

// ⚠️ Assurez-vous que la classe MainFx est bien définie dans ce package
import tn.esprit.MainFx;

public class ListBlogController implements Initializable {

    @FXML
    private ListView<Blog> blogListView;
    @FXML
    private Pagination pagination;
    @FXML
    private HBox commentSection;
    @FXML
    private TextField commentTextField;

    private final ServiceBlog blogService = new ServiceBlog();
    private final ServiceCommentaire commentService = new ServiceCommentaire();
    private List<Blog> filteredList;
    private final int itemsPerPage = 5;
    private Blog selectedBlog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        blogListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Blog blog, boolean empty) {
                super.updateItem(blog, empty);
                if (empty || blog == null) {
                    setText(null);
                } else {
                    setText("📘 " + blog.getTitre() + " - Par " + blog.getAuteur());
                }
            }
        });

        refreshBlogList();
        setupSelectionListener();
    }

    private void setupSelectionListener() {
        blogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedBlog = newVal;
                showBlogDetailsDialog(newVal);
                showCommentSection(true);
            }
        });
    }

    private void showCommentSection(boolean show) {
        commentSection.setVisible(show);
        commentTextField.clear();
    }

    @FXML
    private void handleAddComment(ActionEvent event) {
        if (selectedBlog == null || commentTextField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un blog et entrer un commentaire.", Alert.AlertType.WARNING);
            return;
        }

        try {
            String commentText = commentTextField.getText();

            Commentaire newComment = new Commentaire(
                    selectedBlog.getId(),
                    0,
                    "Utilisateur par défaut",
                    java.time.LocalDate.now().toString(),
                    commentText
            );

            int id = commentService.add(newComment);

            if (id != -1) {
                showAlert("Succès", "Commentaire ajouté avec succès!", Alert.AlertType.INFORMATION);
                refreshBlogList();
            } else {
                showAlert("Erreur", "Erreur lors de l'ajout du commentaire.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout du commentaire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showBlogDetailsDialog(Blog blog) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails du blog");
        dialog.setHeaderText(blog.getTitre());

        VBox content = new VBox();
        content.setPadding(new Insets(10));
        content.setSpacing(5);
        content.getChildren().add(new Label("Auteur: " + blog.getAuteur()));
        content.getChildren().add(new Label("Publié le: " + blog.getDate_publication()));
        content.getChildren().add(new Label("Contenu: " + blog.getContenu()));

        List<Commentaire> comments = commentService.getByBlogId(blog.getId());
        if (comments.isEmpty()) {
            content.getChildren().add(new Label("Aucun commentaire pour ce blog."));
        } else {
            for (Commentaire comment : comments) {
                content.getChildren().add(new Label("🗨️ " + comment.getNom_utilisateur() + " (" + comment.getDate() + "): " + comment.getContenu()));
            }
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshBlogList() {
        try {
            List<Blog> blogs = blogService.getAll();
            filteredList = blogs;
            setupPagination();

            if (blogs.isEmpty()) {
                System.out.println("Aucun blog trouvé dans la base de données");
            } else {
                System.out.println(blogs.size() + " blogs chargés avec succès");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des blogs:");
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les blogs", Alert.AlertType.ERROR);
        }
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) filteredList.size() / itemsPerPage);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredList.size());

        List<Blog> pageItems = filteredList.subList(fromIndex, toIndex);
        ObservableList<Blog> observablePageItems = FXCollections.observableArrayList(pageItems);
        blogListView.setItems(observablePageItems);

        return new VBox(blogListView);
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Blog selectedBlog = blogListView.getSelectionModel().getSelectedItem();
        if (selectedBlog == null) {
            showAlert("Erreur", "Aucun blog sélectionné", Alert.AlertType.WARNING);
            return;
        }

        try {
            MainFx.showEditBlogView(selectedBlog); // Vérifie que cette méthode existe bien
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de l'éditeur: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Blog selectedBlog = blogListView.getSelectionModel().getSelectedItem();

        if (selectedBlog == null) {
            showAlert("Erreur", "Aucun blog sélectionné", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Suppression du blog");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le blog: " + selectedBlog.getTitre() + " ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = blogService.delete(selectedBlog);

                if (success) {
                    showAlert("Succès", "Blog supprimé avec succès", Alert.AlertType.INFORMATION);
                    refreshBlogList();
                } else {
                    showAlert("Erreur", "La suppression a échoué", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            MainFx.showAddBlogView(); // Vérifie que cette méthode existe bien
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de l'éditeur: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
