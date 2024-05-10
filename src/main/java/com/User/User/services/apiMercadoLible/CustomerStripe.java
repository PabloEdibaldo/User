package com.User.User.services.apiMercadoLible;

import com.User.User.models.User;
import com.User.User.repository.UserRepository;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.InvoiceCreateParams;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerStripe {
    private final UserRepository userRepository;

    public void createClientStripe(Long userId) throws StripeException {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID"));
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getName())
                .setPhone(user.getPhoneNumber())
                .setEmail(user.getEmail())
                .build();
        Customer customer = Customer.create(params);

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setIdCustomerStripe(customer.getId());

            userRepository.save(existingUser);
        }
    }

    public void createBillingStripeClient(String customer,String subscription) throws StripeException {
        InvoiceCreateParams params = InvoiceCreateParams.builder()
                .setCustomer(customer)
                .setSubscription(subscription)
                .build();
        Invoice invoice = Invoice.create(params);
    }
}