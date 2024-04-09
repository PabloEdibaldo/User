package com.User.User.services;

import com.User.User.dto.dtoInvoiceServices.InvoiceServicesRequest;
import com.User.User.models.InvoiceServices;
import com.User.User.models.User;
import com.User.User.repository.InvoiceServicesRespository;
import com.User.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceServicesService {

    @Autowired
    private final InvoiceServicesRespository invoiceServicesRespository;
    private final UserRepository userRepository;

    public void createInvoiceService(@NotNull InvoiceServicesRequest invoiceServicesRequest){
        User user = userRepository.findById(invoiceServicesRequest.getIdUser()).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + invoiceServicesRequest.getIdUser()));
        InvoiceServices invoiceServices = InvoiceServices.builder()
                .issued(invoiceServicesRequest.getIssued())
                .expiration(invoiceServicesRequest.getExpiration())
                .status(invoiceServicesRequest.getStatus())
                .taxes(invoiceServicesRequest.getTaxes())
                .type(invoiceServicesRequest.getType())
                .pay(invoiceServicesRequest.getPay())
                .payDay(invoiceServicesRequest.getPayDay())
                .typePay(invoiceServicesRequest.getTypePay())
                .user(user)
                .build();

        invoiceServicesRespository.save(invoiceServices);
        log.info("creation the invoice service {} saved",invoiceServices.getId());
    }

}
