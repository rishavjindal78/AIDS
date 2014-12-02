package org.shunya.server;

import org.shunya.server.engine.PeerState;
import org.shunya.server.services.RestClient;
import org.shunya.server.model.TaskRun;
import org.shunya.server.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramStatusObserver implements StatusObserver {
    private List<String> observerUrls = new ArrayList<>();

    @Autowired
    private RestClient restClient;

    @Autowired
    private TelegramService telegramService;

    public void register(String callbackUrl) {
        observerUrls.add(callbackUrl);
    }

    public void unregister(String callbackUrl) {
        observerUrls.remove(callbackUrl);
    }

    @Override
    public void notifyStatus(int chatId, String message) {
        if (chatId != 0)
            telegramService.sendMessage(new PeerState(chatId, false), message);
//        restClient.ping();
//        restClient.sendStatus(callbackUrl, status);
    }
}
