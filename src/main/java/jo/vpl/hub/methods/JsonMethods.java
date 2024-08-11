package jo.vpl.hub.methods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import jo.vpl.core.HubInfo;

/**
 *
 * @author Joost
 */
public class JsonMethods {

    public static final JsonParser PARSER = new JsonParser();

    @HubInfo(
            identifier = "Json.asList",
            category = "Core",
            description = "Converts a JSON array into a list of string values.")
    public static List<String> asList(String json) {
        JsonArray array = PARSER.parse(json).getAsJsonArray();
        List<String> result = new ArrayList<>();
        for (JsonElement element : array) {
            String value = element.toString();
            result.add(value);
        }
        return result;
    }

    @HubInfo(
            identifier = "Json.getKey",
            category = "Core",
            description = "Returns the element with the specified key in this JSON object.")
    public static String getKey(String json, String key) {
        return PARSER.parse(json).getAsJsonObject().get(key).toString();
    }

    @HubInfo(
            identifier = "Json.getIndex",
            category = "Core",
            description = "Returns the element as string at the specified position in this JSON array.")
    public static String getIndex(String json, int index) {
        return PARSER.parse(json).getAsJsonArray().get(index).toString();
    }

}
