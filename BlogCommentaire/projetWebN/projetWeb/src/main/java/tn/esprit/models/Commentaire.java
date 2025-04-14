package tn.esprit.models;

public class Commentaire {
    private int id;
    private int blog_id;
    private int nombre_like;
    private String nom_utilisateur;
    private String date;
    private String contenu;

    public Commentaire() {}

    public Commentaire(int id, int blog_id, int nombre_like, String nom_utilisateur, String date, String contenu) {
        this.id = id;
        this.blog_id = blog_id;
        this.nombre_like = nombre_like;
        this.nom_utilisateur = nom_utilisateur;
        this.date = date;
        this.contenu = contenu;
    }

    // Sans ID (pour ajout)
    public Commentaire(int blog_id, int nombre_like, String nom_utilisateur, String date, String contenu) {
        this.blog_id = blog_id;
        this.nombre_like = nombre_like;
        this.nom_utilisateur = nom_utilisateur;
        this.date = date;
        this.contenu = contenu;
    }

    // Getters & Setters

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getBlog_id() { return blog_id; }

    public void setBlog_id(int blog_id) { this.blog_id = blog_id; }

    public int getNombre_like() { return nombre_like; }

    public void setNombre_like(int nombre_like) { this.nombre_like = nombre_like; }

    public String getNom_utilisateur() { return nom_utilisateur; }

    public void setNom_utilisateur(String nom_utilisateur) { this.nom_utilisateur = nom_utilisateur; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getContenu() { return contenu; }

    public void setContenu(String contenu) { this.contenu = contenu; }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", blog_id=" + blog_id +
                ", nombre_like=" + nombre_like +
                ", nom_utilisateur='" + nom_utilisateur + '\'' +
                ", date='" + date + '\'' +
                ", contenu='" + contenu + '\'' +
                '}';
    }
}
