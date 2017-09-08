package jo.vpl.hub.str;

import jo.vpl.hub.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;
import jo.vpl.util.IconType;
import jo.vpl.xml.HubTag;

/**
 * TODO fix code
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Str.Concatenate",
        category = "Str",
        description = "Concatenate multiple strings to a single one",
        tags = {"string", "concatenate"})
public class Concatenate extends Hub {

    private Port temporaryPort = null;

    public Concatenate(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Concat");

        addInPortToHub("String", String.class);
        addOutPortToHub("String", String.class);

        Label label = getAwesomeIcon(IconType.FA_BEER);
        addControlToHub(label);
    }

    //Handle the number of ports that are active, always give one more that needed
    @Override
    public void handle_IncomingConnectionAdded(Port source, Port incoming) {
        //check if port which connection is added to is the temporary port
        //if not delete the temporary port and generate a new port
        if (source == temporaryPort) {
            addInPortToHub(source);
        } else {
            addInPortToHub("String", String.class);
            temporaryPort = null;
        }

    }

    @Override
    public void handle_IncomingConnectionRemoved(Port source) {
        //remove port, set it as temporary
        removeInPortFromHub(source);
System.out.println("call");
        temporaryPort = source;
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {

        //if there are no entries, there are no lists
        //if there is just one entry, there can be multiple lists... but all have the same size
        Set<Integer> oneSizeSet = new HashSet<>();

        //Get incoming data
        int numOfActivePorts = inPorts.size() - 1;
        for (int i = 0; i < numOfActivePorts; i++) {
            Object raw = inPorts.get(i).getData();

            //Return if a port except the last is empty 
            if (raw == null) {
                outPorts.get(0).setData(null);
                return;
            }

            if (raw instanceof List) {
                List<String> list = (List<String>) raw;
                oneSizeSet.add(list.size());
            }
        }

        //
        if (oneSizeSet.isEmpty()) {
            String value = "";
            for (int i = 0; i < numOfActivePorts; i++) {
                String piece = (String) inPorts.get(i).getData();
                value += piece;
            }
            outPorts.get(0).setData(value);
        } else if (oneSizeSet.size() == 1) {
            //throw exception that this function is not yet implemented

        } else {
            //throw exception that the sizes of the lists do not match
            outPorts.get(0).setData(null);
        }

        //Check if there are multiple lists and if they are of the same length
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        //Save the number of ports currently available
//        xmlTag.getOtherAttributes().put(QName.valueOf("outDataType"), outPorts.get(0).dataType.getName());
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        //Create the number of ports before running calculate
//        String value = xmlTag.getOtherAttributes().get(QName.valueOf("key"));
        //Specify further initialization statements here
        this.calculate();
    }

    @Override
    public Hub clone() {
        Concatenate hub = new Concatenate(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
