package org.shunya.agent.services;

import org.shunya.shared.TaskContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class ServerWorker {
    private static final Logger logger = Logger.getLogger(ServerWorker.class.getName());

    private RestTemplate restTemplate = new RestTemplate();

    public void postResultToServer(String hardCodedCallbackURL, TaskContext taskContext) {
        HttpEntity<TaskContext> httpEntity = new HttpEntity<>(taskContext);
        ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
        ResponseEntity<String> responseEntity = restTemplate.exchange(hardCodedCallbackURL, HttpMethod.POST, httpEntity, typeRef);
        String body = responseEntity.getBody();
        logger.info(() -> "Posted the Task results back to server - {} " + body);
    }
}
