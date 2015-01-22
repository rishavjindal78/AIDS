package org.shunya.server.engine;

import org.shunya.shared.StringUtils;
import org.shunya.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverrideInputState implements TelegramUserState {
    private static final Logger logger = LoggerFactory.getLogger(OverrideInputState.class);

    private final TelegramTaskRunner taskRunner;

    public OverrideInputState(TelegramTaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public String process(String input) {
        try {
            if (input != null && !input.isEmpty())
                taskRunner.setValuesToOverride(Utils.splitToMap(input, ",", "="));
            int randInt = Utils.randInt(100, 999);
            taskRunner.setRandomNumber(randInt);
            taskRunner.setState(taskRunner.getConfirmState());
            return "To confirm please type : bot " + randInt;
        } catch (Exception e) {
            logger.warn("Invalid properties specified - " + input + "\r\n" + StringUtils.getExceptionStackTrace(e));
            return "Invalid properties specified, please try again, or type bot reset";
        }
    }
}
