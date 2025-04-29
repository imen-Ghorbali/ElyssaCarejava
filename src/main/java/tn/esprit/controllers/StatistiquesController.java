package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.events;
import tn.esprit.services.ServiceEvent;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap; // Added import
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StatistiquesController implements Initializable {

    @FXML
    private BarChart<String, Number> sponsorChart;
    @FXML
    private Label totalEventsLabel;
    @FXML
    private Label activeSponsorsLabel;
    @FXML
    private VBox topSponsorsContainer;
    @FXML
    private Label lastUpdateLabel;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureChart();
        loadStats();
    }

    private void configureChart() {
        sponsorChart.setLegendVisible(false);
        sponsorChart.setAnimated(false);
        sponsorChart.getXAxis().setLabel("Sponsors");
        sponsorChart.getYAxis().setLabel("Nombre d'événements");
    }

    private void loadStats() {
        List<events> eventList = serviceEvent.getAll();
        updateStatistics(eventList);
        updateLastUpdateTime();
    }

    private void updateStatistics(List<events> events) {
        // Statistiques de base
        totalEventsLabel.setText(String.valueOf(events.size()));

        long uniqueSponsors = events.stream()
                .filter(e -> e.getSponsor() != null)
                .map(e -> e.getSponsor().getId())
                .distinct()
                .count();
        activeSponsorsLabel.setText(String.valueOf(uniqueSponsors));

        // Graphique des sponsors
        updateSponsorChart(events);

        // Top sponsors
        displayTopSponsors(events);
    }

    private void updateSponsorChart(List<events> events) {
        Map<String, Long> sponsorStats = events.stream()
                .filter(e -> e.getSponsor() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getSponsor().getName(),
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Événements par Sponsor");

        sponsorStats.forEach((sponsorName, count) ->
                series.getData().add(new XYChart.Data<>(sponsorName, count))
        );

        sponsorChart.getData().clear();
        sponsorChart.getData().add(series);

        // Style des barres
        if (sponsorChart.getData() != null) {
            sponsorChart.getData().forEach(s -> {
                if (s.getData() != null) {
                    s.getData().forEach(data -> {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-bar-fill: #3498db;");
                        }
                    });
                }
            });
        }
    }

    private void displayTopSponsors(List<events> events) {
        Map<String, Long> topSponsors = events.stream()
                .filter(e -> e.getSponsor() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getSponsor().getName(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        topSponsorsContainer.getChildren().clear();

        topSponsors.forEach((sponsor, count) -> {
            HBox sponsorBox = new HBox(10);
            Label rankLabel = new Label((topSponsorsContainer.getChildren().size() + 1) + ".");
            rankLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db;");

            Label sponsorLabel = new Label(String.format("%s: %d événements", sponsor, count));
            sponsorLabel.setStyle("-fx-font-size: 14px;");

            sponsorBox.getChildren().addAll(rankLabel, sponsorLabel);
            topSponsorsContainer.getChildren().add(sponsorBox);
        });
    }

    private void updateLastUpdateTime() {
        lastUpdateLabel.setText("Dernière mise à jour: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
    @FXML
    private void goToAfficherEvents(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherevents.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshStats() {
        loadStats();
    }
}