package com.User.User.services;
import com.User.User.dto.dtoBilling.BillingRequest;
import com.User.User.dto.dtoBilling.BillingResponse;
import com.User.User.models.*;
import com.User.User.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingService {
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PromotionRepository promotionRepository;
    private final ConnectionMtrService connectionMtrService;
    //private final InvoiceServicesService invoiceServices;
    private final MessengerService messengerService;

    //return invoice list
    public List<BillingResponse> getAllBilling() {
        List<Billing> billings = billingRepository.findAll();
        return billings.stream().map(this::mapToBillingsResponse).toList();
    }

    private BillingResponse mapToBillingsResponse(@NotNull Billing billing) {
        return BillingResponse.builder()
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
                .taxes1(billing.getTaxes1())
                .taxes2(billing.getTaxes2())
                .taxes3(billing.getTaxes3())
                .promotion(billing.getPromotion())
                .creationDayTrue(billing.getCreationDayTrue())
                .build();
    }

    //create invoice
    public Long createBilling(@NonNull BillingRequest billingRequest, Long userId, Long serviceId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + billingRequest.getUserId()));
        Servers service = serviceRepository.findById(serviceId).orElseThrow(() -> new EntityNotFoundException("service  not found with ID: " + billingRequest.getServiceId()));

        Billing billing = Billing.builder()
                .type_service(billingRequest.getType_service())
                .payday(billingRequest.getPayday())
                .invoice_creation(billingRequest.getInvoice_creation())
                .taxes(billingRequest.getTaxes())
                .cutoff_date(billingRequest.getCutoff_date())
                .days_of_tolerance(billingRequest.getDays_of_tolerance())
                .mora(billingRequest.getMora())
                .reconnection(billingRequest.getReconnection())
                .taxes1(billingRequest.getTaxes1())
                .taxes2(billingRequest.getTaxes2())
                .taxes3(billingRequest.getTaxes3())
                .promotion(billingRequest.getPromotion())
                .user(user)
                .service(service)
                .creationDayTrue(billingRequest.getCreationDayTrue())
                .build();

        billingRepository.save(billing);
        log.info("Billing {} in saved", billing.getId());
        //messengerService.TypeOfSituation(billing,1);


        return billing.getId();
    }

    public void updateBilling(Long id, @NotNull BillingRequest billingRequest) {
        Optional<Billing> optionalBilling = billingRepository.findById(id);

        User user = userRepository.findById(billingRequest.getUserId()).orElseThrow(() ->
                new EntityNotFoundException("User not found with ID: " + billingRequest.getUserId()));

        Servers service = serviceRepository.findById(billingRequest.getServiceId()).orElseThrow(() ->
                new EntityNotFoundException("Service not found with ID: " + billingRequest.getServiceId()));


        if (optionalBilling.isPresent()) {
            Billing existingBilling = optionalBilling.get();

            existingBilling.setType_service(billingRequest.getType_service());
            existingBilling.setPayday(billingRequest.getPayday());
            existingBilling.setInvoice_creation(billingRequest.getInvoice_creation());
            existingBilling.setTaxes(billingRequest.getTaxes());
            existingBilling.setCutoff_date(billingRequest.getCutoff_date());
            existingBilling.setDays_of_tolerance(billingRequest.getDays_of_tolerance());
            existingBilling.setMora(billingRequest.getMora());
            existingBilling.setReconnection(billingRequest.getReconnection());
            existingBilling.setTaxes1(billingRequest.getTaxes1());
            existingBilling.setTaxes2(billingRequest.getTaxes2());
            existingBilling.setTaxes3(billingRequest.getTaxes3());
            existingBilling.setPromotion(billingRequest.getPromotion());
            existingBilling.setUser(user);
            existingBilling.setService(service);
            billingRepository.save(existingBilling);


            log.info("Package {}  updated ", existingBilling.getId());
        } else {
            log.warn("Package with id {} not found", id);
        }
    }

    public void deleteBilling(Long id) {
        Optional<Billing> optionalBilling = billingRepository.findById(id);
        if (optionalBilling.isPresent()) {
            billingRepository.deleteById(id);
        } else {
            log.warn("Billing with ID {} not found", id);
        }
    }


    //----------------------------------------------------------------------------------------------------
    public void calculateBillingCycle() {
        List<Billing> billings = billingRepository.findAll();
        for (Billing billing : billings) {

        }
    }


    //------------------------------------------------------------------------------------------------------
    public ResponseEntity<Map<String, String>> createClient(Long idBilling) {

        Map<String, String> response = new HashMap<>();
        Billing billing = billingRepository.findById(idBilling).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + idBilling));
        LocalDate today = LocalDate.now(ZoneId.of("UTC-7"));
        try {

            if (billing.getPromotion() != null && billing.getPayday() != 0 && billing.getCreationDay() != null) {
                if (createClientWithPromotionAndSpecificPayDay(billing, today)) {
                    log.info( "Client created with promotion and specific payment day.");
                    messengerService.TypeOfSituation(billing,1);
                } else {
                    log.info( "Client created with promotion and specific payment day.");
                }

            } else if (billing.getPromotion() != null) {
                if (createClientWithPromotionNoSpecificPayDay(billing, today)) {
                    log.info("Client created with promotion and no specific payment day.");
                    messengerService.TypeOfSituation(billing,1);
                } else {
                    log.info("Client created with promotion and no specific payment day.");
                }

            } else if (billing.getCreationDay() != null) {
                if (createClientNoPromotionWithSpecificPayDay(billing, today)) {
                    log.info( "Client created without promotion and with specific payment day.");
                    messengerService.TypeOfSituation(billing,1);
                } else {
                    log.info( "Client created without promotion and with specific payment day.");
                }
            } else {

                if (createClientNoPromotionNoSpecificPayDay(billing, today)) {
                    log.info("Client created without promotion and without specific payment day.");

                    messengerService.TypeOfSituation(billing,1);
                } else {
                    log.info("Client created without promotion and without specific payment day.");
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);

        }catch (EntityNotFoundException e){
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(response);
        }
    }

    private boolean createClientWithPromotionAndSpecificPayDay(@NotNull Billing billing, LocalDate today){
        Promotion promotion =getPromotionById(billing.getPromotion());

        if(promotion != null){
            Mono<Boolean> responseAssessingPromotion =connectionMtrService.assignPromotion(
                    billing.getService().getIdRouter(),
                    promotion.getDescription(),
                    billing.getService().getIp_admin());

            Boolean  response = responseAssessingPromotion.block();
            if(Boolean.TRUE.equals(response)){
                return handleSpecificPayDay(billing ,today);
            }
        }
        return false;
    }
    private boolean createClientWithPromotionNoSpecificPayDay(@NotNull Billing billing, LocalDate today){
        Promotion promotion = getPromotionById(billing.getPromotion());

        if(promotion != null){
            Mono<Boolean> responseAssessingPromotion =connectionMtrService.assignPromotion(
                    billing.getService().getIdRouter(),
                    promotion.getDescription(),
                    billing.getService().getIp_admin());

            Boolean  response = responseAssessingPromotion.block();
            if(Boolean.TRUE.equals(response)){
                return handlePromotionWithoutSpecificPayDay(billing ,today ,promotion);
            }
        }
        return false;
    }
    private boolean createClientNoPromotionWithSpecificPayDay(Billing billing ,LocalDate today){
        return handleSpecificPayDay(billing,today);
    }
    private boolean createClientNoPromotionNoSpecificPayDay(Billing billing, LocalDate today) {
        return handleNoPromotionNoSpecificPayDay(billing, today);
    }





    public boolean handleSpecificPayDay(@NotNull Billing billing, @NotNull LocalDate today){
    LocalDate timeMonths = today.plusMonths(billing.getCutoff_date());
    LocalDate invoiceDieDate = timeMonths.minusDays(billing.getInvoice_creation());
    LocalDate paymentDay = timeMonths.withDayOfMonth(billing.getPayday());

    if(invoiceDieDate.isEqual(today)){
        generationBilling(invoiceDieDate,timeMonths,billing.getService().getPrice());
    }else if(invoiceDieDate.isEqual(paymentDay)){
        LocalDate toleranceDays = paymentDay.plusDays(billing.getDays_of_tolerance());
        if(toleranceDays.isEqual(today)){
            if(pay()){
                return assignPackageClientInternet(billing);
            }else {
                cutCustomerService(billing);
            }
        }
    }
    return assignPackageClientInternet(billing);
    }
    private boolean handlePromotionWithoutSpecificPayDay(@NotNull Billing billing , @NotNull LocalDate today, @NotNull Promotion promotion){
        LocalDate promotionEnd = today.plusDays(promotion.getTimePromotion());
        LocalDate timeGenerationBilling = promotionEnd.minusDays(billing.getInvoice_creation());
        LocalDate daysTolerance = promotionEnd.plusDays(billing.getDays_of_tolerance());

        int option = calculateOption(today,promotionEnd,timeGenerationBilling,daysTolerance);
        switch (option){
            case 1:
                generationBilling(timeGenerationBilling,promotionEnd,billing.getService().getPrice());
                return true;

            case 2:
                if(pay()){
                    return updateClientPackage(billing,promotion.getDescription());
                }else {
                    log.info("no payment received");
                }
                break;
            case 3:
                cutCustomerService(billing);
                break;
        }
        return false;
    }

    private boolean handleNoPromotionNoSpecificPayDay(@NotNull Billing billing, @NotNull LocalDate today){
        LocalDate serviceTime = today.plusMonths(billing.getCutoff_date());
        LocalDate timeGenerationBilling = serviceTime.minusDays(billing.getInvoice_creation());
        LocalDate daysTolerance = serviceTime.plusDays(billing.getDays_of_tolerance());

        if (timeGenerationBilling.isEqual(today)){
            generationBilling(timeGenerationBilling,serviceTime,billing.getService().getPrice());
            return true;
        }else if(timeGenerationBilling.isAfter(daysTolerance)){
            if(pay()){
                return assignPackageClientInternet(billing);
            }

        }
        return false;
    }

    private void cutCustomerService(@NotNull Billing billing){
        connectionMtrService.cutCustomerService(
                billing.getService().getIdRouter(),
                billing.getUser().getName(),
                billing.getService().getIp_admin()
        );

    }
    private boolean updateClientPackage(@NotNull Billing billing, String descriptionPromotion) {
        return Boolean.TRUE.equals(connectionMtrService.packageChangeClientPPPoE(
                billing.getService().getIp_admin(),
                descriptionPromotion,
                billing.getService().getIdRouter(),
                billing.getService().getInternetPackage().getName()
        ).block());
    }
    private boolean assignPackageClientInternet(@NotNull Billing billing){
        return Boolean.TRUE.equals(connectionMtrService.assignPackageClientInternet(
                billing.getUser().getId(),
                billing.getService().getPassword()
        ).block());
    }

    private boolean pay() {
        return false;
    }

    private void generationBilling(LocalDate timeGenerationBilling,LocalDate serviceTime,int pricePackage) {
        //invoiceServices.createInvoiceService();

    }





    private int calculateOption(LocalDate today, LocalDate promotionEnd, @NotNull LocalDate timeGenerationBilling, LocalDate daysTolerance) {
        if (timeGenerationBilling.isEqual(today)) {
            return 1;
        } else if (promotionEnd.isEqual(today)) {
            if(promotionEnd.isAfter(daysTolerance)){
                return pay() ? 2 : 3;
            }
        }
        return 0;
    }
    private Promotion getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with ID: " + promotionId));
    }
}














