package org.shunya.server.controller;

import org.shunya.server.engine.PeerState;
import org.shunya.server.engine.TaskStatusMsg;
import org.shunya.server.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;

/**
 * Created by Aman Verma on 11/23/2014.
 */

@Controller
@RequestMapping("/bot")
public class BotController {
    @Autowired
    private TelegramService telegramService;

    @RequestMapping(value = "postTaskMsg", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void postTaskMsg(@RequestBody TaskStatusMsg taskStatusMsg) throws InterruptedException, ExecutionException {
        int chatId = taskStatusMsg.getChatId();
        String msg = taskStatusMsg.getMsg();
        System.out.println("TaskMessage Received for chat id =" + chatId + "Message:  " + msg);
        telegramService.sendMessage(new PeerState(chatId, false), msg);
    }

}
