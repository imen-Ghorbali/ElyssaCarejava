/*package tn.esprit.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.Role;
import tn.esprit.models.user;
import tn.esprit.services.service_user;import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class ListUser {
    @FXML
    private TableView<user> tableUser;

    @FXML
    private TableColumn<user, Integer> colId;

    @FXML
    private TableColumn<user, String> colName;

    @FXML
    private TableColumn<user, String> colEmail;

    @FXML
    private TableColumn<user, Role> colRole;

    private service_user su = new service_user();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role")); // ou getRoles si tu veux l'objet complet

        loadUsers();
    }

    private void loadUsers() {
        ObservableList<user> userList = FXCollections.observableArrayList(su.getAll());
        tableUser.setItems(userList);
    }
    public void exportToPDF() {
        Document document = new Document();
        try {
            // Créer le fichier PDF
            PdfWriter.getInstance(document, new FileOutputStream("users_list.pdf"));
            document.open();

            // Ajouter un titre au document
            document.add(new Phrase("Liste des Utilisateurs\n", new Font(Font.FontFamily.HELVETICA, 16)));

            // Créer une table avec 4 colonnes (ID, Nom, Email, Rôle)
            PdfPTable table = new PdfPTable(4);

            // Ajouter les en-têtes de colonnes
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Email");
            table.addCell("Role");

            // Remplir la table avec les utilisateurs
            List<user> users = su.getAll(); // Récupérer la liste des utilisateurs
            for (user u : users) {
                table.addCell(String.valueOf(u.getId())); // Ajouter l'ID
                table.addCell(u.getName()); // Ajouter le nom
                table.addCell(u.getEmail()); // Ajouter l'email
                table.addCell(u.getRole().toString()); // Ajouter le rôle (ou u.getRole().getName())
            }

            // Ajouter la table au document PDF
            document.add(table);

            // Fermer le document PDF
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleExportButtonAction() {
        exportToPDF(); // Appeler la méthode pour générer le PDF
    }

}
*/
/*package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import tn.esprit.models.Role;
import tn.esprit.models.user;
import tn.esprit.services.service_user;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.Role;
import tn.esprit.models.user;
import tn.esprit.services.service_user;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import java.io.File;

public class ListUser {

    @FXML
    private TableView<user> tableUser;
    @FXML
    private TableColumn<user, Integer> colId;
    @FXML
    private TableColumn<user, String> colName;
    @FXML
    private TableColumn<user, String> colEmail;
    @FXML
    private TableColumn<user, Role> colRole;
    @FXML
    private PieChart rolePieChart;
    @FXML
    private Label statsLabel;
    @FXML
    private VBox statsContainer;

    private service_user su = new service_user();

    @FXML
    public void initialize() {

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();
        showRoleStatistics();
    }

    private void loadUsers() {
        ObservableList<user> userList = FXCollections.observableArrayList(su.getAll());
        tableUser.setItems(userList);
    }

    private void showRoleStatistics() {
        List<user> users = su.getAll();
        int totalUsers = users.size();

        if (totalUsers == 0) {
            statsLabel.setText("Aucun utilisateur trouvé.");
            return;
        }

        // Compter le nombre d'utilisateurs par rôle
        Map<Role, Long> roleCounts = users.stream()
                .collect(Collectors.groupingBy(user::getRole, Collectors.counting()));

        // Calculer les pourcentages
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<Role, Long> entry : roleCounts.entrySet()) {
            Role role = entry.getKey();
            long count = entry.getValue();
            double percentage = (count * 100.0) / totalUsers;

            pieChartData.add(new PieChart.Data(
                    role.toString() + String.format(" (%.1f%%)", percentage),
                    count
            ));
        }

        // Configurer le PieChart
        rolePieChart.setData(pieChartData);
        rolePieChart.setTitle("Répartition des rôles");

        // Afficher les statistiques textuelles
        StringBuilder statsText = new StringBuilder("Statistiques des inscriptions:\n");
        for (Map.Entry<Role, Long> entry : roleCounts.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalUsers;
            statsText.append(String.format("- %s: %d utilisateurs (%.1f%%)\n",
                    entry.getKey(), entry.getValue(), percentage));
        }

        statsLabel.setText(statsText.toString());
    }

   /* public void exportToPDF() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("users_list.pdf"));
            document.open();

            // Ajouter un titre au document
            document.add(new Phrase("Liste des Utilisateurs\n\n", new Font(Font.FontFamily.HELVETICA, 16)));

            // Ajouter les statistiques
            List<user> users = su.getAll();
            int totalUsers = users.size();

            if (totalUsers > 0) {
                Map<Role, Long> roleCounts = users.stream()
                        .collect(Collectors.groupingBy(user::getRole, Collectors.counting()));

                StringBuilder stats = new StringBuilder("Statistiques des inscriptions:\n");
                for (Map.Entry<Role, Long> entry : roleCounts.entrySet()) {
                    double percentage = (entry.getValue() * 100.0) / totalUsers;
                    stats.append(String.format("- %s: %d utilisateurs (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage));
                }

                document.add(new Phrase(stats.toString() + "\n\n"));
            }

            // Créer une table avec 4 colonnes (ID, Nom, Email, Rôle)
            PdfPTable table = new PdfPTable(4);
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Email");
            table.addCell("Role");

            for (user u : users) {
                table.addCell(String.valueOf(u.getId()));
                table.addCell(u.getName());
                table.addCell(u.getEmail());
                table.addCell(u.getRole().toString());
            }

            document.add(table);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/
  /* public void exportToPDF() {
       Document document = new Document();
       try {
           String filePath = System.getProperty("user.home") + "/Documents/users_report.pdf";
           PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
           document.open();

           // Définition des couleurs
           BaseColor mainColor = new BaseColor(168, 216, 234);  // #a8d8ea
           BaseColor lightColor = new BaseColor(240, 248, 255); // Couleur claire alternative

           // En-tête principal
           Font headerMainFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, mainColor);
           Paragraph header = new Paragraph("Liste des Utilisateurs\n\n", headerMainFont);
           header.setAlignment(Element.ALIGN_CENTER);
           document.add(header);

           // Statistiques
           List<user> users = su.getAll();
           if (!users.isEmpty()) {
               Map<Role, Long> roleCounts = users.stream()
                       .collect(Collectors.groupingBy(user::getRole, Collectors.counting()));

               Font statsFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
               StringBuilder stats = new StringBuilder("Statistiques des inscriptions:\n\n");

               roleCounts.forEach((role, count) -> {
                   double percentage = (count * 100.0) / users.size();
                   stats.append(String.format("- %s: %d utilisateurs (%.1f%%)\n",
                           role, count, percentage));
               });

               document.add(new Paragraph(stats.toString()));
           }

           // En-têtes de tableau
           Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
           PdfPTable table = new PdfPTable(4);
           table.setWidthPercentage(100);
           table.setSpacingBefore(20f);

           // Ajout des en-têtes
           Stream.of( "Nom", "Email", "Rôle")
                   .forEach(headerText -> {
                       PdfPCell headerCell = new PdfPCell(new Phrase(headerText, tableHeaderFont));
                       headerCell.setBackgroundColor(mainColor);
                       headerCell.setPadding(8);
                       headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                       table.addCell(headerCell);
                   });

           // Contenu du tableau
           Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);
           boolean alternateRow = false;

           for (user u : users) {
               BaseColor rowColor = alternateRow ? lightColor : BaseColor.WHITE;

               addTableCell(table, String.valueOf(u.getId()), contentFont, rowColor);
               addTableCell(table, u.getName(), contentFont, rowColor);
               addTableCell(table, u.getEmail(), contentFont, rowColor);
               addTableCell(table, u.getRole().toString(), contentFont, rowColor);

               alternateRow = !alternateRow;
           }

           document.add(table);

           // Pied de page
           Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, mainColor);
           Paragraph footer = new Paragraph("\nGénéré le " + new java.util.Date(), footerFont);
           footer.setAlignment(Element.ALIGN_RIGHT);
           document.add(footer);

           document.close();
           openPDFFile(filePath);

       } catch (Exception e) {
           showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage(), Alert.AlertType.ERROR);
       }
   }

    private void addTableCell(PdfPTable table, String text, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBackgroundColor(backgroundColor);
        table.addCell(cell);
    }

    private void openPDFFile(String filePath) {
        try {
            if (Desktop.isDesktopSupported()) {
                File file = new File(filePath);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                }
            }
        } catch (Exception e) {
            showAlert("Information", "PDF généré avec succès à l'emplacement:\n" + filePath, Alert.AlertType.INFORMATION);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleExportButtonAction() {
        exportToPDF();
    }
}*/
package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import tn.esprit.models.Role;
import tn.esprit.models.user;
import tn.esprit.services.service_user;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListUser {
    @FXML private TableView<user> tableUser;
    @FXML private TableColumn<user, String> colName;
    @FXML private TableColumn<user, String> colEmail;
    @FXML private TableColumn<user, Role> colRole;
    @FXML private PieChart rolePieChart;
    @FXML private Label statsLabel;
    @FXML private ComboBox<Role> roleFilterComboBox;

    private service_user su = new service_user();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        roleFilterComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        roleFilterComboBox.getItems().add(0, null); // Ajouter une option "Tous les rôles"
        roleFilterComboBox.getSelectionModel().selectFirst();


        loadUsers();
        showRoleStatistics();
    }

    private void loadUsers() {
        ObservableList<user> userList = FXCollections.observableArrayList(su.getAll());
        tableUser.setItems(userList);
    }

    private void showRoleStatistics() {
        List<user> users = su.getAll();
        int totalUsers = users.size();

        if (totalUsers == 0) {
            statsLabel.setText("Aucun utilisateur trouvé.");
            return;
        }

        Map<Role, Long> roleCounts = users.stream()
                .collect(Collectors.groupingBy(user::getRole, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Role, Long> entry : roleCounts.entrySet()) {
            Role role = entry.getKey();
            long count = entry.getValue();
            double percentage = (count * 100.0) / totalUsers;
            pieChartData.add(new PieChart.Data(
                    role.toString() + String.format(" (%.1f%%)", percentage),
                    count
            ));
        }

        rolePieChart.setData(pieChartData);
        rolePieChart.setTitle("Répartition des rôles");

        StringBuilder statsText = new StringBuilder("Statistiques des inscriptions:\n");
        for (Map.Entry<Role, Long> entry : roleCounts.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalUsers;
            statsText.append(String.format("- %s: %d utilisateurs (%.1f%%)\n",
                    entry.getKey(), entry.getValue(), percentage));
        }

        statsLabel.setText(statsText.toString());
    }

    public void exportToPDF() {
        Document document = new Document();
        try {
            String filePath = System.getProperty("user.home") + "/Documents/users_report.pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Ajouter le logo
            try {
                Image logo = Image.getInstance(getClass().getResource("/images/logo.png"));
                logo.scaleToFit(80, 80);
                logo.setAbsolutePosition(document.right() - 100, document.top() - 40);
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Logo non trouvé, continuation sans logo");
            }

            // Style
            BaseColor mainColor = new BaseColor(168, 216, 234); // #a8d8ea
            BaseColor lightColor = new BaseColor(240, 248, 255); // AliceBlue

            // Titre
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, mainColor);
            Paragraph title = new Paragraph("Rapport des Utilisateurs\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Statistiques textuelles
            document.add(new Paragraph(statsLabel.getText() + "\n",
                    new Font(Font.FontFamily.HELVETICA, 12)));

            // Tableau sans ID (3 colonnes)
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(20f);

            // En-têtes
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
            String[] headers = {"Nom", "Email", "Rôle"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(mainColor);
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Données
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);
            boolean alternate = false;
            for (user u : su.getAll()) {
                addTableCell(table, u.getName(), contentFont, alternate ? lightColor : BaseColor.WHITE);
                addTableCell(table, u.getEmail(), contentFont, alternate ? lightColor : BaseColor.WHITE);
                addTableCell(table, u.getRole().toString(), contentFont, alternate ? lightColor : BaseColor.WHITE);
                alternate = !alternate;
            }
            document.add(table);

            // Pied de page
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, mainColor);
            Paragraph footer = new Paragraph("\nGénéré le " + new java.util.Date(), footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            openPDFFile(filePath);

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    @FXML
    private void filterByRole() {
        Role selectedRole = roleFilterComboBox.getValue();

        if (selectedRole == null) {
            // Si aucun rôle n'est sélectionné, afficher tous les utilisateurs
            loadUsers();
        } else {
            // Filtrer les utilisateurs par le rôle sélectionné
            ObservableList<user> filteredList = FXCollections.observableArrayList(
                    su.getAll().stream()
                            .filter(user -> user.getRole() == selectedRole)
                            .collect(Collectors.toList())
            );
            tableUser.setItems(filteredList);
        }

        // Mettre à jour les statistiques après le filtrage
        showRoleStatistics();
    }

    private void addTableCell(PdfPTable table, String text, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBackgroundColor(backgroundColor);
        table.addCell(cell);
    }

    private void openPDFFile(String filePath) {
        try {
            if (Desktop.isDesktopSupported()) {
                File file = new File(filePath);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                }
            }
        } catch (IOException e) {
            showAlert("Information", "PDF généré à: " + filePath, Alert.AlertType.INFORMATION);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleExportButtonAction() {
        exportToPDF();
    }
}
