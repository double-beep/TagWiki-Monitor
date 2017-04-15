package services;

import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;
import fr.tunaki.stackoverflow.chat.event.EventType;
import fr.tunaki.stackoverflow.chat.event.MessageReplyEvent;
import fr.tunaki.stackoverflow.chat.event.UserMentionedEvent;
import sun.rmi.runtime.Log;
import utils.LoginUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class Runner {


    private Boolean firstTime;
    private Integer previousEditId ;
    private Room room;
    private ScheduledExecutorService executorService;

    public Runner(Room room){
        firstTime = true;
        this.room = room;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startMonitor(){
        EditSuggestions suggest = new EditSuggestions();
        room.addEventListener(EventType.MESSAGE_REPLY, event->reply(room, event, true));
        room.addEventListener(EventType.USER_MENTIONED,event->mention(room, event, false));
        Runnable runner = () -> runEditBotOnce(room, suggest);
        executorService.scheduleAtFixedRate(runner, 0, 1, TimeUnit.MINUTES);
    }

    private void mention(Room room, UserMentionedEvent event, boolean b) {
        String message = event.getMessage().getPlainContent();
        if(message.toLowerCase().contains("help")){
            room.send("I'm a bot that tracks tag wiki edits");
        }
        else if(message.toLowerCase().contains("alive")){
            room.send("Yep");
        }
    }

    private void reply(Room room, MessageReplyEvent event, boolean b) {
        String message = event.getMessage().getPlainContent();
        if(message.toLowerCase().contains("socvr") &&  room.getUser(event.getUserId()).isRoomOwner()){
            Message report =  room.getMessage(event.getParentMessageId());
            if (report.getPlainContent().contains("Tag wiki links")) {
                StackExchangeClient client = LoginUtils.getClient();
                Room targetRoom = client.joinRoom(ChatHost.STACK_OVERFLOW, 41570);
                String reason = message.split("socvr")[1].trim();
                targetRoom.send(report.getPlainContent() + " [tag:reject-pls] " + reason + "  (*verified by [@" + event.getUserName().replace(" ", "") + "](//chat.stackoverflow.com/transcript/message/" + event.getMessage().getId() + ")*)");
                targetRoom.leave();
            }
        }
    }

    public void restartMonitor(){
        endMonitor();
        startMonitor();
    }

    public void endMonitor(){
        executorService.shutdown();
    }

    public void runEditBotOnce(Room room, EditSuggestions suggestions){
        try{
            List<Integer> editIds = suggestions.getEditIds();
            System.out.println(editIds);
            if(editIds.size()>0) {

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
                            while(i!=editId){
                                ids.add(i);
                                i++;
                            }
                        }
                        i++;
                    }
                }

                previousEditId = endId;
                for (Integer id: ids) {
                    room.send("[ [TagWiki Edit Monitor](https://git.io/vMQjF) ] Tag wiki link [" + id + "](//stackoverflow.com/suggested-edits/" + id + ")");
                    Thread.sleep(1000);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
