package tn.esprit.models;

public class laboratoire {
    private int id;
    private String nom_laboratoire, adresse, telephone, email, responsable, specialite,
            horaire_ouverture, horaire_fermeture;

    public laboratoire() {}

    public laboratoire(int id, String nom_laboratoire, String adresse, String telephone, String email, String responsable, String specialite, String horaire_ouverture, String horaire_fermeture) {
        this.id = id;
        this.nom_laboratoire = nom_laboratoire;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.responsable = responsable;
        this.specialite = specialite;
        this.horaire_ouverture = horaire_ouverture;
        this.horaire_fermeture = horaire_fermeture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_laboratoire() {
        return nom_laboratoire;
    }

    public void setNom_laboratoire(String nom_laboratoire) {
        this.nom_laboratoire = nom_laboratoire;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getHoraire_ouverture() {
        return horaire_ouverture;
    }

    public void setHoraire_ouverture(String horaire_ouverture) {
        this.horaire_ouverture = horaire_ouverture;
    }

    public String getHoraire_fermeture() {
        return horaire_fermeture;
    }

    public void setHoraire_fermeture(String horaire_fermeture) {
        this.horaire_fermeture = horaire_fermeture;
    }

    @Override
    public String toString() {
        return "laboratoire{" +
                "id=" + id +
                ", nom_laboratoire='" + nom_laboratoire + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", responsable='" + responsable + '\'' +
                ", specialite='" + specialite + '\'' +
                ", horaire_ouverture='" + horaire_ouverture + '\'' +
                ", horaire_fermeture='" + horaire_fermeture + '\'' +
                '}';
    }
}
