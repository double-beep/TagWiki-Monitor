package utils;

import org.sobotics.chatexchange.chat.StackExchangeClient;

import services.PropertyService;

/**
 * Created by bhargav.h on 25-Mar-17.
 */
public class LoginUtils {

    public static StackExchangeClient getClient() {
        StackExchangeClient client;

        PropertyService propertyService = new PropertyService();
        client = new StackExchangeClient(propertyService.getEmail(), propertyService.getPassword());
        return client;
    }

}
