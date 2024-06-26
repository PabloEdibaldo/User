package com.User.User.controller;
import com.User.User.dto.dtoBilling.BillingRequest;
import com.User.User.dto.dtoBilling.BillingResponse;
import com.User.User.dto.dtoBilling.ContentBillingResponse;
import com.User.User.dto.dtoInternet.InternetRequest;
import com.User.User.dto.dtoInternet.InternetResponse;
import com.User.User.dto.dtoPromotions.PromotionRequest;
import com.User.User.dto.dtoPromotions.PromotionResponse;
import com.User.User.dto.dtoServices.ServicesRequest;
import com.User.User.dto.dtoServices.ServicesResponse;
import com.User.User.dto.dtoUsers.*;
import com.User.User.models.ContentBilling;
import com.User.User.models.GeneralAdjustments.ConfigEm;
import com.User.User.models.MessageTwilio;
import com.User.User.repository.BillingRepository;
import com.User.User.repository.ServiceRepository;
import com.User.User.repository.UserRepository;
import com.User.User.services.*;
import com.User.User.services.ConfifConnectionDHCPandPPPoE.ConnectionMtrServicePPPoE;
import com.User.User.services.apiMercadoLible.CustomerStripe;
import com.User.User.services.apiMercadoLible.Webhook;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/internet/package/")
@Transactional
 class InternetController {
    private final InternetServices internetServices;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InternetResponse>getAllInternetPackages(){
        return internetServices.getAllInternet();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPackage(@RequestBody InternetRequest internetRequest) throws StripeException {
        internetServices.createPackageInternet(internetRequest);}
    @PutMapping("updatePackage/{id}/")
    @ResponseStatus(HttpStatus.OK)
    public void updateInternetPackage(@PathVariable Long id, @RequestBody InternetRequest internetRequest){
        internetServices.updateInternet(id,internetRequest);
    }
    @DeleteMapping("deletePackage/{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePackage(@PathVariable Long id){
        internetServices.deleteInternet(id);
    }
}

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/users/")
@Transactional
class UserController{
    private final UserService userService;
    private final UserRepository userRepository;
    private final ConnectionMtrServicePPPoE connectionMtrServicePPPoE;
    private final BillingRepository billingRepository;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse>getAllUser(){return userService.getAllUsers();}
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserRequest userRequest){
        userService.createUser(userRequest);
    }
    @PutMapping("updateUser/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest){
        userService.updateUser(id,userRequest);
    }
    @DeleteMapping("deleteUser/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }


    @GetMapping("getAllUserConfigured/")
    @ResponseStatus(HttpStatus.OK)
    public List<UserViewResponse>getAllUserConfigured(){return userService.getAllUsersConfigured();}

    @PostMapping("postMac/")
    @ResponseStatus(HttpStatus.OK)
    public Object postMac(@RequestBody MacUserAssignRequest macUserAssignRequest){
        log.info("mac:{}",macUserAssignRequest);
        return userService.createClientDHCP(macUserAssignRequest);
    }


    @GetMapping("getUserConfigured/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Object getUserConfigured(@PathVariable Long id){
        return userService.getUserConfigured(id);
    }


    @GetMapping("consultClientCoordinates/")
    public List<ConsultClientCoordinatesResponse> consultClientCoordinates() {
        return userService.consultClientCoordinates();
    }
    //getUserConfigured

    /*
      @DeleteMapping("/deleteUser/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id){

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
        User existingUser = optionalUser.get();
        List<Billing> billings =  existingUser.getInvoice();

        for (Billing billing:billings){
            log.info("Billings:{}",billing);
            Mono<Boolean> editPort= connectionMtrService.editPort(existingUser.getName(), billing.getService().getCaja_nap());
            if (Boolean.TRUE.equals(editPort)) {
                userService.deleteUser(id);
            } else {
                log.info("error");
            }
        }
        }
    }
    * */



}
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@Transactional
@RequestMapping("/api/promotion")
class PromotionController{
    private final PromotionService promotionService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse>getAllPromotion(){
        return promotionService.getAllPromotions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPromotion(@RequestBody PromotionRequest promotionRequest){
        promotionService.createPromotion(promotionRequest);
    }
    @PutMapping("/updatePromotion/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePromotion(@PathVariable Long id, @RequestBody PromotionRequest promotionRequest){
        promotionService.updatePromotion(id,promotionRequest);
    }
    @DeleteMapping("/deletePromotion/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromotion(@PathVariable Long id){
        promotionService.deletePromotion(id);
    }

}
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/billing/")
@Transactional
class BillingController{
    private final BillingService billingService;
    private final ServersServices services;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BillingRepository billingRepository;
    private final ServiceRepository serviceRepository;
    private final ConnectionMtrServicePPPoE connectionMtrServicePPPoE;

    private final CustomerStripe customerStripe;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BillingResponse> getAllBilling(){return billingService.getAllBilling();}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> createUserAndBillingAndService(@RequestBody CreateUserBillingServiceRequest request){
        Map<String,String> Response = new HashMap<>();
        try{
            //create user
            Long userId = userService.createUser(request.getUserRequest());
            //create the service for the user
            Long serviceId = services.createServices(request.getServicesRequest(),userId);
            log.info("Service id{}",serviceId);
            //create the billing for the user
            Long billingId = billingService.createBilling(request.getBillingRequest(),userId,serviceId);
            //create client PPPoE in switch
            String userName = userRepository.findById(userId).get().getName();
            String address = serviceRepository.findById(serviceId).get().getIp_admin();
            Long idRouter = serviceRepository.findById(serviceId).get().getIdRouter();
            String password = serviceRepository.findById(serviceId).get().getPassword();

            //type server
            String typeServer = billingRepository.findById(serviceId).get().getType_service();

            log.info(typeServer);


            log.info("billing id:{}",billingId);
            if(billingId != null){
                log.info("no nnueo nulo:{}",billingId);
                if(typeServer.equals("PPPoE")) {
                    connectionMtrServicePPPoE.createClientPPPoE(userId,userName,address,idRouter,password).block();
                }else if(typeServer.equals("DHCP")){

                    log.info("Create DHCP");
                    //userService.createClientDHCP(userName,address,idRouter,macAddress);
                }
                customerStripe.createClientStripe(userId);
                billingService.createClient(billingId);
            }
            //return ResponseEntity.ok("Exit operation ");
        }catch (Exception e){
            log.info("error:{}",e);
            Response.put("Error", "Error create client");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response);
        }
        return null;
    }


    @PutMapping("/updateBilling/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBilling(@PathVariable Long id, @RequestBody BillingRequest billingRequest){
        billingService.updateBilling(id,billingRequest);
    }
    @DeleteMapping("/deleteBilling/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  void deleteBilling(@PathVariable Long id){
        billingService.deleteBilling(id);
    }

    @GetMapping("/getContentBilling/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<ContentBillingResponse> getContentBilling(@PathVariable Long id){
        log.info("id:{}",id);
        return billingService.consultingBillingId(id);
    }

    @GetMapping("/contentBilling/{idBilling}")
    public List<ContentBilling> getContentBillingByIdBilling(@PathVariable Long idBilling) {
        return billingService.getContentBillingByIdBilling(idBilling);
    }
}
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@Transactional
@RequestMapping("/api/serviceClient")
class ServiceController{
    private final ServersServices services;
    private final ConnectionMtrServicePPPoE connectionMtrServicePPPoE;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ServicesResponse> getAllServices(){
        return services.getAllServices();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createService(@RequestBody ServicesRequest servicesRequest){
        //services.createServices(servicesRequest);
    }
    @PutMapping("/updateService/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateService(@PathVariable Long id, @RequestBody ServicesRequest servicesRequest){
        services.updateServices(id,servicesRequest);
    }
    @DeleteMapping("/deleteService/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteService(@PathVariable Long id){
        services.deleteService(id);
    }

//------------------------------------------------------------------------------------------consulting
    @GetMapping("/consulta/cajanap/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Boolean> getAllServices(@PathVariable Long id){
        return connectionMtrServicePPPoE.getNap(id);
    }

    @PostMapping("/ingresar/puerto/cliente")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Boolean> postPort(String name, long idNap, int port,Long idClient){
        return connectionMtrServicePPPoE.postPort(name, idNap, port,idClient);
    }
}
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class CreateUserBillingServiceRequest {
    private UserRequest userRequest;
    private BillingRequest billingRequest;
    private ServicesRequest servicesRequest;

    // getters and setters
}
@RestController
@Slf4j
@RequestMapping("/api/StripeWebhooks")
class StripeWebhooks{
    @Autowired
    private  Webhook webhook;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> Webhooks(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {

        return webhook.handleWebhookEvent(payload,sigHeader);
    }

}


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/messages")
class MessageTemplateController{

    @Autowired
    private MessengerService messengerService;

    @GetMapping
    public List<MessageTwilio> getAllMessages(){
        return messengerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageTwilio> getMessageById(@PathVariable Long id){
        Optional<MessageTwilio> messageTwilio = messengerService.findById(id);
        if(messageTwilio.isPresent()){
            return ResponseEntity.ok(messageTwilio.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public MessageTwilio createMessage(@RequestBody MessageTwilio messageTwilio){
        return messengerService.save(messageTwilio);
    }
    @PutMapping("/{id}")
    public ResponseEntity<MessageTwilio> updateMessage(@PathVariable Long id, @RequestBody MessageTwilio messageTwilio){
        if(messengerService.findById(id).isPresent()){
            messageTwilio.setId(id);
            return  ResponseEntity.ok(messengerService.save(messageTwilio));
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id){
        if(messengerService.findById(id).isPresent()){
            messengerService.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    @Slf4j
    @RestController
    @CrossOrigin(origins = "*")
    @RequestMapping("/api/messages")

    public class CompanyController {

        @Autowired
        private GeneralAdjustmentsServices generalAdjustmentsServices;

        // POST: Crear una nueva compañía y subir un logo
        @PostMapping("/uploadLogo")
        public ResponseEntity<String> uploadLogo(@RequestParam("nameCompany") String nameCompany,
                                                 @RequestParam("address") String address,
                                                 @RequestParam("phone") String phone,
                                                 @RequestParam("idCompany") String idCompany,
                                                 @RequestParam("gmailBackup") String gmailBackup,
                                                 @RequestParam("gmailSupport") String gmailSupport,
                                                 @RequestParam("gmailInvoice") String gmailInvoice,
                                                 @RequestParam("logo") MultipartFile file) {
            try {
                ConfigEm company = ConfigEm.builder()
                        .nameCompany(nameCompany)
                        .address(address)
                        .phone(phone)
                        .idCompany(idCompany)
                        .gmailBackup(gmailBackup)
                        .gmailSupport(gmailSupport)
                        .gmailInvoice(gmailInvoice)
                        .logo(file.getBytes())
                        .build();

                generalAdjustmentsServices.saveConfigEm(company);
                return new ResponseEntity<>("Company created and logo uploaded successfully", HttpStatus.OK);
            } catch ( IOException e) {
                return new ResponseEntity<>("Error uploading logo", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        // PUT: Actualizar el logo de una compañía por ID
        @PutMapping("/updateLogo/{id}")
        public ResponseEntity<String> updateLogo(@PathVariable Long id, @RequestParam("logo") MultipartFile file) {
            try {
                Optional<ConfigEm> updatedConfigEm = generalAdjustmentsServices.findById(id).map(configEm -> {
                    try {
                        configEm.setLogo(file.getBytes());
                        return generalAdjustmentsServices.saveConfigEm(configEm);
                    } catch (IOException e) {
                        throw new RuntimeException("Error updating logo", e);
                    }
                });

                if (updatedConfigEm.isPresent()) {
                    return new ResponseEntity<>("Logo updated successfully", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Company not found", HttpStatus.NOT_FOUND);
                }
            } catch (IOException e) {
                return new ResponseEntity<>("Error updating logo", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        // GET: Obtener todas las configuraciones
        @GetMapping("/all")
        public List<ConfigEm> findAll() {
            return generalAdjustmentsServices.findAll();
        }

    }





}