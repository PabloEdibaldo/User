package com.User.User.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "t_InvoiceServices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceServices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate issued;
    private LocalDate expiration;
    private String status;
    private String sum;
    private String taxes;
    private String type;
    private String pay;
    private LocalDate payDay;
    private String typePay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
}

