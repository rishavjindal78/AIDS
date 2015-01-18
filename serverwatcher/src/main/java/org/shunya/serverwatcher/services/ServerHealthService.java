package org.shunya.serverwatcher.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.shunya.serverwatcher.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.shunya.serverwatcher.ServerType.DEV;
import static org.shunya.serverwatcher.ServerType.QA;

@Service
public class ServerHealthService {
    @Value("#{servletContext.contextPath}")
    private String contextPath;

    @Value("${server-app-path}")
    private String serverAppPath;

    @Autowired
    private DynamicJobScheduler jobScheduler;
    private final List<ServerApp> serverApps = new ArrayList<>(100);
    private ServerAppStatus appStatus;
    private final List<NotificationListener> notificationListeners = new ArrayList<>(10);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpClient;

    public void addNotificationListener(NotificationListener listener) {
        notificationListeners.add(listener);
    }

    @PostConstruct
    public void start() {
        // Increase max total connection to 200
        cm.setMaxTotal(50);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectionRequestTimeout(10000).setConnectTimeout(6000).setSocketTimeout(10000).build();
        httpClient = HttpClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig).build();

        loadServerApps(0);
        appStatus = new ServerAppStatus(serverApps, 2000L);
        scheduleAll();
    }

    public void loadServerApps(long id) {
        List<ServerApp> serverAppList = new ServerAppLoader(serverAppPath).getServerApps();
        for (ServerApp serverApp : serverAppList) {
            addServerApp(serverApp);
        }
        if (id > 0L)
            refresh(id);
        else
            refreshAll();
        notificationListeners.clear();
        notificationListeners.add(new EmailListener());
    }

    public void addServerApp(ServerApp... multiServerApp) {
        for (ServerApp serverApp : multiServerApp) {
            int index = Collections.binarySearch(serverApps, serverApp);
            if (index >= 0) {
                serverApps.remove(index);
            }
            serverApps.add(serverApp);
        }
        Collections.sort(serverApps);
    }

    public ServerApp getServerApp(long id) {
        int index = Collections.binarySearch(serverApps, new ServerAppBuilder().withId(id));
        if (index >= 0) {
            return serverApps.get(index);
        }
        return null;
    }

    public void addServerApps(List<ServerApp> serverAppList) {
        serverApps.addAll(serverAppList);
        Collections.sort(serverApps);
    }

    public void scheduleJob(ServerApp app) {
        if (app.getJobScheduleId() != null && !app.getJobScheduleId().isEmpty()) {
            jobScheduler.unSchedule(app);
        }
        jobScheduler.schedule(app.getPingSchedule(), new ServerHealthChecker(app, httpClient, notificationListeners, executorService, appStatus), app);
    }

    public void saveServerApp(ServerApp serverApp){
        try {
            JAXBHelper.persistServerAppConfig(serverAppPath, ServerApp.class, serverApp);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    public void scheduleAll() {
        for (ServerApp app : serverApps) {
            List<String> predict = jobScheduler.predict(app.getPingSchedule(), 5);
            System.out.println("predict = " + predict);
            jobScheduler.schedule(app.getPingSchedule(), new ServerHealthChecker(app, httpClient, notificationListeners, executorService, appStatus), app);
            if (app.getDiskUsageSchedule() != null && !app.getDiskUsageSchedule().isEmpty()) {
                jobScheduler.schedule(app.getDiskUsageSchedule(), new DiskUsageAgent(app), app);
            }
        }
    }

    public void getStatusForServerId(long id, long cacheId, DeferredResult<String> deferredResult) throws InterruptedException, IOException {
        if (cacheId == 0 || cacheId != appStatus.getCacheId()) {
            ServerAppStatus serverAppStatus = appStatus.fetchStatus(id);
            if (serverAppStatus != null) {
                ObjectMapper mapper = new ObjectMapper();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mapper.writeValue(baos, serverAppStatus);
                deferredResult.setResult(baos.toString());
            } else {
                deferredResult.setResult("{\"ERROR\" : \"No Matching ServerApp Found for id - " + id + "\"}");
            }
        } else {
            appStatus.subscribe(id, deferredResult);
        }
    }

    @Async
    public void refreshAll() {
        for (ServerApp app : serverApps) {
            new ServerHealthChecker(app, httpClient, notificationListeners, executorService, appStatus).run();
        }
    }

    @Async
    public void refresh(long id) {
        for (ServerApp app : serverApps) {
            if (app.getId() == id) {
                new ServerHealthChecker(app, httpClient, notificationListeners, executorService, appStatus).run();
            }
        }
    }

    public static void main(String[] args) throws JAXBException, IOException, InterruptedException {
        ServerApp distDevApp = new ServerAppBuilder().withId(1L).withAppName("TestM Dist Dev").withServerType(DEV)
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("UI App Server").withComponentList(
                        new ServerComponentBuilder().withName("UI Server 1").withUrl("http://endevtdm01:8080/tdm").withExpectedTokenString("<title>Login</title>").build(),
                        new ServerComponentBuilder().withName("UI Server 2").withUrl("http://endevtdm03:8080/tdm").withExpectedTokenString("<title>Login</title>").build())
                        .build())
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("Workers").withComponentList(
                        new ServerComponentBuilder().withName("VDG/Upload").withUrl("http://endevtdm04:8080/tdm").withExpectedTokenString("<title>Login</title>").build())
                        .build())
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("ActiveMQ").withComponentList(
                        new ServerComponentBuilder().withName("ActiveMQ 1").withUrl("http://endevtdm02:8161/").withUserName("admin").withPassword("admin").withExpectedTokenString("ActiveMQ").build(),
                        new ServerComponentBuilder().withName("ActiveMQ 2").withUrl("http://endevtdm03:8161/").withUserName("admin").withPassword("admin").withExpectedTokenString("ActiveMQ").build())
                        .build())
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("Infrastructure").withComponentList(
                        new ServerComponentBuilder().withName("JackRabbit").withUrl("http://endevtdm02:9090/jackrabbit/").build(),
                        new ServerComponentBuilder().withName("ES Master").withUrl("http://endevtdm02:9200/_status").build(),
                        new ServerComponentBuilder().withName("Batch-App").withUrl("http://endevtdm05:8080/tdm-batch/").withExpectedTokenString("TDM-Batch").build())
                        .build())
                .withNotificationEmailIds("munish.chandel@edifecs.com")
                .withPingSchedule("*/2 * * * Mon-Fri")
                .withLeadDeveloper("Rishav Jindal (rishav.jindal@edifecs.com)")
                .withContactDL("ENG.UT.TDM.DEV@edifecs.com")
                .withChatChannel("#TestM_help")
                .withDiskComponent(new DiskComponentBuilder().withName("DB Server").withHostname("http://endevtdm05:9991/rest/punter/").withVolume("e:/").withDiskThreshold(90))
                .build();

        ServerApp distQaApp = new ServerAppBuilder().withId(2L).withAppName("TestM Dist QA").withServerType(QA)
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("UI App Server").withComponentList(
                        new ServerComponentBuilder().withName("UI Server 1").withUrl("http://enqaperftdm-01:8080/tdm").withExpectedTokenString("<title>Login</title>").build(),
                        new ServerComponentBuilder().withName("UI Server 2").withUrl("http://enqaperftdm-09:8080/td").withExpectedTokenString("<title>Login</title>").build())
                        .build())
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("Workers").withComponentList(
                        new ServerComponentBuilder().withName("Upload-1").withUrl("http://enqaperftdm-02:8080/tdm").withExpectedTokenString("<title>Login</title>").build(),
                        new ServerComponentBuilder().withName("Upload-2").withUrl("http://enqaperftdm-08:8080/tdm").withExpectedTokenString("<title>Login</title>").build(),
                        new ServerComponentBuilder().withName("VDG-1").withUrl("http://enqaperftdm-05:8080/tdm").withExpectedTokenString("<title>Login</title>").build(),
                        new ServerComponentBuilder().withName("VDG-2/TestRun-2").withUrl("http://enqaperftdm-07:8080/tdm").withExpectedTokenString("<title>Login</title>").build(),
                        new ServerComponentBuilder().withName("TestRun1").withUrl("http://enqaperftdm-06:8080/tdm").withExpectedTokenString("<title>Login</title>").build()
                )
                        .build())
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("ActiveMQ").withComponentList(
                        new ServerComponentBuilder().withName("ActiveMQ 1").withUrl("http://enqaperftdm-02:8161/").withUserName("admin").withPassword("admin").withExpectedTokenString("ActiveMQ").build(),
                        new ServerComponentBuilder().withName("ActiveMQ 2").withUrl("http://enqaperftdm-09:8161/").withUserName("admin").withPassword("admin").withExpectedTokenString("ActiveMQ").build())
                        .build())
                .withComponentGroup(ComponentGroupBuilder.aComponentGroup().withGroupName("Infrastructure").withComponentList(
                        new ServerComponentBuilder().withName("JackRabbit").withUrl("http://enqaperftdm-02:7070/jackrabbit-webapp-2.4.3/").build(),
                        new ServerComponentBuilder().withName("ES Master").withUrl("http://endevtdm02:9200/_status").build(),
                        new ServerComponentBuilder().withName("Batch-App").withUrl("http://enqaperftdm-01:8050/tdm-batch/").withExpectedTokenString("TDM-Batch").build())
                        .build())
                .withNotificationEmailIds("munish.chandel@edifecs.com")
                .withPingSchedule("*/2 * * * Mon-Fri")
                .withLeadDeveloper("Sonali Bhat (sonali.bhat@EDIFECS.COM)")
                .withContactDL("ENG.UT.TDM.QA@edifecs.com")
                .withChatChannel("#TestM_help")
//                .withDiskComponent(new DiskComponentBuilder().withName("DB Server").withHostname("http://endevtdm05:9991/rest/punter/").withVolume("e:/").withDiskThreshold(90))
                .build();

        JAXBHelper.persistServerAppConfig(System.getProperty("user.home") + "/server-apps/", ServerApp.class, distDevApp, distQaApp/*, aisdbApp, APManApp, wbApp, sftApp, fondueApp, maPortal, advTrader, spotLight, reptile*/);
        ServerHealthService serverHealthService = new ServerHealthService();
        serverHealthService.addServerApp(distDevApp);
        serverHealthService.addNotificationListener(new EmailListener());
//        serverHealthManager.start();
    }
}