package tn.esprit;

import tn.esprit.models.sponsor;
import tn.esprit.services.ServiceSponsor;

public class Main {
    public static void main(String[] args) {
        // Créer un objet de ServiceSponsor
        ServiceSponsor serviceSponsor = new ServiceSponsor();

        // === 1. Ajouter un sponsor ===
        System.out.println("=== Ajout d'un nouveau sponsor ===");
        sponsor newSponsor = new sponsor(
                3, // ID auto-généré par la base de données (0 ou valeur non valide)
                5000, // Prix
                "Sponsor A", // Nom
                "Sponsor principal de l'événement", // Description
                "Type A" // Type
        );
        serviceSponsor.add(newSponsor);

        // === 2. Modifier un sponsor ===
        System.out.println("=== Modification du sponsor avec ID 1 ===");
        sponsor updatedSponsor = new sponsor(
                1, // ID existant
                7500, // Nouveau prix
                "Sponsor A Modifié", // Nouveau nom
                "Description modifiée du sponsor", // Nouvelle description
                "Type B" // Nouveau type
        );
        serviceSponsor.edit(updatedSponsor);

        // === 3. Récupérer un sponsor par ID ===
        System.out.println("=== Affichage du sponsor avec ID 1 ===");
        sponsor retrievedSponsor = serviceSponsor.getById(1);
        if (retrievedSponsor != null) {
            System.out.println("ID: " + retrievedSponsor.getId());
            System.out.println("Nom: " + retrievedSponsor.getName());
            System.out.println("Description: " + retrievedSponsor.getDescription());
            System.out.println("Prix: " + retrievedSponsor.getPrix());
            System.out.println("Type: " + retrievedSponsor.getType());
        } else {
            System.out.println("Aucun sponsor trouvé avec cet ID.");
        }

        // === 4. Afficher tous les sponsors ===
        System.out.println("\n=== Liste de tous les sponsors ===");
        var sponsors = serviceSponsor.getAll();
        if (sponsors.isEmpty()) {
            System.out.println("Aucun sponsor trouvé.");
        } else {
            for (sponsor s : sponsors) {
                System.out.println("ID: " + s.getId() +
                        " | Nom: " + s.getName() +
                        " | Prix: " + s.getPrix() +
                        " | Type: " + s.getType());
            }
        }


    }
}
