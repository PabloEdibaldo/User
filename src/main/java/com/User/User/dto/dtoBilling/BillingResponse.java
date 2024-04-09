package com.User.User.dto.dtoBilling;

import com.User.User.models.Promotion;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingResponse {
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

    private Long promotion;
    private String creationDayTrue;
}
