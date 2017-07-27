package jo.vpl.hub.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import javax.xml.namespace.QName;
import static jo.util.General.getBooleanValue;
import static jo.util.General.getDoubleValue;
import static jo.util.General.getIntegerValue;
import static jo.util.General.getLongValue;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Json.GetItemByKey",
        category = "Json",
        description = "Get a item by key",
        tags = {"json", "get", "jsonobject", "value", "key"})
public class GetItemByKey extends Hub {

    private JsonParser parser;

    public GetItemByKey(VplControl hostCanvas) {
        super(hostCanvas);

        setName("getJsonValue");

        addInPortToHub("String : Json", String.class);
        addInPortToHub("String : Key", String.class);

        addOutPortToHub("String", String.class);

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
        Object rawObject = inPorts.get(0).getData();
        Object rawKey = inPorts.get(1).getData();

        //Finish calculate if there is no incoming data
        if (rawObject == null || rawKey == null) {
            outPorts.get(0).setData(null);
            return;
        }

        //This calculate function does not (yet) support lists
        if (rawObject instanceof List || rawKey instanceof List) {
            outPorts.get(0).setData(null);
            return;
        }

        try {
            JsonElement jsonElement = parser.parse((String) rawObject);

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (!jsonObject.has((String) rawKey)) {
                //Throw exception that Json does not have this property
                return;
            }

            JsonElement value = jsonObject.get((String) rawKey);
            if (value.isJsonPrimitive()) {

                String str = value.toString();
                if (str.startsWith("\"")) {

                    //Set outgoing data
                    outPorts.get(0).dataType = String.class;
                    outPorts.get(0).setName("String");
                    outPorts.get(0).setData(str.substring(1, str.length() - 1));
                    return;
                }

                Boolean bool = getBooleanValue(str);
                if (bool != null) {

                    //Set outgoing data
                    outPorts.get(0).dataType = Boolean.class;
                    outPorts.get(0).setName("Boolean");
                    outPorts.get(0).setData(bool);
                    return;
                }

                Integer integer = getIntegerValue(str);
                if (integer != null) {

                    //Set outgoing data
                    outPorts.get(0).dataType = Integer.class;
                    outPorts.get(0).setName("Integer");
                    outPorts.get(0).setData(integer);
                    return;
                }

                Long lng = getLongValue(str);
                if (lng != null) {

                    //Set outgoing data
                    outPorts.get(0).dataType = Long.class;
                    outPorts.get(0).setName("Long");
                    outPorts.get(0).setData(lng);
                    return;
                }

                Double dbl = getDoubleValue(str);
                if (dbl != null) {

                    //Set outgoing data
                    outPorts.get(0).dataType = Double.class;
                    outPorts.get(0).setName("Double");
                    outPorts.get(0).setData(dbl);
                    return;
                }

            }

            //Set outgoing data
            outPorts.get(0).dataType = String.class;
            if (value.isJsonArray()) {
                outPorts.get(0).nameProperty().set("String : Array");
            } else if (value.isJsonObject()) {
                outPorts.get(0).nameProperty().set("String : Object");
            }

            outPorts.get(0).setData(value.toString());

        } catch (JsonParseException e) {
            //Throw exception that Json is not formatted correctly
            outPorts.get(0).setData(null);
        } catch (IllegalStateException e) {
            //Throw exception that Json is not of type JsonObject and return
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
                outPorts.get(0).setName("Double");
                break;

            case "Integer":
                outPorts.get(0).dataType = Integer.class;
                outPorts.get(0).setName("Integer");
                break;

            case "Long":
                outPorts.get(0).dataType = Long.class;
                outPorts.get(0).setName("Long");
                break;

            case "Boolean":
                outPorts.get(0).dataType = Boolean.class;
                outPorts.get(0).setName("Boolean");
                break;

            case "String":
                outPorts.get(0).dataType = String.class;
                outPorts.get(0).setName("String");
                break;

        }
        //Specify further initialization statements here
        this.calculate();
    }

    @Override
    public Hub clone() {
        GetItemByKey hub = new GetItemByKey(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
