package jo.vpl.hub.check;

import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.hub.loop.TimeInterval;
import jo.vpl.util.IconType;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Check.If",
        category = "Check",
        description = "A boolean statement",
        tags = {"check", "if", "condition"})
public class IfCondition extends Hub {

    private final Label OPERATOR;
    static String[] operators = {">", ">=", "==", "!=", "<=", "<"};

    public IfCondition(VplControl hostCanvas) {
        super(hostCanvas);

        setName(">");

        addInPortToHub("Number : A", Number.class);
        addInPortToHub("Number : B", Number.class);

        addOutPortToHub("Boolean : Result", Boolean.class);

        OPERATOR = new Label("A > B");
        OPERATOR.setUserData(0);
        OPERATOR.getStyleClass().add("hub-text");
        OPERATOR.setOnMouseClicked(this::button_MouseClick);

        addControlToHub(OPERATOR);
    }

    //Toggle between on and off by listening to the switch property of the timer
    private void button_MouseClick(MouseEvent e) {

        int index = (int) OPERATOR.getUserData();
        if (index == 5) {
            OPERATOR.setUserData(0);
        } else {
            OPERATOR.setUserData(++index);
        }
        updateOperator();
    }
    
    private void updateOperator() {
        int index = (int) OPERATOR.getUserData();
        OPERATOR.setText("A " + operators[index] + " B");
        calculate();
    }

    /**
     * Calculate X+Y
     */
    @Override
    public void calculate() {

        //Get data
        Object raw1 = inPorts.get(0).getData();
        Object raw2 = inPorts.get(1).getData();

        //Finish calculate if there is no incoming data
        if (raw1 == null || raw2 == null) {
            outPorts.get(0).setData(null);
            return;
        }

        if (raw1 instanceof List || raw2 instanceof List) {
            //not yet supported
            outPorts.get(0).setData(null);

        } else {
            //Does this 
            int index = (int) OPERATOR.getUserData();
            String operator = operators[index];
            Boolean result = null;
            switch (operator) {
                case ">":
                    result = ((Number) raw1).doubleValue() > ((Number) raw2).doubleValue();
                    break;
                case ">=":
                    result = ((Number) raw1).doubleValue() >= ((Number) raw2).doubleValue();
                    break;
                case "==":
                    result = ((Number) raw1).doubleValue() == ((Number) raw2).doubleValue();
                    break;
                case "!=":
                    result = ((Number) raw1).doubleValue() != ((Number) raw2).doubleValue();
                    break;
                case "<=":
                    result = ((Number) raw1).doubleValue() <= ((Number) raw2).doubleValue();
                    break;
                case "<":
                    result = ((Number) raw1).doubleValue() < ((Number) raw2).doubleValue();
                    break;
            }

            outPorts.get(0).setData(result);
        }
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("operator"), (int) OPERATOR.getUserData() + "");
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("operator"));
        int index = Integer.parseInt(value);
        OPERATOR.setUserData(index);
        updateOperator();
    }

    @Override
    public Hub clone() {
        IfCondition hub = new IfCondition(hostCanvas);
        hub.OPERATOR.setUserData(this.OPERATOR.getUserData());
        hub.updateOperator();
        return hub;
    }
}
