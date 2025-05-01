package tn.esprit.controllers;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import tn.esprit.mainFX;
import tn.esprit.models.Blog;
import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceBlog;
import tn.esprit.services.ServiceCommentaire;
import javafx.animation.RotateTransition;
import javafx.stage.StageStyle;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.ScaleTransition;
import javafx.animation.FillTransition;
import javafx.scene.paint.Color;
import java.io.FileNotFoundException;
import javafx.animation.ParallelTransition;//
import tn.esprit.services.PdfService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.awt.Desktop;




import javafx.scene.control.Button;
import javafx.stage.FileChooser;



public class ListBlogController implements Initializable {

    @FXML private ListView<Blog> blogListView;
    @FXML private Pagination pagination;
    @FXML private HBox commentSection;
    @FXML private TextField commentTextField;
    @FXML private Button viewCommentsButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button addButton;
    @FXML private TextField searchField;
    @FXML private Button exportPdfButton;


    private final ServiceBlog blogService = new ServiceBlog();
    private final ServiceCommentaire commentService = new ServiceCommentaire();
    private List<Blog> filteredList;
    private final int itemsPerPage = 5;
    private Blog selectedBlog;

    // Styles pour les cartes (version médicale)
    private static final String CARD_NORMAL_STYLE =
            "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #a7c7ff; -fx-border-width: 1; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);";

    private static final String CARD_HOVER_STYLE =
            "-fx-background-color: #f0f8ff; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #5dade2; -fx-border-width: 1; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);";

    private static final String CARD_SELECTED_STYLE =
            "-fx-background-color: #e3f2fd; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #4169E1; -fx-border-width: 1.5; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);";

    private void setupButtonAnimations() {
        // Configuration des animations de base
        applyButtonAnimation(viewCommentsButton, "#6c63ff", "#5a52cc");
        applyButtonAnimation(editButton, "#ffce00", "#e6b800");
        applyButtonAnimation(deleteButton, "#e74c3c", "#c0392b");
        applyButtonAnimation(addButton, "#2ecc71", "#27ae60");
        applyButtonAnimation(refreshButton, "#9b59b6", "#8e44ad");

        // Ajoutez l'animation de rotation à tous les boutons
        applyRotationAnimation(viewCommentsButton);
        applyRotationAnimation(editButton);
        applyRotationAnimation(deleteButton);
        applyRotationAnimation(addButton);
        applyRotationAnimation(refreshButton);
    }
    private void applyRotationAnimation(Button button) {
        button.setOnAction(event -> {
            // Créez une animation de rotation
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), button);
            rotateTransition.setByAngle(360); // Rotation complète de 360 degrés
            rotateTransition.setCycleCount(1); // Une seule rotation
            rotateTransition.setAutoReverse(false);

            // Créez une animation de scale (optionnelle)
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);

            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(2);

            // Exécutez les animations en parallèle
            ParallelTransition parallelTransition = new ParallelTransition(
                    rotateTransition,
                    scaleTransition
            );

            // Jouez l'animation
            parallelTransition.play();

