package tn.esprit;

import tn.esprit.models.Medicaments;
import tn.esprit.models.MaterielMedical;
import tn.esprit.services.MedicamentService;
import tn.esprit.services.MaterielMedicalService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MedicamentService serviceMedicament = new MedicamentService();
        MaterielMedicalService serviceMateriel = new MaterielMedicalService();

        while (true) {
            System.out.println("===== MENU PRINCIPAL =====");
            System.out.println("1. Gérer les médicaments");
            System.out.println("2. Gérer les matériels médicaux");
            System.out.println("3. Quitter");
            System.out.print("Choisissez une option : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le saut de ligne

            switch (choix) {
                case 1:
                    menuMedicament(scanner, serviceMedicament, serviceMateriel);
                    break;
                case 2:
                    menuMateriel(scanner, serviceMateriel);
                    break;
                case 3:
                    System.out.println("Au revoir !");
                    return;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    public static void menuMedicament(Scanner scanner, MedicamentService serviceMedicament, MaterielMedicalService serviceMateriel) {
        while (true) {
            System.out.println("===== MENU MÉDICAMENT =====");
            System.out.println("1. Ajouter un médicament");
            System.out.println("2. Afficher les médicaments");
            System.out.println("3. Modifier un médicament");
            System.out.println("4. Supprimer un médicament");
            System.out.println("5. Retour au menu principal");
            System.out.print("Choisissez une option : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le saut de ligne

            try {
                switch (choix) {
                    case 1:
                        System.out.print("Nom : ");
                        String nom = scanner.nextLine();
                        System.out.print("Description : ");
                        String description = scanner.nextLine();
                        System.out.print("Classe : ");
                        String classe = scanner.nextLine();
                        System.out.print("Prix : ");
                        int prix = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Image : ");
                        String image = scanner.nextLine();

                        // Afficher la liste des matériels disponibles
                        System.out.println("Matériels disponibles :");
                        List<MaterielMedical> materiels = serviceMateriel.rechercher();
                        for (MaterielMedical m : materiels) {
                            System.out.println(m.getId() + " - " + m.getNom());
                        }
                        System.out.print("ID du matériel requis (0 si aucun) : ");
                        int idMateriel = scanner.nextInt();
                        scanner.nextLine();

                        MaterielMedical materielRequis = idMateriel == 0 ? null :
                                materiels.stream()
                                        .filter(m -> m.getId() == idMateriel)
                                        .findFirst()
                                        .orElse(null);

                        Medicaments medicament = new Medicaments(nom, description, classe, prix, image, materielRequis);
                        serviceMedicament.ajouter(medicament);
                        break;

                    case 2:
                        List<Medicaments> medicaments = serviceMedicament.getAll();
                        for (Medicaments m : medicaments) {
                            System.out.println(m);
                        }
                        break;

                    case 3:
                        System.out.print("ID du médicament à modifier : ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouveau nom : ");
                        String newNom = scanner.nextLine();
                        System.out.print("Nouvelle description : ");
                        String newDesc = scanner.nextLine();
                        System.out.print("Nouvelle classe : ");
                        String newClasse = scanner.nextLine();
                        System.out.print("Nouveau prix : ");
                        int newPrix = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouvelle image : ");
                        String newImage = scanner.nextLine();

                        // Gestion du matériel
                        System.out.println("Matériels disponibles :");
                        List<MaterielMedical> allMateriels = serviceMateriel.rechercher();
                        for (MaterielMedical m : allMateriels) {
                            System.out.println(m.getId() + " - " + m.getNom());
                        }
                        System.out.print("Nouvel ID du matériel requis (0 si aucun) : ");
                        int newIdMateriel = scanner.nextInt();
                        scanner.nextLine();

                        MaterielMedical newMaterielRequis = newIdMateriel == 0 ? null :
                                allMateriels.stream()
                                        .filter(m -> m.getId() == newIdMateriel)
                                        .findFirst()
                                        .orElse(null);

                        // Création d'un objet temporaire pour la modification
                        Medicaments medicamentToUpdate = new Medicaments();
                        medicamentToUpdate.setId(id);
                        medicamentToUpdate.setNom(newNom);
                        medicamentToUpdate.setDescription(newDesc);
                        medicamentToUpdate.setClasse(newClasse);
                        medicamentToUpdate.setPrix(newPrix);
                        medicamentToUpdate.setImage(newImage);
                        medicamentToUpdate.setMaterielRequis(newMaterielRequis);

                        serviceMedicament.modifier(medicamentToUpdate);
                        break;

                    case 4:
                        System.out.print("ID du médicament à supprimer : ");
                        int idSupprimer = scanner.nextInt();
                        scanner.nextLine();
                        serviceMedicament.supprimer(idSupprimer);
                        break;

                    case 5:
                        return; // Retour au menu principal
                    default:
                        System.out.println("Option invalide.");
                }
            } catch (Exception e) {  // Utilisation de Exception générique pour capturer d'autres erreurs
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    public static void menuMateriel(Scanner scanner, MaterielMedicalService serviceMateriel) {
        while (true) {
            System.out.println("===== MENU MATÉRIEL MÉDICAL =====");
            System.out.println("1. Ajouter un matériel médical");
            System.out.println("2. Afficher les matériels médicaux");
            System.out.println("3. Modifier un matériel médical");
            System.out.println("4. Supprimer un matériel médical");
            System.out.println("5. Retour au menu principal");
            System.out.print("Choisissez une option : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le saut de ligne

            try {
                switch (choix) {
                    case 1:
                        System.out.print("Nom : ");
                        String nom = scanner.nextLine();
                        System.out.print("Description : ");
                        String description = scanner.nextLine();
                        System.out.print("Prix : ");
                        double prix = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Statut : ");
                        String statut = scanner.nextLine();

                        MaterielMedical materiel = new MaterielMedical(nom, description, prix, statut);
                        serviceMateriel.ajouter(materiel);
                        break;

                    case 2:
                        List<MaterielMedical> materiels = serviceMateriel.getAll();
                        for (MaterielMedical m : materiels) {
                            System.out.println(m);
                        }
                        break;

                    case 3:
                        System.out.print("ID du matériel à modifier : ");
                        int idMateriel = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouveau nom : ");
                        String newNom = scanner.nextLine();
                        System.out.print("Nouvelle description : ");
                        String newDesc = scanner.nextLine();
                        System.out.print("Nouveau prix : ");
                        double newPrix = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Nouveau statut : ");
                        String newStatut = scanner.nextLine();

                        MaterielMedical materielToUpdate = new MaterielMedical();
                        materielToUpdate.setId(idMateriel);
                        materielToUpdate.setNom(newNom);
                        materielToUpdate.setDescription(newDesc);
                        materielToUpdate.setPrix(newPrix);
                        materielToUpdate.setStatut(newStatut);

                        serviceMateriel.modifier(materielToUpdate);
                        break;

                    case 4:
                        System.out.print("ID du matériel à supprimer : ");
                        int idSupprimer = scanner.nextInt();
                        scanner.nextLine();
                        serviceMateriel.supprimer(idSupprimer);
                        break;

                    case 5:
                        return; // Retour au menu principal
                    default:
                        System.out.println("Option invalide.");
                }
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }
}
