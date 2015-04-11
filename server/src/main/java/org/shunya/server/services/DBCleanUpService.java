package org.shunya.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.shunya.server.model.Task;
import org.shunya.server.model.TaskRun;
import org.shunya.shared.RunState;
import org.shunya.shared.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.util.Arrays.asList;

@Service
public class DBCleanUpService {
    private static final Logger logger = LoggerFactory.getLogger(DBCleanUpService.class.getName());

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
        logger.debug("Running TaskRun Cleanup Job for Max Age - " + maxTaskRunAge);
        List<TaskRun> taskHistoryByAge = dbService.findTaskHistoryByAge(maxTaskRunAge);
        if (!taskHistoryByAge.isEmpty()) {
            taskHistoryByAge.forEach(taskRun -> dbService.deleteTaskRun(taskRun.getId()));
            logger.info("Job TaskRun Cleanup completed for Max Age - " + maxTaskRunAge + ", deleted entries - " + taskHistoryByAge.size());
        }
    }

    //    @Scheduled(cron = "0 0/2 * * * ?")
    @PostConstruct
    public void synchronizeTasksWhenServerIsRestarted() {
        logger.debug("Running TaskRun Synchronization after server startup");
        List<TaskRun> runningTasks = dbService.findRunningTasks();
        runningTasks.forEach(taskRun -> {
            if (!taskService.isTaskRunning(taskRun)) {
                logger.warn("Fixing TaskRun - " + taskRun.getId() + " - " + taskRun.getName());
                taskRun.setRunState(RunState.COMPLETED);
                taskRun.setRunStatus(RunStatus.FAILURE);
                dbService.save(taskRun);
                taskRun = dbService.getTaskRun(taskRun.getId());
                taskRun.getTaskStepRuns().forEach(taskStepRun -> {
                    if (taskStepRun.getRunState() == RunState.RUNNING || taskStepRun.getRunStatus() == RunStatus.RUNNING) {
                        taskStepRun.setRunState(RunState.COMPLETED);
                        taskStepRun.setRunStatus(RunStatus.FAILURE);
                        dbService.save(taskStepRun);
                    }
                });
            }
        });
    }

    @Scheduled(cron = "${backupTasks.cron.expression}")
    public void backUpTasks() {
        logger.debug("running daily backup all the Tasks into aids home - " + aids_home);
        List<Task> tasks = dbService.listAllTasks();
        tasks.forEach(task -> {
            Task taskToSave = dbService.getTask(task.getId());
            if (taskToSave.getDateUpdated() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy_HHmm");
                String fileName = task.getId() + "_" + sdf.format(taskToSave.getDateUpdated()) + ".json";
                File targetFile = new File(aids_home, fileName);
                if (!targetFile.exists()) {
                    logger.info("Backing up Task - " + fileName);
                    ObjectMapper mapper = new ObjectMapper();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (FileWriter fileWriter = new FileWriter(targetFile)) {
                        mapper.writerWithDefaultPrettyPrinter().writeValue(baos, asList(taskToSave));
                        IOUtils.write(baos.toByteArray(), fileWriter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
