package com.User.User.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "t_ContentBilling")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentBilling {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billingNtp_id")
    private Billing billingNtp;

}
