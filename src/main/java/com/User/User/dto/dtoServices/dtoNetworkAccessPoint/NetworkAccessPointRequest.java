package com.User.User.dto.dtoServices.dtoNetworkAccessPoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NetworkAccessPointRequest {
    private Long id;
    private String name;
    private String coordinates;
    private String location;
    private int ports;
    private String details;

}
