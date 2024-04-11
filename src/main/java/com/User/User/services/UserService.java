package com.User.User.services;
import com.User.User.dto.dtoBilling.BillingResponse;
import com.User.User.dto.dtoUsers.UserRequest;
import com.User.User.dto.dtoUsers.UserResponse;
import com.User.User.dto.dtoUsers.UserViewResponse;
import com.User.User.models.Billing;
import com.User.User.models.User;
import com.User.User.repository.BillingRepository;
import com.User.User.repository.InternetRepository;
import com.User.User.repository.PromotionRepository;
import com.User.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ConnectionMtrService connectionMtrService;

    private final BillingRepository billingRepository;
    public Long createUser(@NonNull UserRequest userRequest) {
        User user = User.builder()
                .name(userRequest.getName())
                .direction(userRequest.getDirection())
                .location(userRequest.getLocation())
                .phoneNumber(userRequest.getPhoneNumber())
                .mobilePhoneNumber(userRequest.getMobilePhoneNumber())
                .email(userRequest.getEmail())
                .build();
        userRepository.save(user);
        log.info("User {} in saved", user.getId());
        return user.getId();
    }
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).toList();
    }

    private UserResponse mapToUserResponse(@NonNull User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .direction(user.getDirection())
                .location(user.getLocation())
                .phoneNumber(user.getPhoneNumber())
                .mobilePhoneNumber(user.getMobilePhoneNumber())
                .email(user.getEmail())
                .build();
    }
    public void updateUser(Long id, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User existingModels = optionalUser.get();

            existingModels.setName(userRequest.getName());
            existingModels.setDirection(userRequest.getDirection());
            existingModels.setLocation(userRequest.getLocation());
            existingModels.setPhoneNumber(userRequest.getPhoneNumber());
            existingModels.setMobilePhoneNumber(userRequest.getMobilePhoneNumber());
            existingModels.setEmail(userRequest.getEmail());

            userRepository.save(existingModels);

            log.info("User {} updated", id);
        } else {
            log.warn("Router with ID {}  not found", id);
        }
    }
    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        List<Billing> billings = optionalUser.get().getInvoice();

        for (Billing billing:billings){

            Mono<Boolean> editPort= connectionMtrService.editPort(optionalUser.get().getName(), billing.getService().getCaja_nap());
            log.info("billing print:{}",editPort);
            Boolean resultEditPort= editPort.onErrorReturn(false).block();
            if(Boolean.TRUE.equals(resultEditPort)){
                    userRepository.deleteById(id);
                    log.warn("User with ID {} not found", id);
            }
        }
    }

    private UserViewResponse mapToUserResponseN(@NonNull Billing billing) {
        Optional<User> user = userRepository.findById(billing.getUser().getId());

        log.info("user:{}",user.get());
        return UserViewResponse.builder()
                .id(billing.getId())
                .type_service(billing.getType_service())
                .payday(billing.getPayday())
                .invoice_creation(billing.getInvoice_creation())
                .taxes(billing.getTaxes())
                .cutoff_date(billing.getCutoff_date())
                .days_of_tolerance(billing.getDays_of_tolerance())
                .mora(billing.getMora())
                .reconnection(billing.getReconnection())
                .creationDay(billing.getCreationDay())

                .promotion(billing.getPromotion())
                .creationDayTrue(billing.getCreationDayTrue())

                //-------------------------------------
                .nameClient(user.get().getName())
                .direction(user.get().getDirection())
                //---------------------------------------
                .service(billing.getService())
                .build();
    }
    public List<UserViewResponse> getAllUsersConfigured() {
        List<Billing> billings = billingRepository.findAll();
        return billings.stream().map(this::mapToUserResponseN).toList();
    }

}
