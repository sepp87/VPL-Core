package jo.vpl.watch3D;

import jo.vpl.watch3D.obj.Model;
import jo.vpl.watch3D.obj.Model.Geometry;
import jo.vpl.watch3D.obj.Model.Material;
import jo.vpl.watch3D.obj.Parser;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;


/**
 *
 * @author JoostMeulenkamp
 */
public class ObjParser {

    public static List<Group> parseObj(File file) {
        Model model = new Model();
        //builder.flipY = false;
        try {
            new Parser(model, file.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(ObjParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Group> groups = new ArrayList<>();

        int size = model.geometryList.size();
        for (Geometry geom : model.geometryList) {
            //As for now sub geometry is not packed into a parent group
            if (geom.hasSubGeometry()) {
                for (Geometry child : geom.getSubGeometry()) {
                    Group group = createGeometryGroup(child, model);
                    groups.add(group);
                }
            } else {
                Group group = createGeometryGroup(geom, model);
                groups.add(group);
            }
        }
        return groups;
    }

    private static Group createGeometryGroup(Geometry geom, Model model) {
        Group group = new Group();
        String guid = geom.getName();
        String materialId = geom.getMaterial();
        int[] indices = geom.getFaces(true);
        float[] points = geom.getVertices();
        float[] normals = geom.getNormals();
        float[] textures = {0, 0};

        TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        mesh.getPoints().setAll(points);
        mesh.getNormals().setAll(normals);
        mesh.getTexCoords().setAll(textures);
        mesh.getFaces().setAll(indices);

        MeshView meshView = new MeshView(mesh);
        if (model.materialMap.containsKey(materialId)) {
            Material rawMaterial = model.materialMap.get(materialId);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(rawMaterial.getDiffuseColor());
            material.setSpecularColor(rawMaterial.getSpecularColor());
            meshView.setMaterial(material);
        }
        
        
        group.getChildren().add(meshView);
        group.setId(guid);
        return group;
    }
}
