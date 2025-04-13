package tn.esprit.services;

import tn.esprit.models.Medicaments;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentService implements IMedicamentService<Medicaments> {
    private final Connection connection = DataBase.getInstance().getConnection();

    @Override
    public void ajouter(Medicaments medicament) {
        String query = "INSERT INTO medicaments (nom, description, classe, prix, image) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getDescription());
            ps.setString(3, medicament.getClasse());
            ps.setInt(4, medicament.getPrix());
            ps.setString(5, medicament.getImage());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medicament.setId((int) generatedKeys.getLong(1)); // Cast to int to match Medicaments class
                    }
                }
                System.out.println("Médicament ajouté avec succès!");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Medicaments medicament) {
        String req = "UPDATE medicaments SET nom=?, description=?, classe=?, prix=?, image=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getDescription());
            ps.setString(3, medicament.getClasse());
            ps.setInt(4, medicament.getPrix());
            ps.setString(5, medicament.getImage());
            ps.setLong(6, medicament.getId()); // Using long for ID

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Médicament mis à jour avec succès !");
            } else {
                System.out.println("Aucun médicament trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du médicament : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(long id) {
        String req = "DELETE FROM medicaments WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setLong(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Médicament supprimé avec succès !");
            } else {
                System.out.println("Aucun médicament trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du médicament : " + e.getMessage());
        }
    }

    @Override
    public List<Medicaments> rechercher() {
        List<Medicaments> medicaments = new ArrayList<>();
        String req = "SELECT * FROM medicaments";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Medicaments m = new Medicaments();
                m.setId(rs.getInt("id")); // Using getInt to match Medicaments class
                m.setNom(rs.getString("nom"));
                m.setDescription(rs.getString("description"));
                m.setClasse(rs.getString("classe"));
                m.setPrix(rs.getInt("prix"));
                m.setImage(rs.getString("image"));

                medicaments.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médicaments : " + e.getMessage());
        }

        return medicaments;
    }

    @Override
    public Medicaments rechercherParId(long id) {
        String req = "SELECT * FROM medicaments WHERE id=?";
        Medicaments medicament = null;

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    medicament = new Medicaments();
                    medicament.setId(rs.getInt("id")); // Using getInt to match Medicaments class
                    medicament.setNom(rs.getString("nom"));
                    medicament.setDescription(rs.getString("description"));
                    medicament.setClasse(rs.getString("classe"));
                    medicament.setPrix(rs.getInt("prix"));
                    medicament.setImage(rs.getString("image"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du médicament : " + e.getMessage());
        }

        return medicament;
    }

    // Ajoute la méthode getAll qui appelle rechercher
    @Override
    public List<Medicaments> getAll() {
        return rechercher();  // Appel à la méthode rechercher pour récupérer tous les médicaments
    }
}
