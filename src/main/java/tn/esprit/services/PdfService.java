package tn.esprit.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

public class PdfService {

    private static final BaseColor PRIMARY_COLOR = new BaseColor(0, 102, 204); // Bleu professionnel
    private static final BaseColor SECONDARY_COLOR = new BaseColor(241, 90, 34); // Orange (accent)
    private static final BaseColor LIGHT_GRAY = new BaseColor(240, 240, 240); // Fond

    public void genererPdf(String nomFichier, String contenuTexte, String eventImagePath) {
        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomFichier));
            document.open();

            // ========== EN-TÊTE AVEC LOGO ==========

            // 1. Logo (réduit)
            Image logo = Image.getInstance(getClass().getResource("/images/logo.png"));
            logo.scaleToFit(60, 60);  // Logo plus petit
            logo.setAlignment(Image.ALIGN_LEFT);

            // 2. Titre avec style
            Paragraph title = new Paragraph("Fiche Événement\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR));
            title.setAlignment(Element.ALIGN_CENTER);

            // 3. Date
            Paragraph date = new Paragraph("Généré le : " + java.time.LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY));
            date.setAlignment(Element.ALIGN_RIGHT);

            // Assemblage de l'en-tête
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.addCell(createImageCell(logo));
            headerTable.addCell(createTextCell(title, date));
            document.add(headerTable);

            // ========== CONTENU PRINCIPAL ==========

            // Séparateur visuel
            LineSeparator sep = new LineSeparator();
            sep.setLineColor(SECONDARY_COLOR);
            sep.setPercentage(80);
            document.add(sep);
            document.add(Chunk.NEWLINE);

            // Contenu formaté
            String[] lines = contenuTexte.split("\n");
            for (String line : lines) {
                if (line.startsWith("▶")) {
                    // Style pour les titres de section
                    Paragraph section = new Paragraph(line.substring(1).trim(),
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, PRIMARY_COLOR));
                    section.setSpacingBefore(15f);
                    document.add(section);
                } else if (line.contains(":")) {
                    // Style pour les paires clé-valeur
                    String[] parts = line.split(":", 2);
                    Paragraph keyValue = new Paragraph();
                    keyValue.add(new Chunk(parts[0].trim() + ": ",
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.DARK_GRAY)));
                    keyValue.add(new Chunk(parts[1].trim(),
                            FontFactory.getFont(FontFactory.HELVETICA, 11)));
                    keyValue.setSpacingBefore(5f);
                    document.add(keyValue);
                } else {
                    // Texte normal
                    document.add(new Paragraph(line,
                            FontFactory.getFont(FontFactory.HELVETICA, 11)));
                }
            }

            // ========== IMAGE DE L'ÉVÉNEMENT ==========

            if (eventImagePath != null && !eventImagePath.isEmpty()) {
                Image eventImage = Image.getInstance(eventImagePath);
                eventImage.scaleToFit(300, 300);  // Ajustez la taille
                eventImage.setAlignment(Image.ALIGN_CENTER);
                document.add(eventImage);
                document.add(Chunk.NEWLINE);
            }

            // ========== PIED DE PAGE ==========

            Paragraph footer = new Paragraph();
            footer.add(new Chunk("© 2023 VotreApplication | ",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY)));
            footer.add(new Chunk("www.votresite.com",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, SECONDARY_COLOR)));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20f);
            document.add(footer);

            document.close();

            // Ouvrir le PDF automatiquement
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(nomFichier));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PdfPCell createImageCell(Image img) throws DocumentException {
        PdfPCell cell = new PdfPCell(img, true);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPaddingLeft(10);
        return cell;
    }

    private PdfPCell createTextCell(Paragraph title, Paragraph date) throws DocumentException {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPaddingRight(10);
        cell.setPaddingTop(20);
        cell.addElement(title);
        cell.addElement(date);
        return cell;
    }
}

