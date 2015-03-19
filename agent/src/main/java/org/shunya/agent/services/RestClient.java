package org.shunya.agent.services;

import org.shunya.shared.TaskContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class RestClient {
    private static final Logger logger = Logger.getLogger(RestClient.class.getName());
    private RestTemplate restTemplate = new RestTemplate();

    public void postResultToServer(TaskContext taskContext) {
        String cookie = authenticateGetCookie(taskContext.getBaseUrl(), taskContext.getUsername(), taskContext.getPassword());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie.substring(0, cookie.indexOf(";")));
        HttpEntity<TaskContext> httpEntity = new HttpEntity<>(taskContext, requestHeaders);
        ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
        ResponseEntity<String> responseEntity = restTemplate.exchange(taskContext.getCallbackURL(), HttpMethod.POST, httpEntity, typeRef);
        String body = responseEntity.getBody();
        logger.info(() -> "Posted the Task results back to server - {} " + body+" ID : "+taskContext.getTaskStepRunDTO().getId());
    }

    public String authenticateGetCookie(String baseUrl, String user, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("j_username", user);
        map.add("j_password", password);
        String authURL = baseUrl + "/j_spring_security_check";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, requestHeaders);
        ResponseEntity<String> result = restTemplate.exchange(authURL, HttpMethod.POST, entity, String.class);
        HttpHeaders respHeaders = result.getHeaders();
//        System.out.println(respHeaders.toString());
//        System.out.println(result.getStatusCode());
        String cookies = respHeaders.getFirst("Set-Cookie");
        return cookies;
    }
}
