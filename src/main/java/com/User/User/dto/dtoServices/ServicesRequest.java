package com.User.User.dto.dtoServices;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicesRequest {
    private Long id;
    private String router;
    private Long idRouter;
    private Boolean firewall;
    private Long internetPackage_id;
    private String description;
    private Long price;
    private Long type_Ipv4;
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
