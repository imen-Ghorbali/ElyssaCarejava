package tn.esprit.services;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailService {

    private String from = "hamed.nisrine@esprit.tn"; // ton email
    private String password = "gupi izoy gblh opkj"; // mot de passe d'application (si Gmail)

    public void sendSponsorNotification(String recipientEmail, String sponsorName, String eventTitle, String eventDate, String eventLocation) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Nouvel événement associé");

            String content = "<h3>Bonjour " + sponsorName + " !</h3>"
                    + "<p>Vous avez été ajouté à un nouvel événement :</p>"
                    + "<ul>"
                    + "<li><strong>Titre :</strong> " + eventTitle + "</li>"
                    + "<li><strong>Date :</strong> " + eventDate + "</li>"
                    + "<li><strong>Lieu :</strong> " + eventLocation + "</li>"
                    + "</ul>"
                    + "<p>Merci pour votre soutien !</p>";

            message.setContent(content, "text/html");

            // Ajouter une ligne pour s'assurer que l'email est bien envoyé
            System.out.println("Envoi de l'email à : " + recipientEmail);

            Transport.send(message);
            System.out.println("Email envoyé avec succès au sponsor.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de l'email.");
        }
    }
}
