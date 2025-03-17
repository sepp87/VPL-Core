package vpllib.method;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author Joost
 */
public class JsonMethods {

    public static final JsonParser PARSER = new JsonParser();
    public static final com.google.gson.Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    @BlockMetadata(
            identifier = "Json.asList",
            category = "Core",
            description = "Converts a JSON array into a list of string values.")
    public static List<Object> asList(String jsonArray) {
        JsonElement element = PARSER.parse(jsonArray);
        return parseJsonElement(element);
    }

    private static List<Object> parseJsonElement(JsonElement element) {
        List<Object> list = new ArrayList<>();
        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                if (item.isJsonObject()) {
                    list.add(new Gson().fromJson(item, Map.class));
                } else if (item.isJsonArray()) {
                    list.add(parseJsonElement(item));
                } else if (item.isJsonPrimitive()) {
                    JsonPrimitive primitive = item.getAsJsonPrimitive();
                    if (primitive.isString()) {
                        list.add(primitive.getAsString());
                    } else if (primitive.isNumber()) {
                        list.add(primitive.getAsNumber());
                    } else if (primitive.isBoolean()) {
                        list.add(primitive.getAsBoolean());
                    }
                } else {
                    list.add(null);
                }
            }
        }
        return list;
    }


    @BlockMetadata(
            identifier = "Json.getKey",
            category = "Core",
            description = "Returns the element with the specified key in this JSON object.")
    public static String getKey(String json, String key) {
        return PARSER.parse(json).getAsJsonObject().get(key).toString();
    }

    @BlockMetadata(
            identifier = "Json.getIndex",
            category = "Core",
            description = "Returns the element as string at the specified position in this JSON array.")
    public static String getIndex(String json, int index) {
        return PARSER.parse(json).getAsJsonArray().get(index).toString();
    }

    @BlockMetadata(
            identifier = "Json.toJson",
            category = "Core",
            description = "This method serializes the specified object into its equivalent Json representation.")
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

}
