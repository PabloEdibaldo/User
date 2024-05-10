package com.User.User.dto.dtoUsers;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MacUserAssignRequest {
    private String mac;
    private Long idService;
    private String nameClient;
}