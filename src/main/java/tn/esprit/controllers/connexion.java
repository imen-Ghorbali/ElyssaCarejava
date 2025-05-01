package tn.esprit.controllers;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import tn.esprit.services.service_user;
import tn.esprit.models.user;
import tn.esprit.utils.DataBase;
import tn.esprit.models.session;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.Node;

public class connexion {

    @FXML
    private Hyperlink forgotPasswordLink;
    @FXML
    private TextField tf_email;
    @FXML
    private PasswordField tf_password;
    @FXML
    private Button btn_login;
    @FXML
    private TextField captchaField;
    @FXML
    private ImageView captchaImageView;
    @FXML
    private Button refreshCaptchaButton;

    private service_user su = new service_user();
    private DefaultKaptcha captchaProducer;
    private String generatedCaptcha;

    @FXML
    public void initialize() {
        // Initialize CAPTCHA
        setupCaptcha();
        generateCaptcha();

        refreshCaptchaButton.setOnAction(event -> generateCaptcha());

        btn_login.setOnAction(event -> handleLogin(event));
    }

    private void setupCaptcha() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.textproducer.char.string", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "6");
        properties.setProperty("kaptcha.textproducer.font.size", "40");
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");

        Config config = new Config(properties);
        captchaProducer = new DefaultKaptcha();
        captchaProducer.setConfig(config);
    }

    private void generateCaptcha() {
        generatedCaptcha = captchaProducer.createText();
        BufferedImage bufferedImage = captchaProducer.createImage(generatedCaptcha);
        Image captchaImage = convertBufferedImageToFXImage(bufferedImage);
        captchaImageView.setImage(captchaImage);
    }

    private Image convertBufferedImageToFXImage(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            return new Image(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = tf_email.getText();
        String password = tf_password.getText();
        String enteredCaptcha = captchaField.getText();

        if (email.isEmpty() || password.isEmpty() || enteredCaptcha.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires.");
            return;
        }

        if (!enteredCaptcha.equalsIgnoreCase(generatedCaptcha)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "CAPTCHA incorrect !");
            generateCaptcha();
            return;
        }

        if (verifierLogin(email, password)) {
            try {
                user utilisateurConnecte = su.getByEmail(email);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/navbar.fxml"));
                Parent root = loader.load();

                // ✅ Récupérer le bon contrôleur
                NavbarController controller = loader.getController();
                controller.setUser(utilisateurConnecte);
                controller.setMainLayout((BorderPane) root); // Important pour attacher les actions

                session.setCurrentUser(utilisateurConnecte);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect.");
        }
    }

    private boolean verifierLogin(String email, String password) {
        Connection cnx = DataBase.getInstance().getCnx();
        String query = "SELECT password FROM user WHERE email = ?";

        try {
            PreparedStatement stmt = cnx.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String motDePasseHache = rs.getString("password");
                // Utilisation de BCrypt pour vérifier le mot de passe
                return BCrypt.checkpw(password, motDePasseHache);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscription.fxml"));
            Parent root = loader.load();
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        loadScene("/ForgotPassword.fxml", "Réinitialisation du mot de passe");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page.");
        }
    }
    @FXML
    private void refreshCaptcha(ActionEvent event) {
        generateCaptcha();
        System.out.println("Captcha refreshed!");  // Debugging

    }
}
