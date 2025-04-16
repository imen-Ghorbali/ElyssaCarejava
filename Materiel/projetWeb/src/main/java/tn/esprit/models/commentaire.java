package tn.esprit.models;

public class commentaire {
    private int blog_id, nombre_like;
    private String nom_utilisateur, date;

    public commentaire() {}

    public commentaire(int blog_id, int nombre_like, String nom_utilisateur, String date) {
       this.blog_id = blog_id;
        this.nombre_like = nombre_like;
        this.nom_utilisateur = nom_utilisateur;
        this.date = date;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    public int getNombre_like() {
        return nombre_like;
    }

    public void setNombre_like(int nombre_like) {
        this.nombre_like = nombre_like;
    }

    public String getNom_utilisateur() {
        return nom_utilisateur;
    }

    public void setNom_utilisateur(String nom_utilisateur) {
        this.nom_utilisateur = nom_utilisateur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "commentaire{" +
                "blog_id=" + blog_id +
                ", nombre_like=" + nombre_like +
                ", nom_utilisateur='" + nom_utilisateur + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
