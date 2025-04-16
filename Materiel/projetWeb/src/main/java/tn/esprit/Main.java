package tn.esprit;

import tn.esprit.models.Medicaments;
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MedicamentService;
import tn.esprit.services.MaterielMedicalService;
import tn.esprit.utils.DataBase;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize database connection
        DataBase database = DataBase.getInstance();
        MedicamentService medicamentService = new MedicamentService();
        MaterielMedicalService materielService = new MaterielMedicalService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Système de Gestion Médicale ===");
            System.out.println("1. Gestion des Médicaments");
            System.out.println("2. Gestion du Matériel Médical");
            System.out.println("3. Quitter");
            System.out.print("Votre choix: ");

            int mainChoice = getValidIntInput(scanner, 1, 3);

            switch (mainChoice) {
                case 1:
                    gestionMedicaments(scanner, medicamentService);
                    break;
                case 2:
                    gestionMaterielMedical(scanner, materielService);
                    break;
                case 3:
                    database.closeConnection();
                    System.out.println("Au revoir!");
                    System.exit(0);
            }
        }
    }

    private static void gestionMedicaments(Scanner scanner, MedicamentService service) {
        while (true) {
            System.out.println("\n=== Gestion des Médicaments ===");
            System.out.println("1. Ajouter un médicament");
            System.out.println("2. Lister tous les médicaments");
            System.out.println("3. Modifier un médicament");
            System.out.println("4. Supprimer un médicament");
            System.out.println("5. Rechercher un médicament par ID");
            System.out.println("6. Retour au menu principal");
            System.out.print("Votre choix: ");

            int choix = getValidIntInput(scanner, 1, 6);

            switch (choix) {
                case 1:
                    ajouterMedicament(scanner, service);
                    break;
                case 2:
                    listerMedicaments(service);
                    break;
                case 3:
                    modifierMedicament(scanner, service);
                    break;
                case 4:
                    supprimerMedicament(scanner, service);
                    break;
                case 5:
                    rechercherMedicamentParId(scanner, service);
                    break;
                case 6:
                    return;
            }
        }
    }

    private static void gestionMaterielMedical(Scanner scanner, MaterielMedicalService service) {
        while (true) {
            System.out.println("\n=== Gestion du Matériel Médical ===");
            System.out.println("1. Ajouter un matériel");
            System.out.println("2. Lister tous les matériels");
            System.out.println("3. Modifier un matériel");
            System.out.println("4. Supprimer un matériel");
            System.out.println("5. Rechercher un matériel par ID");
            System.out.println("6. Retour au menu principal");
            System.out.print("Votre choix: ");

            int choix = getValidIntInput(scanner, 1, 6);

            switch (choix) {
                case 1:
                    ajouterMateriel(scanner, service);
                    break;
                case 2:
                    listerMateriels(service);
                    break;
                case 3:
                    modifierMateriel(scanner, service);
                    break;
                case 4:
                    supprimerMateriel(scanner, service);
                    break;
                case 5:
                    rechercherMaterielParId(scanner, service);
                    break;
                case 6:
                    return;
            }
        }
    }

    // Medicament methods
    private static void ajouterMedicament(Scanner scanner, MedicamentService service) {
        System.out.println("\n=== Nouveau Médicament ===");

        String nom = getValidStringInput(scanner, "Nom: ", 3, 255, false);
        String description = getValidStringInput(scanner, "Description: ", 10, 255, false);
        String classe = getValidStringInput(scanner, "Classe thérapeutique: ", 3, 255, false);
        int prix = getValidIntInput(scanner, "Prix: ", 1, Integer.MAX_VALUE);
        String image = getValidStringInput(scanner, "Image (optionnel): ", 0, 255, true);

        Medicaments med = new Medicaments(nom, description, classe, prix);
        med.setImage(image.isEmpty() ? null : image);

        service.ajouter(med);
        System.out.println("Médicament ajouté avec succès!");
    }

    private static void listerMedicaments(MedicamentService service) {
        System.out.println("\n=== Liste des Médicaments ===");
        List<Medicaments> medicaments = service.rechercher();

        if (medicaments.isEmpty()) {
            System.out.println("Aucun médicament trouvé.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-20s %-10s %-20s%n",
                    "ID", "Nom", "Description", "Classe", "Prix", "Image");
            System.out.println("----------------------------------------------------------------------------------------");
            for (Medicaments m : medicaments) {
                System.out.printf("%-5d %-20s %-30s %-20s %-10d %-20s%n",
                        m.getId(),
                        m.getNom(),
                        m.getDescription(),
                        m.getClasse(),
                        m.getPrix(),
                        m.getImage() != null ? m.getImage() : "N/A");
            }
        }
    }

    private static void modifierMedicament(Scanner scanner, MedicamentService service) {
        System.out.println("\n=== Modification de Médicament ===");
        listerMedicaments(service);

        int id = getValidIntInput(scanner, "ID du médicament à modifier: ", 1, Integer.MAX_VALUE);
        Medicaments existing = service.rechercherParId(id);

        if (existing == null) {
            System.out.println("Aucun médicament trouvé avec cet ID!");
            return;
        }

        System.out.println("\nLaissez vide pour conserver la valeur actuelle");
        System.out.println("Valeur actuelle: " + existing.getNom());
        String nom = getValidStringInput(scanner, "Nouveau nom: ", 3, 255, true);

        System.out.println("Valeur actuelle: " + existing.getDescription());
        String description = getValidStringInput(scanner, "Nouvelle description: ", 10, 255, true);

        System.out.println("Valeur actuelle: " + existing.getClasse());
        String classe = getValidStringInput(scanner, "Nouvelle classe thérapeutique: ", 3, 255, true);

        System.out.println("Valeur actuelle: " + existing.getPrix());
        Integer prix = getValidIntInputOptional(scanner, "Nouveau prix: ", 1, Integer.MAX_VALUE);

        System.out.println("Valeur actuelle: " + (existing.getImage() != null ? existing.getImage() : "N/A"));
        String image = getValidStringInput(scanner, "Nouvelle image (optionnel): ", 0, 255, true);

        // Update only changed fields
        if (!nom.isEmpty()) existing.setNom(nom);
        if (!description.isEmpty()) existing.setDescription(description);
        if (!classe.isEmpty()) existing.setClasse(classe);
        if (prix != null) existing.setPrix(prix);
        existing.setImage(image.isEmpty() ? existing.getImage() : (image.equals("null") ? null : image));

        service.modifier(existing);
        System.out.println("Médicament modifié avec succès!");
    }

    private static void supprimerMedicament(Scanner scanner, MedicamentService service) {
        System.out.println("\n=== Suppression de Médicament ===");
        listerMedicaments(service);

        int id = getValidIntInput(scanner, "ID du médicament à supprimer: ", 1, Integer.MAX_VALUE);

        System.out.print("Êtes-vous sûr de vouloir supprimer ce médicament? (O/N): ");
        String confirmation = scanner.nextLine().trim().toUpperCase();

        if (confirmation.equals("O") || confirmation.equals("OUI")) {
            service.supprimer(id);
            System.out.println("Médicament supprimé avec succès!");
        } else {
            System.out.println("Suppression annulée.");
        }
    }

    private static void rechercherMedicamentParId(Scanner scanner, MedicamentService service) {
        System.out.println("\n=== Recherche de Médicament par ID ===");
        int id = getValidIntInput(scanner, "ID du médicament: ", 1, Integer.MAX_VALUE);

        Medicaments med = service.rechercherParId(id);

        if (med != null) {
            System.out.println("\nDétails du médicament:");
            System.out.println("ID: " + med.getId());
            System.out.println("Nom: " + med.getNom());
            System.out.println("Description: " + med.getDescription());
            System.out.println("Classe thérapeutique: " + med.getClasse());
            System.out.println("Prix: " + med.getPrix());
            System.out.println("Image: " + (med.getImage() != null ? med.getImage() : "N/A"));
        } else {
            System.out.println("Aucun médicament trouvé avec cet ID!");
        }
    }

    // MaterielMedical methods
    private static void ajouterMateriel(Scanner scanner, MaterielMedicalService service) {
        System.out.println("\n=== Nouveau Matériel Médical ===");

        String nom = getValidStringInput(scanner, "Nom: ", 3, 255, false);
        String description = getValidStringInput(scanner, "Description: ", 10, 255, false);
        double prix = getValidDoubleInput(scanner, "Prix: ", 0, Double.MAX_VALUE);
        String statut = getValidStringInput(scanner, "Statut: ", 3, 255, false);
        String image = getValidStringInput(scanner, "Image (optionnel): ", 0, 255, true);

        MaterielMedical materiel = new MaterielMedical(nom, description, prix, statut);
        materiel.setImage(image.isEmpty() ? null : image);

        service.ajouter(materiel);
        System.out.println("Matériel médical ajouté avec succès!");
    }

    private static void listerMateriels(MaterielMedicalService service) {
        System.out.println("\n=== Liste des Matériels Médicaux ===");
        List<MaterielMedical> materiels = service.rechercher();

        if (materiels.isEmpty()) {
            System.out.println("Aucun matériel trouvé.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-10s %-15s %-20s%n",
                    "ID", "Nom", "Description", "Prix", "Statut", "Image");
            System.out.println("----------------------------------------------------------------------------------------");
            for (MaterielMedical m : materiels) {
                System.out.printf("%-5d %-20s %-30s %-10.2f %-15s %-20s%n",
                        m.getId(),
                        m.getNom(),
                        m.getDescription(),
                        m.getPrix(),
                        m.getStatut(),
                        m.getImage() != null ? m.getImage() : "N/A");
            }
        }
    }

    private static void modifierMateriel(Scanner scanner, MaterielMedicalService service) {
        System.out.println("\n=== Modification de Matériel Médical ===");
        listerMateriels(service);

        long id = getValidLongInput(scanner, "ID du matériel à modifier: ", 1, Long.MAX_VALUE);
        MaterielMedical existing = service.rechercherParId(id);

        if (existing == null) {
            System.out.println("Aucun matériel trouvé avec cet ID!");
            return;
        }

        System.out.println("\nLaissez vide pour conserver la valeur actuelle");
        System.out.println("Valeur actuelle: " + existing.getNom());
        String nom = getValidStringInput(scanner, "Nouveau nom: ", 3, 255, true);

        System.out.println("Valeur actuelle: " + existing.getDescription());
        String description = getValidStringInput(scanner, "Nouvelle description: ", 10, 255, true);

        System.out.println("Valeur actuelle: " + existing.getPrix());
        Double prix = getValidDoubleInputOptional(scanner, "Nouveau prix: ", 0, Double.MAX_VALUE);

        System.out.println("Valeur actuelle: " + existing.getStatut());
        String statut = getValidStringInput(scanner, "Nouveau statut: ", 3, 255, true);

        System.out.println("Valeur actuelle: " + (existing.getImage() != null ? existing.getImage() : "N/A"));
        String image = getValidStringInput(scanner, "Nouvelle image (optionnel): ", 0, 255, true);

        // Update only changed fields
        if (!nom.isEmpty()) existing.setNom(nom);
        if (!description.isEmpty()) existing.setDescription(description);
        if (prix != null) existing.setPrix(prix);
        if (!statut.isEmpty()) existing.setStatut(statut);
        existing.setImage(image.isEmpty() ? existing.getImage() : (image.equals("null") ? null : image));

        service.modifier(existing);
        System.out.println("Matériel médical modifié avec succès!");
    }

    private static void supprimerMateriel(Scanner scanner, MaterielMedicalService service) {
        System.out.println("\n=== Suppression de Matériel Médical ===");
        listerMateriels(service);

        long id = getValidLongInput(scanner, "ID du matériel à supprimer: ", 1, Long.MAX_VALUE);

        System.out.print("Êtes-vous sûr de vouloir supprimer ce matériel? (O/N): ");
        String confirmation = scanner.nextLine().trim().toUpperCase();

        if (confirmation.equals("O") || confirmation.equals("OUI")) {
            service.supprimer(id);
            System.out.println("Matériel médical supprimé avec succès!");
        } else {
            System.out.println("Suppression annulée.");
        }
    }

    private static void rechercherMaterielParId(Scanner scanner, MaterielMedicalService service) {
        System.out.println("\n=== Recherche de Matériel Médical par ID ===");
        long id = getValidLongInput(scanner, "ID du matériel: ", 1, Long.MAX_VALUE);

        MaterielMedical materiel = service.rechercherParId(id);

        if (materiel != null) {
            System.out.println("\nDétails du matériel médical:");
            System.out.println("ID: " + materiel.getId());
            System.out.println("Nom: " + materiel.getNom());
            System.out.println("Description: " + materiel.getDescription());
            System.out.println("Prix: " + materiel.getPrix());
            System.out.println("Statut: " + materiel.getStatut());
            System.out.println("Image: " + (materiel.getImage() != null ? materiel.getImage() : "N/A"));
        } else {
            System.out.println("Aucun matériel trouvé avec cet ID!");
        }
    }

    // Validation methods
    private static String getValidStringInput(Scanner scanner, String prompt, int minLength, int maxLength, boolean optional) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (optional && input.isEmpty()) {
                return input;
            }

            if (input.isEmpty()) {
                System.out.println("Ce champ est obligatoire!");
            } else if (input.length() < minLength) {
                System.out.printf("Doit contenir au moins %d caractères!%n", minLength);
            } else if (input.length() > maxLength) {
                System.out.printf("Doit contenir au maximum %d caractères!%n", maxLength);
            }
        } while (input.isEmpty() || input.length() < minLength || input.length() > maxLength);

        return input;
    }

    private static int getValidIntInput(Scanner scanner, String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(scanner.nextLine());
                if (value < min || value > max) {
                    System.out.printf("Doit être entre %d et %d!%n", min, max);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide!");
            }
        }
        return value;
    }

    private static int getValidIntInput(Scanner scanner, int min, int max) {
        return getValidIntInput(scanner, "", min, max);
    }

    private static Integer getValidIntInputOptional(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.printf("Doit être entre %d et %d!%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide ou laisser vide!");
            }
        }
    }

    private static long getValidLongInput(Scanner scanner, String prompt, long min, long max) {
        long value;
        System.out.print(prompt);
        while (true) {
            try {
                value = Long.parseLong(scanner.nextLine());
                if (value < min || value > max) {
                    System.out.printf("Doit être entre %d et %d! Réessayez: ", min, max);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.print("Veuillez entrer un nombre valide: ");
            }
        }
        return value;
    }

    private static double getValidDoubleInput(Scanner scanner, String prompt, double min, double max) {
        double value;
        System.out.print(prompt);
        while (true) {
            try {
                value = Double.parseDouble(scanner.nextLine());
                if (value < min || value > max) {
                    System.out.printf("Doit être entre %.2f et %.2f! Réessayez: ", min, max);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.print("Veuillez entrer un nombre valide: ");
            }
        }
        return value;
    }

    private static Double getValidDoubleInputOptional(Scanner scanner, String prompt, double min, double max) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        try {
            double value = Double.parseDouble(input);
            if (value < min || value > max) {
                System.out.printf("Doit être entre %.2f et %.2f!%n", min, max);
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un nombre valide ou laisser vide!");
            return null;
        }
    }
}