package tn.esprit.services;

import tn.esprit.models.sponsor;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceSponsor {
    Connection cnx = DataBase.getInstance().getCnx();

    // Ajouter un sponsor (avec contrôle de saisie)
    public void add(sponsor s) {
        if (s.getPrix() <= 0 ||
                s.getName() == null || s.getName().isEmpty() ||
                s.getDescription() == null || s.getDescription().isEmpty() ||
                s.getType() == null || s.getType().isEmpty()) {
            System.out.println("Erreur : Tous les champs obligatoires doivent être valides et remplis.");
            return;
        }

        String sql = "INSERT INTO sponsor (prix, name, description, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, s.getPrix());
            pstmt.setString(2, s.getName());
            pstmt.setString(3, s.getDescription());
            pstmt.setString(4, s.getType());
            int res = pstmt.executeUpdate();
            if (res > 0) {
                System.out.println("Sponsor ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout du sponsor.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Modifier un sponsor (avec contrôle de saisie)
    public void edit(sponsor s) {
        if (s.getId() <= 0 ||
                s.getPrix() <= 0 ||
                s.getName() == null || s.getName().isEmpty() ||
                s.getDescription() == null || s.getDescription().isEmpty() ||
                s.getType() == null || s.getType().isEmpty()) {
            System.out.println("Erreur : Données invalides pour la modification.");
            return;
        }

        String sql = "UPDATE sponsor SET prix=?, name=?, description=?, type=? WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, s.getPrix());
            pstmt.setString(2, s.getName());
            pstmt.setString(3, s.getDescription());
            pstmt.setString(4, s.getType());
            pstmt.setInt(5, s.getId());
            int res = pstmt.executeUpdate();
            if (res > 0) {
                System.out.println("Sponsor modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée. Vérifiez l'ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Supprimer un sponsor (avec contrôle)
    public void delete(int id) {
        if (id <= 0) {
            System.out.println("Erreur : ID invalide.");
            return;
        }

        String sql = "DELETE FROM sponsor WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int res = pstmt.executeUpdate();
            if (res > 0) {
                System.out.println("Sponsor supprimé avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée. Vérifiez l'ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Récupérer tous les sponsors
    public List<sponsor> getAll() {
        List<sponsor> sponsors = new ArrayList<>();
        String sql = "SELECT * FROM sponsor";
        try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(sql)) {
            while (res.next()) {
                sponsor s = new sponsor(
                        res.getInt("id"),
                        res.getInt("prix"),
                        res.getString("name"),
                        res.getString("description"),
                        res.getString("type")
                );
                sponsors.add(s);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return sponsors;
    }


    // Récupérer un sponsor par ID (avec contrôle)
    public sponsor getById(int id) {
        if (id <= 0) {
            System.out.println("Erreur : ID invalide.");
            return null;
        }

        String sql = "SELECT * FROM sponsor WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                return new sponsor(
                        res.getInt("id"),
                        res.getInt("prix"),
                        res.getString("name"),
                        res.getString("description"),
                        res.getString("type")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
