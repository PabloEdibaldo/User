package com.User.User.models.GeneralAdjustments;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "t_company")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigEm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameCompany;
    private String address;
    private String phone;
    private String idCompany;

    //-----------------Config basic
    private String gmailBackup;
    private String gmailSupport;
    private String gmailInvoice;
    @Lob
    private byte[] logo;


}
