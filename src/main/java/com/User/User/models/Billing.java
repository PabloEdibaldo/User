package com.User.User.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "t_billing_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type_service;
    private int payday;
    private int  invoice_creation;
    private int taxes;
    private int cutoff_date;
    private int days_of_tolerance;
    private Double mora;
    private Double reconnection;
    private LocalDate creationDay;

    //taxes
    private int taxes1;
    private int taxes2;
    private int taxes3;
    private Long promotion;
    private String creationDayTrue;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id",nullable = false)
    private Servers service;

    @PrePersist
    protected void onCreate(){
        creationDay = LocalDate.now();
    }
    @OneToMany(mappedBy = "billingNtp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContentBilling> contentBillings = new ArrayList<>();
}
