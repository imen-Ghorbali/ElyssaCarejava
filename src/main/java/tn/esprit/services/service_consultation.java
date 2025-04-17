package tn.esprit.services;

import tn.esprit.models.consultation;
import tn.esprit.utils.DataBase;
import tn.esprit.models.user;
import tn.esprit.interfaces.IService_consultation;
import java.sql.Connection;
import tn.esprit.models.type_specialite;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import tn.esprit.models.Role;
import java.util.ArrayList;

public class service_consultation implements IService_consultation<consultation> {

    private Connection cnx;

    public service_consultation() {
        cnx = DataBase.getInstance().getCnx();
    }
    private final service_user userService = new service_user();




    @Override
    public void add(consultation c) {
        String sql = "INSERT INTO consultation (date, status, patient, doctor, specialite) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(sql);

            // On suppose que la date est bien remplie
            preparedStatement.setTimestamp(1, Timestamp.valueOf(c.getDate()));

            // Le statut est une chaîne comme "En attente"
            preparedStatement.setString(2, c.getStatus());

            // patient et doctor sont des objets de type user, on prend leurs IDs
            if (c.getPatient() == null || c.getDoctor() == null || c.getPatient().getId() <= 0 || c.getDoctor().getId() <= 0) {
                System.err.println("❌ Erreur : patient ou doctor null OU ID invalide !");
                return;
            }


            preparedStatement.setInt(3, c.getPatient().getId());
            preparedStatement.setInt(4, c.getDoctor().getId());

            // La spécialité est une énumération ou une chaîne
            preparedStatement.setString(5, c.getSpecialite().toString());

            // Exécution
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("✅ Consultation ajoutée avec succès !");
            } else {
                System.out.println("⚠️ Aucun enregistrement inséré.");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout de la consultation : " + e.getMessage());
        }
    }


    @Override
    public ArrayList<consultation> getAll() {
        ArrayList<consultation> list = new ArrayList<>();
        String req = "SELECT * FROM consultation";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                consultation c = new consultation();
                c.setId(rs.getInt("id"));
                c.setDate(rs.getTimestamp("date").toLocalDateTime());
                c.setStatus(rs.getString("status"));

                // Chargement simplifié des users
                user patient = getUserById(rs.getInt("patient"));
                user doctor = getUserById(rs.getInt("doctor"));
                c.setPatient(patient);
                c.setDoctor(doctor);

                type_specialite spec = type_specialite.valueOf(rs.getString("specialite"));
                c.setSpecialite(spec);

                list.add(c);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des consultations : " + e.getMessage());
        }
        return list;
    }


    private user getUserById(int id) {
        user u = null;
        String req = "SELECT * FROM user WHERE id = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                u = new user();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));

                String roleStr = rs.getString("roles");
                if (roleStr != null) {
                    roleStr = roleStr.replace("[", "").replace("]", "").replace("\"", "");
                    Role role = Role.valueOf(roleStr);
                    u.setRoles(role);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de l'utilisateur : " + e.getMessage());
        }
        return u;
    }


    @Override
    public void update(consultation c) {
        String req = "UPDATE consultation SET date = ?, status = ?, doctor = ?, specialite = ? WHERE id = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(c.getDate()));
            pst.setString(2, c.getStatus());
            pst.setInt(3, c.getDoctor().getId());
            pst.setString(4, c.getSpecialite().name());
            pst.setInt(5, c.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Consultation mise à jour avec succès !");
            } else {
                System.out.println("❌ Aucune consultation trouvée avec cet ID.");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour de la consultation : " + e.getMessage());
        }
    }


    @Override
    public boolean delete(consultation c) {
        String req = "DELETE FROM consultation WHERE id = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, c.getId());

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("🗑️ Consultation supprimée avec succès !");
                return true;
            } else {
                System.out.println("❌ Aucune consultation trouvée avec cet ID.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression de la consultation : " + e.getMessage());
            return false;
        }
    }
    public boolean deleteByDate(Timestamp date) {
        String req = "DELETE FROM consultation WHERE date = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setTimestamp(1, date);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("🗑️ Consultation supprimée par date !");
                return true;
            } else {
                System.out.println("❌ Aucune consultation trouvée à cette date.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur suppression consultation par date : " + e.getMessage());
            return false;
        }
    }

}
