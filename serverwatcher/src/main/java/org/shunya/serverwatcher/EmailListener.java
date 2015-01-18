package org.shunya.serverwatcher;


public class EmailListener implements NotificationListener {
    @Override
    public void notify(ServerApp serverApp) {
        for (ComponentGroup group : serverApp.getComponentGroups())
            for (ServerComponent component : group.getComponentList()) {
                if (component.isStateChanged()) {
                    System.out.println("sending email for component = " + component.getName() + ", To users - " + serverApp.getNotificationEmailIds());
                    EmailService.getInstance().sendEMail(component.getStatus() + " : " + serverApp.getName() + "/" + component.getName(), serverApp.getNotificationEmailIds(), component.getStatus() + " : " + serverApp.getName() + "/" + component.getName() + " [" + component.getResponse() + ":" + component.getStatusCode() + "]");
                }
            }

        for (DiskComponent component : serverApp.getDiskComponents()) {
            if (component.getDiskUsage() >= component.getDiskThreshold()) {
                System.out.println("sending email for disc component = " + component.getName() + ", To users - " + serverApp.getNotificationEmailIds());
                EmailService.getInstance().sendEMail(component.getName() + " : " + "High Disc Usage = " + component.getDiskUsage() + "%", serverApp.getNotificationEmailIds(), component.getName() + " : " + serverApp.getName() + "/" + component.getName() + " [ Usage -> " + component.getDiskUsage() + "%, Limit - " + component.getDiskThreshold() + "%]");
            }
        }
    }
}
