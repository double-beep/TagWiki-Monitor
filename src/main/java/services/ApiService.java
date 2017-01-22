package services;

import com.google.gson.JsonObject;
import utils.ApiUtils;
import utils.FilePathUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class ApiService {

    private String apiKey;
    private String site;

    private static int quota=0;

    public ApiService(String site){
        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(FilePathUtils.loginPropertiesFile));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        this.site = site;
        this.apiKey = prop.getProperty("apikey");

        if(apiKey.equals(null)){
            apiKey = System.getenv("apikey");
        }

    }

    public JsonObject getFirstPageOfSuggestedEdits(Instant fromTimestamp) throws IOException{
        JsonObject answersJson = ApiUtils.getFirstPageOfSuggestedEdits(fromTimestamp,site,apiKey);
        quota = answersJson.get("quota_remaining").getAsInt();
        return answersJson;
    }


    public int getQuota(){
        return quota;
    }
}
