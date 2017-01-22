package services;

import fr.tunaki.stackoverflow.chat.Room;

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
        Runnable runner = () -> runEditBotOnce(room, suggest);
        executorService.scheduleAtFixedRate(runner, 0, 1, TimeUnit.MINUTES);
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
                if (firstTime) {
                    previousEditId = editIds.get(0) - 1;
                    firstTime = false;
                }
                Integer endId = editIds.get(editIds.size() - 1);
                String printString = "";

                if (endId - previousEditId != editIds.size()) {
                    System.out.println("Tag wikis detected");
                    int i = previousEditId + 1;
                    for (Integer editId : editIds) {
                        if (i != editId) {
                            while(i!=editId){
                                printString += "[" + i + "](//stackoverflow.com/suggested-edits/" + i + "); ";
                                i++;
                            }
                        }
                        i++;
                    }
                }

                previousEditId = endId;
                if (!printString.equals(""))
                    room.send("[ [TagWiki Edit Monitor](https://git.io/vMQbz) ] Tag wiki links " + printString);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
