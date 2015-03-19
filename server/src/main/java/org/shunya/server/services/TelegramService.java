package org.shunya.server.services;

import org.shunya.server.StatusObserver;
import org.shunya.server.engine.JavaSerializer;
import org.shunya.server.engine.MemoryApiState;
import org.shunya.server.engine.PeerState;
import org.shunya.server.engine.TelegramTaskRunner;
import org.shunya.server.model.Task;
import org.shunya.server.model.TaskRun;
import org.shunya.server.model.Team;
import org.shunya.server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.api.*;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.engine.*;
import org.telegram.api.engine.file.Uploader;
import org.telegram.api.messages.TLAbsSentMessage;
import org.telegram.api.messages.TLAbsStatedMessage;
import org.telegram.api.requests.*;
import org.telegram.api.updates.TLState;
import org.telegram.mtproto.log.LogInterface;
import org.telegram.mtproto.log.Logger;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*
Todo:
 1. Mobile Phone Registration
 2. Get list of observable Chat Channels and their mappings to team
 3. Design Command Interceptor: create session for all conversations
    3.1: Identify the team for the chatId
    3.2: Provide help options specific to team
    3.3: Get user input for the task run
    3.4: Send task execution command to server
    3.5: Track task run status
*/
@Service
public class TelegramService implements StatusObserver {
    private HashMap<Integer, PeerState> userStates = new HashMap<>();
    private HashMap<Integer, PeerState> chatStates = new HashMap<>();
    private MemoryApiState apiState;
    private TelegramApi api;
    private Random rnd = new Random();
    private long lastOnline = System.currentTimeMillis();
    private Executor mediaSender = Executors.newSingleThreadExecutor();
    private ConcurrentMap<PeerState, TelegramTaskRunner> userStateMap = new ConcurrentHashMap<>();

    @Autowired
    private DBService dbService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TelegramStatusObserver telegramStatusObserver;

    @Value("${telegram.api.key.location}")
    private String telegramApiLocation;

    @PostConstruct
    public void start() throws IOException {
        System.out.println("telegramApiLocation = " + telegramApiLocation);
        telegramStatusObserver.register(this);
        disableLogging();
//        apiState = JavaSerializer.load(FileSystems.getDefault().getPath(System.getProperty("user.home")).toString());
        apiState = JavaSerializer.load(telegramApiLocation);
        createApi();
//        login();
//        workLoop();
    }

    private synchronized String generateRandomString(int size) {
        String res = "";
        for (int i = 0; i < size; i++) {
            res += (char) ('a' + rnd.nextInt('z' - 'a'));
        }
        return res;
    }

    private synchronized PeerState[] getAllSpamPeers() {
        ArrayList<PeerState> peerStates = new ArrayList<>();
        for (PeerState state : userStates.values()) {
            if (state.isSpamEnabled()) {
                peerStates.add(state);
            }
        }
        for (PeerState state : chatStates.values()) {
            if (state.isSpamEnabled()) {
                peerStates.add(state);
            }
        }
        return peerStates.toArray(new PeerState[0]);
    }

    private synchronized PeerState getUserPeer(int uid) {
        if (!userStates.containsKey(uid)) {
            userStates.put(uid, new PeerState(uid, true));
        }

        return userStates.get(uid);
    }

    private synchronized PeerState getChatPeer(int chatId) {
        if (!chatStates.containsKey(chatId)) {
            chatStates.put(chatId, new PeerState(chatId, false));
        }

        return chatStates.get(chatId);
    }

