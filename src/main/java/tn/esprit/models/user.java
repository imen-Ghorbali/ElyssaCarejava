package tn.esprit.models;
import java.util.List;
import java.util.ArrayList;

public class user {
    private int id ;
    private String name, email, password  ;
    private Role roles;
    private List<consultation> consultations;


    public user() {}
    public user(int id, String name, String email, Role roles, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.password = password;

    }
    public List<consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<consultation> consultations) {
        this.consultations = consultations;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Role roles) {
        this.roles = roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPatient() {
        return roles == Role.PATIENT;
    }

    public boolean isDoctor() {
        return roles == Role.DOCTOR;
    }
    public boolean isAdmin() {
        return roles == Role.ADMIN;
    }


    @Override
   /* public String toString() {
        return "user{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    @Override*/
    public String toString() {
        return this.name;  // ou email ou autre champ représentatif
    }

}
