package project.service.amineservice;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.logging.Logger;

public class StripeService {

    private static final String STRIPE_SECRET_KEY = "sk_test_51QxwVZQB3eKD8sMl8r6Plsbjgg2XXCosLZcpyZG41CtMy2lqtlUZVzlC8QxdR9raJ0LKNyyUOXsiA1YabRjEcFe000MLEmS0V0";
    private static final Logger LOGGER = Logger.getLogger(StripeService.class.getName());
    private static final String SUCCESS_URL = "http://localhost:8000/payment/success"; // Remplacez par votre URL de succès
    private static final String CANCEL_URL = "http://localhost:8000/payment/cancel";   // Remplacez par votre URL d'annulation

    public StripeService() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    public String createCheckoutSession(float amount, String currency, String description) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount((long) (amount * 100)) // Amount in cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(description)
                                                                .build())
                                                .build())
                                .setQuantity(1L)
                                .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    // La méthode createCharge avec token n'est plus nécessaire pour cette approche.
}