package tn.esprit.models;

public class medicaments {
    private int id, prix;
    private String nom, description, classe, image;

    public medicaments() {}

    public medicaments(int id, int prix, String nom, String description, String classe, String image) {
        this.id = id;
        this.prix = prix;
        this.nom = nom;
        this.description = description;
        this.classe = classe;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "medicaments{" +
                "id=" + id +
                ", prix=" + prix +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", classe='" + classe + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

}
