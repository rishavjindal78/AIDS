package org.shunya.server.services;

import org.quartz.CronExpression;
import org.shunya.server.MyTask;
import org.shunya.server.ScheduledTaskRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

@Service
public class DynamicJobScheduler implements MyJobScheduler {

    private static final Logger logger = Logger.getLogger(DynamicJobScheduler.class.getName());

    @Autowired
    @Qualifier("myScheduler")
    private TaskScheduler scheduler;

    @Autowired
    @Qualifier("myExecutor")
    private TaskExecutor executor;

    @Autowired
    private DBService dbService;

    @Autowired
    private TaskService taskService;

    private ConcurrentMap<Long, ScheduledFuture> futureConcurrentMap = new ConcurrentHashMap<>();

    public void schedule(String cronExpression, long taskId) {
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(new ScheduledTaskRunner(taskId, dbService, taskService), new CronTrigger(cronExpression));
        futureConcurrentMap.putIfAbsent(taskId, scheduledFuture);
        logger.info(() -> "Task Scheduled - " + taskId);
    }

    @Override
    public void unSchedule(long taskId) {
        futureConcurrentMap.computeIfPresent(taskId, (aLong, scheduledFuture) -> {
                    scheduledFuture.cancel(true);
                    return scheduledFuture;
                }
        );
        futureConcurrentMap.remove(taskId);
        logger.info(() -> "Task UnScheduled - " + taskId);
    }

    @Override
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