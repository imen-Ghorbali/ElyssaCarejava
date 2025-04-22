package tn.esprit.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class PdfService {

    public void genererPdf(String nomFichier, String contenuTexte) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50); // marges
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomFichier));
            document.open();

            // 📅 Date du jour
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph date = new Paragraph("Date : " + LocalDate.now(), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            // 🎫 Titre principal
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("🎫 Fiche Détail Événement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // 🔹 Ligne de séparation
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(BaseColor.LIGHT_GRAY);
            document.add(separator);

            // ✍️ Bloc de contenu
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            String[] lignes = contenuTexte.split("\n");
            for (String ligne : lignes) {
                if (ligne.contains("▶")) {
                    String[] parts = ligne.split("▶");
                    if (parts.length > 1) {
                        String[] data = parts[1].split(":", 2);
                        if (data.length == 2) {
                            Chunk label = new Chunk(data[0].trim() + " : ", labelFont);
                            Chunk value = new Chunk(data[1].trim() + "\n", valueFont);
                            Paragraph paragraph = new Paragraph();
                            paragraph.add(label);
                            paragraph.add(value);
                            paragraph.setSpacingBefore(5f);
                            document.add(paragraph);
                        } else {
                            document.add(new Paragraph(parts[1], valueFont));
                        }
                    }
                } else {
                    document.add(new Paragraph(ligne, valueFont));
                }
            }

            // 🔻 Espace final
            document.add(new Paragraph("\n\n"));

            // ✅ Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, BaseColor.DARK_GRAY);
            Paragraph footer = new Paragraph("Merci pour votre confiance.\nCentre Médical Santé+ | www.santeplus.tn", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20f);
            document.add(footer);

            document.close();
            System.out.println("PDF généré avec succès : " + nomFichier);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(nomFichier));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
