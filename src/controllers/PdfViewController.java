package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;  // Import crucial pour WebView
import javafx.stage.Stage;

public class PdfViewController {

    @FXML
    private WebView pdfWebView;

    public void loadPdf(String filePath) {
        pdfWebView.getEngine().load(filePath);
    }

    @FXML
    private void handleZoomIn() {
        pdfWebView.setZoom(pdfWebView.getZoom() + 0.1);
    }

    @FXML
    private void handleZoomOut() {
        pdfWebView.setZoom(Math.max(0.5, pdfWebView.getZoom() - 0.1));
    }

    @FXML
    private void handleFullscreen() {
        Stage stage = (Stage) pdfWebView.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
}