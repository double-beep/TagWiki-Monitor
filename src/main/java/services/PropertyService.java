package services;

import utils.FilePathUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by bhargav.h on 23-May-17.
 */
public class PropertyService {

    private Properties prop;

    public PropertyService() {

        prop = new Properties();
        try
        {
            prop.load(new FileInputStream(FilePathUtils.loginPropertiesFile));
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }

    }

    public String getEmail(){
        String email = prop.getProperty("email");
        if (email == null)
            email = System.getenv("email");
        return email;
    }

    public String getPassword(){
        String password = prop.getProperty("password");
        if (password == null)
            password = System.getenv("password");
        return password;
    }

    public String getRedundaKey(){
        String redundaKey = prop.getProperty("redundaKey");
        if (redundaKey == null)
            redundaKey = System.getenv("redundaKey");
        return redundaKey;
    }

    public String getLocation(){
        String location = prop.getProperty("location");
        if (location == null)
            location = System.getenv("location");
        return location;
    }

}
