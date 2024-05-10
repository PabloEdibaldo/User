package com.User.User.services.ConfifConnectionDHCPandPPPoE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
@Service
public class ConnectionMtrServiceDHCP {
    @Autowired
    private RestTemplate restTemplate;
    public Object PostActionDHCP(String url,Object object ){
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
