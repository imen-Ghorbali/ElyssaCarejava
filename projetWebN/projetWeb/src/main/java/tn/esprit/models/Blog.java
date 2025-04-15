package tn.esprit.models;

public class Blog {
    private int id ;
    private String titre, contenu, date_publication, auteur, image ;

    public Blog() {}


    public Blog(int id, String titre, String contenu, String date_publication, String auteur, String image) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.date_publication = date_publication;
        this.auteur = auteur;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getDate_publication() {
        return date_publication;
    }

    public void setDate_publication(String date_publication) {
        this.date_publication = date_publication;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", date_publication='" + date_publication + '\'' +
                ", auteur='" + auteur + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}