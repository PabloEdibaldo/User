package com.User.User.services;
import com.User.User.dto.dtoUsers.UserRequest;
import com.User.User.dto.dtoUsers.UserResponse;
import com.User.User.dto.dtoUsers.UserViewResponse;
import com.User.User.models.Billing;
import com.User.User.models.Servers;
import com.User.User.models.User;
import com.User.User.repository.*;
import com.User.User.services.ConfifConnectionDHCPandPPPoE.ConnectionMtrServicePPPoE;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ConnectionMtrServicePPPoE connectionMtrServicePPPoE;
    private final InternetRepository internetRepository;

    private final BillingRepository billingRepository;
    private final ServiceRepository serviceRepository;
    public Long createUser(@NonNull UserRequest userRequest) {
        User user = User.builder()
                .name(userRequest.getName())
                .mainDirection(userRequest.getMainDirection())
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
                .mainDirection(user.getMainDirection())
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
            existingModels.setMainDirection(userRequest.getMainDirection());
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
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Billing> billings = user.getInvoice();

            for (Billing billing : billings) {
                Mono<Boolean> editPort = connectionMtrServicePPPoE.editPort(user.getName(), billing.getService().getCaja_nap());
                log.info("billing print1: {}", editPort);
                Boolean resultEditPort = editPort.onErrorReturn(false).block();
                log.info("billing print2: {}", resultEditPort);
                if (Boolean.TRUE.equals(resultEditPort)) {
                    userRepository.deleteById(id);
                    log.warn("User with ID {} deleted successfully", id);
                }
            }
        } else {
            log.warn("User with ID {} not found", id);
        }
    }

    private UserViewResponse mapToUserResponseN(@NonNull Billing billing) {
        Optional<User> user = userRepository.findById(billing.getUser().getId());
        Optional<Servers> service = serviceRepository.findById(billing.getService().getId());


        return UserViewResponse.builder()
                .id(billing.getId())
                .type_service(billing.getType_service())
                
                .reconnection(billing.getReconnection())
                .creationDay(billing.getCreationDay())

                .promotion(billing.getPromotion())
                .creationDayTrue(billing.getCreationDayTrue())

                //-------------------------------------

                .nameClient(user.get().getName())
                .direction(user.get().getMainDirection())
                //---------------------------------------


                .box(service.get().getCaja_nap())
                .port(service.get().getPort_nap())
                .nameRouter(service.get().getRouter())
                .ip(service.get().getIp_admin())
                .namePackage(service.get().getInternetPackage().getName())

                //----------------------------------------
                .build();
    }
    public List<UserViewResponse> getAllUsersConfigured() {
        List<Billing> billings = billingRepository.findAll();
        return billings.stream().map(this::mapToUserResponseN).toList();
    }



}
