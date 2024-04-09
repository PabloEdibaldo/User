package com.User.User.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_Internet_package")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Internet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String lowSpeed;
    @Column(nullable = false)
    private String uploadSpeed;
    @Column(nullable = false)
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

    public Internet(String name, int price, String lowSpeed, String uploadSpeed, String description, String mora, int burstLimitLow, int burstLimitUpload, int burstThresholdLow, int burstThresholdUpload, int burstTimeLow, int burstLimitLow1, String queueTypeLow, String queueTypeUpload, String parent, String priority, String addressList) {
    }
}
