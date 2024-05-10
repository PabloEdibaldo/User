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
//------------------------------------
    private Long idClient;
    private String nameClient;
    private String direction;

    private String password;
    ///-------------------------------
    private Long box;
    private int port;
    private String nameRouter;
    private String ip;
    private String namePackage;

    private Long idService;

    private Long promotion;
    private String creationDayTrue;
}
