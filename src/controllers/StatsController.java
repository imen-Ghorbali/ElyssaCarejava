package tn.esprit.controllers;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.shape.Arc;
import javafx.util.Duration;
import tn.esprit.models.Blog;
import tn.esprit.services.ServiceBlog;
import tn.esprit.services.ServiceCommentaire;

import java.util.Comparator;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;


public class StatsController {

    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private Label topBlogLabel;
    @FXML private Label topBlogCount;
    @FXML private Label recentActivityLabel;
    @FXML private ProgressIndicator activityProgress;
    @FXML private Label engagementRateLabel;
    @FXML private Arc engagementArc;

    private final ServiceBlog blogService = new ServiceBlog();
    private final ServiceCommentaire commentService = new ServiceCommentaire();

    @FXML
    public void initialize() {
        setupCharts();
        loadStats();
    }

    private void setupCharts() {
        pieChart.setStyle("-fx-font-size: 12px;");
        barChart.setStyle("-fx-font-size: 12px;");
        xAxis.setLabel("Blogs");
        yAxis.setLabel("Nombre de commentaires");
    }

    private void loadStats() {
        fadeOutCharts(() -> {
            Task<ObservableList<Blog>> loadTask = new Task<>() {
                @Override
                protected ObservableList<Blog> call() {
                    return FXCollections.observableArrayList(blogService.getAll());
                }
            };

            loadTask.setOnSucceeded(e -> {
                ObservableList<Blog> blogs = loadTask.getValue();
                blogs.sort(Comparator.comparingInt(b -> -commentService.getByBlogId(b.getId()).size()));
                applyDataWithAnimations(blogs);
                updateIndicators(blogs);
            });

            new Thread(loadTask).start();
        });
    }

    private void fadeOutCharts(Runnable onFinished) {
        ParallelTransition fadeOut = new ParallelTransition();

        FadeTransition fadePie = new FadeTransition(Duration.millis(300), pieChart);
        fadePie.setFromValue(1.0);
        fadePie.setToValue(0.2);

        FadeTransition fadeBar = new FadeTransition(Duration.millis(300), barChart);
        fadeBar.setFromValue(1.0);
        fadeBar.setToValue(0.2);

        fadeOut.getChildren().addAll(fadePie, fadeBar);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    private void applyDataWithAnimations(ObservableList<Blog> blogs) {
        // PieChart
        PieChart.Data[] pieData = blogs.stream()
                .limit(5)
                .map(blog -> new PieChart.Data(
                        String.format("%s (%d)", blog.getTitre(),
                                commentService.getByBlogId(blog.getId()).size()),
                        commentService.getByBlogId(blog.getId()).size()))
                .toArray(PieChart.Data[]::new);

        animatePieChart(pieData);

        // BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commentaires");
        blogs.stream()
                .limit(5)
                .forEach(blog -> series.getData().add(
                        new XYChart.Data<>(blog.getTitre(),
                                commentService.getByBlogId(blog.getId()).size())));

        animateBarChart(series);
    }

    private void animatePieChart(PieChart.Data[] data) {
        pieChart.getData().clear();

        SequentialTransition seqTransition = new SequentialTransition();

        for (PieChart.Data d : data) {
            pieChart.getData().add(d);

            ScaleTransition st = new ScaleTransition(Duration.millis(200), d.getNode());
            st.setFromX(0);
            st.setToX(1);
            st.setFromY(0);
            st.setToY(1);

            seqTransition.getChildren().add(st);
        }

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), pieChart);
        fadeIn.setToValue(1.0);

        new ParallelTransition(seqTransition, fadeIn).play();
    }

    private void animateBarChart(XYChart.Series<String, Number> series) {
        barChart.getData().clear();
        barChart.getData().add(series);

        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            node.setOpacity(0);

            ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
            st.setFromY(0);
            st.setToY(1);

            FadeTransition ft = new FadeTransition(Duration.millis(300), node);
            ft.setFromValue(0);
            ft.setToValue(1);

            ParallelTransition pt = new ParallelTransition(st, ft);
            pt.setDelay(Duration.millis(100 * series.getData().indexOf(data)));
            pt.play();
        }

        new ParallelTransition(
                new ScaleTransition(Duration.millis(500), xAxis),
                new ScaleTransition(Duration.millis(500), yAxis)
        ).play();
    }

    private void updateIndicators(ObservableList<Blog> blogs) {
        // 1. Blog le plus populaire
        if (!blogs.isEmpty()) {
            Blog topBlog = blogs.get(0);
            int commentCount = commentService.getByBlogId(topBlog.getId()).size();

            topBlogLabel.setText(topBlog.getTitre());
            topBlogCount.setText(commentCount + " commentaires");

            // Animation du compteur
            animateCounter(topBlogCount, commentCount);
        }

        // 2. Activité récente
        int recentComments = commentService.getRecentCommentCount(1); // 1 heure
        recentActivityLabel.setText(recentComments + " nouveaux commentaires");

        // Animation de la jauge
        Timeline progressAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(activityProgress.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(activityProgress.progressProperty(),
                        Math.min(recentComments / 10.0, 1.0)))
        );
        progressAnim.play();

        // 3. Taux d'engagement
        double engagementRate = calculateEngagementRate(blogs);
        engagementRateLabel.setText(String.format("%.1f%%", engagementRate * 100));

        // Animation de l'arc
        Timeline arcAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(engagementArc.lengthProperty(), 0)),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(engagementArc.lengthProperty(), -engagementRate * 360))
        );
        arcAnim.play();
    }

    private void animateCounter(Label label, int targetValue) {
        Timeline timeline = new Timeline();
        final IntegerProperty counter = new SimpleIntegerProperty(0);

        label.textProperty().bind(counter.asString().concat(" commentaires"));

        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(counter, targetValue, Interpolator.EASE_OUT))
        );
        timeline.play();
    }

    private double calculateEngagementRate(ObservableList<Blog> blogs) {
        int totalComments = blogs.stream()
                .mapToInt(b -> commentService.getByBlogId(b.getId()).size())
                .sum();

        int totalBlogs = blogs.size();

        return totalBlogs > 0 ? (double) totalComments / totalBlogs / 10 : 0;
    }


    @FXML
    private void handleRefresh() {
        loadStats();
    }
}
