package clients;

import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;
import fr.tunaki.stackoverflow.chat.Room;

import fr.tunaki.stackoverflow.chat.event.EventType;
import services.Runner;
import utils.FilePathUtils;
import utils.LoginUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class RunMonitor {

    public static void main(String[] args) {


        System.out.println("STARTED - "+Instant.now());

        StackExchangeClient client = LoginUtils.getClient();

        Room room = client.joinRoom(ChatHost.STACK_OVERFLOW ,111347);

        room.send("Hiya o/ (Tag wiki monitor - random edition)");

        Runner runner = new Runner(room);
        runner.startMonitor();

        System.out.println("LOADED  - "+Instant.now());

    }

}
