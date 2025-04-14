package tn.esprit.services;

import tn.esprit.models.events;
import tn.esprit.models.sponsor;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent {
    Connection cnx = DataBase.getInstance().getCnx();

    // Ajouter un événement (avec contrôle de saisie)
    public void add(events e) {

        if (e.getTitle() == null || e.getTitle().isEmpty() ||
                e.getDescription() == null || e.getDescription().isEmpty() ||
                e.getLieu() == null || e.getLieu().isEmpty() ||
                e.getDate() == null || e.getImage() == null || e.getImage().isEmpty()) {
            System.out.println("Erreur : Tous les champs obligatoires doivent être remplis.");
            return;
        }

        String sql = "INSERT INTO events (title, description, lieu, date, image, id_sponsor) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, e.getTitle());
            pstmt.setString(2, e.getDescription());
            pstmt.setString(3, e.getLieu());
            pstmt.setTimestamp(4, Timestamp.valueOf(e.getDate()));
            pstmt.setString(5, e.getImage());
            if (e.getSponsor() != null) {
                pstmt.setInt(6, e.getSponsor().getId());  // Utilisation de l'ID du sponsor
            } else {
                pstmt.setNull(6, Types.INTEGER);  // Si aucun sponsor, on met la valeur à NULL
            }

            int res = pstmt.executeUpdate();
            if (res > 0) {
                System.out.println("Événement ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout de l'événement.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Modifier un événement existant (avec contrôle de saisie)
    public void edit(events e) {
        if (e.getId() <= 0 ||
                e.getTitle() == null || e.getTitle().isEmpty() ||
                e.getDescription() == null || e.getDescription().isEmpty() ||
                e.getLieu() == null || e.getLieu().isEmpty() ||
                e.getDate() == null || e.getImage() == null || e.getImage().isEmpty()) {
            System.out.println("Erreur : Données invalides pour la modification.");
            return;
        }

        String req = "UPDATE events SET title=?, description=?, lieu=?, date=?, image=? WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, e.getTitle());
            pstmt.setString(2, e.getDescription());
            pstmt.setString(3, e.getLieu());
            pstmt.setTimestamp(4, Timestamp.valueOf(e.getDate()));
            pstmt.setString(5, e.getImage());
            pstmt.setInt(6, e.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Événement modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée. Vérifiez l'ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Supprimer un événement par ID (avec contrôle)
    public void delete(int id) {
        if (id <= 0) {
            System.out.println("Erreur : ID invalide.");
            return;
        }

        String req = "DELETE FROM events WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Événement supprimé avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée. Vérifiez l'ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<events> getAll() {
        List<events> eventsList = new ArrayList<>();
        String req = "SELECT * FROM events ORDER BY date DESC";
        try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(req)) {
            while (res.next()) {
                // Récupérer les données de l'événement
                int eventId = res.getInt("id");
                String title = res.getString("title");
                String description = res.getString("description");
                String image = res.getString("image");
                String lieu = res.getString("lieu");
                LocalDateTime date = res.getTimestamp("date").toLocalDateTime();
                int sponsorId = res.getInt("id_sponsor");

                // Récupérer le sponsor lié à l'événement
                sponsor sponsor = getSponsorById(sponsorId);

                // Créer l'objet events avec le sponsor
                events e = new events(eventId, title, description, image, lieu, date, sponsor);
                eventsList.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return eventsList;
    }

    // Méthode pour récupérer un sponsor par son ID
    private sponsor getSponsorById(int sponsorId) {
        sponsor sponsor = null;
        String req = "SELECT * FROM sponsor WHERE id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, sponsorId);
            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    sponsor = new sponsor(
                            res.getInt("id"),
                            res.getInt("prix"), // Changer cette ligne pour correspondre au type correct du prix
                            res.getString("name"),
                            res.getString("description"),
                            res.getString("type")
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return sponsor;
    }

    // Récupérer un événement par ID (avec contrôle)
    public events getById(int id) {
        if (id <= 0) {
            System.out.println("Erreur : ID invalide.");
            return null;
        }

        String req = "SELECT * FROM events WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                // Récupérer les données de l'événement
                int eventId = res.getInt("id");
                String title = res.getString("title");
                String description = res.getString("description");
                String image = res.getString("image");
                String lieu = res.getString("lieu");
                LocalDateTime date = res.getTimestamp("date").toLocalDateTime();
                int sponsorId = res.getInt("id_sponsor");

                // Récupérer le sponsor lié à l'événement
                sponsor sponsor = getSponsorById(sponsorId);

                // Créer l'objet events avec le sponsor
                return new events(eventId, title, description, image, lieu, date, sponsor);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
