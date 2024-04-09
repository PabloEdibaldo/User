package com.User.User.dto.dtoInternet;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternetResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String lowSpeed;
    private String uploadSpeed;
    private String description;
    private String mora;

    private int burstLimitLow;
    private int burstLimitUpload;

    private int burstThresholdLow;
    private int burstThresholdUpload;

    private int burstTimeLow;
    private int burstTimeUpload;

    private String QueueTypeLow;
    private String QueueTypeUpload;

    private String parent;
    private String priority;
    private String addressList;

    private String link;
}
