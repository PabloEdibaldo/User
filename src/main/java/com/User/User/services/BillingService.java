package com.User.User.services;
import com.User.User.dto.dtoBilling.BillingRequest;
import com.User.User.dto.dtoBilling.BillingResponse;
import com.User.User.models.*;
import com.User.User.repository.*;
import com.User.User.services.ConfifConnectionDHCPandPPPoE.ConnectionMtrServicePPPoE;
import com.User.User.services.apiMercadoLible.CustomerStripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingService {
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final MessengerService messengerService;
    private final CustomerStripe customerStripe;
    private final ContentBillingRepository contentBillingRepository;


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
        log.info("user:{} ", user.getId());
        Servers service = serviceRepository.findById(serviceId).orElseThrow(() -> new EntityNotFoundException("service  not found with ID: " + billingRequest.getServiceId()));
        log.info("service:{}", service.getId());
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

    private void createCusBillingObject(Billing billing,LocalDate firstDayOfNextMonth,LocalDate lastDayOfMonth ){
        ContentBilling contentBilling = ContentBilling.builder()
                .nameClient(billing.getUser().getName())
                .packageInternetId(billing.getService().getInternetPackage().getId())
                .idBilling(billing.getId())
                .price(billing.getService().getPrice())
                .paymentType("na")
                .billingInit(firstDayOfNextMonth)
                .billingEnd(lastDayOfMonth)
                .billingCreationBilling(lastDayOfMonth.minusDays(billing.getInvoice_creation()))
                .billingCreateSystem(LocalDate.now())
                .directionClient(billing.getUser().getMainDirection())
                .gmailClient(billing.getUser().getEmail())
                .packageInternetName(billing.getService().getInternetPackage().getName())
                .pay(false)
                .typePay("na")
                .idClient(billing.getUser().getId())
                .billingNtp(billing)
                .numberPhoneClient(billing.getUser().getMobilePhoneNumber())
                .build();

        contentBillingRepository.save(contentBilling);
    }

    //------------------------------------------------------------------------------------------------------
    public void createClient(Long idBilling) {
        Billing billing = billingRepository.findById(idBilling).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + idBilling));
                    log.info("Client created without promotion and with specific payment day.");
                    messengerService.TypeOfSituation(billing, 1);

                    LocalDate firstDayOfNextMonth = LocalDate.now().plusMonths(billing.getCutoff_date()).withDayOfMonth(billing.getCutoff_date());
                    LocalDate lastDayOfMonth = firstDayOfNextMonth.withDayOfMonth(firstDayOfNextMonth.lengthOfMonth());
        createCusBillingObject(billing,firstDayOfNextMonth,lastDayOfMonth);


    }

    public void sendMessageClientNotPay(){


    }




    public void actionWebHookPayCase(String latestCharge,String typePay ) throws StripeException {
        Charge charge = customerStripe.getAChargeId(latestCharge);



        List<ContentBilling> contentBillingPay = contentBillingRepository.findAll()
                .stream()
                .filter(contentBilling -> !contentBilling.isPay())
                .toList();


        for (ContentBilling contentBillingNotPay:contentBillingPay){
            log.info("contentBillingNotPay:{}",contentBillingNotPay.getNameClient());
            if(contentBillingNotPay.getBillingCreationBilling().isEqual(LocalDate.now())) {
                messengerService.TypeOfSituation(contentBillingNotPay.getBillingNtp(), 2);
            }
        }
          updateBilling(charge,typePay,contentBillingPay);


    }




    private void updateBilling(Charge charge,String typePay,List<ContentBilling> contentBillingPay){

        ContentBilling matchingBilling = contentBillingPay.stream()
                .filter(contentBilling -> contentBilling.getGmailClient().equals(charge.getBillingDetails().getEmail()))
                .findFirst()
                .orElse(null);
//        log.info("matchingBilling.getNameClient():{}",matchingBilling.getNameClient());
//
//        Optional<ContentBilling> optionalContentBilling = contentBillingRepository.findById(matchingBilling.getId());
//
//        if(optionalContentBilling.isPresent()){
//            ContentBilling contentBilling1 = optionalContentBilling.get();
//
//            if (charge.getBillingDetails().getPhone().equals(contentBilling1.getBillingNtp().getUser().getMobilePhoneNumber()) &&
//                    charge.getBillingDetails().getEmail().equals(contentBilling1.getGmailClient())) {
//
//                contentBilling1.setPaymentType(typePay);
//                contentBilling1.setPay(true);
//
//                createBilling(
//                        contentBilling1.getBillingInit(),
//                        contentBilling1.getBillingEnd(),
//                        contentBilling1.getBillingCreationBilling(),
//                        contentBilling1.getBillingCreateSystem(),
//                        contentBilling1.getBillingNtp());
//
//                messengerService.TypeOfSituation(contentBilling1.getBillingNtp(),3);
//
//
//            }else{
//                if (LocalDate.now().isAfter(contentBilling1.getBillingEnd())) {
//                    cutService(contentBilling1);
//
//                }
//            }
//        }
    }

    private void cutService(ContentBilling contentBilling) {
        sendMessageClientNotPay();
    }

    private void createBilling(
            LocalDate billingInit,
            LocalDate billingEnd,
            LocalDate BillingCreationBilling,
            LocalDate billingCreateSystem,
            Billing contentBilling){
        LocalDate newFirstDayOfNextMonth = billingInit.plusMonths(contentBilling.getCutoff_date()).withDayOfMonth(contentBilling.getCutoff_date());
        LocalDate newLastDayOfMonth = newFirstDayOfNextMonth.withDayOfMonth(newFirstDayOfNextMonth.lengthOfMonth());

        createCusBillingObject(contentBilling,newFirstDayOfNextMonth,newLastDayOfMonth);

    }


}

























