package tn.esprit.models;

public enum type_specialite {
    DENTIST,
    GENERALIST,
    CARDIOLOGIST,
    PEDIATRICIAN,
    GYNECOLOGIST,
    ORTHOPEDIST,
    NEUROLOGIST;

    public String getDisplayName() {
        switch (this) {
            case DENTIST: return "Dentiste";
            case GENERALIST: return "Médecin Généraliste";
            case CARDIOLOGIST: return "Cardiologue";
            case PEDIATRICIAN: return "Pédiatre";
            case GYNECOLOGIST: return "Gynécologue";
            case ORTHOPEDIST: return "Orthopédiste";
            case NEUROLOGIST: return "Neurologue";
            default: return "Inconnu";
        }
    }
}
