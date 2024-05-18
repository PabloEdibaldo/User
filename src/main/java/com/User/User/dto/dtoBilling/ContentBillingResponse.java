package com.User.User.dto.dtoBilling;

import com.User.User.models.Billing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentBillingResponse {

    private Long id;

    private String nameClient;
    private Long packageInternetId;
    private Long idBilling;
    private Long price;
    private String paymentType;
    private LocalDate billingInit;
    private LocalDate billingEnd;
    private LocalDate billingCreationBilling;
    private LocalDate billingCreateSystem;
    private String directionClient;
    private String gmailClient;
    private String packageInternetName;
    private boolean pay;
    private String typePay;
    private Long idClient;
    private String numberPhoneClient;
    private Billing billingNtp;
}
