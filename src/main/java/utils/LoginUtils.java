package utils;

import fr.tunaki.stackoverflow.chat.StackExchangeClient;
import services.PropertyService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
