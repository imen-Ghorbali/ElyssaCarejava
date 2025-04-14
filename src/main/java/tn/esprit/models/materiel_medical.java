package tn.esprit.models;

public class materiel_medical {
    private int id, prix;
    private String nom, description, image, statu;

    public materiel_medical() {}

    public materiel_medical(int id, int prix, String nom, String description, String image, String statu) {
        this.id = id;
        this.prix = prix;
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.statu = statu;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    @Override
    public String toString() {
        return "materiel_medical{" +
                "id=" + id +
                ", prix=" + prix +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", statu='" + statu + '\'' +
                '}';
    }
}
