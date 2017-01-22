package services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhargav.h on 22-Jan-17.
 */
public class EditSuggestions {

    private Instant previousTimestamp;
    private ApiService apiService;

    public EditSuggestions() {
        this.previousTimestamp = Instant.now().minusSeconds(60);
        apiService = new ApiService("stackoverflow");
    }

    public List<Integer> getEditIds(){
        List<Integer>  editIds = new ArrayList<>();

        try {
            JsonObject edits = apiService.getFirstPageOfSuggestedEdits(previousTimestamp);


            System.out.println(editIds);

            for (JsonElement element : edits.get("items").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                editIds.add(object.get("suggested_edit_id").getAsInt());
            }

            previousTimestamp = Instant.now().plusSeconds(1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return editIds;
    }

}
