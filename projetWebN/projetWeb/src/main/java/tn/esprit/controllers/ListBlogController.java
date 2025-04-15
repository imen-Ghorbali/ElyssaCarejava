package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.MainFx;
import tn.esprit.models.Blog;
import tn.esprit.services.ServiceBlog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

import java.util.Optional;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class ListBlogController implements Initializable {

    @FXML
    private ListView<Blog> blogListView;
    @FXML
    private Pagination pagination;

    private final ServiceBlog blogService = new ServiceBlog();

    private List<Blog> filteredList;
    private final int itemsPerPage = 5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        blogListView.setCellFactory(param -> new BlogCell());
        refreshBlogList();
        setupSelectionListener();
    }

    private void setupSelectionListener() {
        blogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showBlogDetailsDialog(newVal);
            }
        });
    }

    private void showBlogDetailsDialog(Blog blog) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails du blog");
        dialog.setHeaderText(blog.getTitre());

        ImageView imageView = new ImageView();
        try {
            if (blog.getImage() != null && !blog.getImage().isEmpty()) {
                Image image = new Image("file:" + blog.getImage());
                imageView.setImage(image);
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-blog.png")));
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement d'image: " + e.getMessage());
            imageView.setImage(null);
        }
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);

        Label auteurLabel = new Label("Auteur: " + blog.getAuteur());
        Label dateLabel = new Label("Date: " + blog.getDate_publication());
        Label contenuLabel = new Label("Contenu: " + blog.getContenu());
        contenuLabel.setWrapText(true);
        contenuLabel.setMaxWidth(300);

        VBox content = new VBox(10,
                new HBox(20, imageView, new VBox(5, auteurLabel, dateLabel)),
                contenuLabel);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().setPrefSize(400, 300);

        dialog.showAndWait();
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
            MainFx.showAddBlogView();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de l'éditeur: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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
            MainFx.showEditBlogView(selectedBlog);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de l'éditeur: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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

    @FXML
    private void handleAddCommentaire(ActionEvent event) {
        Blog selectedBlog = blogListView.getSelectionModel().getSelectedItem();

        if (selectedBlog == null) {
            showAlert("Erreur", "Veuillez sélectionner un blog avant de commenter", Alert.AlertType.WARNING);
            return;
        }

        try {
            MainFx.showAddCommentaireView(selectedBlog.getId());
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur de commentaires", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
