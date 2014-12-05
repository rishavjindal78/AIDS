package org.shunya.server;

public class MyTask implements Runnable{

    @Override
    public void run() {
        System.out.println("Hey, you reached me...:)");
    }
}