/*
*
*
    private void generationBilling(Billing billing){
    }
    private Promotion getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with ID: " + promotionId));
    }
    private int calculateOption(LocalDate today, LocalDate promotionEnd, @NotNull LocalDate timeGenerationBilling, LocalDate daysTolerance) {
        if (timeGenerationBilling.isEqual(today)) {
            return 1;
        } else if (promotionEnd.isEqual(today)) {
            if(promotionEnd.isAfter(daysTolerance)){
                return pay() ? 2 : 3;
            }
        }
        return 0;
    }



    public ResponseEntity<Map<String, String>> createClient(Long idBilling){
        Map<String,String> Response = new HashMap<>();
        Billing billing = billingRepository.findById(idBilling).orElseThrow(() ->
                new EntityNotFoundException("User not found with ID: "+idBilling));
        LocalDate today = LocalDate.now(ZoneId.of("UTC-7"));

        boolean success = false;
        //create customer witch promotion
        if(billing.getPromotion()!=null && billing.getPayday()!=0 && billing.getCreationDay() !=null){
        //create customer with a specific payment day
            success=createCustomerWithSpecificPaymentDay(billing,today);

        }else if (billing.getPromotion()!=null){
            //create customer without a specific payment day
            String s = createCustomerWithoutSpecificPaymentDay(billing, today) ? Response.put("Success", "create customer without a specific payment day") :
                    Response.put("Error", "Error create client");

        }else if(billing.getCreationDay()!=null){
            //create customer with specific payday without promotion
           boolean createCustomerWithSpecificPaydayWithoutPromotion = createCustomerWithSpecificPaydayWithoutPromotion(billing, today );

        }else{
           //create customer without a specific payment day
            boolean createClient =  createClient(billing,today);

        }

        if(Response.get("Success").equals("Success")){
            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(Response);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(Response);
        }

    }
    private boolean createClient(Billing billing, LocalDate today){
        //time end promotion
        LocalDate serviceTime = today.plusMonths(billing.getCutoff_date());

        LocalDate timeGenerationBilling = serviceTime.minusDays(billing.getInvoice_creation());

        //days of tolerance
        LocalDate daysTolerance = serviceTime.plusDays(billing.getDays_of_tolerance());

        if(timeGenerationBilling.isEqual(today)){
            generationBilling(billing);
        } else if (timeGenerationBilling.isAfter(daysTolerance)) {
            if(pay().equals(true)){
                Mono<Boolean> resultAssignPackageClient = connectionMtrService.assignPackageClientInternet(
                        billing.getService().getIdRouter(),
                        billing.getService().getInternetPackage().getName(),
                        billing.getService().getIp_admin());
                Boolean response = resultAssignPackageClient.block();
                return Boolean.TRUE.equals(response);


            }
        }
        return false;

    }


    private boolean createCustomerWithSpecificPaydayWithoutPromotion(Billing billing, LocalDate today){
        return false;
    }
    private boolean createCustomerWithSpecificPaymentDay(Billing billing, LocalDate today) {

        LocalDate timeMonths = today.plusMonths(billing.getCutoff_date());
        LocalDate invoiceDueDate = timeMonths.minusDays(billing.getInvoice_creation());
        LocalDate paymentDay = timeMonths.withDayOfMonth(billing.getPayday());

        if (invoiceDueDate.isEqual(today)) {
            LocalDate toleranceDays = paymentDay.plusDays(billing.getDays_of_tolerance());

            if (toleranceDays.isAfter(today)) {
                if (pay().equals(true)) {
                    return true;
                    //log.info("actualizacion de paquete");
                } else {
                    connectionMtrService.cutCustomerService(
                            billing.getService().getIdRouter(),
                            billing.getUser().getName(),
                            billing.getService().getIp_admin()
                    ).block();
                }
            }
        }
        return false;
    }



    private boolean createCustomerWithoutSpecificPaymentDay( Billing billing, LocalDate today){
        Promotion promotion = getPromotionById(billing.getPromotion());

        Mono<Boolean> responseAssessingPromotion =connectionMtrService.assignPromotion(
                billing.getService().getIdRouter(),
                promotion.getDescription(),
                billing.getService().getIp_admin());
        Boolean  response = responseAssessingPromotion.block();
        if (Boolean.TRUE.equals(response)){

            //time end promotion
            LocalDate promotionEnd = today.plusMonths(promotion.getTimePromotion());
            //calculate the day generate billing
            LocalDate timeGenerationBilling = promotionEnd.minusDays(billing.getInvoice_creation());
            //calculate payday
            //LocalDate payDay =promotionEnd.withDayOfMonth(billing.getPayday());
            //days of tolerance
            LocalDate daysTolerance = promotionEnd.plusDays(billing.getDays_of_tolerance());

            int opt;
            do{
                opt= calculateOption(today,promotionEnd,timeGenerationBilling,daysTolerance);
                switch (opt){
                    case 1:
                        generationBilling(billing);
                        break;
                    case 2:
                        if (pay().equals(true)){
                            //continue with the internet service
                            connectionMtrService.packageChangeClientPPPoE(
                                    billing.getService().getIp_admin(),
                                    promotion.getDescription(),
                                    billing.getService().getIdRouter(),
                                    billing.getService().getInternetPackage().getName()
                            ).block();

                        }else{
                            log.info("not pay");
                        }
                        break;
                    case 3:
                        //cut service
                        connectionMtrService.cutCustomerService(
                                billing.getService().getIdRouter(),
                                billing.getUser().getName(),
                                billing.getService().getIp_admin()
                        ).block();
                        break;
                }
            }while (opt!=0);
        }else{
            log.info("Error occurred while assigning promotion.");
        }
        return false;
    }
    private  Boolean pay(){
       return true;
    }
}

*/
