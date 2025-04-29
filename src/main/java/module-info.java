module projetWeb {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    requires jbcrypt;
    requires org.apache.poi.ooxml;
    requires itextpdf;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires javafx.web;
    // Ajouts pour JSON et Logging
    requires com.fasterxml.jackson.databind; // Pour manipuler JSON avec Jackson
    requires jdk.xml.dom;
    requires com.google.gson;
    requires jdk.jsobject;
    exports tn.esprit.models;
    exports tn.esprit.services;
    exports tn.esprit.interfaces;
    exports tn.esprit.controllers;
    requires okhttp3;
    requires java.net.http;


    // Ouvertures pour les packages
    opens tn.esprit to javafx.graphics;
    opens tn.esprit.controllers to javafx.fxml;
    opens tn.esprit.models to javafx.base;


}
