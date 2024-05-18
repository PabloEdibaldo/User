package com.User.User.services.ConfifConnectionDHCPandPPPoE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ConnectionMtrServicePPPoE {
    private final WebClient webClient;
    public ConnectionMtrServicePPPoE(WebClient webClient) {
        this.webClient = webClient;
    }
    public Mono<Boolean> getNap(Long id_nap){
        assert webClient != null;
        String url ="http://localhost:8081/api/box/consultBox/"+id_nap+"/";
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Error api external"+response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Error server API"+response.statusCode())))
                .bodyToMono(Boolean.class);
    }
    //assign the port on the box a client
    public Mono<Boolean> postPort(String nameUser, Long idNap, int port){
        // Create a map of parameters
        Map<String, Object> params = new HashMap<>();
        params.put("boxNap", idNap);
        params.put("portNumber", port);
        params.put("nameClient", nameUser);


        return webClient.post()
                .uri("http://localhost:8081/api/box/userAssignedPort/")
                .bodyValue(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Error querying router" + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Error server API" + response.statusCode())))
                .bodyToMono(Boolean.class);
       }
    public Mono<Boolean> postIp(String nameUser, Long redIpv4, String ip){
        // Create a map of parameters
        Map<String, Object> params = new HashMap<>();
        params.put("userName", nameUser);
        params.put("redIpv4", redIpv4);
        params.put("ip", ip);


        return webClient.post()
                .uri("http://localhost:8081/api/box/userAssignedPort/")
                .bodyValue(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Error querying router" + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Error server API" + response.statusCode())))
                .bodyToMono(Boolean.class);
    }
       //edit port for de client == delete client
       public Mono<Boolean> editPort(String name, Long idBox  ){
            log.info("name:{} id Box:{}",name,idBox);
            Map<String,Object> nameUser = new HashMap<>();
            nameUser.put("nameUser",name);
            nameUser.put("idNap",idBox);
            log.info("idBox:{}",idBox);

           return webClient.post()
                   .uri("http://localhost:8081/api/box/EditPortNap/")
                   .bodyValue(nameUser)
                   .retrieve()
                   .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Error querying router" + response.statusCode())))
                   .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Error server API" + response.statusCode())))
                   .bodyToMono(Boolean.class);
       }

       //--------------------------------------------------------------POST CREATE PROMOTION------------------------------------------------------
/*    public Mono<Object> createPromotion(@NotNull Promotion promotion){
        log.info("titlePromotion:{}",promotion.getDescription());
        log.info("maxLimit:{}",promotion.getMax_limit());
        log.info("idRouter:{}",promotion.getIdRouter());

        Map<String,Object> promotionData = new HashMap<>();
        promotionData.put("titlePromotion",promotion.getDescription());
        promotionData.put("maxLimit",promotion.getMax_limit());
        promotionData.put("idRouter",promotion.getIdRouter());

        return webClient.post()
                .uri("http://localhost:8081/api/QueriesFromOtherMicroservices/createPromotion")
                .bodyValue(promotionData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,r->Mono.error(new RuntimeException(("Error query promotion"+r.statusCode()))))
                .onStatus(HttpStatusCode::is5xxServerError,r->Mono.error(new RuntimeException("Error api server"+r.statusCode())))
                .bodyToMono(Object.class);
    }*/
    //---------------------------------------------------CREATE CLIENT PPPOE-------------------------------------------------------------------
    public Mono<Boolean> createClientPPPoE( Long userId, String userName, String address, Long idRouter,String password){


        Map<String,Object> promotionData = new HashMap<>();
        promotionData.put("idUser",userId);
        promotionData.put("userName",userName);
        promotionData.put("address",address);
        promotionData.put("idRouter",idRouter);
        promotionData.put("userPassword",password);

        

        return  webClient.post()
                .uri("http://localhost:8081/api/QueriesFromOtherMicroservices/createClientPPPoE/")
                .bodyValue(promotionData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new RuntimeException(("Error query promotion" + r.statusCode()))))
                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("Error api server" + r.statusCode())))
                .bodyToMono(Boolean.class);
    }
    //---------------------------------------------------ASSIGN PROMOTION-------------------------------------------------------------------

    public Mono<Boolean> assignPromotion(Long idRouter,String listPromotionName,String address){


        Map<String,Object> promotionData = new HashMap<>();
        promotionData.put("idRouter",idRouter);
        promotionData.put("listPromotionName",listPromotionName);
        promotionData.put("address",address);


        log.info("data client:{}",promotionData);

        return  webClient.post()
                .uri("http://localhost:8081/api/QueriesFromOtherMicroservices/createClientPPPoE/")
                .bodyValue(promotionData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new RuntimeException(("Error query promotion" + r.statusCode()))))
                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("Error api server" + r.statusCode())))
                .bodyToMono(Boolean.class);
    }

