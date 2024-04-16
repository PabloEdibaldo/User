package com.User.User.services;

import com.User.User.models.Billing;
import com.User.User.models.Servers;
import com.User.User.models.User;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessengerService {


    public void TypeOfSituation(Billing billing, int caseMessage){
        User user = billing.getUser();
        Servers service = billing.getService();
        log.info("servicio:{}",service);

        switch (caseMessage){
            case 1:
                String requestBodyWelcome = "Bienvenido a SOLIT Hola "+user.getName() + "Nos emociona tenerte como parte de nuestra comunidad! Esperamos";


                postMessage(requestBodyWelcome, user.getPhoneNumber());
                break;
//            case 2:
//                String requestBodyPayDayReminder = "{ \"messaging_product\": \"whatsapp\"," +
//                        " \"recipient_type\": \"individual\", " +
//                        "\"to\": \"52"+user.getMobilePhoneNumber()+"\", " +
//                        "\"type\": \"text\", " +
//                        "\"text\": " +
//                        "{ " +
//                        "\"preview_url\": false," +
//                        " \"body\": \" Resivo de facturacion\" " +
//                        "}" +
//                        " }";
//
//                postMessage(requestBodyPayDayReminder);
//                break;
//            case 3:
//                String requestBodyCut = "{ \"messaging_product\": \"whatsapp\"," +
//                        " \"recipient_type\": \"individual\", " +
//                        "\"to\": \"52"+user.getMobilePhoneNumber()+"\", " +
//                        "\"type\": \"text\", " +
//                        "\"text\": " +
//                        "{ " +
//                        "\"preview_url\": false," +
//                        " \"body\": \" Corte de internet \" " +
//                        "}" +
//                        " }";
//                postMessage(requestBodyCut);
//                break;


        }
    }

    private void postMessage(String requestBody,String number){





            //Twilio.init(, AUTH_TOKEN);
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber("whatsapp:+521"+number),
                    new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                    requestBody).create();

            System.out.println(message.getSid());

    }
}
