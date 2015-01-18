package org.shunya.serverwatcher.services;

import org.shunya.serverwatcher.ServerApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

@Service
public class DynamicJobScheduler {
    private static final Logger logger = Logger.getLogger(DynamicJobScheduler.class.getName());

    @Autowired
    @Qualifier("myScheduler")
    private TaskScheduler scheduler;

    @Autowired
    @Qualifier("myExecutor")
    private TaskExecutor executor;

    private ConcurrentMap<ServerApp, ScheduledFuture<?>> futureConcurrentMap = new ConcurrentHashMap<>();

    public void schedule(String cronExpression, Runnable runnable, ServerApp serverApp) {
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(runnable, new CronTrigger(cronExpression));
        futureConcurrentMap.putIfAbsent(serverApp, scheduledFuture);
        logger.info(() -> "Task Scheduled - " + serverApp);
    }

    public void unSchedule(ServerApp serverApp) {
        futureConcurrentMap.computeIfPresent(serverApp, (aLong, scheduledFuture) -> {
                    scheduledFuture.cancel(true);
                    return scheduledFuture;
                }
        );
        futureConcurrentMap.remove(serverApp);
        logger.info(() -> "Task UnScheduled - " + serverApp);
    }

    public List<String> predict(String cronExpression, int times) {
        List<String> results = new ArrayList<>();
        try {
            CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
            System.out.println(generator.toString());
            Date seedDate = new Date();
            for (int i = 0; i < times; i++) {
                Date date = generator.next(seedDate);
                results.add(date.toString());
                seedDate = date;
            }
        } catch (Exception e) {
            logger.info(() -> "Error in Cron Expression - " + e.getMessage());
            results.add(e.getMessage());
        }
        return results;
    }
}