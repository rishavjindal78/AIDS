package org.shunya.server.services;

public class MyTask implements Runnable{

    @Override
    public void run() {
        System.out.println("Hey, you reached me...:)");
    }
}