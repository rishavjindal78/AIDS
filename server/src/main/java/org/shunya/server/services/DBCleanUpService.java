package org.shunya.server.services;

import org.shunya.server.model.TaskRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.logging.Logger;

public class DBCleanUpService {
    private static final Logger logger = Logger.getLogger(DBCleanUpService.class.getName());

    @Value("${maxTaskRunAge}")
    private int maxTaskRunAge;

    @Autowired
    private DBService dbService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void cleanOldTaskHistory() {
        logger.info(() -> "Running TaskRun Cleanup Job for Max Age - " + maxTaskRunAge);
        List<TaskRun> taskHistoryByAge = dbService.findTaskHistoryByAge(maxTaskRunAge);
        taskHistoryByAge.forEach(taskRun -> dbService.deleteTaskRun(taskRun.getId()));
        logger.info(() -> "Job TaskRun Cleanup completed for Max Age - " + maxTaskRunAge + ", deleted entries - " + taskHistoryByAge.size());
    }
}
