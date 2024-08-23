package jo.vpl.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
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
public class GraphSaver {

    public static void serialize(File file, Workspace workspace) {
        try {

            ObjectFactory factory = new ObjectFactory();

            HubsTag hubsTag = factory.createHubsTag();

            for (Hub hub : workspace.hubSet) {
                HubTag hubTag = factory.createHubTag();
                hub.serialize(hubTag);
                hubsTag.getHub().add(hubTag);
            }

            ConnectionsTag connectionsTag = factory.createConnectionsTag();

            for (Connection connection : workspace.connectionSet) {
                ConnectionTag connectionTag = factory.createConnectionTag();
                connection.serialize(connectionTag);
                connectionsTag.getConnection().add(connectionTag);
            }

            DocumentTag documentTag = factory.createDocumentTag();
            documentTag.setScale(workspace.getScale());
            documentTag.setTranslateX(workspace.getTranslateX());
            documentTag.setTranslateY(workspace.getTranslateY());

            documentTag.setHubs(hubsTag);
            documentTag.setConnections(connectionsTag);

            JAXBElement<DocumentTag> document = factory.createDocument(documentTag);

            JAXBContext context = JAXBContext.newInstance("jo.vpl.xml");
            Marshaller marshaller = context.createMarshaller();

            //Pretty output
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(document, file);

        } catch (JAXBException ex) {
            Logger.getLogger(GraphSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
