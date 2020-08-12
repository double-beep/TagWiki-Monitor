package utils;

import java.io.IOException;
import java.time.Instant;

import com.google.gson.JsonObject;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class ApiUtils {
    public static JsonObject getFirstPageOfSuggestedEdits(Instant fromTimestamp, String site, String apiKey) throws IOException {
        String answersUrl = "https://api.stackexchange.com/2.2/suggested-edits";
        String fromdate = String.valueOf(fromTimestamp.minusSeconds(1).getEpochSecond());
        return JsonUtils.get(answersUrl, "order", "asc", "sort", "creation", "page", "1", "pagesize", "100", "fromdate", fromdate, "site", site, "key", apiKey, "sort", "creation");
    }
}
