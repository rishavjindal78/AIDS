package org.shunya.serverwatcher;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import static org.apache.http.util.EntityUtils.consume;

public class ServerHealthChecker implements Runnable {
    private static final Logger logger = Logger.getLogger(ServerHealthChecker.class.getName());

    private ServerApp serverApp;
    private List<NotificationListener> notificationListeners;
    private final CloseableHttpClient httpClient;
    private final ExecutorService executorService;
    private final ServerAppStatus appStatus;

    public ServerHealthChecker(ServerApp serverApp, CloseableHttpClient httpClient, List<NotificationListener> notificationListeners, ExecutorService executorService, ServerAppStatus appStatus) {
        this.serverApp = serverApp;
        this.notificationListeners = notificationListeners;
        this.httpClient = httpClient;
        this.executorService = executorService;
        this.appStatus = appStatus;
    }

    public void authenticateAndCheckServerStatus(ServerApp app) throws AuthenticationException, IOException {
        for (ComponentGroup group : app.getComponentGroups()) {
            for (ServerComponent component : group.getComponentList()) {
                try {
                    ServerResponse serverResponse = getServerStatus(httpClient, component);
                    component.setResponse(serverResponse.getResponse());
                    component.setException(serverResponse.getException());
                    component.setStatusCode(serverResponse.getStatusCode());
                    validateResponse(component, serverResponse, responseCodeValidator, stringTokenValidator, setServerStatusUp);
                } catch (Exception e) {
                    logger.fine("Exception getting server component status - " + StringUtils.getExceptionHeaders(e));
                }
            }
            group.calculateStatus();
        }
        app.calculateStatus();
    }

    public void getDiscSpace(ServerApp app) throws AuthenticationException, IOException {
       /* for (DiskComponent component : app.getDiskComponents()) {
            try {
                DiscUsageAgent2 agent2 = new DiscUsageAgent2();
                int discSpace = agent2.getDiscSpace(component.getHostname(), component.getVolume());
                component.setDiskUsage(discSpace);
            } catch (Exception e) {
                component.setDiskUsage(0);
            }
        }*/
    }

    private boolean isRcasCookieExpired(ServerApp app) {
        return app.getCookieStore() == null;
    }

    private boolean isRcasAuthRequired(ServerApp app) {
        return app.getRcasUrl() != null && app.getUsername() != null;
    }

    private void validateResponse(ServerComponent component, ServerResponse serverResponse, ResponseValidator... validators) {
        for (ResponseValidator validator : validators) {
            if (validator.validate(component, serverResponse) == false)
                break;
        }
    }

    private ResponseValidator responseCodeValidator = (component, serverResponse) -> {
        for (int responseCode : component.getExpectedResponseCode()) {
            if (responseCode == serverResponse.getStatusCode())
                return true;
        }
        component.setStatus(ServerStatus.SCM);
        return false;
    };

    private ResponseValidator stringTokenValidator = (component, serverResponse) -> {
        if (component.getExpectedTokenString() != null && !component.getExpectedTokenString().isEmpty()) {
            if (serverResponse.getContent() != null && !serverResponse.getContent().contains(component.getExpectedTokenString())) {
                component.setStatus(ServerStatus.TM);
                return false;
            }
        }
        return true;
    };

    private ResponseValidator setServerStatusUp = (component, serverResponse) -> {
        component.setStatus(ServerStatus.UP);
        return true;
    };

    public ServerResponse getServerStatus(CloseableHttpClient httpClient, ServerComponent component) {
        ServerResponse serverResponse = new ServerResponse();
        CloseableHttpResponse response = null;
        try {
            if (component.getUsername() != null && !component.getUsername().isEmpty()) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials(component.getUsername(), component.getPassword()));
                HttpClientContext context = HttpClientContext.create();
                context.setCredentialsProvider(credsProvider);
                response = httpClient.execute(new HttpGet(component.getUrl()), context);
            } else {
                response = httpClient.execute(new HttpGet(component.getUrl()));
            }
            serverResponse.setResponse("" + response.getStatusLine().getReasonPhrase());
            serverResponse.setStatusLine("" + response.getStatusLine());
            serverResponse.setStatusCode(response.getStatusLine().getStatusCode());
//            System.out.println(url + " = " + response.getStatusLine());
            serverResponse.setContent(IOUtils.toString(response.getEntity().getContent()));
        } catch (IOException e) {
            serverResponse.setStatus(ServerStatus.DOWN);
            serverResponse.setResponse(e.getMessage());
            serverResponse.setException(StringUtils.getExceptionHeaders(e));
            logger.fine("Exception getting server component status - " + StringUtils.getExceptionHeaders(e));
        } catch (Exception e) {
            serverResponse.setResponse(e.toString());
            serverResponse.setStatus(ServerStatus.DOWN);
            serverResponse.setException(StringUtils.getExceptionHeaders(e));
            logger.fine("Exception getting server component status - " + StringUtils.getExceptionHeaders(e));
        } finally {
            try {
                consume(response.getEntity());
                response.close();
            } catch (Exception e) {
                //swallow it
            }
        }
        return serverResponse;
    }

    @Override
    public void run() {
        executorService.submit(() -> {
            try {
                logger.info(() -> "Scanning servers - " + serverApp.getId() + " - " + serverApp.getName());
                serverApp.setLastStatusUpdateTime(LocalDateTime.now());
                authenticateAndCheckServerStatus(serverApp);
                getDiscSpace(serverApp);
                for (NotificationListener notificationListener : notificationListeners) {
                    notificationListener.notify(serverApp);
                }
            } catch (Exception e) {
                serverApp.setServerResponse(StringUtils.getExceptionHeaders(e));
                logger.fine("Exception getting server app status - " + StringUtils.getExceptionHeaders(e));
            } finally {
                appStatus.updateCacheId();
            }
        });
    }
}
