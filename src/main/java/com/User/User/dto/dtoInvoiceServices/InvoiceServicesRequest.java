package com.User.User.dto.dtoInvoiceServices;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceServicesRequest {
    private LocalDate issued;
    private LocalDate expiration;
    private String status;
    private String sum;
    private String taxes;
    private String type;
    private String pay;
    private LocalDate payDay;
    private String typePay;
    private Long idUser;

}
