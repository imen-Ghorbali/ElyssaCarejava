module projetWeb {requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    requires jbcrypt;
    requires org.apache.poi.ooxml;
    requires itextpdf;
    exports tn.esprit.models;
    exports tn.esprit.services;
    exports tn.esprit.interfaces;
    exports tn.esprit.controllers;
    requires java.mail;
    requires kaptcha;
    requires com.calendarfx.view;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires javafx.swing;
    requires java.prefs;
    // pour WebView

    requires java.net.http;


    // 🔥 AJOUTE CECI pour corriger ton problème
    opens tn.esprit to javafx.graphics;
    opens tn.esprit.controllers to javafx.fxml;
    opens tn.esprit.models to javafx.base;
    exports tn.esprit.utils;
    opens tn.esprit.utils to javafx.fxml;
}