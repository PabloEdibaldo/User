package com.User.User.services.apiMercadoLible;
import com.User.User.models.Internet;
import com.User.User.models.User;
import com.User.User.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionStripe {
    private final UserRepository userRepository;

    public void createClientStripe(Long userId) throws StripeException {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID"));
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getName())
                .setPhone(user.getPhoneNumber())
                .setEmail(user.getEmail())
                .build();
        Customer customer = Customer.create(params);

    }

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
    }
}