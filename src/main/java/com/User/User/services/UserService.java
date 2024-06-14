package com.User.User.services;
import com.User.User.dto.dtoUsers.*;
import com.User.User.models.Billing;
import com.User.User.models.Servers;
import com.User.User.models.User;
import com.User.User.repository.*;
import com.User.User.services.ConfifConnectionDHCPandPPPoE.ConnectionMtrServiceDHCP;
import com.User.User.services.ConfifConnectionDHCPandPPPoE.ConnectionMtrServicePPPoE;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ConnectionMtrServicePPPoE connectionMtrServicePPPoE;
    private final ConnectionMtrServiceDHCP connectionMtrServiceDHCP;
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
                Boolean resultEditPort = editPort.onErrorReturn(false).block();

                Mono<Boolean> editIp = connectionMtrServicePPPoE.editIp(user.getName(), billing.getService().getType_Ipv4());
                Boolean resultEditIp = editIp.onErrorReturn(false).block();

                if (Boolean.TRUE.equals(resultEditPort && Boolean.TRUE.equals(resultEditIp))) {
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
                .idClient(user.get().getId())
                .nameClient(user.get().getName())
                .direction(user.get().getMainDirection())
                //---------------------------------------
                .password(service.get().getPassword())
                .box(service.get().getCaja_nap())
                .port(service.get().getPort_nap())
                .nameRouter(service.get().getRouter())
                .ip(service.get().getIp_admin())
                .namePackage(service.get().getInternetPackage().getName())
                .idService(service.get().getId())
                //----------------------------------------
                .build();
    }

    public List<UserViewResponse> getAllUsersConfigured() {
        List<Billing> billings = billingRepository.findAll();
        return billings.stream().map(this::mapToUserResponseN).toList();
    }
    public Object getUserConfigured(Long idBilling){

        Billing billing = billingRepository.findById(idBilling).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + idBilling));
        String nameGenerate = String.format("%010d",billing.getUser().getId());
        return UserViewConfigSwitchResponse.builder()
                .idUser(nameGenerate)
                .name(billing.getUser().getName())
                .password(billing.getService().getPassword())
                .modeConfigOnu(billing.getType_service())
                .build();
    }

    public Object createClientDHCP(MacUserAssignRequest macUserAssign) {
        Optional<Servers> service = serviceRepository.findById(macUserAssign.getIdService());
        log.info("hjdd{}",service.get().getIp_admin());
        if(service.isPresent()){
            Servers  existingServer= service.get();
            existingServer.setMac(macUserAssign.getMac());
            serviceRepository.save(existingServer);

            return assignMac(macUserAssign.getMac(),
                    macUserAssign.getNameClient(),
                    service.get().getIp_admin(),
                    service.get().getIdRouter());
        }
        return null;
    }

    private Object assignMac(String MAC, String nameClient, String ip, Long idRouter){

        Map<String, Object> promotionData = new HashMap<>();

        promotionData.put("userName", nameClient);
        promotionData.put("address", ip);
        promotionData.put("idRouter",idRouter);
        promotionData.put("macAddress", MAC);
        return connectionMtrServiceDHCP.PostActionDHCP("http://localhost:8081/api/QueriesFromOtherMicroservicesDHCP/createProfileDHCP/", promotionData);

    }





    public  List<ConsultClientCoordinatesResponse> consultClientCoordinates() {
        List<Billing> billings = billingRepository.findAll();

        return billings.stream()
                .map(billing -> {
                    String[] coordinates = billing.getUser().getLocation().split(", ");
                    String coordinateX = coordinates.length> 0 ? coordinates[0] : "";
                    String coordinateY = coordinates.length> 1 ? coordinates[1] : "";

                    return ConsultClientCoordinatesResponse.builder()

                            .coordinatesX(coordinateX)
                            .coordinatesY(coordinateY)
                            .packageInternet(billing.getService().getInternetPackage().getName())
                            .nameClient(billing.getUser().getName())
                            .location(billing.getUser().getLocation())
                            .ip(billing.getService().getIp_admin())
                            .phoneNumber(billing.getUser().getMobilePhoneNumber()).build();


                }).collect(Collectors.toList());
    }
}
































