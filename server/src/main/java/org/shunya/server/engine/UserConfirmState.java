package org.shunya.server.engine;

public class UserConfirmState implements TelegramUserState {
    private final TelegramTaskRunner taskRunner;
    private int maxRetryCount = 2;

    public UserConfirmState(TelegramTaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public String process(String input) {
        try {
            int userInput = Integer.parseInt(input);
            if (taskRunner.getRandomNumber() == userInput && maxRetryCount > 0) {
                taskRunner.setState(taskRunner.getInputState());
                return taskRunner.execute();
            } else if (maxRetryCount <= 0) {
                maxRetryCount = 2;
                taskRunner.setState(taskRunner.getInputState());
                return "Max retry limit reached, resetting trx";
            } else {
                --maxRetryCount;
                return "You have typed wrong confirmation number, please try again. To reset trx, type reset";
            }
        } catch (Exception e) {
            return "You have typed wrong confirmation number, please try again. To reset trx, type reset";
        }
    }

}
