/*package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import tn.esprit.models.consultation;
import tn.esprit.services.service_consultation;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import java.time.ZoneId;
import java.util.List;

public class AgendaConsultation {

    @FXML
    private AnchorPane agendaPane;

    private final service_consultation service = new service_consultation();
    private int doctorId = 0; // Initialisé à 0 par défaut

    @FXML
    public void initialize() {
        try {
            // Vérification initiale
            if (agendaPane == null) {
                System.err.println("Erreur: agendaPane n'est pas injecté");
                return;
            }

            // Création de la vue calendrier
            CalendarView calendarView = new CalendarView();
            configureCalendarView(calendarView);

            // Configuration du calendrier
            Calendar calendrierConsultations = createConsultationCalendar();

            // Ajout des consultations
            addConsultationsToCalendar(calendrierConsultations);

            // Configuration de la source du calendrier
            CalendarSource source = new CalendarSource("Mes consultations");
            source.getCalendars().add(calendrierConsultations);
            calendarView.getCalendarSources().add(source);

            // Ajout à l'interface
            addCalendarToPane(calendarView);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'agenda", e.getMessage());
        }
    }

    private void configureCalendarView(CalendarView calendarView) {
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowDeveloperConsole(false);
        calendarView.setShowToolBar(false);
    }

    private Calendar createConsultationCalendar() {
        Calendar calendar = new Calendar("Consultations");
        calendar.setStyle(Calendar.Style.STYLE1);
        return calendar;
    }

    private void addConsultationsToCalendar(Calendar calendar) {
        List<consultation> consultations = service.getAll().stream()
                .filter(c -> c.getDoctor() != null && c.getDoctor().getId() == doctorId)
                .toList();

        for (consultation c : consultations) {
            try {
                Entry<String> entry = new Entry<>("Patient: " +
                        (c.getPatient() != null ? c.getPatient().getName() : "Inconnu"));

                entry.setLocation(c.getSpecialite() != null ?
                        c.getSpecialite().getDisplayName() : "Non spécifié");

                if (c.getDate() != null) {
                    entry.setInterval(c.getDate(), c.getDate().plusMinutes(30));
                    entry.setZoneId(ZoneId.systemDefault());
                    entry.setCalendar(calendar);
                }
            } catch (Exception e) {
                System.err.println("Erreur avec la consultation ID: " + c.getId());
                e.printStackTrace();
            }
        }
    }

    private void addCalendarToPane(CalendarView calendarView) {
        agendaPane.getChildren().clear();
        agendaPane.getChildren().add(calendarView);

        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
        System.out.println("Médecin ID défini pour l'agenda: " + doctorId);

        // Recharger les données si la vue est déjà initialisée
        if (agendaPane != null) {
            initialize();
        }
    }

    private void showAlert(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}*/package tn.esprit.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import tn.esprit.models.consultation;
import tn.esprit.services.service_consultation;
import java.time.ZoneId;
import java.util.List;

public class AgendaConsultation {

    @FXML
    private AnchorPane agendaPane;

    private final service_consultation service = new service_consultation();
    private int doctorId;

    @FXML
    public void initialize() {
        loadAgenda();
    }

    private void loadAgenda() {
        try {
            // Création de la vue calendrier
            CalendarView calendarView = createCompatibleCalendarView();

            // Création du calendrier des consultations
            Calendar consultationsCalendar = new Calendar("Consultations");
            consultationsCalendar.setStyle(Calendar.Style.STYLE2);

            // Ajout des consultations
            addConsultationsToCalendar(consultationsCalendar);

            // Configuration de la source
            CalendarSource calendarSource = new CalendarSource("Mes Consultations");
            calendarSource.getCalendars().add(consultationsCalendar);
            calendarView.getCalendarSources().add(calendarSource);

            // Ajout à l'interface
            setupCalendarView(calendarView);

        } catch (Exception e) {
            showError("Erreur", "Impossible de charger l'agenda", e.getMessage());
            e.printStackTrace();
        }
    }

    private CalendarView createCompatibleCalendarView() {
        CalendarView view = new CalendarView();

        // Configuration de base compatible avec toutes les versions
        view.setShowAddCalendarButton(false);
        view.setShowPrintButton(true);
        view.setShowSearchField(true);

        // Essai des méthodes avancées (sans échec si non disponibles)
        trySetMethod(view, "setShowWeekNumbers", true);
        trySetMethod(view, "setShowWeekdayFilters", true);
        trySetMethod(view, "setShowToolBar", true);

        return view;
    }

    private void trySetMethod(CalendarView view, String methodName, boolean value) {
        try {
            view.getClass().getMethod(methodName, boolean.class).invoke(view, value);
        } catch (Exception e) {
            System.out.println("Méthode non disponible: " + methodName);
        }
    }

    private void addConsultationsToCalendar(Calendar calendar) {
        List<consultation> consultations = service.getAll().stream()
                .filter(c -> c.getDoctor() != null && c.getDoctor().getId() == doctorId)
                .toList();

        for (consultation c : consultations) {
            try {
                Entry<String> entry = new Entry<>(createEntryTitle(c));
                entry.setLocation(createEntryLocation(c));

                if (c.getDate() != null) {
                    entry.setInterval(c.getDate(), c.getDate().plusMinutes(30));
                    entry.setZoneId(ZoneId.systemDefault());
                    calendar.addEntry(entry);
                }
            } catch (Exception e) {
                System.err.println("Erreur avec consultation ID: " + c.getId());
            }
        }
    }

    private String createEntryTitle(consultation c) {
        return String.format("%s - %s",
                c.getPatient() != null ? c.getPatient().getName() : "Patient",
                c.getSpecialite() != null ? c.getSpecialite().getDisplayName() : "Spécialité");
    }

    private String createEntryLocation(consultation c) {
        return c.getStatus() != null ? c.getStatus() : "Programmée";
    }

    private void setupCalendarView(CalendarView calendarView) {
        agendaPane.getChildren().clear();
        agendaPane.getChildren().add(calendarView);

        // Configuration d'ancrage pour remplir l'espace
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
        if (agendaPane != null) {
            loadAgenda();
        }
    }

    private void showError(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}