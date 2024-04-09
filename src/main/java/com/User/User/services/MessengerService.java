package com.User.User.services;

import com.User.User.models.Billing;
import com.User.User.models.Servers;
import com.User.User.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Slf4j
@Service
public class MessengerService {
    private final WebClient webClient;

    public MessengerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void TypeOfSituation(Billing billing, int caseMessage){
        User user = billing.getUser();
        Servers service = billing.getService();
        log.info("servicio:{}",service);

        switch (caseMessage){
            case 1:
                String requestBodyWelcome = "{ " +
                        "\"messaging_product\": \"whatsapp\"," +
                        " \"recipient_type\": \"individual\"," +
                        " \"to\": \"52"+user.getMobilePhoneNumber()+"\", " +
                        "\"type\": \"text\"," +
                        " \"text\": " +
                        "{ " +
                        "\"preview_url\": false," +
                        " \"body\": \"¡Bienvenido a SOLIT! \\n\\nHola "+user.getName()+" " +
                        ",\\\\n\\\\n¡Nos emociona tenerte como parte de nuestra comunidad!\\\\n\\\\nEsperamos" +
                        " que disfrutes de tu conexión a Internet con nosotros. Si tienes alguna pregunta o " +
                        "\necesitas asistencia, no dudes en ponerte en contacto con nuestro equipo de soporte." +
                        "\\\\n\\\\nSaludos cordiales,\\\\nSOLIT\\ \" " +
                        "} " +
                        "}";

                postMessage(requestBodyWelcome);
                break;
            case 2:
                String requestBodyPayDayReminder = "{ \"messaging_product\": \"whatsapp\"," +
                        " \"recipient_type\": \"individual\", " +
                        "\"to\": \"52"+user.getMobilePhoneNumber()+"\", " +
                        "\"type\": \"text\", " +
                        "\"text\": " +
                        "{ " +
                        "\"preview_url\": false," +
                        " \"body\": \" Resivo de facturacion\" " +
                        "}" +
                        " }";

                postMessage(requestBodyPayDayReminder);
                break;
            case 3:
                String requestBodyCut = "{ \"messaging_product\": \"whatsapp\"," +
                        " \"recipient_type\": \"individual\", " +
                        "\"to\": \"52"+user.getMobilePhoneNumber()+"\", " +
                        "\"type\": \"text\", " +
                        "\"text\": " +
                        "{ " +
                        "\"preview_url\": false," +
                        " \"body\": \" Corte de internet \" " +
                        "}" +
                        " }";
                postMessage(requestBodyCut);
                break;


        }
    }

    private void postMessage(String requestBody){

        String numberId = "259436700566783";
        String urls = "https://graph.facebook.com/v18.0/"+numberId+"/messages";
        webClient.post()
                .uri(urls)
                .header("Authorization", "Bearer EAAK8WDjuukUBOzjtBZCixlHUW3qiUv5ZCuZBXIJmZAH1F1SNTE4Ih6HZCr0X7ja12iLZC9ZC3d9wfzZAKE9PAWv2DN3GuKo2G5Vk5QgFuDJRqtACZA8gg5AZCUD0rA5qa2iKhQ93Lzeq4FPFEvg1ytYt0pYqIDGlIflk4XM4sgw0XDBq86x92YZCZCtkRPFR5calsj0zoSnmWmewZC3Npd1a08ScZD")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Error api external"+ response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError,response -> Mono.error(new RuntimeException("Error server api"+ response.statusCode())))
                .bodyToMono(String.class)
                .block();


    }
}
