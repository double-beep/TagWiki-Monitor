package utils;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class ApiUtils {
        public static JsonObject getFirstPageOfSuggestedEdits(Instant fromTimestamp, String site, String apiKey) throws IOException{
        String answersUrl = "https://api.stackexchange.com/2.2/suggested-edits";
        return JsonUtils.get(answersUrl,"order","asc","sort","creation","page","1","pagesize","100","fromdate",String.valueOf(fromTimestamp.minusSeconds(1).getEpochSecond()),"site",site,"key",apiKey,"sort","creation");
    }
}
