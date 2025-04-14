package tn.esprit.services;

import tn.esprit.models.Commentaire;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommentaire {
    private Connection cnx;

    public ServiceCommentaire() {
        cnx = DataBase.getInstance().getCnx();
    }

    public void add(Commentaire c) {
        String sql = "INSERT INTO commentaire (blog_id, nombre_like, nom_utilisateur, date, contenu) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, c.getBlog_id());
            ps.setInt(2, c.getNombre_like());
            ps.setString(3, c.getNom_utilisateur());
            ps.setString(4, c.getDate());
            ps.setString(5, c.getContenu());

            ps.executeUpdate();
            System.out.println("✅ Commentaire ajouté !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void edit(Commentaire c) {
        String sql = "UPDATE commentaire SET blog_id = ?, nombre_like = ?, nom_utilisateur = ?, date = ?, contenu = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, c.getBlog_id());
            ps.setInt(2, c.getNombre_like());
            ps.setString(3, c.getNom_utilisateur());
            ps.setString(4, c.getDate());
            ps.setString(5, c.getContenu());
            ps.setInt(6, c.getId());

            ps.executeUpdate();
            System.out.println("✅ Commentaire modifié !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM commentaire WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Commentaire supprimé !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Commentaire getById(int id) {
        String sql = "SELECT * FROM commentaire WHERE id = ?";
        Commentaire c = null;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                c = new Commentaire(
                        rs.getInt("id"),
                        rs.getInt("blog_id"),
                        rs.getInt("nombre_like"),
                        rs.getString("nom_utilisateur"),
                        rs.getString("date"),
                        rs.getString("contenu")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return c;
    }

    public List<Commentaire> getAll() {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaire";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Commentaire c = new Commentaire(
                        rs.getInt("id"),
                        rs.getInt("blog_id"),
                        rs.getInt("nombre_like"),
                        rs.getString("nom_utilisateur"),
                        rs.getString("date"),
                        rs.getString("contenu")
                );
                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
