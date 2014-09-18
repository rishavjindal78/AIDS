package org.shunya.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicJobScheduler implements MyJobScheduler{

    @Autowired
    @Qualifier("myScheduler")
    private TaskScheduler scheduler;

    @Autowired
    @Qualifier("myExecutor")
    private TaskExecutor executor;

    public void schedule(String cronExpression) {
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(new MyTask(), new CronTrigger(cronExpression));
        scheduledFuture.cancel(true);
    }

    public void unSchedule(){
//        scheduler.
    }
}