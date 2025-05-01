package tn.esprit.models;

public enum Role {
    PATIENT, ADMIN, DOCTOR;

    public static Role fromJsonRole(String jsonRole) {
        String clean = jsonRole.replace("[", "").replace("]", "").replace("\"", "");
        return Role.valueOf(clean.toUpperCase());
    }
}
