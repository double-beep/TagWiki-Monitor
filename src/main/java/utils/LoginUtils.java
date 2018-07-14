package utils;

import org.sobotics.chatexchange.chat.StackExchangeClient;
import services.PropertyService;

/**
 * Created by bhargav.h on 25-Mar-17.
 */
public class LoginUtils {

    public static StackExchangeClient getClient() {
        StackExchangeClient client;

        PropertyService ps = new PropertyService();
        client = new StackExchangeClient(ps.getEmail(), ps.getPassword());
        return client;
    }

}
