package clients;

import java.time.Instant;

import org.sobotics.chatexchange.chat.ChatHost;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.StackExchangeClient;

import services.Runner;
import utils.LoginUtils;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class RunMonitor {

    public static void main(String[] args) {

        System.out.println("STARTED - " + Instant.now());

        StackExchangeClient client = LoginUtils.getClient();
        Room room = client.joinRoom(ChatHost.STACK_OVERFLOW, 111347);
        room.send("Hiya o/ (Tag wiki monitor - random edition)");
        Runner runner = new Runner(room);
        runner.startMonitor();

        System.out.println("LOADED  - " + Instant.now());

    }

}
