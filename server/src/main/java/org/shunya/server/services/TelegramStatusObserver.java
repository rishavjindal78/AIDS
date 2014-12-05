package org.shunya.server.services;

import org.shunya.server.StatusObserver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramStatusObserver implements StatusObserver {
    private List<StatusObserver> observers = new ArrayList<>();

    @Override
    public void notifyStatus(int chatId, boolean notifyStatus, String message) {
        observers.forEach(statusObserver -> statusObserver.notifyStatus(chatId, notifyStatus, message));
    }

    public void register(StatusObserver statusObserver) {
        observers.add(statusObserver);
    }
}
