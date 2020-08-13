package services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sobotics.chatexchange.chat.ChatHost;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.StackExchangeClient;
import org.sobotics.chatexchange.chat.event.EventType;
import org.sobotics.chatexchange.chat.event.MessagePostedEvent;
import org.sobotics.chatexchange.chat.event.MessageReplyEvent;
import org.sobotics.chatexchange.chat.event.UserMentionedEvent;
import org.sobotics.redunda.PingService;

import utils.LoginUtils;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class Runner {

    private Boolean firstTime;
    private Integer previousEditId;
    private Boolean previousState;
    private Room room;
    private ScheduledExecutorService executorService;

    public Runner(Room room) {
        firstTime = true;
        previousState = false;
        this.room = room;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startMonitor() {

        String redundaKey = new PropertyService().getRedundaKey();
        boolean useRedunda = new PropertyService().getUseRedunda();
        PingService redunda = new PingService(redundaKey, "random");
        if (useRedunda) {
            redunda.start();
        } else {
            redunda.setDebugging(true);
        }

        EditSuggestions suggest = new EditSuggestions();
        room.addEventListener(EventType.MESSAGE_REPLY, event -> reply(room, event, redunda, true));
        room.addEventListener(EventType.USER_MENTIONED, event -> mention(room, event, redunda, false));
        room.addEventListener(EventType.MESSAGE_POSTED, event -> newMessage(room, event, redunda, false));

        Runnable runner = () -> runEditBotOnce(room, suggest, redunda);
        executorService.scheduleAtFixedRate(runner, 0, 1, TimeUnit.MINUTES);
    }

    private static void newMessage(Room room, MessagePostedEvent event, PingService redunda, boolean b) {
        String message = event.getMessage().getContent();
        int cp = Character.codePointAt(message, 0);
        if (!redunda.standby.get() && message.trim().startsWith("@bots alive")) {
            room.send("Not feeling well, but still alive");
        } else if (!redunda.standby.get() && (cp == 128642 || (cp >= 128644 && cp <= 128650))) {
            room.send("[\uD83D\uDE83](https://www.youtube.com/watch?v=Sagg08DrO5U)");
        }
    }

    private void mention(Room room, UserMentionedEvent event, PingService redunda, boolean b) {
        String message = event.getMessage().getContent();
        if (!redunda.standby.get() && message.toLowerCase().contains("help")) {
            room.send("I'm a bot that tracks tag wiki edits");
        } else if (!redunda.standby.get() && message.toLowerCase().contains("alive")) {
            room.send(new PropertyService().getLocation() + " reporting for duty.");
        } else if (message.toLowerCase().contains("status")) {
            room.send(new PropertyService().getLocation() + " running with status - " + (previousState ? "running" : "standby"));
        }
    }

    private void reply(Room room, MessageReplyEvent event, PingService redunda, boolean b) {
        String message = event.getMessage().getContent();
        if (!redunda.standby.get() && message.toLowerCase().contains("socvr") && room.getUser(event.getUserId()).isRoomOwner()) {
            Message report = room.getMessage(event.getParentMessageId());
            if (report.getContent().contains("Tag wiki link")) {
                StackExchangeClient client = LoginUtils.getClient();
                Room targetRoom = client.joinRoom(ChatHost.STACK_OVERFLOW, 41570);
                String reason = message.split("socvr")[1].trim();
                targetRoom.send(report.getContent() + " [tag:reject-pls] " + reason + "  (*verified by [@" + event.getUserName().replace(" ", "")
                                + "](//chat.stackoverflow.com/transcript/message/" + event.getMessage().getId() + ")*)");
                targetRoom.leave();
            }
        }
    }

    public void restartMonitor() {
        endMonitor();
        startMonitor();
    }

    public void endMonitor() {
        executorService.shutdown();
    }

    public void runEditBotOnce(Room room, EditSuggestions suggestions, PingService redunda) {

        boolean presentState = !redunda.standby.get();

        if (!previousState && presentState && !firstTime) {
            room.send("Received failover - " + new PropertyService().getLocation() + " switching to running");
            firstTime = true;
        }
        if (previousState && !presentState && !firstTime) {
            room.send("Received stop - " + new PropertyService().getLocation() + " switching to standby");
            firstTime = true;
        }

        if (presentState) {
            try {
                List<Integer> editIds = suggestions.getEditIds();
                System.out.println(editIds);
                if (editIds.size() > 0) {

                    List<Integer> ids = new ArrayList<>();

                    if (firstTime) {
                        previousEditId = editIds.get(0) - 1;
                        firstTime = false;
                    }
                    Integer endId = editIds.get(editIds.size() - 1);

                    if (endId - previousEditId != editIds.size()) {

                        System.out.println("Tag wikis detected");
                        int i = previousEditId + 1;

                        for (Integer editId : editIds) {
                            if (i != editId) {
                                if (editId - i > 10) {
                                    room.send("Some issue with the system, @BhargavRao, I'm refreshing the queue now. " + (editId - i));
                                    i = editId - 1;
                                }
                                while (i != editId) {
                                    ids.add(i);
                                    i++;
                                }
                            }
                            i++;
                        }
                    }

                    previousEditId = endId;
                    for (Integer id : ids) {
                        room.send("[ [TagWiki Edit Monitor](https://git.io/vMQjF) ] Tag wiki link [" + id + "](//stackoverflow.com/suggested-edits/" + id + ")");
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        previousState = presentState;
    }
}
