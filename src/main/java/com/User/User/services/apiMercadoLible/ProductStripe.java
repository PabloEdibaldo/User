package com.User.User.services.apiMercadoLible;

import com.User.User.models.Internet;
import com.User.User.models.User;
import com.User.User.repository.InternetRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductStripe {
    private final InternetRepository internetRepository;
    public void createProduct(@NotNull Internet internet) throws StripeException {

        Long value = (long) internet.getPrice();


        PriceCreateParams params =
                PriceCreateParams.builder()
                        .setCurrency("MXN")
                        .setUnitAmount(value)
                        .setProductData(
                                PriceCreateParams.ProductData.builder()
                                        .setName(internet.getName())
                                        .setId(internet.getId().toString()).build()
                        ).build();
        Price price = Price.create(params);


        PaymentLinkCreateParams paramsLink =
                PaymentLinkCreateParams.builder()
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem.builder()
                                        .setPrice(price.getId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .addPaymentMethodType(PaymentLinkCreateParams.PaymentMethodType.OXXO)
                        .addPaymentMethodType(PaymentLinkCreateParams.PaymentMethodType.CARD)
                        .build();
        PaymentLink paymentLink = PaymentLink.create(paramsLink);

        Optional<Internet> optionalUser = internetRepository.findById(internet.getId());

        if (optionalUser.isPresent()) {
            Internet existingInternet = optionalUser.get();
            existingInternet.setLink(paymentLink.getUrl());

            internetRepository.save(existingInternet);
        }
    }
}
