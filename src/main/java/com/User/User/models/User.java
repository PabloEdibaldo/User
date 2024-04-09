package com.User.User.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "t_users_customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    private String direction;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String mobilePhoneNumber;
    @Column(nullable = false)
    private String email;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Billing> invoice;


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<InvoiceServices> invoiceServices;
}
