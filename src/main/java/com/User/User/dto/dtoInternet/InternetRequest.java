package com.User.User.dto.dtoInternet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternetRequest {

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

