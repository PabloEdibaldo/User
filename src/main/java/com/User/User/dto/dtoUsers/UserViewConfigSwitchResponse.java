package com.User.User.dto.dtoUsers;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserViewConfigSwitchResponse {
    private String idUser;
    private String name;
    private String password;
    private String modeConfigOnu;

}
