package tn.esprit.models;

import java.time.LocalDateTime;
import tn.esprit.models.sponsor;

import tn.esprit.models.sponsor;

public class events {
    private int id;
    private String title, description, image, lieu;
    private LocalDateTime date;
    private sponsor sponsor;
    public events() {}

    public events(int id, String title, String description, String image, String lieu, LocalDateTime date, sponsor sponsor) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.lieu = lieu;
        this.date = date;
        this.sponsor = sponsor;





    }

    // À supprimer : constructeur incorrect et inutile
    // public events(int id, String conférenceSanté, String description, String tunis, String date, String image) {
    //     this.id = id;
    // }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(sponsor sponsor) {
        this.sponsor = sponsor;
    }

    @Override
    public String toString() {
        return "events{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", lieu='" + lieu + '\'' +
                ", date=" + date +
                '}';
    }
}
