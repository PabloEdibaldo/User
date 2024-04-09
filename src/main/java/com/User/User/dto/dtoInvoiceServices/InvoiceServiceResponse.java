package com.User.User.dto.dtoInvoiceServices;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceServiceResponse {
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
