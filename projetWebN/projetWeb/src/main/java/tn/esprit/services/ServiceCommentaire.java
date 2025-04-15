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

    /**
     * Ajoute un nouveau commentaire et retourne l'ID généré
     */
    public int add(Commentaire c) throws SQLException {
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
                    return rs.getInt(1); // Retourne l'ID généré
                }
            }
        }
        return -1; // Retourne -1 si l'insertion a échoué
    }

    /**
     * Met à jour un commentaire existant
     */
    public boolean update(Commentaire commentaire) throws SQLException {
        String sql = "UPDATE commentaire SET " +
                "blog_id = ?, nombre_like = ?, nom_utilisateur = ?, " +
                "date = ?, contenu = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, commentaire.getBlog_id());
            ps.setInt(2, commentaire.getNombre_like());
            ps.setString(3, commentaire.getNom_utilisateur());
            ps.setString(4, commentaire.getDate());
            ps.setString(5, commentaire.getContenu());
            ps.setInt(6, commentaire.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Supprime un commentaire par son ID
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM commentaire WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Récupère un commentaire par son ID
     */
    public Commentaire getById(int id) throws SQLException {
        String sql = "SELECT * FROM commentaire WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCommentaire(rs);
                }
                return null;
            }
        }
    }

    /**
     * Récupère tous les commentaires
     */
    public List<Commentaire> getAll() throws SQLException {
        List<Commentaire> comments = new ArrayList<>();
        String sql = "SELECT * FROM commentaire";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                comments.add(mapResultSetToCommentaire(rs));
            }
        }
        return comments;
    }

    /**
     * Récupère les commentaires d'un blog spécifique
     */
    public List<Commentaire> getByBlogId(int blogId) throws SQLException {
        List<Commentaire> comments = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE blog_id = ? ORDER BY date DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, blogId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToCommentaire(rs));
                }
            }
        }
        return comments;
    }

    /**
     * Incrémente le nombre de likes d'un commentaire
     */
    public boolean incrementLike(int commentId) throws SQLException {
        String sql = "UPDATE commentaire SET nombre_like = nombre_like + 1 WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Méthode utilitaire pour mapper un ResultSet vers un objet Commentaire
     */
    private Commentaire mapResultSetToCommentaire(ResultSet rs) throws SQLException {
        return new Commentaire(
                rs.getInt("id"),
                rs.getInt("blog_id"),
                rs.getInt("nombre_like"),
                rs.getString("nom_utilisateur"),
                rs.getString("date"),
                rs.getString("contenu")
        );
    }

    /**
     * Récupère les commentaires les plus populaires (avec le plus de likes)
     */
    public List<Commentaire> getTopComments(int limit) throws SQLException {
        List<Commentaire> comments = new ArrayList<>();
        String sql = "SELECT * FROM commentaire ORDER BY nombre_like DESC LIMIT ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToCommentaire(rs));
                }
            }
        }
        return comments;
    }

    /**
     * Compte le nombre de commentaires pour un blog donné
     */
    public int countCommentsForBlog(int blogId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commentaire WHERE blog_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, blogId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}