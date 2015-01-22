package org.shunya.server.engine;

import org.shunya.shared.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTaskSelectionState implements TelegramUserState {
    private static final Logger logger = LoggerFactory.getLogger(UserTaskSelectionState.class);
    private final TelegramTaskRunner taskRunner;

    public UserTaskSelectionState(TelegramTaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public String process(String input) {
        try {
            Long taskId = Long.parseLong(input);
            taskRunner.setTaskId(taskId);
            taskRunner.setState(taskRunner.getPropertiesState());
            return "Please specify custom properties : bot key=value";
        } catch (Exception e) {
            logger.warn("Invalid TaskId specified - " + input + "\r\n" + StringUtils.getExceptionStackTrace(e));
            return "Invalid TaskId specified, please try again";
        }
    }
}
