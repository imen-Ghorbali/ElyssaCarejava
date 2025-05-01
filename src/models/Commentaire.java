package tn.esprit.models;

public class Commentaire {
    private int id;
    private int blog_id;
    private int nombre_like;
    private String nom_utilisateur;
    private String date;
    private String contenu;

    // Constructeur par défaut
    public Commentaire() {}

    // Constructeur complet
    public Commentaire(int id, int blog_id, int nombre_like, String nom_utilisateur, String date, String contenu) {
        this.id = id;
        this.blog_id = blog_id;
        this.nombre_like = nombre_like;
        this.nom_utilisateur = nom_utilisateur;
        this.date = date;
        this.contenu = contenu;
    }

    // Constructeur sans ID pour l'ajout
    public Commentaire(int blog_id, int nombre_like, String nom_utilisateur, String date, String contenu) {
        this.blog_id = blog_id;
        this.nombre_like = nombre_like;
        this.nom_utilisateur = nom_utilisateur;
        this.date = date;
        this.contenu = contenu;
    }

    // Constructeur avec texte et blog
    public Commentaire(String contenu, Blog blog) {
        this.contenu = contenu;
        this.blog_id = blog.getId();  // Assurez-vous que la méthode getId() existe dans votre classe Blog.
        this.nombre_like = 0;  // Le nombre de likes initialisé à 0
        this.nom_utilisateur = "Utilisateur";  // Par défaut, mais peut être modifié pour l'utilisateur actuel
        this.date = "2025-04-16";  // Vous pouvez remplacer ceci par la date actuelle
    }

    // Getters et Setters
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

    // Méthode pour incrémenter les likes
    public void incrementLikes() {
        this.nombre_like++;
    }

    // Redéfinition de la méthode toString pour une présentation claire
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
