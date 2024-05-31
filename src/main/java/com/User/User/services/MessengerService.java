package com.User.User.services;

import com.User.User.dto.dtoPromotions.PromotionResponse;
import com.User.User.models.*;
import com.User.User.repository.MessageRepository;
import com.twilio.rest.api.v2010.account.Message;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class MessengerService {
    @Autowired
    private final MessageRepository messageRepository;

    public List<MessageTwilio> findAll(){
        return messageRepository.findAll();
    }
    public Optional<MessageTwilio> findById(Long id){
        return messageRepository.findById(id);
    }
    public MessageTwilio save(MessageTwilio messageTwilio){
        return messageRepository.save(messageTwilio);
    }
    public void deleteById(Long id){
        messageRepository.deleteById(id);
    }



    public void TypeOfSituation(Billing billing, int caseMessage) {
        User user = billing.getUser();
        Servers service = billing.getService();

        MessageTwilio messageTemplate = messageRepository.findByCaseMessage(caseMessage);


        if (messageTemplate != null) {
            String messageTwilio = messageTemplate.getMessageTemplate()
                    .replace("{userName}", user.getName())
                    .replace("{serviceName}", service.getInternetPackage().getName())
                    .replace("{paymentLink}", service.getInternetPackage().getLink())
                    .replace("{currentDate}", LocalDate.now().toString());

            //postMessage(message, user.getMobilePhoneNumber());
        }
    }
}

//    private void postMessage(String requestBody,String number){
//
//
//
//
//
//            //Twilio.init(, AUTH_TOKEN);
//            Message message = Message.creator(
//                    new com.twilio.type.PhoneNumber("whatsapp:+521"+number),
//                    new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
//                    requestBody).create();
//
//            System.out.println(message.getSid());
//
//    }