    private void sendMedia(PeerState peerState, String fileName) {
        TLAbsInputPeer inputPeer = peerState.isUser() ? new TLInputPeerContact(peerState.getId()) : new TLInputPeerChat(peerState.getId());

        int task = api.getUploader().requestTask(fileName, null);
        api.getUploader().waitForTask(task);
        int resultState = api.getUploader().getTaskState(task);
        Uploader.UploadResult result = api.getUploader().getUploadResult(task);
        TLAbsInputFile inputFile;
        if (result.isUsedBigFile()) {
            inputFile = new TLInputFileBig(result.getFileId(), result.getPartsCount(), "file.jpg");
        } else {
            inputFile = new TLInputFile(result.getFileId(), result.getPartsCount(), "file.jpg", result.getHash());
        }
        try {
            TLAbsStatedMessage res = api.doRpcCall(new TLRequestMessagesSendMedia(inputPeer, new TLInputMediaUploadedPhoto(inputFile), rnd.nextInt()), 30000);
            res.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(PeerState peerState, String message) {
        if (peerState.isUser()) {
            sendMessageUser(peerState.getId(), message);
        } else {
            sendMessageChat(peerState.getId(), message);
        }
    }

    private void sendMessageChat(int chatId, String message) {
        api.doRpcCall(new TLRequestMessagesSendMessage(new TLInputPeerChat(chatId), message, rnd.nextInt()),
                15 * 60000,
                new RpcCallbackEx<TLAbsSentMessage>() {
                    @Override
                    public void onConfirmed() {

                    }

                    @Override
                    public void onResult(TLAbsSentMessage result) {

                    }

                    @Override
                    public void onError(int errorCode, String message) {
                    }
                });
    }

    private void sendMessageUser(int uid, String message) {
        api.doRpcCall(new TLRequestMessagesSendMessage(new TLInputPeerContact(uid), message, rnd.nextInt()),
                15 * 60000,
                new RpcCallbackEx<TLAbsSentMessage>() {
                    @Override
                    public void onConfirmed() {

                    }

                    @Override
                    public void onResult(TLAbsSentMessage result) {

                    }

                    @Override
                    public void onError(int errorCode, String message) {

                    }
                });
    }

    //Chat with User- Peer to Peer
    private void onIncomingMessageUser(int uid, int fromId, String message) {
        //Todo:Enable for individual User
        System.out.println("Incoming message from user #" + uid + ": " + message);
        PeerState peerState = getUserPeer(uid);
        if (message.startsWith("bot")) {
            sendMessageUser(uid, "Received: " + message);
            processCommandUser(message.trim().substring(3).trim(), peerState, fromId);

        } else {
            if (peerState.isForwardingEnabled()) {
                sendMessageUser(uid, "FW: " + message);
            }
        }
    }

    private void processCommandUser(String message, PeerState peerState, int fromId) {
        try {
            String[] args = message.split(" ");
            if (args.length == 0) {
                sendMessage(peerState, "Unknown command");
            }
            long taskId = Long.parseLong(args[0]);
            String comments = "process run by ChatId - " + fromId;
            if (args.length > 1) {
                comments = fromId + args[1];
            }
            Task task = dbService.getTask(taskId);
            TaskRun taskRun = new TaskRun();
            taskRun.setTask(task);
            taskRun.setName(task.getName());
            taskRun.setStartTime(new Date());
            taskRun.setComments(comments);
            taskRun.setNotifyStatus(true);
            taskRun.setTeam(task.getTeam());
            User user = dbService.findUserByTelegramId(fromId);
            if (user != null) {
                taskRun.setRunBy(user);
            }
            taskService.execute(taskRun, new HashMap<>());
            sendMessage(peerState, "Command Sent to Server - " + task.getName());
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(peerState, "Unknown command '");
        }
    }

    //Chat Channel - Chat
    private void onIncomingMessageChat(int chatId, int fromId, String message) {
        //Todo: Desing command interceptor-> 1. Identify the team for the chat Id, 2. Provide search/help options specific to team
        System.out.println("Incoming message from in chat #" + chatId + ": " + message);
        PeerState peerState = getChatPeer(chatId);
        if (message.toLowerCase().startsWith("bot")) {
            processCommand(message.trim().substring(3).trim(), getChatPeer(chatId), fromId);
        } else if (message.toLowerCase().contains("bot")) {
            try {
                User user = dbService.findUserByTelegramId(fromId);
                sendMessageChat(chatId, "Hey " + user.getName() + ", are you talking about me ? type <bot help>");
            } catch (Exception e) {
                e.printStackTrace();
                sendMessageChat(chatId, "Are you talking about me ? type <bot help>");
            }
        } else {
            if (peerState.isForwardingEnabled()) {
                sendMessageChat(chatId, "FW: " + message);
            }
        }
    }

    private String getWalkerString(int len, int position) {
        int realPosition = position % len * 2;
        if (realPosition > len) {
            realPosition = len - (realPosition - len);
        }
        String res = "|";
        for (int i = 0; i < realPosition; i++) {
            res += ".";
        }
        res += "\uD83D\uDEB6";
        for (int i = realPosition + 1; i < len; i++) {
            res += ".";
        }
        return res + "|";
    }

    private void processCommand(String message, final PeerState peerState, int fromId) {
        TelegramTaskRunner userTaskRunner = userStateMap.computeIfAbsent(peerState, peerState1 -> new TelegramTaskRunner(dbService, taskService, peerState1, fromId));
        String[] args = message.split(" ");
        if (args.length == 0) {
            sendMessage(peerState, "Unknown command");
        }
        String command = args[0].trim().toLowerCase();
        if (command.equals("enable_forward")) {
            sendMessage(peerState, "Forwarding enabled");
            peerState.setForwardingEnabled(true);
        } else if (command.equals("disable_forward")) {
            peerState.setForwardingEnabled(false);
            sendMessage(peerState, "Forwarding disabled");
        } else if (command.equals("random")) {
            if (args.length == 2) {
                int count = Integer.parseInt(args[1].trim());
                if (count <= 0) {
                    count = 32;
                }
                if (count > 4 * 1024) {
                    sendMessage(peerState, "WarAndPeace.ANGRY");
                } else {
                    sendMessage(peerState, "Random: " + (generateRandomString(count)));
                }
            } else {
                sendMessage(peerState, "Random: " + (generateRandomString(32)));
            }
        } else if (command.equals("start_flood")) {
            int delay = 15;
            if (args.length == 2) {
                delay = Integer.parseInt(args[1].trim());
            }
            peerState.setMessageSendDelay(delay);
            peerState.setSpamEnabled(true);
            peerState.setLastMessageSentTime(0);
            sendMessage(peerState, "Flood enabled with delay " + delay + " sec");
        } else if (command.equals("stop_flood")) {
            peerState.setSpamEnabled(false);
            sendMessage(peerState, "Flood disabled");
        } else if (command.equals("ping")) {
            for (int i = 0; i < 50; i++) {
                sendMessage(peerState, "pong " + getWalkerString(10, i) + " #" + i);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (command.equals("war_ping")) {
            for (int i = 0; i < 50; i++) {
                sendMessage(peerState, "WarAndPeace.TEXT2");
            }
        } else if (command.equals("war")) {
            sendMessage(peerState, "WarAndPeace.TEXT2");
        } else if (command.equals("war2")) {
            sendMessage(peerState, "WarAndPeace.TEXT");
        } else if (command.equals("abort")) {
            sendMessage(peerState, userTaskRunner.abort());
        } else if (command.equals("help")) {
            sendMessage(peerState, userTaskRunner.help());
            /*sendMessage(peerState, "Bot commands:\n" +
                    "bot enable_forward/disable_forward - forwarding of incoming messages\n" +
                    "bot start_flood [delay] - Start flood with [delay] sec (default = 15)\n" +
                    "bot stop_flood - Stop flood\n" +
                    "bot random [len] - Write random string of length [len] (default = 32)\n" +
                    "bot ping - ping with 50 pongs\n" +
                    "bot war - war and peace fragment\n" +
                    "bot war2 - alternative war and peace fragment (currently unable to send)\n" +
                    "bot war_ping - ping with 50 war and peace fragments\n" +
                    "bot img - sending sample image\n" +
                    "bot img50 - sending sample image\n");*/
        } else if (command.equals("img")) {
            mediaSender.execute(() -> sendMedia(peerState, "demo.jpg"));
        } else if (command.equals("img50")) {
            for (int i = 0; i < 50; i++) {
                mediaSender.execute(() -> sendMedia(peerState, "demo.jpg"));
            }
        } else {
            sendMessage(peerState, userTaskRunner.process(args[0]));
        }
    }

    private void workLoop() {
        while (true) {
            try {
                PeerState[] states = getAllSpamPeers();
                for (PeerState state : states) {
                    if (state.isSpamEnabled()) {
                        if (System.currentTimeMillis() - state.getLastMessageSentTime() > state.getMessageSendDelay() * 1000L) {
                            int messageId = state.getMessagesSent() + 1;
                            state.setMessagesSent(messageId);
                            sendMessage(state, "Flood " + getWalkerString(10, messageId) + " #" + messageId);
                            state.setLastMessageSentTime(System.currentTimeMillis());
                        }
                    }
                }
                if (System.currentTimeMillis() - lastOnline > 60 * 1000) {
                    api.doRpcCallWeak(new TLRequestAccountUpdateStatus(false));
                    lastOnline = System.currentTimeMillis();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void disableLogging() {
        Logger.registerInterface(new LogInterface() {
            @Override
            public void w(String tag, String message) {

            }

            @Override
            public void d(String tag, String message) {

            }

            @Override
            public void e(String tag, Throwable t) {

            }
        });
        org.telegram.api.engine.Logger.registerInterface(new LoggerInterface() {
            @Override
            public void w(String tag, String message) {

            }

            @Override
            public void d(String tag, String message) {

            }

            @Override
            public void e(String tag, Throwable t) {

            }
        });
    }

    private void createApi() throws IOException {
        System.out.println("Using production servers");
        if (apiState == null)
            apiState = new MemoryApiState(false);
        api = new TelegramApi(apiState, new AppInfo(5, "console", "???", "???", "en"), new ApiCallback() {
            @Override
            public void onAuthCancelled(TelegramApi api) {

            }

            @Override
            public void onUpdatesInvalidated(TelegramApi api) {

            }

            @Override
            public void onUpdate(TLAbsUpdates updates) {
                if (updates instanceof TLUpdateShortMessage) {
                    onIncomingMessageUser(((TLUpdateShortMessage) updates).getFromId(), ((TLUpdateShortMessage) updates).getFromId(), ((TLUpdateShortMessage) updates).getMessage());
                } else if (updates instanceof TLUpdateShortChatMessage) {
                    onIncomingMessageChat(((TLUpdateShortChatMessage) updates).getChatId(), ((TLUpdateShortChatMessage) updates).getFromId(), ((TLUpdateShortChatMessage) updates).getMessage());
                }
            }
        });
    }

    private void login() throws IOException {
        //Todo: Mobile Phone Registration- Create UI
        Scanner scanner = new Scanner(System.in);
        System.out.print("Loading fresh DC list...");
        api.switchToDc(1);
        TLConfig config = api.doRpcCallNonAuth(new TLRequestHelpGetConfig());
        apiState.updateSettings(config);
        System.out.println("completed.");
        if (!apiState.isAuthenticated(5)) {
            System.out.print("Phone number for bot : ");
            String phone = scanner.nextLine();
            System.out.print("Sending sms to phone " + phone + "...");
            TLSentCode sentCode;
            try {
                sentCode = api.doRpcCallNonAuth(new TLRequestAuthSendCode(phone, 0, 5, "1c5c96d5edd401b1ed40db3fb5633e2d", "en"));
            } catch (RpcException e) {
                if (e.getErrorCode() == 303) {
                    int destDC;
                    if (e.getErrorTag().startsWith("NETWORK_MIGRATE_")) {
                        destDC = Integer.parseInt(e.getErrorTag().substring("NETWORK_MIGRATE_".length()));
                    } else if (e.getErrorTag().startsWith("PHONE_MIGRATE_")) {
                        destDC = Integer.parseInt(e.getErrorTag().substring("PHONE_MIGRATE_".length()));
                    } else if (e.getErrorTag().startsWith("USER_MIGRATE_")) {
                        destDC = Integer.parseInt(e.getErrorTag().substring("USER_MIGRATE_".length()));
                    } else {
                        throw e;
                    }
                    api.switchToDc(destDC);
                    sentCode = api.doRpcCallNonAuth(new TLRequestAuthSendCode(phone, 0, 5, "1c5c96d5edd401b1ed40db3fb5633e2d", "en"));
                } else {
                    throw e;
                }
            }
            System.out.println("sent.");
            System.out.print("Activation code:");
            String code = scanner.nextLine();
            System.out.println(sentCode.getPhoneCodeHash());
            System.out.println(code);
            activateBot(phone, sentCode, code);
        }
        api.switchToDc(5);
        System.out.print("Loading Initial State...");
        TLState state = api.doRpcCall(new TLRequestUpdatesGetState());
        System.out.println("loaded.");
        JavaSerializer.save(apiState, telegramApiLocation);
    }

    private void activateBot(String phone, TLSentCode sentCode, String code) throws IOException {
        TLAuthorization auth = api.doRpcCallNonAuth(new TLRequestAuthSignIn(phone, sentCode.getPhoneCodeHash(), code));
        apiState.setAuthenticated(apiState.getPrimaryDc(), true);
        System.out.println("Activation Complete.");
    }

    @Override
    public void notifyStatus(int chatId, boolean notifyStatus, String message) {
        if (chatId != 0 && notifyStatus)
            sendMessage(new PeerState(chatId, false), message);
    }
}
