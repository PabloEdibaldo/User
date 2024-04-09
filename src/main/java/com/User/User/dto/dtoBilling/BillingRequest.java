package com.User.User.dto.dtoBilling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingRequest {
    private Long id;

    private String type_service;
    private int payday;
    private int invoice_creation;
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

    //USer and Service IDs
    private Long userId;
    private Long serviceId;
    private Long promotion;
    private String creationDayTrue;


}
