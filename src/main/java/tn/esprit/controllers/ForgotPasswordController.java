package tn.esprit.controllers;
import java.time.LocalTime;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.services.service_user;
import tn.esprit.utils.EmailService;
import tn.esprit.models.*;
import javafx.application.Platform;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.time.LocalDateTime;

/*public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField verificationCodeField;
    @FXML private PasswordField newPasswordField; // Utilisation de PasswordField pour plus de sécurité
    @FXML private Label statusLabel;
    @FXML private Hyperlink backToLogin;

    private final service_user userService = new service_user();
    private final EmailService emailService = new EmailService();
    private String generatedCode;
    private String userEmail;

    @FXML
    private void handleSendCode() {
        userEmail = emailField.getText().trim();

        // Validation de l'email
        if (userEmail.isEmpty()) {
            showStatus("Veuillez entrer votre adresse email", "red");
            return;
        }

        if (!userService.emailExists(userEmail)) {
            showStatus("Cette adresse email n'est pas enregistrée", "red");
            return;
        }

        try {
            // Génération et envoi du code
            generatedCode = emailService.generateVerificationCode();
            String subject = "Réinitialisation de mot de passe - " + LocalDateTime.now().getHour() + "h" + LocalDateTime.now().getMinute();
            String body = "Votre code de vérification est : " + generatedCode +
                    "\n\nCe code est valide 10 minutes." +
                    "\n\nSi vous n'avez pas demandé cette réinitialisation, ignorez cet email.";

            emailService.sendEmail(userEmail, subject, body);

            showStatus("Code envoyé! Vérifiez votre boîte mail.", "green");
            verificationCodeField.setDisable(false);
            newPasswordField.setDisable(false);

        } catch (Exception e) {
            showStatus("Échec d'envoi: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

   /* @FXML
    private void handleVerifyCodeAndChangePassword() {
        String enteredCode = verificationCodeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        // Validation des champs
        if (enteredCode.isEmpty() || newPassword.isEmpty()) {
            showStatus("Tous les champs sont obligatoires", "red");
            return;
        }

        if (!enteredCode.equals(generatedCode)) {
            showStatus("Code de vérification incorrect", "red");
            return;
        }

        if (newPassword.length() < 8) {
            showStatus("Le mot de passe doit contenir au moins 8 caractères", "red");
            return;
        }

        try {
            // Mise à jour du mot de passe
            userService.updatePassword(userEmail, newPassword);
            showStatus("Mot de passe réinitialisé avec succès!", "green");

            // Retour automatique à la page de connexion après 3 secondes
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> handleBackToLogin());
                        }
                    },
                    3000
            );

        } catch (Exception e) {
            showStatus("Erreur lors de la mise à jour du mot de passe", "red");
            e.printStackTrace();
        }
    }*/


   /* @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/connexion.fxml"));
            AnchorPane loginPage = loader.load();

            Stage stage = (Stage) backToLogin.getScene().getWindow();
            stage.setScene(new Scene(loginPage));
            stage.setTitle("Connexion");

        } catch (IOException e) {
            showStatus("Erreur lors du chargement de la page", "red");
            e.printStackTrace();
        }
    }

    private void showStatus(String message, String color) {
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
        statusLabel.setText(message);
    }

    // Méthode d'initialisation (optionnelle)
    @FXML
    private void initialize() {
        verificationCodeField.setDisable(true);
        newPasswordField.setDisable(true);
    }
}*/




public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField verificationCodeField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;
    @FXML private Hyperlink backToLogin;
    @FXML private Button submitNewPasswordButton; // Ajout de la référence au bouton

    private final service_user userService = new service_user();
    private final EmailService emailService = new EmailService();
    private String generatedCode;
    private String userEmail;

    @FXML
    private void initialize() {
        verificationCodeField.setDisable(true);
        newPasswordField.setDisable(true);
        submitNewPasswordButton.setDisable(true); // Désactivation initiale

        // Activation du bouton quand les champs sont remplis
        verificationCodeField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
    }

    private void checkFields() {
        boolean fieldsValid = !verificationCodeField.getText().isEmpty()
                && !newPasswordField.getText().isEmpty();
        submitNewPasswordButton.setDisable(!fieldsValid);
    }

    @FXML
    private void handleSendCode() {
        userEmail = emailField.getText().trim();

        if (userEmail.isEmpty()) {
            showStatus("Veuillez entrer votre email", "red");
            return;
        }

        if (!userService.emailExists(userEmail)) {
            showStatus("Email non enregistré", "red");
            return;
        }

        try {
            generatedCode = emailService.generateVerificationCode();
            String subject = "Code de réinitialisation";
            String body = "Votre code est : " + generatedCode;

            emailService.sendEmail(userEmail, subject, body);
            showStatus("Code envoyé ! Vérifiez vos emails", "green");

            verificationCodeField.setDisable(false);
            newPasswordField.setDisable(false);

        } catch (Exception e) {
            showStatus("Échec d'envoi : " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    /*@FXML
    private void handleVerifyCodeAndChangePassword() {
        String enteredCode = verificationCodeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (enteredCode.isEmpty() || newPassword.isEmpty()) {
            showStatus("Tous les champs sont requis", "red");
            return;
        }

        if (!enteredCode.equals(generatedCode)) {
            showStatus("Code de vérification incorrect", "red");
            return;
        }

        if (newPassword.length() < 8) {
            showStatus("Le mot de passe doit contenir au moins 8 caractères", "red");
            return;
        }

        try {
            // Mise à jour du mot de passe
            userService.updatePassword(userEmail, newPassword);
            showStatus("Mot de passe réinitialisé avec succès!", "green");

            // Redirection après 2 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(this::redirectToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showStatus("Erreur lors de la réinitialisation: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }*/
    @FXML
    private void handleVerifyCodeAndChangePassword() {
        String enteredCode = verificationCodeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        // Validation des champs
        if (enteredCode.isEmpty() || newPassword.isEmpty()) {
            showStatus("Tous les champs sont requis", "red");
            return;
        }

        if (!enteredCode.equals(generatedCode)) {
            showStatus("Code de vérification incorrect", "red");
            return;
        }

        if (newPassword.length() < 8) {
            showStatus("Le mot de passe doit contenir au moins 8 caractères", "red");
            return;
        }

        try {
            // Récupère l'utilisateur avant la mise à jour
            user user = userService.getByEmail(userEmail);
            if (user == null) {
                showStatus("Utilisateur introuvable", "red");
                return;
            }

            // Met à jour le mot de passe
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            userService.update(user); // Utilise la méthode update complète

            showStatus("Mot de passe réinitialisé avec succès!", "green");

            // Redirection après 2 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(this::redirectToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showStatus("Erreur lors de la réinitialisation: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleBackToLogin() {
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/connexion.fxml"));
            Stage stage = (Stage) backToLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showStatus("Erreur lors du chargement de la page de connexion", "red");
            e.printStackTrace();
        }
    }

    private void showStatus(String message, String color) {
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
        statusLabel.setText(message);
    }
}