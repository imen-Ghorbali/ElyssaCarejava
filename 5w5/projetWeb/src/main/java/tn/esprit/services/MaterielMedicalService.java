package tn.esprit.services;

import tn.esprit.models.MaterielMedical;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterielMedicalService implements IMedicamentService<MaterielMedical> {
    private final Connection connection = DataBase.getInstance().getConnection();

    @Override
    public void ajouter(MaterielMedical materiel) {
        String req = "INSERT INTO materiel_medical (nom, image, description, prix, statut) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, materiel.getNom());
            ps.setString(2, materiel.getImage());
            ps.setString(3, materiel.getDescription());
            ps.setDouble(4, materiel.getPrix());
            ps.setString(5, materiel.getStatut());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        materiel.setId(generatedKeys.getLong(1));
                    }
                }
                System.out.println("Matériel médical ajouté avec succès !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du matériel médical : " + e.getMessage());
        }
    }

    @Override
    public void modifier(MaterielMedical materiel) {
        String req = "UPDATE materiel_medical SET nom=?, image=?, description=?, prix=?, statut=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, materiel.getNom());
            ps.setString(2, materiel.getImage());
            ps.setString(3, materiel.getDescription());
            ps.setDouble(4, materiel.getPrix());
            ps.setString(5, materiel.getStatut());
            ps.setLong(6, materiel.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Matériel médical mis à jour avec succès !");
            } else {
                System.out.println("Aucun matériel médical trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du matériel médical : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(long id) {
        String req = "DELETE FROM materiel_medical WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setLong(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Matériel médical supprimé avec succès !");
            } else {
                System.out.println("Aucun matériel médical trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du matériel médical : " + e.getMessage());
        }
    }

    @Override
    public List<MaterielMedical> rechercher() {
        List<MaterielMedical> materiels = new ArrayList<>();
        String req = "SELECT * FROM materiel_medical";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                MaterielMedical m = new MaterielMedical();
                m.setId(rs.getLong("id"));
                m.setNom(rs.getString("nom"));
                m.setImage(rs.getString("image"));
                m.setDescription(rs.getString("description"));
                m.setPrix(rs.getDouble("prix"));
                m.setStatut(rs.getString("statut"));

                materiels.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des matériels médicaux : " + e.getMessage());
        }

        return materiels;
    }

    @Override
    public MaterielMedical rechercherParId(long id) {
        String req = "SELECT * FROM materiel_medical WHERE id=?";
        MaterielMedical materiel = null;

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    materiel = new MaterielMedical();
                    materiel.setId(rs.getLong("id"));
                    materiel.setNom(rs.getString("nom"));
                    materiel.setImage(rs.getString("image"));
                    materiel.setDescription(rs.getString("description"));
                    materiel.setPrix(rs.getDouble("prix"));
                    materiel.setStatut(rs.getString("statut"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du matériel médical : " + e.getMessage());
        }

        return materiel;
    }
    @Override
    public List<MaterielMedical> getAll() {
        return rechercher();  // Appel à la méthode rechercher() pour récupérer tous les matériels médicaux
    }

}