package tn.esprit.models;
import java.util.List;
import java.util.ArrayList;

import java.time.LocalDateTime;

public class consultation {
    private int id;
    private LocalDateTime date;
    private String status; // En attente, confirmé, annulé
    private user patient; // Référence au patient (celui qui prend la consultation)
    private user doctor;  // Référence au médecin
    private type_specialite specialite;

    public consultation() {}
    public consultation(LocalDateTime date, String status, user patient, user doctor, type_specialite specialite) {

        this.date = date;
        this.status = status;
        this.patient = patient;
        this.doctor = doctor;
        this.specialite = specialite;

    }


    // Constructeurs, getters et setters

    public user getPatient() {
        return patient;
    }

    public void setPatient(user patient) {
        this.patient = patient;
    }

    public user getDoctor() {
        return doctor;
    }

    public void setDoctor(user doctor) {
        this.doctor = doctor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;

    }
    public type_specialite getSpecialite() {
        return specialite;

    }
    public void setSpecialite(type_specialite specialite) {
        this.specialite = specialite;
    }

    @Override
    public String toString() {
        return "consultation{" +
                "id=" + id +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", patient=" + patient +
                ", doctor=" + doctor +
                ", specialite=" + specialite +
                '}';
    }
}
