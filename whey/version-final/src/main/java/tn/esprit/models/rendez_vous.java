package tn.esprit.models;

public class rendez_vous {
    private int laboratoire_id;
    private String patient_nom, patient_email, patient_telephone, date, heure, etat;

    public rendez_vous() {}

    public rendez_vous(int laboratoire_id, String patient_nom, String patient_email, String patient_telephone, String date, String heure, String etat) {
        this.laboratoire_id = laboratoire_id;
        this.patient_nom = patient_nom;
        this.patient_email = patient_email;
        this.patient_telephone = patient_telephone;
        this.date = date;
        this.heure = heure;
        this.etat = etat;
    }

    public int getLaboratoire_id() {
        return laboratoire_id;
    }

    public void setLaboratoire_id(int laboratoire_id) {
        this.laboratoire_id = laboratoire_id;
    }

    public String getPatient_nom() {
        return patient_nom;
    }

    public void setPatient_nom(String patient_nom) {
        this.patient_nom = patient_nom;
    }

    public String getPatient_email() {
        return patient_email;
    }

    public void setPatient_email(String patient_email) {
        this.patient_email = patient_email;
    }

    public String getPatient_telephone() {
        return patient_telephone;
    }

    public void setPatient_telephone(String patient_telephone) {
        this.patient_telephone = patient_telephone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "rendez_vous{" +
                "laboratoire_id=" + laboratoire_id +
                ", patient_nom='" + patient_nom + '\'' +
                ", patient_email='" + patient_email + '\'' +
                ", patient_telephone='" + patient_telephone + '\'' +
                ", date='" + date + '\'' +
                ", heure='" + heure + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }
}
