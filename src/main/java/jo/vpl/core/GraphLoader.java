
package jo.vpl.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jo.vpl.xml.ConnectionTag;
import jo.vpl.xml.ConnectionsTag;
import jo.vpl.xml.DocumentTag;
import jo.vpl.xml.HubTag;
import jo.vpl.xml.HubsTag;
import jo.vpl.xml.ObjectFactory;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphLoader {
    
    public static void deserialize(File file, Workspace workspace) {

        String errorMessage = "";

        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<DocumentTag> document = (JAXBElement<DocumentTag>) unmarshaller.unmarshal(file);
            DocumentTag documentTag = document.getValue();

            workspace.setScale(documentTag.getScale());
            workspace.setTranslateX(documentTag.getTranslateX());
            workspace.setTranslateY(documentTag.getTranslateY());

            HubsTag hubsTag = documentTag.getHubs();
            List<HubTag> hubTagList = hubsTag.getHub();
            if (hubTagList
                    != null) {
                for (HubTag hubTag : hubTagList) {
                    errorMessage = "Hub type " + hubTag.getType() + " not found.";
                    Class type = HubLoader.HUB_TYPE_MAP.get(hubTag.getType());
//                    Class type = Class.forName(hubTag.getType());
                    Hub hub = (Hub) type.getConstructor(Workspace.class).newInstance(workspace);
                    hub.deserialize(hubTag);
                    workspace.hubSet.add(hub);
                    workspace.getChildren().add(hub);
                }
            }

            ConnectionsTag connectionsTag = documentTag.getConnections();
            List<ConnectionTag> connectionTagList = connectionsTag.getConnection();
            if (connectionTagList
                    != null) {
                for (ConnectionTag connectionTag : connectionTagList) {

                    UUID startHubUUID = UUID.fromString(connectionTag.getStartHub());
                    int startPortIndex = connectionTag.getStartIndex();
                    UUID endHubUUID = UUID.fromString(connectionTag.getEndHub());
                    int endPortIndex = connectionTag.getEndIndex();

                    Hub startHub = null;
                    Hub endHub = null;
                    for (Hub hub : workspace.hubSet) {
                        if (hub.uuid.compareTo(startHubUUID) == 0) {
                            startHub = hub;
                        } else if (hub.uuid.compareTo(endHubUUID) == 0) {
                            endHub = hub;
                        }
                    }

                    if (startHub != null && endHub != null) {
                        Port startPort = startHub.outPorts.get(startPortIndex);
                        Port endPort = endHub.inPorts.get(endPortIndex);
                        Connection connection = new Connection(workspace, startPort, endPort);
                        workspace.connectionSet.add(connection);
                    }
                }
            }
        } catch (JAXBException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GraphLoader.class.getName()).log(Level.SEVERE, errorMessage, ex);
        }
    }

}
