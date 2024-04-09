package com.User.User.services.apiMercadoLible;

import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Component
public class Webhook {
    public ResponseEntity<String> handleWebhookEvent(@RequestBody String payload, String sigHeader) {
        Event event;
        try {
            String endpointSecret = "whsec_5e7c1d9a261322955f60b18d2f92247cffd125a0ec49a81960ef27324df2c6fe";
            event = com.stripe.net.Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (JsonSyntaxException e) {
            // Invalid signature
            log.info("Invalid payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (SignatureVerificationException e) {
            log.info("Invalid signature");
            throw new RuntimeException(e);
        }


        //PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = ((EventDataObjectDeserializer) dataObjectDeserializer).getObject().orElse(null);

        if (stripeObject == null) {
            // Handle deserialization failure
            log.error("Deserialization failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                log.info("Succeeded: " + paymentIntent.getId());
                log.info("El cliente pagó el valor OXXO antes del vencimiento.-->" +
                        "Entrega de los bienes o servicios que el cliente compró.");

                break;
            case "payment_intent.payment_failed":
                PaymentIntent failedPaymentIntent = (PaymentIntent) stripeObject;
                log.info("Failed: " + failedPaymentIntent.getId());
                log.info("El cliente no pagó el valor OXXO antes del vencimiento.-->" +
                        "Contacta al cliente por correo electrónico o envía una notificación push y solicita otro método de pago.");
                break;
            case "charge.succeeded":
                Charge charge = (Charge) stripeObject;
                log.info("Charge succeeded: " + charge.getId());
                break;
            case "payment_intent.requires_action":
                PaymentIntent requires_action = (PaymentIntent) stripeObject;
                log.info("El vale OXXO se creó correctamente." +
                        "Espera a que el cliente pague el vale OXXO.");
                break;
            case "payment_intent.processing":
                log.info("El cliente ya no puede pagar el vale OXXO.-->" +
                        "Espera hasta saber si el pago se concreta o no.");
                break;
            default:
                // Handle other event types
                log.info("Unhandled event type: " + event.getType());
                break;
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
