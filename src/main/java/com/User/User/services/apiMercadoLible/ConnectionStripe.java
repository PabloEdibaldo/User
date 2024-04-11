package com.User.User.services.apiMercadoLible;
import com.User.User.models.Internet;
import com.User.User.models.User;
import com.User.User.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionStripe {






    public void PaymentCreate(Customer customer) throws StripeException {

        CustomerSessionCreateParams params = CustomerSessionCreateParams.builder()
                        .setCustomer(customer.getId())
                        .setComponents(
                                CustomerSessionCreateParams.Components.builder()
                                        .setBuyButton(
                                                CustomerSessionCreateParams.Components.BuyButton.builder()
                                                        .setEnabled(true)
                                                        .build()
                                        )

                                        .build()
                        )
                        .build();

        CustomerSession customerSession = CustomerSession.create(params);

    }
}














































