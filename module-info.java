module projetWeb {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    // Java standard
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires java.prefs;

    // Bibliothèques externes
    requires unirest.java;
    requires twilio;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.poi.ooxml;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires jbcrypt;
    requires itextpdf;
    requires jakarta.mail; // Keep this
    requires kaptcha;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires com.github.librepdf.openpdf;
    requires com.google.gson;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires org.json;
    requires com.calendarfx.view;
    requires jdk.jsobject;

    // Exports
    exports tn.esprit.models;
    exports tn.esprit.services;
    exports tn.esprit.interfaces;
    exports tn.esprit.controllers;
    exports tn.esprit.utils;

    // Opens pour JavaFX (FXML, etc.)
    opens tn.esprit to javafx.graphics;
    opens tn.esprit.controllers to javafx.fxml;
    opens tn.esprit.models to javafx.base;
    opens tn.esprit.services to javafx.fxml;
    opens tn.esprit.utils to javafx.fxml;
}