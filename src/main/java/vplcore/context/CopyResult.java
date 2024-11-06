package vplcore.context;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import vplcore.graph.model.Block;
import vplcore.graph.model.Connection;

/**
 *
 * @author Joost
 */
public class CopyResult {

    public Bounds boundingBox;
    public List<Block> blocks = new ArrayList<>();
    public List<Connection> connections = new ArrayList<>();

    public CopyResult() {

    }
}
