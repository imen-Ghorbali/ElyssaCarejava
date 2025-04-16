/*package tn.esprit;

import models.Blog;
import services.ServiceBlog;

import java.util.List;
import java.util.Scanner;

public class tn.esprit.Main {
    public static void main(String[] args) {
        ServiceBlog service = new ServiceBlog();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== MENU BLOG ===");
            System.out.println("1. Ajouter un blog");
            System.out.println("2. Modifier un blog");
            System.out.println("3. Supprimer un blog");
            System.out.println("4. Afficher un blog par ID");
            System.out.println("5. Afficher tous les blogs");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // flush newline

            switch (choice) {
                case 1 -> {
                    System.out.println("== Ajouter un blog ==");
                    Blog blog = lireBlog(scanner, false);
                    if (blog != null) service.add(blog);
                }
                case 2 -> {
                    System.out.print("ID du blog à modifier : ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    Blog blog = lireBlog(scanner, true);
                    if (blog != null) {
                        blog.setId(id);
                        service.edit(blog);
                    }
                }
                case 3 -> {
                    System.out.print("ID du blog à supprimer : ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    service.delete(id);
                }
                case 4 -> {
                    System.out.print("ID du blog à afficher : ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    Blog blog = service.getById(id);
                    if (blog != null) {
                        System.out.println(blog);
                    } else {
                        System.out.println("❌ Blog introuvable.");
                    }
                }
                case 5 -> {
                    List<Blog> blogs = service.getAll();
                    for (Blog b : blogs) {
                        System.out.println(b);
                    }
                }
                case 0 -> {
                    running = false;
                    System.out.println("Au revoir !");
                }
                default -> System.out.println("Choix invalide.");
            }
        }

        scanner.close();
    }

    // Contrôle de saisie pour Blog
    private static Blog lireBlog(Scanner scanner, boolean isUpdate) {
        System.out.print("Titre : ");
        String titre = scanner.nextLine();
        if (titre.isEmpty()) {
            System.out.println("❌ Le titre est obligatoire.");
            return null;
        }

        System.out.print("Contenu : ");
        String contenu = scanner.nextLine();
        if (contenu.isEmpty()) {
            System.out.println("❌ Le contenu est obligatoire.");
            return null;
        }

        System.out.print("Date de publication (ex: 2025-04-10) : ");
        String date = scanner.nextLine();
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("❌ Format de date invalide.");
            return null;
        }

        System.out.print("Auteur : ");
        String auteur = scanner.nextLine();
        if (auteur.isEmpty()) {
            System.out.println("❌ L'auteur est obligatoire.");
            return null;
        }

        System.out.print("Image (ex: image.jpg) : ");
        String image = scanner.nextLine();
        if (image.isEmpty()) {
            System.out.println("❌ L'image est obligatoire.");
            return null;
        }

        return new Blog(0, titre, contenu, date, auteur, image);
    }


import tn.esprit.models.Commentaire;
import tn.esprit.services.ServiceCommentaire;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ServiceCommentaire service = new ServiceCommentaire();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== MENU COMMENTAIRE ===");
            System.out.println("1. Ajouter un commentaire");
            System.out.println("2. Modifier un commentaire");
            System.out.println("3. Supprimer un commentaire");
            System.out.println("4. Afficher un commentaire par ID");
            System.out.println("5. Afficher tous les commentaires");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // vider le buffer

            switch (choice) {
                case 1 -> {
                    Commentaire c = lireCommentaire(scanner, false);
                    if (c != null) service.add(c);
                }
                case 2 -> {
                    System.out.print("ID du commentaire à modifier : ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    Commentaire c = lireCommentaire(scanner, true);
                    if (c != null) {
                        c.setId(id);
                        service.edit(c);
                    }
                }
                case 3 -> {
                    System.out.print("ID du commentaire à supprimer : ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    service.delete(id);
                }
                case 4 -> {
                    System.out.print("ID du commentaire à afficher : ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    Commentaire c = service.getById(id);
                    if (c != null) System.out.println(c);
                    else System.out.println("❌ Commentaire introuvable.");
                }
                case 5 -> {
                    List<Commentaire> list = service.getAll();
                    for (Commentaire c : list) {
                        System.out.println(c);
                    }
                }
                case 0 -> {
                    running = false;
                    System.out.println("👋 Fin du programme !");
                }
                default -> System.out.println("❌ Choix invalide.");
            }
        }

        scanner.close();
    }

    private static Commentaire lireCommentaire(Scanner scanner, boolean isUpdate) {
        System.out.print("Nom utilisateur : ");
        String nom = scanner.nextLine();
        if (nom.isEmpty()) {
            System.out.println("❌ Le nom est obligatoire.");
            return null;
        }

        System.out.print("Date (ex: 2025-04-10) : ");
        String date = scanner.nextLine();
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("❌ Format de date invalide.");
            return null;
        }

        System.out.print("Contenu : ");
        String contenu = scanner.nextLine();
        if (contenu.isEmpty()) {
            System.out.println("❌ Le contenu est obligatoire.");
            return null;
        }

        System.out.print("Nombre de likes : ");
        int likes = scanner.nextInt();
        scanner.nextLine();
        if (likes < 0) {
            System.out.println("❌ Le nombre de likes doit être >= 0.");
            return null;
        }

        System.out.print("ID du blog associé : ");
        int blogId = scanner.nextInt();
        scanner.nextLine();

        return new Commentaire(0, blogId, likes, nom, date, contenu);
    }
}*/



