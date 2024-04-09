package com.User.User.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class  Promotion{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false,columnDefinition = "INT DEFAULT 0")
    private Integer promotionalMonths;
    //-------------------------------------------------------------
    private int timePromotion;
    private String max_limit;
    //-------------------------------------------------------------
    private Long idRouter;


}