/*
*
*
*  List<ContentBilling> contentBilling = contentBillingRepository.findAll()
                    .stream().
                    filter(
                            i -> Objects.equals(
                                    i.getBillingNtp().getUser().getEmail(),
                                    charge.getBillingDetails().getEmail()))
                    .toList();

* */



/*
if (charge.getBillingDetails().getPhone().equals(existingBilling.getUser().getMobilePhoneNumber()) &&
                        charge.getBillingDetails().getEmail().equals(existingBilling.getUser().getEmail())) {

                    charge.getAmount();
                }
                * */




//
//    private boolean createClientWithPromotionAndSpecificPayDay(@NotNull Billing billing, LocalDate today){
//        Promotion promotion =getPromotionById(billing.getPromotion());
//
//        if(promotion != null){
//            Mono<Boolean> responseAssessingPromotion = connectionMtrServicePPPoE.assignPromotion(
//                    billing.getService().getIdRouter(),
//                    promotion.getDescription(),
//                    billing.getService().getIp_admin());
//
//            Boolean  response = responseAssessingPromotion.block();
//            if(Boolean.TRUE.equals(response)){
//                return handleSpecificPayDay(billing ,today);
//            }
//        }
//        return false;
//    }
//    private boolean createClientWithPromotionNoSpecificPayDay(@NotNull Billing billing, LocalDate today){
//        Promotion promotion = getPromotionById(billing.getPromotion());
//
//        if(promotion != null){
//            Mono<Boolean> responseAssessingPromotion = connectionMtrServicePPPoE.assignPromotion(
//                    billing.getService().getIdRouter(),
//                    promotion.getDescription(),
//                    billing.getService().getIp_admin());
//
//            Boolean  response = responseAssessingPromotion.block();
//            if(Boolean.TRUE.equals(response)){
//                return handlePromotionWithoutSpecificPayDay(billing ,today ,promotion);
//            }
//        }
//        return false;
//    }
//    private boolean createClientNoPromotionWithSpecificPayDay(Billing billing ,LocalDate today){
//        return handleSpecificPayDay(billing,today);
//    }
//    private boolean createClientNoPromotionNoSpecificPayDay(Billing billing, LocalDate today) {
//        return handleNoPromotionNoSpecificPayDay(billing, today);
//    }
//
//
//
//
//
//
//
//
//    public boolean handleSpecificPayDay(@NotNull Billing billing, @NotNull LocalDate today){
//    LocalDate timeMonths = today.plusMonths(billing.getCutoff_date());
//    LocalDate invoiceDieDate = timeMonths.minusDays(billing.getInvoice_creation());
//    LocalDate paymentDay = timeMonths.withDayOfMonth(billing.getPayday());
//
//    if(invoiceDieDate.isEqual(today)){
//        //generationBilling(invoiceDieDate,timeMonths,billing.getService().getPrice());
//    }else if(invoiceDieDate.isEqual(paymentDay)){
//        LocalDate toleranceDays = paymentDay.plusDays(billing.getDays_of_tolerance());
//        if(toleranceDays.isEqual(today)){
//            if(pay()){
//                return assignPackageClientInternet(billing);
//            }else {
//                cutCustomerService(billing);
//            }
//        }
//    }
//    return assignPackageClientInternet(billing);
//    }
//    private boolean handlePromotionWithoutSpecificPayDay(@NotNull Billing billing , @NotNull LocalDate today, @NotNull Promotion promotion){
//        LocalDate promotionEnd = today.plusDays(promotion.getTimePromotion());
//        LocalDate timeGenerationBilling = promotionEnd.minusDays(billing.getInvoice_creation());
//        LocalDate daysTolerance = promotionEnd.plusDays(billing.getDays_of_tolerance());
//
//        int option = calculateOption(today,promotionEnd,timeGenerationBilling,daysTolerance);
//        switch (option){
//            case 1:
//                //generationBilling(timeGenerationBilling,promotionEnd,billing.getService().getPrice());
//                return true;
//
//            case 2:
//                if(pay()){
//                    return updateClientPackage(billing,promotion.getDescription());
//                }else {
//                    log.info("no payment received");
//                }
//                break;
//            case 3:
//                cutCustomerService(billing);
//                break;
//        }
//        return false;
//    }
//
//    private boolean handleNoPromotionNoSpecificPayDay(@NotNull Billing billing, @NotNull LocalDate today){
//        LocalDate serviceTime = today.plusMonths(billing.getCutoff_date());
//        LocalDate timeGenerationBilling = serviceTime.minusDays(billing.getInvoice_creation());
//        LocalDate daysTolerance = serviceTime.plusDays(billing.getDays_of_tolerance());
//
//        if (timeGenerationBilling.isEqual(today)){
//           // generationBilling(timeGenerationBilling,serviceTime,billing.getService().getPrice());
//            return true;
//        }else if(timeGenerationBilling.isAfter(daysTolerance)){
//            if(pay()){
//                return assignPackageClientInternet(billing);
//            }
//
//        }
//        return false;
//    }
//
//    private void cutCustomerService(@NotNull Billing billing){
//        connectionMtrServicePPPoE.cutCustomerService(
//                billing.getService().getIdRouter(),
//                billing.getUser().getName(),
//                billing.getService().getIp_admin()
//        );
//
//    }
//    private boolean updateClientPackage(@NotNull Billing billing, String descriptionPromotion) {
//        return Boolean.TRUE.equals(connectionMtrServicePPPoE.packageChangeClientPPPoE(
//                billing.getService().getIp_admin(),
//                descriptionPromotion,
//                billing.getService().getIdRouter(),
//                billing.getService().getInternetPackage().getName()
//        ).block());
//    }
//    private boolean assignPackageClientInternet(@NotNull Billing billing){
//       return true;
//    }
//
//    private boolean pay() {
//        return false;
//    }
//
//    private void generationBilling(LocalDate timeGenerationBilling,LocalDate serviceTime,int pricePackage) {
//        //invoiceServices.createInvoiceService();
//
//    }
//
//
//
//
//
//    private int calculateOption(LocalDate today, LocalDate promotionEnd, @NotNull LocalDate timeGenerationBilling, LocalDate daysTolerance) {
//        if (timeGenerationBilling.isEqual(today)) {
//            return 1;
//        } else if (promotionEnd.isEqual(today)) {
//            if(promotionEnd.isAfter(daysTolerance)){
//                return pay() ? 2 : 3;
//            }
//        }
//        return 0;
//    }
//    private Promotion getPromotionById(Long promotionId) {
//        return promotionRepository.findById(promotionId)
//                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with ID: " + promotionId));
//    }
//}
//




