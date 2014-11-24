package org.shunya.server;

public interface StatusObserver {
    void notifyStatus(int chatId, String message);
}
