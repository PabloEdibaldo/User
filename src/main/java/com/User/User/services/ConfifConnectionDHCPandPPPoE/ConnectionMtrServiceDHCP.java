package com.User.User.services.ConfifConnectionDHCPandPPPoE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConnectionMtrServiceDHCP {
    @Autowired
    private RestTemplate restTemplate;
    public void PostActionDHCP(String url,Object object ){
        HttpEntity<Object> requestEntity = new HttpEntity<>(object);

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Object.class);


        //return response.getBody();
    }
}