            // Appelez la méthode originale si nécessaire
            if (button == viewCommentsButton) {
                handleViewComments(event);
            } else if (button == editButton) {
                handleEdit(event);
            } else if (button == deleteButton) {
                handleDelete(event);
            } else if (button == addButton) {
                handleAdd(event);
            } else if (button == refreshButton) {
                refreshBlogList();
            }
        });
    }

    private void applyButtonAnimation(Button button, String baseColor, String hoverColor) {
        // Style de base
        button.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        // Animation de survol
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.05);
            st.setToY(1.05);

            FillTransition ft = new FillTransition();
            ft.setDuration(Duration.millis(100));
            ft.setToValue(Color.web(hoverColor));

            ParallelTransition pt = new ParallelTransition(st);
            pt.play();

            button.setStyle(button.getStyle() + "-fx-background-color: " + hoverColor + ";");
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            button.setStyle(button.getStyle().replace(hoverColor, baseColor));
        });

        // Animation de clic
        button.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(50), button);
            st.setToX(0.95);
            st.setToY(0.95);
            st.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(50), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
    }
    private void applyButtonEffects(Button button, String baseColor, String hoverColor, Runnable action) {
        // Style de base
        button.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        // Animation de survol
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
            button.setStyle(button.getStyle().replace(baseColor, hoverColor));
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            button.setStyle(button.getStyle().replace(hoverColor, baseColor));
        });

        // Animation de clic avec rotation
        button.setOnAction(e -> {
            // Animation de rotation
            RotateTransition rt = new RotateTransition(Duration.millis(300), button);
            rt.setByAngle(360);
            rt.setCycleCount(1);

            // Animation de pulse
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.1);
            st.setToY(1.1);
            st.setAutoReverse(true);
            st.setCycleCount(2);

            // Exécution en parallèle
            ParallelTransition pt = new ParallelTransition(rt, st);
            pt.setOnFinished(event -> action.run());
            pt.play();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupStyles();
        setupButtonAnimations();
        setupListView();
        setupListeners();
        refreshBlogList();
    }
    private void setupExportButton() {
        exportPdfButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        applyButtonAnimation(exportPdfButton, "#e74c3c", "#c0392b");

        exportPdfButton.setOnAction(this::handleExportPdf);
    }

    @FXML
    private void handleExportPdf(ActionEvent event) {
        if (selectedBlog == null) {
            showAlert("Erreur", "Veuillez sélectionner un blog", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("blog_" + selectedBlog.getId() + ".pdf");

        File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file != null) {
            try {
                // Vérification de l'extension
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    file = new File(file.getAbsolutePath() + ".pdf");
                }

                PdfService.generateBlogPdf(selectedBlog, file.getAbsolutePath());

                // Vérification que le fichier a bien été créé
                if (!file.exists() || file.length() == 0) {
                    throw new IOException("Le fichier PDF n'a pas été généré correctement");
                }

                openPdfFile(file);

                showAlert("Succès", "PDF généré avec succès!", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                showAlert("Erreur Critique",
                        "Échec de génération du PDF:\n" + e.getMessage(),
                        Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void openPdfFile(File file) {
        try {
            if (!Desktop.isDesktopSupported()) {
                throw new UnsupportedOperationException("Desktop non supporté");
            }

            Desktop desktop = Desktop.getDesktop();
            if (!file.exists()) {
                throw new FileNotFoundException("Fichier PDF introuvable");
            }

            desktop.open(file);

        } catch (Exception e) {
            // Solution alternative
            try {
                String os = System.getProperty("os.name").toLowerCase();
                Runtime rt = Runtime.getRuntime();

                if (os.contains("win")) {
                    rt.exec(new String[] {"cmd", "/c", "start", file.getAbsolutePath()});
                } else if (os.contains("mac")) {
                    rt.exec(new String[] {"open", file.getAbsolutePath()});
                } else if (os.contains("nix") || os.contains("nux")) {
                    rt.exec(new String[] {"xdg-open", file.getAbsolutePath()});
                }
            } catch (Exception ex) {
                System.err.println("Échec d'ouverture du PDF: " + ex.getMessage());
            }
        }
    }
    @FXML
    private void showStats(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stats.fxml"));
            Parent root = loader.load();

            // Configuration de la scène avec effet de transition
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);

            // Animation d'ouverture
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), root);
            scaleIn.setFromX(0.9);
            scaleIn.setFromY(0.9);
            scaleIn.setToX(1);
            scaleIn.setToY(1);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ParallelTransition openAnim = new ParallelTransition(scaleIn, fadeIn);
            openAnim.setOnFinished(e -> {
                stage.initStyle(StageStyle.DECORATED);
                scene.setFill(Color.WHITE);
            });

            stage.show();
            openAnim.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Ajoutez cette déclaration avec les autres @FXML
    @FXML private Button refreshButton;

    // Ajoutez cette méthode avec les autres méthodes handle
    @FXML
    private void handleRefresh(ActionEvent event) {
        // Animation spéciale pour le bouton refresh
        RotateTransition rt = new RotateTransition(Duration.millis(500), refreshButton);
        rt.setByAngle(360);

        ScaleTransition st = new ScaleTransition(Duration.millis(200), refreshButton);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);

        ParallelTransition pt = new ParallelTransition(rt, st);
        pt.setOnFinished(e -> refreshBlogList());
        pt.play();
    }
    private void setupStyles() {
        // Supprimez les styles des boutons car ils sont maintenant gérés par setupButtonAnimations()
        commentTextField.setStyle("-fx-padding: 8; -fx-font-size: 14px; -fx-background-color: #fdf5fc; " +
                "-fx-border-radius: 15; -fx-background-radius: 15;");
        commentSection.setStyle("-fx-background-color: #f8e1f4; -fx-padding: 10; -fx-background-radius: 5;");
        blogListView.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    // Ajoutez cette méthode pour gérer les effets de survol
    private void setupButtonHoverEffect(Button button, String hoverColor) {
        String originalStyle = button.getStyle();

        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle + "-fx-background-color: " + hoverColor + ";");
        });

        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });
    }

    private void setupListView() {
        blogListView.setCellFactory(param -> new ListCell<Blog>() {
            private final ImageView imageView = new ImageView();
            private final Label titleLabel = new Label();
            private final Label authorLabel = new Label();
            private final Label dateLabel = new Label();
            private final VBox textContainer = new VBox(5, titleLabel, authorLabel, dateLabel);
            private final HBox contentBox = new HBox(10, imageView, textContainer);
            private final StackPane card = new StackPane();


            {
                // Configuration initiale de la carte
                card.setStyle(CARD_NORMAL_STYLE);
                card.setPadding(new Insets(15));

                // Configuration de l'image avec cadre pastel
                imageView.setFitWidth(120);
                imageView.setFitHeight(90);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-border-radius: 5; -fx-border-color: #f0b6e1; -fx-border-width: 2;");

                // Configuration des labels avec couleurs harmonieuses
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #5a3a51;");
                authorLabel.setStyle("-fx-text-fill: #8a5a7a; -fx-font-size: 13px;");
                dateLabel.setStyle("-fx-text-fill: #8a5a7a; -fx-font-size: 13px;");

                // Configuration du contenu
                textContainer.setPadding(new Insets(5));
                contentBox.setAlignment(Pos.CENTER_LEFT);

                // Effet de survol amélioré
                card.setOnMouseEntered(e -> {
                    if (!isSelected()) {
                        card.setStyle(CARD_HOVER_STYLE);
                        imageView.setStyle("-fx-border-radius: 5; -fx-border-color: #e893d4; -fx-border-width: 2;");
                    }
                });

                card.setOnMouseExited(e -> {
                    if (!isSelected()) {
                        card.setStyle(CARD_NORMAL_STYLE);
                        imageView.setStyle("-fx-border-radius: 5; -fx-border-color: #f0b6e1; -fx-border-width: 2;");
                    }
                });

                // Ajout du contenu à la carte
                card.getChildren().add(contentBox);
                this.setPadding(new Insets(0, 0, 10, 0));
            }

            @Override
            protected void updateItem(Blog blog, boolean empty) {
                super.updateItem(blog, empty);
                if (empty || blog == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(blog.getTitre());
                    authorLabel.setText("Par " + blog.getAuteur());
                    dateLabel.setText("Publié le: " + blog.getDate_publication());
                    loadBlogImage(blog);
                    setGraphic(card);

                    // Mise à jour du style selon la sélection
                    if (isSelected()) {
                        card.setStyle(CARD_SELECTED_STYLE);
                        imageView.setStyle("-fx-border-radius: 5; -fx-border-color: #df70c7; -fx-border-width: 2;");
                    } else {
                        card.setStyle(CARD_NORMAL_STYLE);
                        imageView.setStyle("-fx-border-radius: 5; -fx-border-color: #f0b6e1; -fx-border-width: 2;");
                    }
                }
            }

            private void loadBlogImage(Blog blog) {
                try {
                    String imagePath = blog.getImage();
                    if (imagePath != null && !imagePath.trim().isEmpty()) {
                        if (imagePath.startsWith("file:")) {
                            imageView.setImage(new Image(imagePath));
                        }
                        else if (imagePath.startsWith("images/")) {
                            // Solution plus robuste pour les ressources
                            try {
                                // Essayer directement comme ressource
                                String resourcePath = "/" + imagePath;
                                if (getClass().getResource(resourcePath) != null) {
                                    imageView.setImage(new Image(getClass().getResourceAsStream(resourcePath)));
                                }
                                // Essayer comme fichier dans le système
                                else {
                                    File file = new File("src/main/resources/" + imagePath);
                                    if (file.exists()) {
                                        imageView.setImage(new Image(file.toURI().toString()));
                                    } else {
                                        loadDefaultImage();
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Erreur de chargement (ressource): " + e.getMessage());
                                loadDefaultImage();
                            }
                        }
                        else {
                            // Pour les chemins absolus
                            File file = new File(imagePath);
                            if (file.exists()) {
                                imageView.setImage(new Image(file.toURI().toString()));
                            } else {
                                loadDefaultImage();
                            }
                        }
                    } else {
                        loadDefaultImage();
                    }
                } catch (Exception e) {
                    System.err.println("Erreur générale de chargement: " + e.getMessage());
                    loadDefaultImage();
                }
            }
            private void loadDefaultImage() {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/blog-placeholder.png")));
            }
        });
    }

    private void setupListeners() {
        blogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedBlog = newVal;
            blogListView.refresh(); // Rafraîchir pour mettre à jour les styles
        });

        blogListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedBlog != null) {
                showBlogDetailsDialog(selectedBlog);
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBlogList(newVal));
    }

    private void showBlogDetailsDialog(Blog blog) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("📄 Dossier Médical");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setMinSize(720, 650);
        dialog.getDialogPane().setPrefSize(720, 650);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #f5f9ff; " +
                        "-fx-border-color: #5dade2; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );

        // Image du blog
        ImageView blogImageView = new ImageView();
        blogImageView.setFitWidth(650);
        blogImageView.setFitHeight(300);
        blogImageView.setPreserveRatio(true);
        blogImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0.3, 0, 4); " +
                "-fx-background-radius: 12; -fx-border-color: #a7c7ff; -fx-border-width: 2;");

        try {
            String imagePath = blog.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("images/")) {
                    blogImageView.setImage(new Image(getClass().getResourceAsStream("/" + imagePath)));
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        blogImageView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        blogImageView.setImage(new Image(getClass().getResourceAsStream("/images/blog-placeholder.png")));
                    }
                }
            } else {
                blogImageView.setImage(new Image(getClass().getResourceAsStream("/images/blog-placeholder.png")));
            }
        } catch (Exception e) {
            blogImageView.setImage(new Image(getClass().getResourceAsStream("/images/blog-placeholder.png")));
        }

        // Contenu principal
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = new Label(blog.getTitre());
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #4a148c;");

        HBox infoBox = new HBox(20);
        infoBox.setStyle("-fx-text-fill: #555;");
        Label authorLabel = new Label("\u270D\uFE0F Auteur: " + blog.getAuteur());
        Label dateLabel = new Label("\uD83D\uDCC5 Date: " + blog.getDate_publication());
        authorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        infoBox.getChildren().addAll(authorLabel, dateLabel);

        TextArea contentArea = new TextArea(blog.getContenu());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setStyle("-fx-font-size: 15px; -fx-background-color: #f9f9f9; -fx-border-color: transparent; -fx-padding: 10;");

        // Section commentaires avec mise à jour
        VBox commentsBox = new VBox(12);
        commentsBox.setPadding(new Insets(20, 0, 0, 0));
        commentsBox.setStyle("-fx-background-color: #f3e5f5; -fx-background-radius: 12; -fx-padding: 15;");

        // Classe interne pour gérer les mises à jour des commentaires
        class CommentsUpdater {
            void updateComments() {
                commentsBox.getChildren().clear();

                List<Commentaire> comments = commentService.getByBlogId(blog.getId());
                if (!comments.isEmpty()) {
                    Label commentsTitle = new Label("\uD83D\uDCAC Commentaires (" + comments.size() + ")");
                    commentsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #6a1b9a;");
                    commentsBox.getChildren().add(commentsTitle);

                    int index = 0;
                    for (Commentaire comment : comments) {
                        VBox commentCard = new VBox(6);
                        commentCard.setPadding(new Insets(10));
                        commentCard.setStyle(
                                "-fx-background-color: " + (index % 2 == 0 ? "#e1bee7;" : "#f3e5f5;") +
                                        "-fx-background-radius: 10;");

                        Label info = new Label("\uD83D\uDC64 " + comment.getNom_utilisateur()
                                + " | 📅 " + comment.getDate()
                                + " | ❤️ " + comment.getNombre_like());
                        info.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

                        Label commentText = new Label(comment.getContenu());
                        commentText.setWrapText(true);
                        commentText.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                        Button likeButton = new Button("\u2764️ " + comment.getNombre_like());
                        boolean hasLiked = commentService.userHasLiked(comment.getId());
                        likeButton.setDisable(hasLiked);
                        likeButton.setStyle(hasLiked ?
                                "-fx-background-color: #cccccc;" :
                                "-fx-background-color: #ec407a; -fx-text-fill: white;");

                        likeButton.setOnAction(e -> {
                            if (commentService.incrementLike(comment.getId())) {
                                comment.incrementLikes();
                                likeButton.setText("\u2764️ " + comment.getNombre_like());
                                likeButton.setDisable(true);
                                likeButton.setStyle("-fx-background-color: #cccccc;");
                            }
                        });

                        commentCard.getChildren().addAll(info, commentText, likeButton);
                        commentsBox.getChildren().add(commentCard);
                        index++;
                    }
                } else {
                    Label noComment = new Label("Aucun commentaire pour ce blog.");
                    noComment.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
                    commentsBox.getChildren().add(noComment);
                }
            }
        }

        // Initialisation et première mise à jour
        CommentsUpdater updater = new CommentsUpdater();
        updater.updateComments();

        // Assemblage du contenu
        content.getChildren().addAll(
                blogImageView,
                titleLabel,
                infoBox,
                new Separator(),
                contentArea,
                new Separator(),
                commentsBox
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }


    private void filterBlogList(String query) {
        if (query == null || query.isEmpty()) {
            filteredList = blogService.getAll();
        } else {
            filteredList = blogService.getAll().stream()
                    .filter(b -> b.getTitre().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        setupPagination();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showCommentSection(boolean show) {
        commentSection.setVisible(show);
        if (!show) commentTextField.clear();
    }

    @FXML
    private void handleViewComments(ActionEvent event) {
        // Votre logique existante
        if (selectedBlog != null) {
            mainFX.showListCommentaireView(selectedBlog.getId());
        } else {
            showAlert("Erreur", "Veuillez sélectionner un blog", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleAddComment(ActionEvent event) {
        if (selectedBlog == null || commentTextField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un blog et entrer un commentaire.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Commentaire newComment = new Commentaire(selectedBlog.getId(), 0, "Utilisateur", java.time.LocalDate.now().toString(), commentTextField.getText());
            if (commentService.add(newComment) != -1) {
                showAlert("Succès", "Commentaire ajouté!", Alert.AlertType.INFORMATION);
                commentTextField.clear();
                refreshBlogList();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (selectedBlog == null) {
            showAlert("Erreur", "Aucun blog sélectionné", Alert.AlertType.WARNING);
            return;
        }
        mainFX.showEditBlogView(selectedBlog);
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedBlog == null) {
            showAlert("Erreur", "Aucun blog sélectionné", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le blog");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer \"" + selectedBlog.getTitre() + "\" ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (blogService.delete(selectedBlog)) {
                showAlert("Succès", "Blog supprimé", Alert.AlertType.INFORMATION);
                refreshBlogList();
            }
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        mainFX.showAddBlogView();
    }
    public void refreshBlogList() {
        try {
            // Animation de rotation du bouton
            RotateTransition rt = new RotateTransition(Duration.millis(500), refreshButton);
            rt.setByAngle(360);
            rt.play();

            filteredList = blogService.getAll();
            setupPagination();

            // Petite animation de fade pour la liste
            FadeTransition ft = new FadeTransition(Duration.millis(300), blogListView);
            ft.setFromValue(0.5);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur de chargement: " + e.getMessage(), Alert.AlertType.ERROR);
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
        ObservableList<Blog> pageItems = FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));
        blogListView.setItems(pageItems);
        return new VBox(blogListView);
    }
}