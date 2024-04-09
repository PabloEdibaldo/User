package com.User.User.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name="t_Services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Servers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String router;
    private Long idRouter;
    private Boolean firewall;
    @ManyToOne
    @JoinColumn(name = "internet_package_id", nullable = false)
    private Internet internetPackage;
    private String description;
    private int price;
    private String type_Ipv4;
    private String mac;
    private String ppp_hs;
    private String password;
    private Long caja_nap;
    private int port_nap;
    private String direction;
    private LocalDate time_Installation;
    private String connection;
    private String ip_admin;
    private String type_antenna;



}
