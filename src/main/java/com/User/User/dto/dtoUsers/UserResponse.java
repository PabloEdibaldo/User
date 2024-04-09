package com.User.User.dto.dtoUsers;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.*;
import org.springframework.data.annotation.Id;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String direction;
    private String location;
    private String phoneNumber;
    private String mobilePhoneNumber;
    private String email;
}
