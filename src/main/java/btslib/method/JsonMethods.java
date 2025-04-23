package btslib.method;

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
import btscore.graph.block.BlockMetadata;
import btscore.utils.ParsingUtils;

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
                        Number number = primitive.getAsNumber().doubleValue();
                        list.add(ParsingUtils.castToBestNumericType(number));
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
            identifier = "Json.getPath",
            category = "Core",
            description = "Returns the element with the specified path in this JSON object.")
    public static String getPath(String json, String path) {
        JsonElement element = PARSER.parse(json);
        String[] parts = path.split("\\.");

        for (String part : parts) {
            if (part.contains("[") && part.contains("]")) {
                // Handle array index like "bar[0]"
                String key = part.substring(0, part.indexOf("["));

//                String[] indeces = part.replaceFirst(key, "").replaceAll("\\[", "]").replaceAll("]]", "]").split("]");
                String[] indices = part.replaceAll(".*?\\[", "").split("\\]|\\[");

                if (!key.isEmpty()) {
                    element = element.getAsJsonObject().get(key);
                }

                for (String value : indices) {
                    if (value.isEmpty()) {
                        throw new IllegalArgumentException("Index cannot be empty in path: " + path);
                    }
                    int index = Integer.parseInt(value);
                    element = element.getAsJsonArray().get(index);
                }

            } else {
                // Handle regular object key
                element = element.getAsJsonObject().get(part);
            }
        }

        return element.toString();
    }

    @BlockMetadata(
            identifier = "Json.toJson",
            category = "Core",
            description = "This method serializes the specified object into its equivalent Json representation.")
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
