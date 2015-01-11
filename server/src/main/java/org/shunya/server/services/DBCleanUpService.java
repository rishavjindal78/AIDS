package org.shunya.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.shunya.server.model.Task;
import org.shunya.server.model.TaskRun;
import org.shunya.shared.RunState;
import org.shunya.shared.RunStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

public class DBCleanUpService {
    private static final Logger logger = Logger.getLogger(DBCleanUpService.class.getName());

    @Value("${maxTaskRunAge}")
    private int maxTaskRunAge;

    @Value("${maxSystemFailureTimeInHours}")
    private int maxSystemFailureTimeInHours;

    @Value("${db.path}")
    private String aids_home;

    @Autowired
    private DBService dbService;

    @Autowired
    private TaskService taskService;

    //    @Scheduled(cron = "0 0/2 * * * ?")
    @Scheduled(cron = "${cleanOldTaskHistory.cron.expression}")
    public void cleanOldTaskHistory() {
        logger.info(() -> "Running TaskRun Cleanup Job for Max Age - " + maxTaskRunAge);
        List<TaskRun> taskHistoryByAge = dbService.findTaskHistoryByAge(maxTaskRunAge);
        taskHistoryByAge.forEach(taskRun -> dbService.deleteTaskRun(taskRun.getId()));
        logger.info(() -> "Job TaskRun Cleanup completed for Max Age - " + maxTaskRunAge + ", deleted entries - " + taskHistoryByAge.size());
    }

    @Scheduled(cron = "0 0/2 * * * ?")
    public void synchronizeTasks1(){
        logger.info(() -> "Running TaskRun Synchronization");
        List<TaskRun> runningTasks = dbService.findRunningTasks();
        runningTasks.forEach(taskRun -> {
            if(!taskService.isTaskRunning(taskRun)){
                taskRun.setRunState(RunState.COMPLETED);
                taskRun.setRunStatus(RunStatus.FAILURE);
                dbService.save(taskRun);
            }
        });
    }

    @Scheduled(cron = "${backupTasks.cron.expression}")
    public void backUpTasks() {
        logger.info(() -> "running daily backup all the Tasks into aids home - " + aids_home);
        List<Task> tasks = dbService.listTasks();
        tasks.forEach(task -> {
            Task taskToSave = dbService.getTask(task.getId());
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (FileWriter fileWriter = new FileWriter(new File(aids_home, task.getId() + ".json"))) {
                mapper.writeValue(baos, taskToSave);
                IOUtils.write(baos.toByteArray(), fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
