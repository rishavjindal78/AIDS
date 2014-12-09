package org.shunya.server.engine;

import org.shunya.shared.utils.Utils;

public class UserTaskSelectionState implements TelegramUserState {
    private final TelegramTaskRunner taskRunner;

    public UserTaskSelectionState(TelegramTaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public String process(String input) {
        try {
            Long taskId = Long.parseLong(input);
            taskRunner.setTaskId(taskId);
            int randInt = Utils.randInt(100, 999);
            taskRunner.setRandomNumber(randInt);
            taskRunner.setState(taskRunner.getConfirmState());
            return "To confirm please type : bot " + randInt;
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid TaskId specified, please try again";
        }
    }
}
