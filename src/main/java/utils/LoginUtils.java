package utils;

import fr.tunaki.stackoverflow.chat.StackExchangeClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by bhargav.h on 25-Mar-17.
 */
public class LoginUtils {

    public static StackExchangeClient getClient() {
        StackExchangeClient client;

        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(FilePathUtils.loginPropertiesFile));
        }
        catch (IOException e){
            e.printStackTrace();
        }


        String email = prop.getProperty("email");
        String password = prop.getProperty("password");

        if (email == null || password == null){
            // For heroku only
            email = System.getenv("email");
            password = System.getenv("password");
        }

        client = new StackExchangeClient(email, password);
        return client;
    }

}
