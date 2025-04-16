package tn.esprit.services;

import tn.esprit.models.Medicaments;
import tn.esprit.models.MaterielMedical;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentService implements IMedicamentService<Medicaments> {
    private final Connection connection = DataBase.getInstance().getConnection();

    @Override
    public void ajouter(Medicaments medicament) {
        String query = "INSERT INTO medicaments (nom, description, classe, prix, image, materiel_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getDescription());
            ps.setString(3, medicament.getClasse());
            ps.setInt(4, medicament.getPrix());
            ps.setString(5, medicament.getImage());

            if (medicament.getMaterielRequis() != null) {
                ps.setLong(6, medicament.getMaterielRequis().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medicament.setId(generatedKeys.getInt(1));
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
        String req = "UPDATE medicaments SET nom=?, description=?, classe=?, prix=?, image=?, materiel_id=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getDescription());
            ps.setString(3, medicament.getClasse());
            ps.setInt(4, medicament.getPrix());
            ps.setString(5, medicament.getImage());

            if (medicament.getMaterielRequis() != null) {
                ps.setLong(6, medicament.getMaterielRequis().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setLong(7, medicament.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Médicament mis à jour avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification: " + e.getMessage());
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
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @Override
    public List<Medicaments> getAll() {
        return getAllWithMateriel();
    }

    public List<Medicaments> getAllWithMateriel() {
        List<Medicaments> medicaments = new ArrayList<>();
        String query = "SELECT m.*, mm.id as mm_id, mm.nom as mm_nom, mm.description as mm_desc, " +
                "mm.prix as mm_prix, mm.statut as mm_statut, mm.image as mm_image " +
                "FROM medicaments m LEFT JOIN materiel_medical mm ON m.materiel_id = mm.id";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Medicaments m = createMedicamentFromResultSet(rs);

                if (rs.getObject("materiel_id") != null) {
                    MaterielMedical materiel = new MaterielMedical();
                    materiel.setId(rs.getLong("mm_id"));
                    materiel.setNom(rs.getString("mm_nom"));
                    materiel.setDescription(rs.getString("mm_desc"));
                    materiel.setPrix(rs.getDouble("mm_prix"));
                    materiel.setStatut(rs.getString("mm_statut"));
                    materiel.setImage(rs.getString("mm_image"));
                    m.setMaterielRequis(materiel);
                }

                medicaments.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération avec matériel: " + e.getMessage());
        }

        return medicaments;
    }

    private Medicaments createMedicamentFromResultSet(ResultSet rs) throws SQLException {
        Medicaments m = new Medicaments();
        m.setId(rs.getInt("id"));
        m.setNom(rs.getString("nom"));
        m.setDescription(rs.getString("description"));
        m.setClasse(rs.getString("classe"));
        m.setPrix(rs.getInt("prix"));
        m.setImage(rs.getString("image"));
        return m;
    }

    @Override
    public Medicaments rechercherParId(long id) {
        String req = "SELECT m.*, mm.id as mm_id, mm.nom as mm_nom, mm.description as mm_desc, " +
                "mm.prix as mm_prix, mm.statut as mm_statut, mm.image as mm_image " +
                "FROM medicaments m LEFT JOIN materiel_medical mm ON m.materiel_id = mm.id " +
                "WHERE m.id=?";
        Medicaments medicament = null;

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    medicament = createMedicamentFromResultSet(rs);

                    if (rs.getObject("materiel_id") != null) {
                        MaterielMedical materiel = new MaterielMedical();
                        materiel.setId(rs.getLong("mm_id"));
                        materiel.setNom(rs.getString("mm_nom"));
                        materiel.setDescription(rs.getString("mm_desc"));
                        materiel.setPrix(rs.getDouble("mm_prix"));
                        materiel.setStatut(rs.getString("mm_statut"));
                        materiel.setImage(rs.getString("mm_image"));
                        medicament.setMaterielRequis(materiel);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }

        return medicament;
    }

    @Override
    public List<Medicaments> rechercher() {
        return getAll();
    }
}