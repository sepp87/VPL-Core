/*
 * Modified by Joost Meulenkamp
 * 
 * Adapted from source available at https://github.com/seanrowens/oObjLoader,
 * written by Sean R. Rowens. Used under license GNU GPLv3 available at 
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 */
package jo.vpl.watch3D.obj;

import java.util.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class Model {

    String objFilename = null;
    String objectName = null;

    void setObjFilename(String filename) {
        this.objFilename = filename;
    }

    public String getObjFilename() {
        return objFilename;
    }

    void addPoints(int[] values) {
    }

    void addLine(int[] values) {
    }

    void addObjectName(String name) {
        this.objectName = name;
    }

    void addMapLib(String[] names) {
        if (null == names) {
            return;
        }
        if (names.length == 1) {
            return;
        }
        for (int loopi = 0; loopi < names.length; loopi++) {
        }
    }

    void setCurrentUseMap(String name) {
    }

    void addMaterial(String name) {
        currentMaterial = new Material(name);
        materialMap.put(name, currentMaterial);
    }

    void setXYZ(int type, float x, float y, float z) {
    }

    void setRGB(int type, float r, float g, float b) {
    }

    void setIllum(int illumModel) {
    }

    void setD(boolean halo, float factor) {
    }

    void setNs(float exponent) {
    }

    void setSharpness(float value) {
    }

    void setNi(float opticalDensity) {
    }

    void setMapDecalDispBump(int type, String filename) {
    }

    void setRefl(int type, String filename) {
    }

    void doneParsingMtl(String filename) {
        currentMaterial = null;
    }

    void doneParsingObj(String filename) {
        currentGeometry = null;
        for (Geometry geometry : geometryList) {
            geometry.postProcessGeometry();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public boolean flipY = true;

    //Material
    Material currentMaterial;
    public Map<String, Material> materialMap;

    //Geometry
    Geometry currentGeometry;
    public List<Geometry> geometryList;
    List<Vertex> vertexList;
    List<Vertex> normalList;

    public Model() {
        materialMap = new HashMap<>();
        geometryList = new ArrayList<>();
        vertexList = new ArrayList<>();
        normalList = new ArrayList<>();
    }

    void addGeometry(String[] names) {
        //names seems to be one all the time
        if (names.length == 1) {
            Geometry geom = new Geometry(names[0]);
            geometryList.add(geom);
            currentGeometry = geom;

            if (geometryList.size() == 1) {
                geom.offset = 1;
            } else {
                geom.offset = vertexList.size() + 1;
            }

        } else {
            System.out.println("Oops, array length was not 1. Actual length was: " + names.length);
        }
    }

    void setSmoothingGroup(int number) {
        if (number != 0) {
            currentGeometry.smoothingGroup = number;
        }
    }

    void addVertex(float x, float y, float z) {
        vertexList.add(new Vertex(x, y, z));
    }

    void addNormal(float x, float y, float z) {
        normalList.add(new Vertex(x, y, z));
    }

    public class Material {

        String name;

        private ObjectProperty<Color> specularColor;
        private ObjectProperty<Color> diffuseColor;

        public Material(String name) {
            specularColor = new SimpleObjectProperty();
            diffuseColor = new SimpleObjectProperty();
            this.name = name;
        }

        public void setSpecularColor(Color color) {
            this.specularColor.set(color);
        }

        public void setDiffuseColor(Color color) {
            this.diffuseColor.set(color);
        }

        public Color getSpecularColor() {
            return specularColor.get();
        }

        public Color getDiffuseColor() {
            return diffuseColor.get();
        }

        public ObjectProperty<Color> specularColorProperty() {
            return specularColor;
        }

        public ObjectProperty<Color> diffuseColorProperty() {
            return diffuseColor;
        }
    }

    public class Geometry {

        Geometry currentSubGeometry;
        boolean hasSubGeometry = false;
        private List<Geometry> subGeometryList;

        String name;
        String material;
        int smoothingGroup;

        //Super fast list
        private List<Integer> faces;
        private List<Float> vertices;
        private List<Float> normals;

        //The offset compared to the complete vertices list
        int offset;
        boolean verticesDone = false;

        public Geometry(String name, String material) {
            this(name);
            this.material = material;
        }

        public Geometry(String name) {

            faces = new ArrayList<>();
            vertices = new ArrayList<>();
            normals = new ArrayList<>();
            subGeometryList = new ArrayList<>();

            this.name = name;
        }

        public boolean hasSubGeometry() {
            return hasSubGeometry;
        }

        public List<Geometry> getSubGeometry() {
            return subGeometryList;
        }

        void addMaterial(String material) {

            hasSubGeometry = true;
            currentSubGeometry = new Geometry(name, material);
            subGeometryList.add(currentSubGeometry);

            //Add vertices and normals to the geometry when first encountering a material
            if (!verticesDone) {
                addVerticesAndNormalsToGeometry();
                verticesDone = true;
            }
        }

        private void addVerticesAndNormalsToGeometry() {
            int size = vertexList.size();
            for (int i = offset - 1; i < size; i++) {

                Vertex v = vertexList.get(i);
                vertices.add(v.x);

                Vertex n = normalList.get(i);
                normals.add(n.x);
                if (flipY) {
                    if (v.z == 0) {
                        vertices.add(v.z);
                    } else {
                        vertices.add(-v.z);
                    }
                    vertices.add(v.y);

                    if (n.z == 0) {
                        normals.add(n.z);
                    } else {
                        normals.add(-n.z);
                    }
                    normals.add(n.y);
                } else {
                    vertices.add(v.y);
                    vertices.add(v.z);

                    normals.add(n.y);
                    normals.add(n.z);
                }
            }
        }

        void postProcessGeometry() {
            if (subGeometryList.size() == 1) {
                material = currentSubGeometry.material;
                faces = currentSubGeometry.faces;
                currentSubGeometry = null;
                hasSubGeometry = false;
                subGeometryList.clear();
            } else if (subGeometryList.size() > 1) {
                for (Geometry subGeometry : subGeometryList) {
                    //TODO Not very efficient since there will be a lot of 
                    //redundant vertices and normals
                    subGeometry.normals = normals;
                    subGeometry.vertices = vertices;
                }
            }
        }

        void addFace(int[] indices) {
            if (hasSubGeometry) {
                currentSubGeometry.faces.add(indices[0] - offset);
                currentSubGeometry.faces.add(indices[3] - offset);
                currentSubGeometry.faces.add(indices[6] - offset);
            } else {
                faces.add(indices[0] - offset);
                faces.add(indices[3] - offset);
                faces.add(indices[6] - offset);
            }
        }

        /**
         * Get the name of the geom
         *
         * @return the name of the geom
         */
        public String getName() {
            return name;
        }

        /**
         * Get the material of the geom
         *
         * @return the material of the geom
         */
        public String getMaterial() {
            return material;
        }

        /**
         * @param withNormals in VertexFormat.POINT_NORMAL_TEXCOORD
         * @return the faces
         */
        public int[] getFaces(boolean withNormals) {

            if (withNormals) {
                int[] faces = new int[this.faces.size() * 3];
                int counter = 0;
                for (int i : this.faces) {
                    faces[counter] = i;
                    counter++;
                    faces[counter] = i;
                    counter++;
                    faces[counter] = 0;
                    counter++;
                }
                return faces;

            } else {
                int[] faces = new int[this.faces.size() * 2];
                int counter = 0;
                for (int i : this.faces) {
                    faces[counter] = i;
                    counter++;
                    faces[counter] = 0;
                    counter++;
                }
                return faces;
            }

        }

        /**
         * @return the vertices
         */
        public float[] getVertices() {

            float[] vertices = new float[this.vertices.size()];
            int counter = 0;
            for (float f : this.vertices) {
                vertices[counter] = f;
                counter++;
            }
            return vertices;
        }

        /**
         * @return the normals
         */
        public float[] getNormals() {

            float[] normals = new float[this.normals.size()];
            int counter = 0;
            for (float f : this.normals) {
                normals[counter] = f;
                counter++;
            }
            return normals;
        }
    }

    class Vertex {

        float x, y, z;

        public Vertex(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
