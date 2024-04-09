package com.User.User.dto.dtoServices;

import com.User.User.dto.dtoServices.dtoNetworkAccessPoint.NetworkAccessPointRequest;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicesResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String router;
    private Long idRouter;
    private Boolean firewall;
    private String description;
    private Long internetPackage_id;
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
