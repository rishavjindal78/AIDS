package org.shunya.server;

import org.shunya.server.services.RestClient;
import org.shunya.shared.model.TaskRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestfulStatusObserver implements StatusObserver {
    private List<String> observerUrls = new ArrayList<>();

    @Autowired
    private RestClient restClient;

    public void register(String callbackUrl){
        observerUrls.add(callbackUrl);
    }

    public void unregister(String callbackUrl){
        observerUrls.remove(callbackUrl);
    }

    @Override
    public void notifyStatus(TaskRun taskRun, String message) {
//        restClient.ping();
//        restClient.sendStatus(callbackUrl, status);
    }
}
