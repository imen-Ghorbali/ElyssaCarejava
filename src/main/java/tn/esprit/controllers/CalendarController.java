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
import tn.esprit.services.ServiceEvent;

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

    private ServiceEvent eventService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentYearMonth = YearMonth.now();
        eventService = new ServiceEvent();
        updateCalendar();
    }

    private void updateCalendar() {
        generateCalendar(currentYearMonth.atDay(1));
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
    }

    private void generateCalendar(LocalDate date) {
        calendarGrid.getChildren().clear();

        YearMonth yearMonth = YearMonth.from(date);
        LocalDate firstDay = yearMonth.atDay(1);
        int lengthOfMonth = yearMonth.lengthOfMonth();

        int firstDayOfWeek = firstDay.getDayOfWeek().getValue(); // 1 = Lundi, 7 = Dimanche

        int row = 0;
        int col = firstDayOfWeek - 1;

        List<events> eventList = eventService.getEventsForMonth(currentYearMonth);
        LocalDate today = LocalDate.now();

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate currentDate = yearMonth.atDay(day);

            VBox dayCell = new VBox();
            dayCell.setPrefSize(100, 80);
            dayCell.setStyle("-fx-border-color: lightgray; -fx-padding: 5;");

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold;");
            dayCell.getChildren().add(dayLabel);

            boolean hasEvent = false;
            for (events e : eventList) {
                if (e.getDate().toLocalDate().equals(currentDate)) {
                    Label eventLabel = new Label(e.getTitle());
                    eventLabel.setStyle("-fx-font-size: 11; -fx-text-fill: darkblue;");
                    dayCell.getChildren().add(eventLabel);
                    hasEvent = true;
                }
            }

            if (hasEvent) {
                dayCell.setStyle(dayCell.getStyle() + "-fx-background-color: #e0f7fa;");
            }

            // 💡 Bloquer les dates passées
            if (!currentDate.isBefore(today)) {
                dayCell.setOnMouseClicked(event -> openAddEventForm(currentDate));
            } else {
                // Désactiver visuellement les dates passées
                dayCell.setStyle(dayCell.getStyle() + "-fx-opacity: 0.4;");
            }

            calendarGrid.add(dayCell, col, row);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private void openAddEventForm(LocalDate selectedDate) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterevents.fxml"));
            Parent root = loader.load();

            AjouterEventsController controller = loader.getController();
            controller.setSelectedDate(selectedDate);

            Stage stage = (Stage) calendarGrid.getScene().getWindow();
            stage.setTitle("Ajouter un Événement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
