package org.shunya.serverwatcher;


public class EmailListener implements NotificationListener {
    @Override
    public void notify(ServerApp serverApp) {
        if (serverApp.isStateChanged()) {
            System.out.println("sending email for ServerApp = " + serverApp.getName() + ", To users - " + serverApp.getNotificationEmailIds());
            String body = "";
            for (ComponentGroup group : serverApp.getComponentGroups())
                for (ServerComponent component : group.getComponentList()) {
                    if (component.isStateChanged()) {
                        body += component.getStatus() + " : " + serverApp.getName() + "/" + component.getName() + " [" + component.getResponse() + ":" + component.getStatusCode() + "]\r\n";

                    }
                }
            EmailService.getInstance().sendEMail(serverApp.getStatus() + " : " + serverApp.getName(), serverApp.getNotificationEmailIds(), body);
            /*for (DiskComponent component : serverApp.getDiskComponents()) {
                if (component.getDiskUsage() >= component.getDiskThreshold()) {
                    System.out.println("sending email for disc component = " + component.getName() + ", To users - " + serverApp.getNotificationEmailIds());
                    EmailService.getInstance().sendEMail(component.getName() + " : " + "High Disc Usage = " + component.getDiskUsage() + "%", serverApp.getNotificationEmailIds(), component.getName() + " : " + serverApp.getName() + "/" + component.getName() + " [ Usage -> " + component.getDiskUsage() + "%, Limit - " + component.getDiskThreshold() + "%]");
                }
            }*/
        }
    }
}
