/*
 * Copyright (c) 2013 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jo.vpl.watch3D;

import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import javafx.scene.shape.Box;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.PhongMaterial;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;

/**
 *
 * @author JoostMeulenkamp
 */
public class ObjViewer extends BorderPane {

    final Group root = new Group();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -30;
    private static final double CAMERA_INITIAL_X_ANGLE = 350.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    private static final double AXIS_LENGTH = 1.0;
    final Xform axisGroup = new Xform();

    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    //For Model Viewer
    public ObjViewer(Pane pane) {
        SubScene scene = createScene();
        scene.widthProperty().bind(pane.widthProperty());
        scene.heightProperty().bind(pane.heightProperty());
        scene.setManaged(false);
        setCenter(scene);

        scene.setOnKeyPressed(this::scene_KeyPress);
        scene.setOnMousePressed(this::scene_MousePress);
        scene.setOnMouseDragged(this::scene_MouseDrag);

        scene.setOnScroll(this::scene_Scroll);
    }

    public void addGeometry(Group group) {
        world.getChildren().add(group);
    }

    public void addGeometry(Shape3D shape) {
        world.getChildren().add(shape);
    }

    public void removeGeometry(Group group) {
        world.getChildren().remove(group);
    }

    public void removeGeometry(Shape3D shape) {
        world.getChildren().remove(shape);
    }

    public void clearGeometry() {
        world.getChildren().clear();
        buildAxes();
    }

    public void setColorByID(String id, Color color) {
        Group element = (Group) world.lookup("#" + id);

        if (element == null) {
            return;
        }

        MeshView mv = (MeshView) element.getChildren().get(0);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color.brighter());
        mv.setMaterial(material);
    }

    private SubScene createScene() {
        root.getChildren().add(world);
        SubScene scene = new SubScene(root, 320, 240, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.rgb(255, 255, 255, 0.));
        buildCamera();
        buildAxes();
        scene.setCamera(camera);
        return scene;
    }

    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    public void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        redMaterial.setSpecularColor(Color.LIGHTSALMON);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.GREEN);
        greenMaterial.setSpecularColor(Color.LIGHTGREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.BLUE);
        blueMaterial.setSpecularColor(Color.LIGHTBLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 0.02, 0.02);
        final Box yAxis = new Box(0.02, AXIS_LENGTH, 0.02);
        final Box zAxis = new Box(0.02, 0.02, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }

    private void scene_KeyPress(KeyEvent e) {
//        System.out.println(e.getCode());
        switch (e.getCode()) {
            case Z:
                cameraXform2.t.setX(0.0);
                cameraXform2.t.setY(0.0);
                cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                break;
            case X:
                axisGroup.setVisible(!axisGroup.isVisible());
                break;
            case V:
                //moleculeGroup.setVisible(!moleculeGroup.isVisible());
                break;
        }
    }

    private void scene_MousePress(MouseEvent e) {
        mousePosX = e.getSceneX();
        mousePosY = e.getSceneY();
        mouseOldX = e.getSceneX();
        mouseOldY = e.getSceneY();
    }

    private void scene_MouseDrag(MouseEvent e) {
//        System.out.println("fire ObjViewer");
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = e.getSceneX();
        mousePosY = e.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        double modifier = 1.0;

        if (e.isControlDown()) {
            modifier = CONTROL_MULTIPLIER;
        }
        if (e.isShiftDown()) {
            modifier = SHIFT_MULTIPLIER;
        }
        if (e.isPrimaryButtonDown()) {
            double x = camera.getTranslateX();
            double y = camera.getTranslateY();
            double newX = x - mouseDeltaX * MOUSE_SPEED * modifier;
            double newY = y - mouseDeltaY * MOUSE_SPEED * modifier;
            camera.setTranslateX(newX);
            camera.setTranslateY(newY);

        } else if (e.isSecondaryButtonDown()) {
            cameraXform.ry.setAngle(cameraXform.ry.getAngle() + mouseDeltaX * modifier * ROTATION_SPEED);
            cameraXform.rx.setAngle(cameraXform.rx.getAngle() - mouseDeltaY * modifier * ROTATION_SPEED);
        } else if (e.isMiddleButtonDown()) {
            cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
            cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
        }
        e.consume();
    }

    /**
     * Zooming function on scrolling
     *
     * @param e
     */
    private void scene_Scroll(ScrollEvent e) {
        double z = camera.getTranslateZ();
        double newZ = z + e.getDeltaY() * MOUSE_SPEED;
        camera.setTranslateZ(newZ);
    }

}
