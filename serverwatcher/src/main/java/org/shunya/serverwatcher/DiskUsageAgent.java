package org.shunya.serverwatcher;

import java.util.List;

public class DiskUsageAgent implements Runnable {
    private final SshScriptRunner sshScriptRunner;
    private final ServerApp serverApp;

    public DiskUsageAgent(ServerApp serverApp) {
        this.serverApp = serverApp;
        this.sshScriptRunner = new SshScriptRunner();
    }

    @Override
    public void run() {
        try {
            List<DiskComponent> diskComponents = serverApp.getDiskComponents();
            for (DiskComponent diskComponent : diskComponents) {
                if (diskComponent.getHostname() != null && !diskComponent.getHostname().isEmpty() && diskComponent.getVolume()!=null && !diskComponent.getVolume().isEmpty()) {
                    sshScriptRunner.connect(diskComponent.getHostname(), serverApp.getUsername(), serverApp.getPassword());
                    diskComponent.setDiskUsage(extractDiskUsageFromResponse(sshScriptRunner.execute("df -h "+diskComponent.getVolume()+"|grep -vE '^Filesystem|:'|awk '{print $4}'")[0]));
                    sshScriptRunner.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double extractDiskUsageFromResponse(String response) {
        String[] split = response.split("[\\n]");
        String diskUsage = split[2].substring(0, split[2].indexOf('%'));
        return Double.parseDouble(diskUsage);
    }
}
