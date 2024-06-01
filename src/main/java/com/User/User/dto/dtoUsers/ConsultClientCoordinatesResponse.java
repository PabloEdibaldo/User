package com.User.User.dto.dtoUsers;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultClientCoordinatesResponse {
    private String nameClient;
    private String coordinatesX;
    private String coordinatesY;
    private String packageInternet;
    private String location;
    private String ip;
    private String phoneNumber;
}
