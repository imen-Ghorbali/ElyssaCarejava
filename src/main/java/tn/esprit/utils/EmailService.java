package tn.esprit.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/*public class EmailService {

    private final String fromEmail = "ton.email@esprit.tn"; // Remplacez par l'email de l'expéditeur
    private final String password = "motdepasseapplication"; // Remplacez par votre mot de passe d'application

    public void sendEmail(String toEmail, String subject, String body) {
        // Paramètres SMTP de Mailtrap
        String host = "sandbox.smtp.mailtrap.io";
        String port = "2525"; // Utiliser le port 2525 recommandé par Mailtrap
        String username = "1885e1c60324ec"; // Remplacez par votre nom d'utilisateur Mailtrap
        String password = "****ad70"; // Remplacez par votre mot de passe Mailtrap

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Utiliser STARTTLS
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.debug", "true"); // Pour voir les logs dans la console

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès à " + toEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String generateVerificationCode() {
        int verificationCode = (int) (Math.random() * 900000) + 100000; // Code 6 chiffres
        return String.valueOf(verificationCode);
    }
}*/



import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class EmailService {
    private final String fromEmail = "nourrajhi33@gmail.com"; // Votre email Gmail
    private final String appPassword = "nhibuvkjsagzuzsg"; // Mot de passe 16 caractères sans espaces

    public void sendEmail(String toEmail, String subject, String body) {
        // 1. Configuration SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Configuration de sécurité critique
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // 2. Création de la session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            // 3. Construction du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // 4. Envoi avec gestion des erreurs
            Transport.send(message);
            System.out.println("[SUCCÈS] Email envoyé à " + toEmail);

        } catch (MessagingException e) {
            handleEmailError(e);
        }
    }

    private void handleEmailError(MessagingException e) {
        System.err.println("[ERREUR] Échec d'envoi :");
        e.printStackTrace();

        // Suggestions spécifiques selon l'erreur
        if (e.getMessage().contains("535")) {
            System.err.println("Solution : Vérifiez le mot de passe d'application");
        } else if (e.getMessage().contains("Could not connect")) {
            System.err.println("Solution : Vérifiez votre connexion Internet/firewall");
        }
    }

    public String generateVerificationCode() {
        // Génère un code à 6 chiffres
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

}