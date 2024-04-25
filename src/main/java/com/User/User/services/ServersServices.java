package com.User.User.services;
import com.User.User.dto.dtoServices.ServicesRequest;
import com.User.User.dto.dtoServices.ServicesResponse;
import com.User.User.models.Internet;
import com.User.User.models.Promotion;
import com.User.User.models.Servers;
import com.User.User.models.User;
import com.User.User.repository.InternetRepository;
import com.User.User.repository.PromotionRepository;
import com.User.User.repository.ServiceRepository;
import com.User.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ServersServices {
    private final ServiceRepository serviceRepository;
    private final InternetRepository internetRepository;
    private final ConnectionMtrService connectionMtrService;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    public List<ServicesResponse> getAllServices(){
        List<Servers> servers = serviceRepository.findAll();
        return servers.stream().map(this::mapToServices).toList();
    }
    private ServicesResponse mapToServices(@NotNull Servers servers) {
        return ServicesResponse.builder()
                .id(servers.getId())
                .router(servers.getRouter())
                .idRouter(servers.getIdRouter())
                .firewall(servers.getFirewall())
                .internetPackage_id(servers.getInternetPackage().getId())
                .description(servers.getDescription())
                .price(servers.getPrice())
                .type_Ipv4(servers.getType_Ipv4())
                .mac(servers.getMac())
                .ppp_hs(servers.getPpp_hs())
                .password(servers.getPassword())
                .caja_nap(servers.getCaja_nap())
                .port_nap(servers.getPort_nap())
                .direction(servers.getDirection())
                .time_Installation(servers.getTime_Installation())
                .connection(servers.getConnection())
                .ip_admin(servers.getIp_admin())
                .type_antenna(servers.getType_antenna())

                .build();
    }
    public Long createServices(@NotNull ServicesRequest servicesRequest,Long idUser ){
        log.info( "id user:{} ",idUser);
        Long id_nap = servicesRequest.getCaja_nap();
        Mono<Boolean> IdNap = connectionMtrService.getNap(id_nap);
        Boolean resultGetNap = IdNap.onErrorReturn(false).block();
//-------------------------------------------------------------------------------
        Optional<User> user =  userRepository.findById(idUser);
        String  nameUser = user.get().getName();
        Mono<Boolean> PostPort=connectionMtrService.postPort(nameUser,servicesRequest.getCaja_nap(),servicesRequest.getPort_nap());
        Boolean resultPostPort = PostPort.onErrorReturn(false).block();
//--------------------------------------------------------------------------------

        Mono<Boolean> ip=connectionMtrService.postIp(nameUser,servicesRequest.getType_Ipv4(),servicesRequest.getIp_admin());
        Boolean resultPostIp = ip.onErrorReturn(false).block();
        //--------------------------------------------------------------------------

        if (Boolean.TRUE.equals(resultGetNap) && Boolean.TRUE.equals(resultPostPort)){
            Internet internet_package = internetRepository.findById(servicesRequest.getInternetPackage_id())
                    .orElseThrow(() -> new EntityNotFoundException("Internet package  not found with ID: " + servicesRequest.getInternetPackage_id()));


            Servers servers = Servers.builder()
                    .router(servicesRequest.getRouter())
                    .idRouter(servicesRequest.getIdRouter())
                    .firewall(servicesRequest.getFirewall())
                    .internetPackage(internet_package)
                    .description(servicesRequest.getDescription())
                    .price(servicesRequest.getPrice())
                    .type_Ipv4(servicesRequest.getType_Ipv4())
                    .mac(servicesRequest.getMac())
                    .ppp_hs(servicesRequest.getPpp_hs())
                    .password(servicesRequest.getPassword())
                    .caja_nap(servicesRequest.getCaja_nap())
                    .port_nap(servicesRequest.getPort_nap())
                    .direction(servicesRequest.getDirection())
                    .time_Installation(servicesRequest.getTime_Installation())
                    .connection(servicesRequest.getConnection())
                    .ip_admin(servicesRequest.getIp_admin())
                    .type_antenna(servicesRequest.getType_antenna())

                    .build();
            serviceRepository.save(servers);
            log.info("Service {} in saved",servers.getId());
            return servers.getId();
        }else {
            return null;
        }
    }
    public void updateServices(Long id, ServicesRequest servicesRequest){
        Optional<Servers> optionalServers = serviceRepository.findById(id);
        if (optionalServers.isPresent()){
            Servers existingService = optionalServers.get();
            existingService.setRouter(servicesRequest.getRouter());
            existingService.setIdRouter(servicesRequest.getIdRouter());
            existingService.setFirewall(servicesRequest.getFirewall());
            existingService.setDescription(servicesRequest.getDescription());
            existingService.setPrice(servicesRequest.getPrice());
            existingService.setType_Ipv4(servicesRequest.getType_Ipv4());
            existingService.setMac(servicesRequest.getMac());
            existingService.setPpp_hs(servicesRequest.getPpp_hs());
            existingService.setPassword(servicesRequest.getPassword());
            existingService.setCaja_nap(servicesRequest.getCaja_nap());
            existingService.setPort_nap(servicesRequest.getPort_nap());
            existingService.setDirection(servicesRequest.getDirection());
            existingService.setTime_Installation(servicesRequest.getTime_Installation());
            existingService.setConnection(servicesRequest.getConnection());
            existingService.setIp_admin(servicesRequest.getIp_admin());
    }}

    public void deleteService (Long id){
        Optional<Servers> optionalServers = serviceRepository.findById(id);

        if(optionalServers.isPresent()){
            serviceRepository.deleteById(id);
        }
        else {
            log.warn("Service with  ID {} not found", id);
        }
    }


}
