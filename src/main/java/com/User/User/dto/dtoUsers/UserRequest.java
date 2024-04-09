package com.User.User.dto.dtoUsers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Long id;

    private String name;
    private String direction;
    private String location;
    private String phoneNumber;
    private String mobilePhoneNumber;
    private String email;
}
