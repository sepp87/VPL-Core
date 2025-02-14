package vpllib.method;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import vplcore.graph.model.BlockMetadata;

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
    public static List<?> asList(String json) {
        JsonArray array = PARSER.parse(json).getAsJsonArray();

        Boolean isIntegerList = isIntegerList(array);
        if (isIntegerList == null) {
            List<String> result = new ArrayList<>();
            for (JsonElement element : array) {
                String value = element.toString();
                result.add(value);
            }
            return result;
        }

        if (isIntegerList) {
            List<Integer> result = new ArrayList<>();
            for (JsonElement element : array) {
                int value = element.getAsNumber().intValue();
                result.add(value);
            }
            return result;
        }

        List<Double> result = new ArrayList<>();
        for (JsonElement element : array) {
            Double value = element.getAsNumber().doubleValue();
            result.add(value);
        }
        return result;
    }

    /**
     *
     * @param a
     * @return returns true when array contains only integers, false when double
     * and null when none of both
     */
    private static Boolean isIntegerList(JsonArray a) {
        Boolean result = true;
        Iterator<JsonElement> iterator = a.iterator();
        while (iterator.hasNext()) {

            JsonElement next = iterator.next();
            if (!next.isJsonPrimitive()) {
                return null;
            }

            JsonPrimitive primitive = next.getAsJsonPrimitive();
            if (!primitive.isNumber()) {
                return null;
            }

            if (primitive.getAsNumber().doubleValue() % 1 != 0) {
                result = false;
            }
        }
        return result;
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