//-----------------------------------------------------------------------change package client PPPoE and assign profile PPPoE----------------------------------------------------------------
    public Mono<Boolean> packageChangeClientPPPoE(String addressClient,String promotionName,Long idRouter,String namePackageInternet){
        Map<String,Object> packageChangeData = new HashMap<>();

        packageChangeData.put("address",addressClient);
        packageChangeData.put("namePromotion",promotionName);
        packageChangeData.put("idRouter",idRouter);
        packageChangeData.put("profile",namePackageInternet);

        return  webClient.post()
                .uri("http://localhost:8081/api/QueriesFromOtherMicroservices/packageChangeOfPPPClient/")
                .bodyValue(packageChangeData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new RuntimeException(("Error query promotion" + r.statusCode()))))
                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("Error api server" + r.statusCode())))
                .bodyToMono(Boolean.class);

    }


//-----------------------------------------------------------------------assign package client PPPoE----------------------------------------------------------------
    public Boolean assignPackageClientInternet(Long idUser, String password){
        Map<String,Object> assignPackageClientPPoEData = new HashMap<>();
        assignPackageClientPPoEData.put("idUser",idUser);
        assignPackageClientPPoEData.put("password",password);
        return Boolean.TRUE;

//        return  webClient.post()
//                .uri("")
//                .bodyValue(assignPackageClientPPoEData)
//                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new RuntimeException(("Error query promotion" + r.statusCode()))))
//                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("Error api server" + r.statusCode())))
//                .bodyToMono(Boolean.class);
    }

    //-----------------------------------------------------------------------assign package client PPPoE----------------------------------------------------------------
    public Mono<Boolean> cutCustomerService(Long idRouter,String nameClient,String address){

        Map<String,Object> assignPackageClientPPoEData = new HashMap<>();
        assignPackageClientPPoEData.put("idRouter",idRouter);
        assignPackageClientPPoEData.put("nameClient",nameClient);
        assignPackageClientPPoEData.put("address",address);

        return  webClient.post()
                .uri("http://localhost:8081/api/QueriesFromOtherMicroservices/cutServiceClient/")
                .bodyValue(assignPackageClientPPoEData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new RuntimeException(("Error query promotion" + r.statusCode()))))
                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("Error api server" + r.statusCode())))
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> createProfilePPP(String name,String lowSpeed,String uploadSpeed){
        Map<String,Object> assignPackageClientPPoEData = new HashMap<>();
        assignPackageClientPPoEData.put("name",name);
        assignPackageClientPPoEData.put("lowSpeed",lowSpeed);
        assignPackageClientPPoEData.put("uploadSpeed",uploadSpeed);

        return  webClient.post()
                .uri("http://localhost:8081/api/QueriesFromOtherMicroservices/cutServiceClient/")
                .bodyValue(assignPackageClientPPoEData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new RuntimeException(("Error query promotion" + r.statusCode()))))
                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("Error api server" + r.statusCode())))
                .bodyToMono(Boolean.class);
    }


    @Autowired
    private RestTemplate restTemplate;
    public Object PostActionPPPoE(String url,Object object ){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Object> requestEntity = new HttpEntity<>(object, headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Object.class);
        return response.getBody();
    }


}















































