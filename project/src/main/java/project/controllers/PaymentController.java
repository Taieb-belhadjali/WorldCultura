package project.controllers;

import com.stripe.exception.StripeException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import project.models.aminemodels.Reservation;
import project.service.amineservice.StripeService;

public class PaymentController {

    @FXML
    private WebView paymentWebView;

    public void loadPaymentView(Reservation reservation, StripeService stripeService) {
        if (reservation != null && reservation.getRehla() != null) {
            float price = reservation.getRehla().getPrice();
            String currency = "eur"; // Or get it dynamically
            String description = "Paiement pour la réservation ID: " + reservation.getId() + " (" + reservation.getRehla().getDepart() + " à " + reservation.getRehla().getDestination() + ")";

            try {
                String checkoutUrl = stripeService.createCheckoutSession(price, currency, description);
                WebEngine webEngine = paymentWebView.getEngine();
                webEngine.load(checkoutUrl);
            } catch (StripeException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de paiement");
                alert.setHeaderText("Impossible de démarrer le paiement.");
                alert.setContentText("Erreur Stripe : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}