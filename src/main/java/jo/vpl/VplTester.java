package jo.vpl;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import jo.vpl.core.VplControl;

public class VplTester extends Application {

    public static String[] EXTERNAL_LIBRARIES_DIR = {"build", "ext"};
    int snapshotCounter = 0;

    public void start(Stage stage) throws Exception {
        System.out.println(Thread.currentThread().getName());
        
        AnchorPane pane = new AnchorPane();

        VplControl host = new VplControl();
        Group vplContent = host.Go();

        pane.getChildren().addAll(vplContent);
        pane.getStylesheets().add("css/flat_white.css");
//        pane.getStylesheets().add("css/flat_dark.css");
//        pane.getStylesheets().add("css/default.css");
        pane.getStyleClass().add("vpl");

        Scene scene = new Scene(pane, 1600, 1200);

        stage.setScene(scene);
        stage.setTitle("VPLTester");
        stage.show();

        stage.setFullScreen(false);

//        test(host);
//        scanExternalLibraries();

    }

//    public void defineRunEnvironment() {
//        boolean compiled = false;
//
//        if (compiled) {
//            EXTERNAL_LIBRARIES_DIR = Arrays.copyOfRange(EXTERNAL_LIBRARIES_DIR, 1, EXTERNAL_LIBRARIES_DIR.length);
//        }
//    }
//
//    public void scanExternalLibraries() {
//
//        File dir = jo.util.IO.getDirectory(EXTERNAL_LIBRARIES_DIR);
//        if (!dir.exists() && !dir.isDirectory()) {
//            return;
//        }
//        List<File> externalLibs = jo.util.IO.filterFilesByRegex(dir.listFiles(), "^.*(.jar)$");
//
//    }
//
//    void test(VplControl hostCanvas) throws ClassNotFoundException, IOException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
//
//        //        https://stackoverflow.com/questions/11016092/how-to-load-classes-at-runtime-from-a-folder-or-jar
//        String pathToJar = "build/ext/tp.vpl-0.1.jar";
//        JarFile jarFile = new JarFile(pathToJar);
//        Enumeration<JarEntry> e = jarFile.entries();
//
//        ClassPool cp = ClassPool.getDefault();
//        cp.insertClassPath(pathToJar);
//
//        URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
//        URLClassLoader cl = URLClassLoader.newInstance(urls);
//
//        while (e.hasMoreElements()) {
//            JarEntry je = e.nextElement();
//            if (je.isDirectory() || !je.getName().endsWith(".class")) {
//                continue;
//            }
//            // -6 because of .class
//            String className = je.getName().substring(0, je.getName().length() - 6);
//            className = className.replace('/', '.');
//            CtClass ctClass = cp.get(className);
//
//            
////            if(ctClass.getSuperclass().equals(Hub.class)){
////                
////            }
////            System.out.println(ctClass.getSuperclass());
//
////            System.out.println(ctClass.getSuperclass().getName());
//            Class c = cl.loadClass(className);
//
////            Hub hub = (Hub) c.getConstructor(VplControl.class).newInstance(hostCanvas);
//            Hub hub = (Hub) c.getConstructor(VplControl.class).newInstance(hostCanvas);
//            hub.setLayoutX(100);
//            hub.setLayoutY(100);
//
//            hostCanvas.getChildren().add(hub);
//            hostCanvas.hubSet.add(hub);
//        }
//
//    }

//    private void takeSnapshot(final Scene scene) {
//        // Take snapshot of the scene
//        final WritableImage writableImage = scene.snapshot(null);
//
//        // Write snapshot to file system as a .png image
//        final File outFile = new File("snapshot/radialmenu-snapshot-"
//                + snapshotCounter + ".png");
//        outFile.getParentFile().mkdirs();
//        try {
//            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png",
//                    outFile);
//        } catch (final IOException ex) {
//            System.out.println(ex.getMessage());
//        }
//
//        snapshotCounter++;
//    }
}

//    private final ObjectProperty<Color> sceneColorProperty
//            = new SimpleObjectProperty<>(Color.WHITE);
//    @Override
//    public void start(Stage primaryStage) {
//
//        Rectangle rect = new Rectangle(400, 400);
//        rect.fillProperty().bind(sceneColorProperty);
//
//        StackPane pane = new StackPane(rect);
//        
//        Scene scene = new Scene(pane, 400, 400);
//        scene.getStylesheets().add("css/color.css");
//        scene.setOnMouseClicked(e -> {
//            if (e.getButton().equals(MouseButton.SECONDARY)) {
//                CustomColorBox myCustomColorPicker = new CustomColorBox();
//                myCustomColorPicker.setCurrentColor(sceneColorProperty.get());
//
//                CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
//                itemColor.setHideOnClick(false);
//                sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
//                ContextMenu contextMenu = new ContextMenu(itemColor);
//                contextMenu.setOnHiding(t -> sceneColorProperty.unbind());
//                contextMenu.show(scene.getWindow(), e.getScreenX(), e.getScreenY());
//            }
//        });
//
//        primaryStage.setTitle("Custom Color Selector");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
