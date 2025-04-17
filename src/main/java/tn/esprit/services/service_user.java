package tn.esprit.services;

import tn.esprit.models.user;
import tn.esprit.models.Role;
import tn.esprit.utils.DataBase;
import tn.esprit.interfaces.IService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.mindrot.jbcrypt.BCrypt;

public class service_user implements IService<user> {
    private Connection cnx;

    public service_user() {
        cnx = DataBase.getInstance().getCnx();
    }


    public user getById(int id) {
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
                u.setRoles(Role.valueOf(rs.getString("roles")));
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de l'utilisateur : " + e.getMessage());
        }
        return u;
    }

    @Override
    public int add(user u) {
        int generatedId = -1;
        try {
            String req = "INSERT INTO user (name, email, roles, password) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());

            String roleJson = "[\"" + u.getRole().name() + "\"]"; // format JSON
            ps.setString(3, roleJson);

            ps.setString(4, BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));


            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }

        return generatedId;
    }



    @Override
    public ArrayList<user> getAll() {
        ArrayList<user> user = new ArrayList<>();
        String qry = "SELECT * FROM `user`";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                user u = new user();
                u.setId(rs.getInt(1));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                String roleStr = rs.getString("roles");
                roleStr = roleStr.replace("[", "").replace("]", "").replace("\"", "");
                Role role = Role.valueOf(roleStr);
                u.setRoles(role);

                u.setPassword(rs.getString("password"));
                user.add(u);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }
    @Override
    public void update(user u) {
        String qry = "UPDATE `user` SET `name` = ?, `email` = ?, `roles` = ?, `password` = ? WHERE `id` = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, u.getName());
            pstm.setString(2, u.getEmail());
            String roleJson = "[\"" + u.getRole().name() + "\"]"; // Format JSON for roles
            pstm.setString(3, roleJson);
            pstm.setString(4, u.getPassword());
            pstm.setInt(5, u.getId());

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public boolean delete(user u) {
        String qry = "DELETE FROM `user` WHERE `id` = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, u.getId());

            int rowsDeleted = pstm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("🗑️ Utilisateur supprimé avec succès !");
                return true;
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec cet ID.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            return false;
        }
    }



    public user getUserByEmail(String email) {
        user u = null;
        try {
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String roleStr = rs.getString("roles");
                roleStr = roleStr.replace("[", "").replace("]", "").replace("\"", "");
                Role role = Role.valueOf(roleStr);
                String password = rs.getString("password");

                u = new user(id, name, email, role, password);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return u;
    }
    public user getByEmail(String email) {
        Connection cnx = DataBase.getInstance().getCnx();
        String query = "SELECT * FROM user WHERE email = ?";
        try {
            PreparedStatement stmt = cnx.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user u = new user();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                String roleStr = rs.getString("roles");
                roleStr = roleStr.replace("[", "").replace("]", "").replace("\"", "");
                u.setRoles(Role.valueOf(roleStr));
                // selon ta logique de rôle
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public user login(String email, String password) throws SQLException {
        String req = "SELECT * FROM user WHERE email=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String hashedPassword = rs.getString("password");
            if (BCrypt.checkpw(password, hashedPassword)) {
                user u = new user();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setName(rs.getString("name"));
                u.setPassword(hashedPassword);
                String roleStr = rs.getString("roles");
                if (roleStr != null) {
                    roleStr = roleStr.replace("[", "").replace("]", "").replace("\"", "");
                    u.setRoles(Role.valueOf(roleStr));
                }
                return u;
            } else {
                System.out.println("❌ Mot de passe incorrect.");
                return null;
            }
        } else {
            System.out.println("❌ Email introuvable.");
            return null;
        }
    }

    public List<user> getAllDoctors() {
        List<user> doctors = new ArrayList<>();
        String req = "SELECT * FROM user WHERE roles LIKE '%DOCTOR%'";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                user u = new user();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));

                String roleStr = rs.getString("roles").replace("[", "").replace("]", "").replace("\"", "");
                u.setRoles(Role.valueOf(roleStr));

                doctors.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des docteurs : " + e.getMessage());
        }
        return doctors;
    }
    public class PasswordUtils {
        public static String hashPassword(String password) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256"); // ou "SHA-512"
                byte[] hashBytes = md.digest(password.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b)); // convertit chaque byte en hexadécimal
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
            }
        }
    }
/* @Override
    public void update(user u) {
        String qry = "UPDATE `user` SET `name` = ?, `email` = ?, `roles` = ?, `password` = ? WHERE `id` = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, u.getName());
            pstm.setString(2, u.getEmail());
            String roleJson = "[\"" + u.getRole().name() + "\"]";
            pstm.setString(3, roleJson);

            // On ne hache plus le mot de passe ici
            pstm.setString(4, u.getPassword());

            pstm.setInt(5, u.getId());

            int rowsUpdated = pstm.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec l'ID : " + u.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public boolean delete(user u) {
        String qry = "DELETE FROM `user` WHERE `id` = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, u.getId());

            int rowsDeleted = pstm.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("✅ Utilisateur supprimé avec succès !");
                return true;
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec l'ID : " + u.getId());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }*/






}
