package jo.vpl.hub.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import javax.xml.namespace.QName;
import static jo.vpl.core.Util.getBooleanValue;
import static jo.vpl.core.Util.getDoubleValue;
import static jo.vpl.core.Util.getIntegerValue;
import static jo.vpl.core.Util.getLongValue;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Json.ArrayAsList",
        category = "Json",
        description = "Get a array as list",
        tags = {"json", "get", "jsonarray", "list"})
public class ArrayAsList extends Hub {

    private JsonParser parser;

    public ArrayAsList(VplControl hostCanvas) {
        super(hostCanvas);

        setName("ArrayAsList");

        addInPortToHub("String : Json", String.class);
        addOutPortToHub("String : List", String.class);

        Label label = getAwesomeIcon(IconType.FA_PAPER_PLANE);
        addControlToHub(label);

        parser = new JsonParser();
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {

        //Get incoming data
        Object raw = inPorts.get(0).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outPorts.get(0).setData(null);
            return;
        }

        //This calculate function does not (yet) support lists
        if (raw instanceof List) {
            outPorts.get(0).setData(null);
            return;
        }

        try {
            JsonElement jsonElement = parser.parse((String) raw);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            int size = jsonArray.size();
            if (size == 0) {
                return;
            }

            //Get the type within the array
            JsonElement first = jsonArray.get(0);
            boolean allToString = false;

            if (first.isJsonPrimitive()) {

                String str = first.toString();

                try {
                    Boolean bool = getBooleanValue(str);
                    if (bool != null) {
                        List<Boolean> list = new ArrayList<>();

                        for (int i = 0; i < size; i++) {
                            Boolean value = null;
                            if (!jsonArray.get(i).isJsonNull()) {
                                value = getBooleanValue(jsonArray.get(i).toString());
                                if (value == null) {
                                    throw new ClassCastException();
                                }
                            }
                            list.add(value);
                        }
                        //Set outgoing data
                        outPorts.get(0).dataType = Boolean.class;
                        outPorts.get(0).nameProperty().set("Boolean : List");
                        outPorts.get(0).setData(list);
                        return;
                    }

                    Integer integer = getIntegerValue(str);
                    if (integer != null) {
                        List<Integer> list = new ArrayList<>();

                        for (int i = 0; i < size; i++) {
                            Integer value = null;
                            if (!jsonArray.get(i).isJsonNull()) {
                                value = getIntegerValue(jsonArray.get(i).toString());
                                if (value == null) {
                                    throw new ClassCastException();
                                }
                            }
                            list.add(value);
                        }
                        //Set outgoing data
                        outPorts.get(0).dataType = Integer.class;
                        outPorts.get(0).nameProperty().set("Integer : List");
                        outPorts.get(0).setData(list);
                        return;
                    }

                    Long lng = getLongValue(str);
                    if (lng != null) {
                        List<Long> list = new ArrayList<>();

                        for (int i = 0; i < size; i++) {
                            Long value = null;
                            if (!jsonArray.get(i).isJsonNull()) {
                                value = getLongValue(jsonArray.get(i).toString());
                                if (value == null) {
                                    throw new ClassCastException();
                                }
                            }
                            list.add(value);
                        }
                        //Set outgoing data
                        outPorts.get(0).dataType = Long.class;
                        outPorts.get(0).nameProperty().set("Long : List");
                        outPorts.get(0).setData(list);
                        return;
                    }

                    Double dbl = getDoubleValue(str);
                    if (dbl != null) {
                        List<Double> list = new ArrayList<>();

                        for (int i = 0; i < size; i++) {
                            Double value = null;
                            if (!jsonArray.get(i).isJsonNull()) {
                                value = getDoubleValue(jsonArray.get(i).toString());
                                if (value == null) {
                                    throw new ClassCastException();
                                }
                            }
                            list.add(value);
                        }
                        //Set outgoing data
                        outPorts.get(0).dataType = Double.class;
                        outPorts.get(0).nameProperty().set("Double : List");
                        outPorts.get(0).setData(list);
                        return;
                    }

                    //First must be of type string or some value type that is not supported
                    allToString = true;
                } catch (ClassCastException e) {
                    //Not all items in the array are of the same type
                    allToString = true;
                }
            }

            if (allToString || first.isJsonObject()) {
                List<String> list = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    JsonElement value = jsonArray.get(i);
                    list.add(value.toString());
                }
                //Set outgoing data
                outPorts.get(0).dataType = String.class;
                outPorts.get(0).nameProperty().set("String : List");
                outPorts.get(0).setData(list);

            }

        } catch (JsonParseException e) {
            //Throw exception that Json is not formatted correctly
            outPorts.get(0).setData(null);
        } catch (IllegalStateException e) {
            //Throw exception that Json is not of type JsonArray and return
            outPorts.get(0).setData(null);
        }

    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("outDataType"), outPorts.get(0).dataType.getSimpleName());
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("outDataType"));
        switch (value) {

            case "Double":
                outPorts.get(0).dataType = Double.class;
                outPorts.get(0).setName("Double : List");
                break;

            case "Integer":
                outPorts.get(0).dataType = Integer.class;
                outPorts.get(0).setName("Integer : List");
                break;

            case "Long":
                outPorts.get(0).dataType = Long.class;
                outPorts.get(0).setName("Long : List");
                break;

            case "Boolean":
                outPorts.get(0).dataType = Boolean.class;
                outPorts.get(0).setName("Boolean : List");
                break;

            case "String":
                outPorts.get(0).dataType = String.class;
                outPorts.get(0).setName("String : List");
                break;

        }
        //Specify further initialization statements here

        this.calculate();
    }

    @Override
    public Hub clone() {
        ArrayAsList hub = new ArrayAsList(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
