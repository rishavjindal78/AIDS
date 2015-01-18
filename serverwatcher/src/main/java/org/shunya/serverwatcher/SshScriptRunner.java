package org.shunya.serverwatcher;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SshScriptRunner {
    private JSch jsch;
    private Session session;
    private Channel channel;
    private ProxyOutputStream proxyOutputStream;
    private PrintStream printStream;

    public void connect(String hostname,String username,String password) {
        try {
            this.proxyOutputStream = new ProxyOutputStream(Logger.getLogger(this.getClass().getName()), "\nmunish1234");
            jsch = new JSch();
//            jsch.addIdentity("");
            session = jsch.getSession(username, hostname, 22);
            //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
            channel = session.openChannel("shell");
            PipedOutputStream pipeOut = new PipedOutputStream();
            printStream = new PrintStream(pipeOut);
            PipedInputStream pipeIn = new PipedInputStream(pipeOut);
            channel.setOutputStream(proxyOutputStream);
            channel.setInputStream(pipeIn);
            ((ChannelShell) channel).setPtyType("vt102");
            channel.connect(5 * 1000);
            TimeUnit.SECONDS.sleep(2);
            printStream.print("echo munish1234\r");
            printStream.flush();
            proxyOutputStream.waitForToken();
                        } catch (Exception e) {
            e.printStackTrace();
                        }
                    }

    public String[] execute(String... commands) {
        List<String> results=new ArrayList<String>(10);
        for (String command : commands) {
            printStream.print(command + "\r");
            printStream.print("echo munish1234\r");
            printStream.flush();
            results.add(proxyOutputStream.waitForToken());
                }
        return results.toArray(new String[]{});
            }

    public String disconnect() throws InterruptedException {
        printStream.print("exit\r");
        printStream.flush();
        TimeUnit.SECONDS.sleep(1);
            session.disconnect();
            channel.disconnect();
        return proxyOutputStream.getLogs();
    }

    public static void main(String[] args) {
        try {
            SshScriptRunner client= new SshScriptRunner();
            client.connect("xldn4622vdap.ldn.swissbank.com", "chandemu", "Dare13dream$");
            String[] strings = client.execute("df -h /sbcimp/dyn/logfiles/retrotool|grep -vE '^Filesystem|:'|awk '{print $4}'");
            for (String string : strings) {
                String[] split = string.split("[\\n]");
                for (String s : split) {
                    System.out.println("s = " + s);
                }
            }
            System.out.println("strings = " + client.disconnect());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
