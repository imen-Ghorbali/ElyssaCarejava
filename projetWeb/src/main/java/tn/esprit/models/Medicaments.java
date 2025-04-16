package tn.esprit.models;

public class Medicaments {
    private int id;
    private String nom;
    private String description;
    private String classe;
    private int prix;
    private String image;

    public Medicaments() {
    }

    public Medicaments(String nom, String description, String classe, int prix) {
        this.nom = nom;
        this.description = description;
        this.classe = classe;
        this.prix = prix;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Medicament{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", classe='" + classe + '\'' +
                ", prix=" + prix +
                ", image='" + image + '\'' +
                '}';
    }
}