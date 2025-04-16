package tn.esprit.models;

public class Medicaments {
    private int id;
    private String nom;
    private String description;
    private String classe;
    private int prix;
    private String image;
    private MaterielMedical materielRequis;

    public Medicaments() {
    }

    public Medicaments(String nom, String description, String classe, int prix, String image, MaterielMedical materielRequis) {
        this.nom = nom;
        this.description = description;
        this.classe = classe;
        this.prix = prix;
        this.image = image;
        this.materielRequis = materielRequis;
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

    public MaterielMedical getMaterielRequis() {
        return materielRequis;
    }

    public void setMaterielRequis(MaterielMedical materielRequis) {
        this.materielRequis = materielRequis;
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
                ", materielRequis=" + (materielRequis != null ? materielRequis.getNom() : "Aucun") +
                '}';
    }
}