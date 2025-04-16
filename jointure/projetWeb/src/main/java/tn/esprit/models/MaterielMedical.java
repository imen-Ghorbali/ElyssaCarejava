package tn.esprit.models;

public class MaterielMedical {
    private long id;
    private String nom;
    private String image;
    private String description;
    private double prix;
    private String statut;
    private String marque;

    // Constructeurs
    public MaterielMedical() {}

    public MaterielMedical(String nom, String description, double prix, String statut) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.statut = statut;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }


    @Override
    public String toString() {
        return String.format("%s - %.2f TND (%s)", nom, prix, statut);
    }
}