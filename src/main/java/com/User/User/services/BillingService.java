package com.User.User.services;
import com.User.User.dto.dtoBilling.BillingRequest;
import com.User.User.dto.dtoBilling.BillingResponse;
import com.User.User.dto.dtoBilling.ContentBillingResponse;
import com.User.User.models.*;
import com.User.User.repository.*;
import com.User.User.services.ConfifConnectionDHCPandPPPoE.ConnectionMtrServiceDHCP;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final ConnectionMtrServiceDHCP connectionMtrServiceDHCP;
    private final ConnectionMtrServicePPPoE connectionMtrServicePPPoE;



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


    //------------------------------------------------------------------------------------------------------
    //Method to create client and initial invoice
    public void createClient(Long idBilling) {
        Billing billing = billingRepository.findById(idBilling).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + idBilling));

                    messengerService.TypeOfSituation(billing, 1);

                    LocalDate firstDayOfNextMonth = LocalDate.now().plusMonths(billing.getCutoff_date()).withDayOfMonth(billing.getCutoff_date());
                    LocalDate lastDayOfMonth = firstDayOfNextMonth.withDayOfMonth(firstDayOfNextMonth.lengthOfMonth());
        createCusBillingObject(billing,firstDayOfNextMonth,lastDayOfMonth);


    }
    //Method to create the ContentBilling entity and save it to the database
    private void createCusBillingObject(@NotNull Billing billing, LocalDate firstDayOfNextMonth, LocalDate lastDayOfMonth ){
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

    public void actionWebHookPayCase(String latestCharge,String typePay ) throws StripeException {
        Charge charge = customerStripe.getAChargeId(latestCharge);

        List<ContentBilling> contentBillingPay = contentBillingRepository.findAll()
                .stream()
                .filter(contentBilling -> !contentBilling.isPay())
                .toList();

        updateBilling(charge,typePay,contentBillingPay);
    }

    private void updateBilling(Charge charge, String typePay, @NotNull List<ContentBilling> contentBillingPay){

        ContentBilling matchingBilling = contentBillingPay.stream()
                .filter(contentBilling -> contentBilling.getGmailClient().equals(charge.getBillingDetails().getEmail()))
                .findFirst()
                .orElse(null);

        if(matchingBilling != null){
            Optional<ContentBilling> optionalContentBilling = contentBillingRepository.findById(matchingBilling.getId());

            if(optionalContentBilling.isPresent()){

                ContentBilling contentBilling1 = optionalContentBilling.get();
                if (charge.getBillingDetails().getEmail().equals(contentBilling1.getGmailClient())&& charge.getStatus().equals("succeeded")) {

                    contentBilling1.setPaymentType(typePay);
                    contentBilling1.setPay(true);

                    createBilling(contentBilling1);

                    messengerService.TypeOfSituation(contentBilling1.getBillingNtp(),3);
                }
            }
        }
    }
    private void createBilling(@NotNull ContentBilling contentBilling){
        LocalDate newFirstDayOfNextMonth =contentBilling.getBillingInit().plusMonths(contentBilling.getBillingNtp().getCutoff_date()).withDayOfMonth(contentBilling.getBillingNtp().getCutoff_date());
        LocalDate newLastDayOfMonth = newFirstDayOfNextMonth.withDayOfMonth(newFirstDayOfNextMonth.lengthOfMonth());

        createCusBillingObject(contentBilling.getBillingNtp(),newFirstDayOfNextMonth,newLastDayOfMonth);
    }
    //Method that verifies invoices and sends messages if it is the invoice creation day
    public void checkBillingAndSendMessages(){
        List<ContentBilling> contentBillings =contentBillingRepository.findAll();
        LocalDate today = LocalDate.now();

        contentBillings.forEach(contentBilling -> {
            if(contentBilling.getBillingCreationBilling().isEqual(today)){
                messengerService.TypeOfSituation(contentBilling.getBillingNtp(), 2);
            }
            if(today.isAfter(contentBilling.getBillingEnd()) && ! contentBilling.isPay()){
                cutService(contentBilling);
            }
        });
    }



    private void cutService(@NotNull ContentBilling contentBilling) {
        messengerService.TypeOfSituation(contentBilling.getBillingNtp(), 4);

        if(contentBilling.getBillingNtp().getService().equals("PPPoE")){
            Map<String, Object> ObjectServiceCutPPPoE = new HashMap<>();

            ObjectServiceCutPPPoE.put("remoteAddress", contentBilling.getBillingNtp().getService().getIp_admin());
            ObjectServiceCutPPPoE.put("idRouter", contentBilling.getBillingNtp().getService().getIdRouter());
             connectionMtrServicePPPoE.PostActionPPPoE("http://localhost:8081/api/QueriesFromOtherMicroservices/cutServiceClientPPPoE/",ObjectServiceCutPPPoE);

        }else if(contentBilling.getBillingNtp().getService().equals("DHCP")){
            Map<String, Object> ObjectServiceCutDCHP = new HashMap<>();

            ObjectServiceCutDCHP.put("macAddress", contentBilling.getBillingNtp().getService().getMac());
            ObjectServiceCutDCHP.put("idRouter", contentBilling.getBillingNtp().getService().getIdRouter());
            ObjectServiceCutDCHP.put("nameClientDHCP", contentBilling.getNameClient());

            connectionMtrServiceDHCP.PostActionDHCP("http://localhost:8081/api/QueriesFromOtherMicroservicesDHCP/cutServiceClientDHCP/",ObjectServiceCutDCHP);

        }

    }

    public List<ContentBillingResponse> consultingBillingId(Long idUser){
        log.info("idUser:{}",idUser);
        List<ContentBilling> contentBillings =  contentBillingRepository.findAll()
                .stream()
                .filter(contentBilling -> contentBilling.getIdClient().equals(idUser))
                .collect(Collectors.toList());
        return contentBillings.stream().map(this::mapToContentBillingsResponse).toList();


    }

    private ContentBillingResponse mapToContentBillingsResponse(@NotNull ContentBilling contentBilling) {
        return ContentBillingResponse.builder()
                .id(contentBilling.getId())
                .packageInternetId(contentBilling.getPackageInternetId())
                .idBilling(contentBilling.getIdBilling())
                .price(contentBilling.getPrice())
                .paymentType(contentBilling.getPaymentType())
                .billingInit(contentBilling.getBillingInit())
                .billingEnd(contentBilling.getBillingEnd())
                .billingCreationBilling(contentBilling.getBillingCreationBilling())
                .billingCreateSystem(contentBilling.getBillingCreateSystem())
                .directionClient(contentBilling.getDirectionClient())
                .gmailClient(contentBilling.getGmailClient())
                .packageInternetName(contentBilling.getPackageInternetName())
                .pay(contentBilling.isPay())
                .typePay(contentBilling.getTypePay())
                .idClient(contentBilling.getIdClient())
                .numberPhoneClient(contentBilling.getNumberPhoneClient())
                .build();
    }





}





















