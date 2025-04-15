package tn.esprit.models;

public class dossier_medical {
    private int id;
    private String groupeSanguin;
    private String allergies;
    private String antecedentsMedicaux;
    private String traitementsActuels;

    private user patient; // Lien vers l'utilisateur (rôle = "patient")

    public dossier_medical() {}

    public dossier_medical(int id, String groupeSanguin, String allergies, String antecedentsMedicaux, String traitementsActuels, user patient) {
        this.id = id;
        this.groupeSanguin = groupeSanguin;
        this.allergies = allergies;
        this.antecedentsMedicaux = antecedentsMedicaux;
        this.traitementsActuels = traitementsActuels;
        this.patient = patient;
    }

    public int getId() {
        return id;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getAntecedentsMedicaux() {
        return antecedentsMedicaux;
    }

    public String getTraitementsActuels() {
        return traitementsActuels;
    }

    public user getPatient() {
        return patient;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public void setAntecedentsMedicaux(String antecedentsMedicaux) {
        this.antecedentsMedicaux = antecedentsMedicaux;
    }

    public void setTraitementsActuels(String traitementsActuels) {
        this.traitementsActuels = traitementsActuels;
    }

    public void setPatient(user patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "DossierMedical{" +
                "id=" + id +
                ", groupeSanguin='" + groupeSanguin + '\'' +
                ", allergies='" + allergies + '\'' +
                ", antecedentsMedicaux='" + antecedentsMedicaux + '\'' +
                ", traitementsActuels='" + traitementsActuels + '\'' +
                ", patient=" + patient.getName() + // pour afficher juste le nom du patient
                '}';
    }

}
