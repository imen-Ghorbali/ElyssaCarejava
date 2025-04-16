package tn.esprit.services;

import tn.esprit.models.Commentaire;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommentaire {
    private final Connection cnx;

    public ServiceCommentaire() {
        this.cnx = DataBase.getInstance().getCnx();
    }

    // Ajouter un commentaire à un blog
    public int add(Commentaire c) {
        String sql = "INSERT INTO commentaire (blog_id, nombre_like, nom_utilisateur, date, contenu) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getBlog_id());
            ps.setInt(2, c.getNombre_like());
            ps.setString(3, c.getNom_utilisateur());
            ps.setString(4, c.getDate());
            ps.setString(5, c.getContenu());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retourne l'ID du commentaire ajouté
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du commentaire: " + e.getMessage());
        }
        return -1; // Retourne -1 si l'ajout échoue
    }

    // Supprimer un commentaire par son ID
    public boolean delete(int id) {
        String sql = "DELETE FROM commentaire WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0; // Retourne true si le commentaire a été supprimé
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            return false; // Retourne false en cas d'erreur
        }
    }

    // Récupérer la liste des commentaires d'un blog en fonction de son ID
    public List<Commentaire> getByBlogId(int blogId) {
        List<Commentaire> comments = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE blog_id = ? ORDER BY date DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, blogId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(new Commentaire(
                            rs.getInt("id"),
                            rs.getInt("blog_id"),
                            rs.getInt("nombre_like"),
                            rs.getString("nom_utilisateur"),
                            rs.getString("date"),
                            rs.getString("contenu")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des commentaires: " + e.getMessage());
        }
        return comments; // Retourne la liste des commentaires associés au blog
    }

    // Incrémenter le nombre de "likes" d'un commentaire
    public boolean incrementLike(int commentId) {
        String sql = "UPDATE commentaire SET nombre_like = nombre_like + 1 WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0; // Retourne true si le like a été ajouté
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du like: " + e.getMessage());
            return false; // Retourne false en cas d'erreur
        }
    }

    // Mettre à jour un commentaire existant
    public boolean update(Commentaire commentaire) {
        String sql = "UPDATE commentaire SET nombre_like = ?, nom_utilisateur = ?, contenu = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, commentaire.getNombre_like());
            ps.setString(2, commentaire.getNom_utilisateur());
            ps.setString(3, commentaire.getContenu());
            ps.setInt(4, commentaire.getId());

            return ps.executeUpdate() > 0; // Retourne true si la mise à jour a réussi
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du commentaire: " + e.getMessage());
            return false; // Retourne false en cas d'erreur
        }
    }
}
