package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.events;
import tn.esprit.services.ServiceEvent; // Assurez-vous d'avoir un service pour récupérer les événements

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ResourceBundle;

public class CalendarController implements Initializable {

    private YearMonth currentYearMonth;

    @FXML
    private Label monthLabel;

    @FXML
    private GridPane calendarGrid;

    private ServiceEvent eventService;  // Service pour récupérer les événements

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser le mois courant
        currentYearMonth = YearMonth.now();

        // Initialiser le service pour récupérer les événements
        eventService = new ServiceEvent(); // Vous devrez l'adapter selon votre code

        // Afficher le calendrier
        updateCalendar();
    }

    private void updateCalendar() {
        // Mettre à jour l'affichage du calendrier
        generateCalendar(currentYearMonth.atDay(1));
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
    }

    private void generateCalendar(LocalDate date) {
        // Vider le calendrier existant
        calendarGrid.getChildren().clear();

        // Obtenir le premier jour du mois et la longueur du mois
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate firstDay = yearMonth.atDay(1);
        int lengthOfMonth = yearMonth.lengthOfMonth();

        // Calculer quel jour de la semaine commence le mois (1 = Lundi, 7 = Dimanche)
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue(); // 1 = Lundi, 7 = Dimanche

        int row = 0;
        int col = firstDayOfWeek - 1;

        // Récupérer les événements du mois courant depuis le service
        List<events> eventList = eventService.getEventsForMonth(currentYearMonth);

        // Parcourir chaque jour du mois
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate currentDate = yearMonth.atDay(day);

            // Créer une cellule pour chaque jour
            VBox dayCell = new VBox();
            dayCell.setPrefSize(100, 80);
            dayCell.setStyle("-fx-border-color: lightgray; -fx-padding: 5;");

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold;");
            dayCell.getChildren().add(dayLabel);

            // Ajouter les événements du jour
            boolean hasEvent = false;
            for (events e : eventList) {
                if (e.getDate().toLocalDate().equals(currentDate)) {
                    Label eventLabel = new Label(e.getTitle());
                    eventLabel.setStyle("-fx-font-size: 11; -fx-text-fill: darkblue;");
                    dayCell.getChildren().add(eventLabel);
                    hasEvent = true;
                }
            }

            // Mettre en surbrillance les jours avec événements
            if (hasEvent) {
                dayCell.setStyle(dayCell.getStyle() + "-fx-background-color: #e0f7fa;");
            }

            // Ajouter un événement au clic sur un jour
            dayCell.setOnMouseClicked(event -> {
                System.out.println("Événements pour le " + currentDate + " :");
                for (events e : eventList) {
                    if (e.getDate().toLocalDate().equals(currentDate)) {
                        System.out.println("• " + e.getTitle());
                    }
                }
            });

            // Ajouter la cellule du jour à la grille
            calendarGrid.add(dayCell, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    // Méthodes pour gérer les boutons "précédent" et "suivant"
    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }
    @FXML
    private void goToAfficherEvents(ActionEvent event) {
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
}
