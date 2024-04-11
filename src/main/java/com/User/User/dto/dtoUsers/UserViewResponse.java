package com.User.User.dto.dtoUsers;

import com.User.User.models.Servers;

import lombok.*;

import java.time.LocalDate;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserViewResponse {
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

    private String nameClient;
    private String direction;
    private Servers service;
    //taxes
    private int taxes1;
    private int taxes2;
    private int taxes3;

    private Long promotion;
    private String creationDayTrue;
}
