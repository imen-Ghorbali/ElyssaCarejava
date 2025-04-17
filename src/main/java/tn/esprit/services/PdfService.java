package tn.esprit.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfService {

    public void genererPdf(String nomFichier, String contenu) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomFichier));
            document.open();
            document.add(new Paragraph("Détails de l'Événement"));
            document.add(new Paragraph(contenu));
            document.close();
            System.out.println("PDF généré avec succès : " + nomFichier);

            // Ouvre automatiquement le fichier
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(nomFichier));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
