package com.User.User.services;

import com.User.User.dto.dtoInternet.InternetRequest;
import com.User.User.dto.dtoInternet.InternetResponse;

import com.User.User.models.Internet;
import com.User.User.repository.InternetRepository;

import com.User.User.services.apiMercadoLible.ConnectionStripe;
import com.stripe.exception.StripeException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j

public class InternetServices {
    @Autowired
    private final InternetRepository internetRepository;
    private final ConnectionMtrService connectionMtrService;
 private final ConnectionStripe connectionStripe;
    public List<InternetResponse> getAllInternet(){
        List<Internet> packagesInternet = internetRepository.findAll();
        return packagesInternet.stream().map(this::mapToPackagesResponse).toList();
    }
    private InternetResponse mapToPackagesResponse(@NonNull Internet internet) {
        return  InternetResponse.builder()
                .id(internet.getId())
                .name(internet.getName())
                .price(internet.getPrice())
                .lowSpeed(internet.getLowSpeed())
                .uploadSpeed(internet.getUploadSpeed())
                .description(internet.getDescription())



                .mora(internet.getMora())
                .burstLimitLow(internet.getBurstLimitLow())
                .burstLimitUpload(internet.getBurstLimitUpload())
                .burstThresholdLow(internet.getBurstThresholdLow())
                .burstThresholdUpload(internet.getBurstThresholdUpload())
                .burstTimeLow(internet.getBurstTimeLow())
                .burstTimeUpload(internet.getBurstTimeUpload())
                .QueueTypeLow(internet.getQueueTypeLow())
                .QueueTypeUpload(internet.getQueueTypeUpload())
                .parent(internet.getParent())
                .priority(internet.getPriority())
                .addressList(internet.getAddressList())
                .link(internet.getLink())
                .build();
    }

    public void createPackageInternet(@NonNull InternetRequest internetRequest) throws StripeException {
        Mono<Boolean> responseCreateProfileService = connectionMtrService.createProfilePPP(internetRequest.getName(),internetRequest.getLowSpeed(),internetRequest.getUploadSpeed());
        Boolean responsePostProfilePPP = responseCreateProfileService.block();

        if(Boolean.FALSE.equals(responsePostProfilePPP)){
            Internet internet = Internet.builder()
                    .name(internetRequest.getName())
                    .price(internetRequest.getPrice())
                    .lowSpeed(internetRequest.getLowSpeed())
                    .uploadSpeed(internetRequest.getUploadSpeed())
                    .description(internetRequest.getDescription())
                    .mora(internetRequest.getMora())
                    .burstLimitLow(internetRequest.getBurstLimitLow())
                    .burstLimitUpload(internetRequest.getBurstLimitUpload())
                    .burstThresholdLow(internetRequest.getBurstThresholdLow())
                    .burstThresholdUpload(internetRequest.getBurstThresholdUpload())
                    .burstTimeLow(internetRequest.getBurstTimeLow())
                    .burstTimeUpload(internetRequest.getBurstTimeUpload())
                    .QueueTypeLow(internetRequest.getQueueTypeLow())
                    .QueueTypeUpload(internetRequest.getQueueTypeUpload())
                    .parent(internetRequest.getParent())
                    .priority(internetRequest.getPriority())
                    .addressList(internetRequest.getAddressList())
                    .link(internetRequest.getLink())
                    .build();
            internetRepository.save(internet);
            connectionStripe.createProduct(internet);
            log.info("Package {} in saved",internet.getId());

        }


    }
    public void updateInternet(Long id, InternetRequest internetRequest){
        Optional<Internet> optionalInternet = internetRepository.findById(id);

        if (optionalInternet.isPresent()){
            Internet existingInternet = optionalInternet.get();
            existingInternet.setName(internetRequest.getName());
            existingInternet.setPrice(internetRequest.getPrice());
            existingInternet.setLowSpeed(internetRequest.getLowSpeed());
            existingInternet.setUploadSpeed(internetRequest.getUploadSpeed());
            existingInternet.setDescription(internetRequest.getDescription());
            existingInternet.setMora(internetRequest.getMora());
            existingInternet.setBurstLimitLow(internetRequest.getBurstLimitLow());
            existingInternet.setBurstLimitUpload(internetRequest.getBurstLimitUpload());
            existingInternet.setBurstThresholdLow(internetRequest.getBurstThresholdLow());
            existingInternet.setBurstThresholdUpload(internetRequest.getBurstThresholdUpload());
            existingInternet.setBurstTimeLow(internetRequest.getBurstTimeLow());
            existingInternet.setBurstLimitUpload(internetRequest.getBurstThresholdUpload());
            existingInternet.setQueueTypeLow(internetRequest.getQueueTypeLow());
            existingInternet.setBurstTimeUpload(internetRequest.getBurstTimeUpload());
            existingInternet.setLink(internetRequest.getLink());

            internetRepository.save(existingInternet);

            log.info("Package {} updated",id);

        }else{
            log.warn("Package with ID {}  not found",id);
        }
    }
    public void deleteInternet(Long id){
        Optional<Internet> optionalInternet = internetRepository.findById(id);
        if(optionalInternet.isPresent()){
            internetRepository.deleteById(id);
        }
        else
            log.warn("User with ID {} not found",id);

    }

}
