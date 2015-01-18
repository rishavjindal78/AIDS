package org.shunya.serverwatcher;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class ProxyOutputStream extends OutputStream {
    private StringBuilder logs = new StringBuilder(10000);
    private StringBuilder buffer = new StringBuilder(10000);
    private StringBuilder lineBuffer = new StringBuilder(100);
    private volatile boolean found = false;
    private String token;

    public ProxyOutputStream(Logger logger, String token) {
        this.token = token;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        char theChar = (char) b;
        buffer.append(theChar);
        logs.append(theChar);
        if (theChar == '\n') {
            writeLine(lineBuffer.toString());
            lineBuffer.delete(0, lineBuffer.length());
            lineBuffer.setLength(0);
        } else {
            lineBuffer.append(theChar);
        }
        if (buffer.toString().contains(token)) {
            resetToken();
//            buffer.append(" -- Found token.." + "\r\n");
//            logs.append(" -- Found token.." + "\r\n");
            found = true;
            notifyAll();
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeLine(String line) {
//        logger.log(Level.INFO, line);
    }

    public synchronized String waitForToken() {
        try {
            while (!found)
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String lastResult = buffer.toString();
        buffer.delete(0, buffer.length());
        buffer.setLength(0);
        found = false;
        notifyAll();
        return lastResult;
    }

    public String getLogs() {
        return logs.toString();
    }
    private void resetToken(){
        token = "\nmunish1234";
    }
    public void setToken(String token) {
        this.token = token;
    }
}
