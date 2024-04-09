package com.User.User.dto.dtoPromotions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    private String description;
    private Integer promotionalMonths;
    private int timePromotion;
    private String max_limit;
    private Long idRouter;

}
