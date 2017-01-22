package clients;

import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;
import fr.tunaki.stackoverflow.chat.Room;

import services.Runner;
import utils.FilePathUtils;

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

        StackExchangeClient client;

        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(FilePathUtils.loginPropertiesFile));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("STARTED - "+Instant.now());

        client = new StackExchangeClient(prop.getProperty("email"), prop.getProperty("password"));
        Room room = client.joinRoom(ChatHost.STACK_OVERFLOW ,111347);

        room.send("Hiya o/ (Tag wiki monitor - random edition)");

        Runner runner = new Runner(room);
        runner.startMonitor();

        System.out.println("LOADED  - "+Instant.now());

    }

}
