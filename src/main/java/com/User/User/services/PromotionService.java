package com.User.User.services;

import com.User.User.dto.dtoPromotions.PromotionRequest;
import com.User.User.dto.dtoPromotions.PromotionResponse;
import com.User.User.models.Promotion;
import com.User.User.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    @Autowired
    private final PromotionRepository promotionRepository;


    public List<PromotionResponse> getAllPromotions(){
        List<Promotion> promotions =promotionRepository.findAll();
        return promotions.stream().map(this::mapToPromotionsResponse).toList();
    }

    private PromotionResponse mapToPromotionsResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .description(promotion.getDescription())
                .promotionalMonths(promotion.getPromotionalMonths())
                .timePromotion(promotion.getTimePromotion())
                .max_limit(promotion.getMax_limit())
                .idRouter(promotion.getIdRouter())

                .build();
    }

    public void createPromotion(PromotionRequest promotionRequest){
        Promotion promotion = Promotion.builder()
                .description(promotionRequest.getDescription())
                .promotionalMonths(promotionRequest.getPromotionalMonths())
                .timePromotion(promotionRequest.getTimePromotion())
                .max_limit(promotionRequest.getMax_limit())
                .idRouter(promotionRequest.getIdRouter())

                .build();

        //connectionMtrService.createPromotion(promotion);
        promotionRepository.save(promotion);
        log.info("Promotion {} in saved", promotion.getId());
    }

    public void updatePromotion(Long id, PromotionRequest promotionRequest){
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);

        if (optionalPromotion.isPresent()){
            Promotion existingPromotion = optionalPromotion.get();

            existingPromotion.setDescription(promotionRequest.getDescription());
            existingPromotion.setPromotionalMonths(promotionRequest.getPromotionalMonths());
            existingPromotion.setTimePromotion(promotionRequest.getTimePromotion());
            existingPromotion.setMax_limit(promotionRequest.getMax_limit());
            existingPromotion.setIdRouter(promotionRequest.getIdRouter());

            promotionRepository.save(existingPromotion);
            log.info("Promotion {}  updated",id);
        }else {
            log.warn("Promotion with ID {} not found", id);
        }
    }
    public void deletePromotion(Long id){
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);
        if(optionalPromotion.isPresent()){
            promotionRepository.deleteById(id);
        }else{
            log.warn("Promotion with ID {} not found", id);
        }
    }


}
