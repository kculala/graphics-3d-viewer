package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static sample.Matrix.multiplyMatrix;

public class Main extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final double SHAPE_DIMENSION = 20.0;
    private static final double ROTATION_FACTOR = 0.2;

    private static final String SCENE_TITLE = "COMP 4560 Assignment 5 - A00797801";
    private static final String USAGE_TITLE = "Usage";
    private static final String INSTRUCTIONS = "Please select two (2) files:\n" +
            "The first file should contain a list of 3 numbers representing a vertex\n" +
            "e.g. 10 10 0\n" +
            "The second file should contain a list of 2 numbers representing a line\n" +
            "e.g. 1 2";
    private static final String MENU_LABEL_FILE = "File";
    private static final String MENU_ITEM_LABEL_NEW_DATA = "New Data...";

    private enum ImagePaths {
        TRANSLATE_LEFT ("left"),
        TRANSLATE_RIGHT ("right"),
        TRANSLATE_UP ("up"),
        TRANSLATE_DOWN ("down"),
        SCALE_UP ("scale-up"),
        SCALE_DOWN ("scale-down"),
        ROTATE_X ("rotate-x"),
        ROTATE_Y ("rotate-y"),
        ROTATE_Z ("rotate-z"),
        SHEAR_LEFT ("shear-left"),
        SHEAR_RIGHT ("shear-right"),
        RESTORE ("home"),
        EXIT ("exit");

        private final String path;

        ImagePaths(final String s) {
            path = "resources/images/" + s + ".png";
        }

        public String toString() {
            return this.path;
        }
    }

    private enum Tooltips {
        TRANSLATE_LEFT ("Translate Left 25"),
        TRANSLATE_RIGHT ("Translate Right 25"),
        TRANSLATE_UP ("Translate Up 10"),
        TRANSLATE_DOWN ("Translate Down 10"),
        SCALE_UP ("Scale 10% Up"),
        SCALE_DOWN ("Scale 10% Down"),
        ROTATE_X ("Rotate About X"),
        ROTATE_Y ("Rotate About Y"),
        ROTATE_Z ("Rotate About Z"),
        SHEAR_LEFT ("Shear Left in Y"),
        SHEAR_RIGHT ("Shear Right in Y"),
        RESTORE ("Restore Initial Image"),
        EXIT ("Exit");

        private final String text;

        Tooltips(final String s) {
            text = s;
        }

        public String toString() {
            return this.text;
        }
    }

    // initial points
    private Matrix initialPoints = new Matrix();
    // cumulative transformation matrix
    private Matrix tNet = new Matrix();
    private List<Pair> lines = new ArrayList<>();
    private Canvas canvas;

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle(SCENE_TITLE);
        primaryStage.setMaximized(true);
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(USAGE_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(INSTRUCTIONS);
        // alert.showAndWait();

        // canvas
        Pane wrapper = new Pane();
        canvas = new Canvas();
        wrapper.getChildren().add(canvas);
        canvas.widthProperty().bind(wrapper.widthProperty());
        canvas.heightProperty().bind(wrapper.heightProperty());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);

        // toolbar
        Image imageLeft = new Image(new FileInputStream(ImagePaths.TRANSLATE_LEFT.toString()));
        Button buttonTranslateLeft = new Button();
        buttonTranslateLeft.setGraphic(new ImageView(imageLeft));
        Tooltip tooltipLeft = new Tooltip(Tooltips.TRANSLATE_LEFT.toString());
        buttonTranslateLeft.setTooltip(tooltipLeft);
        buttonTranslateLeft.setOnAction(e -> {
            translate(-75.0, 0.0, 0.0);
            draw(gc);
        });

        Image imageRight = new Image(new FileInputStream(ImagePaths.TRANSLATE_RIGHT.toString()));
        Button buttonTranslateRight = new Button();
        buttonTranslateRight.setGraphic(new ImageView(imageRight));
        Tooltip tooltipRight = new Tooltip(Tooltips.TRANSLATE_RIGHT.toString());
        buttonTranslateRight.setTooltip(tooltipRight);
        buttonTranslateRight.setOnAction(e -> {
            translate(75.0, 0.0, 0.0);
            draw(gc);
        });

        Image imageUp = new Image(new FileInputStream(ImagePaths.TRANSLATE_UP.toString()));
        Button buttonTranslateUp = new Button();
        buttonTranslateUp.setGraphic(new ImageView(imageUp));
        Tooltip tooltipUp = new Tooltip(Tooltips.TRANSLATE_UP.toString());
        buttonTranslateUp.setTooltip(tooltipUp);
        buttonTranslateUp.setOnAction(e -> {
            translate(0.0, -35.0, 0.0);
            draw(gc);
        });

        Image imageDown = new Image(new FileInputStream(ImagePaths.TRANSLATE_DOWN.toString()));
        Button buttonTranslateDown = new Button();
        buttonTranslateDown.setGraphic(new ImageView(imageDown));
        Tooltip tooltipDown = new Tooltip(Tooltips.TRANSLATE_DOWN.toString());
        buttonTranslateDown.setTooltip(tooltipDown);
        buttonTranslateDown.setOnAction(e -> {
            translate(0.0, 35.0, 0.0);
            draw(gc);
        });

        Image imageScaleUp = new Image(new FileInputStream(ImagePaths.SCALE_UP.toString()));
        Button buttonScaleUp = new Button();
        buttonScaleUp.setGraphic(new ImageView(imageScaleUp));
        Tooltip tooltipScaleUp = new Tooltip(Tooltips.SCALE_UP.toString());
        buttonScaleUp.setTooltip(tooltipScaleUp);
        buttonScaleUp.setOnAction(e -> {
            // calculate middle of shape
            double xMiddle = tNet.getRow(3).getX();
            double yMiddle = tNet.getRow(3).getY();
            double zMiddle = tNet.getRow(3).getZ();
            // offset top left point of shape by half of size
            xMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(0).getX());
            yMiddle -= SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(1).getY());
            zMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(2).getZ());

            // move middle of shape to 0, 0
            translate(-xMiddle, -yMiddle, -zMiddle);

            scale(1.1, 1.1, 1.1);

            // move shape back
            translate(xMiddle, yMiddle, zMiddle);

            draw(gc);
        });

        Image imageScaleDown = new Image(new FileInputStream(ImagePaths.SCALE_DOWN.toString()));
        Button buttonScaleDown = new Button();
        buttonScaleDown.setGraphic(new ImageView(imageScaleDown));
        Tooltip tooltipScaleDown = new Tooltip(Tooltips.SCALE_DOWN.toString());
        buttonScaleDown.setTooltip(tooltipScaleDown);
        buttonScaleDown.setOnAction(e -> {
            // calculate middle of shape
            double xMiddle = tNet.getRow(3).getX();
            double yMiddle = tNet.getRow(3).getY();
            double zMiddle = tNet.getRow(3).getZ();
            // offset top left point of shape by half of size
            xMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(0).getX());
            yMiddle -= SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(1).getY());
            zMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(2).getZ());

            // move middle of shape to 0,0
            translate(-xMiddle, -yMiddle, -zMiddle);

            scale(0.9, 0.9, 0.9);

            // move shape back
            translate(xMiddle, yMiddle, zMiddle);

            draw(gc);
        });

        Image imageRotateX = new Image(new FileInputStream(ImagePaths.ROTATE_X.toString()));
        Button buttonRotateX = new Button();
        buttonRotateX.setGraphic(new ImageView(imageRotateX));
        Tooltip tooltipRotateX = new Tooltip(Tooltips.ROTATE_X.toString());
        buttonRotateX.setTooltip(tooltipRotateX);
        buttonRotateX.setOnAction(e -> {
            System.out.println("ORIGINAL\n" + multiplyMatrix(initialPoints, tNet));
            // calculate middle of shape
            double xMiddle = tNet.getRow(3).getX();
            double yMiddle = tNet.getRow(3).getY();
//            double zMiddle = tNet.getRow(3).getZ();
            // offset top left point of shape by half of size
            xMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(0).getX());
            yMiddle -= SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(1).getY());
            double zMiddle = SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(2).getZ());

            // move middle of shape to 0, 0
//            translate(-xMiddle, -yMiddle, -zMiddle);
            System.out.println("MOVE TO ORIGIN\n" + multiplyMatrix(initialPoints, tNet));

            // rotate
            rotateX();
//            System.out.println("ROTATE\n" + multiplyMatrix(initialPoints, tNet));

            // move shape back
            translate(xMiddle, yMiddle, zMiddle);

            draw(gc);
        });

        Image imageRotateY = new Image(new FileInputStream(ImagePaths.ROTATE_Y.toString()));
        Button buttonRotateY = new Button();
        buttonRotateY.setGraphic(new ImageView(imageRotateY));
        Tooltip tooltipRotateY = new Tooltip(Tooltips.ROTATE_Y.toString());
        buttonRotateY.setTooltip(tooltipRotateY);
        buttonRotateY.setOnAction(e -> {
            // calculate middle of shape
            double xMiddle = tNet.getRow(3).getX();
            double yMiddle = tNet.getRow(3).getY();
            // offset top left point of shape by half of size
            xMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(0).getX());
            yMiddle -= SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(0).getX());
            double zOffset =  SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(0).getX());

            // move middle of shape to 0, 0
            translate(-xMiddle, -yMiddle, -zOffset);

            // rotate
            rotateY();

            // move shape back
            translate(xMiddle, yMiddle, zOffset);

            draw(gc);
        });

        Image imageRotateZ = new Image(new FileInputStream(ImagePaths.ROTATE_Z.toString()));
        Button buttonRotateZ = new Button();
        buttonRotateZ.setGraphic(new ImageView(imageRotateZ));
        Tooltip tooltipRotateZ = new Tooltip(Tooltips.ROTATE_Z.toString());
        buttonRotateZ.setTooltip(tooltipRotateZ);
        buttonRotateZ.setOnAction(e -> {
            // calculate middle of shape
            double xMiddle = tNet.getRow(3).getX();
            double yMiddle = tNet.getRow(3).getY();
            double zMiddle = tNet.getRow(3).getZ();
            // offset top left point of shape by half of size
            xMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(0).getX());
            yMiddle -= SHAPE_DIMENSION * Math.abs(tNet.getRow(1).getY());
            zMiddle += SHAPE_DIMENSION/2 * Math.abs(tNet.getRow(2).getZ());

            // move middle of shape to 0, 0
            translate(-xMiddle, -yMiddle, -zMiddle);

            // rotate
            rotateZ();

            // move shape back
            translate(xMiddle, yMiddle, 0.0);

            draw(gc);
        });

        Image imageShearLeft = new Image(new FileInputStream(ImagePaths.SHEAR_LEFT.toString()));
        Button buttonShearLeft = new Button();
        buttonShearLeft.setGraphic(new ImageView(imageShearLeft));
        Tooltip tooltipShearLeft = new Tooltip(Tooltips.SHEAR_LEFT.toString());
        buttonShearLeft.setTooltip(tooltipShearLeft);

        Image imageShearRight = new Image(new FileInputStream(ImagePaths.SHEAR_RIGHT.toString()));
        Button buttonShearRight = new Button();
        buttonShearRight.setGraphic(new ImageView(imageShearRight));
        Tooltip tooltipShearRight = new Tooltip(Tooltips.SHEAR_RIGHT.toString());
        buttonShearRight.setTooltip(tooltipShearRight);

        Image imageRestore = new Image(new FileInputStream(ImagePaths.RESTORE.toString()));
        Button buttonRestore = new Button();
        buttonRestore.setGraphic(new ImageView(imageRestore));
        Tooltip tooltipRestore = new Tooltip(Tooltips.RESTORE.toString());
        buttonRestore.setTooltip(tooltipRestore);

        buttonRestore.setOnAction(e -> {
            initShape();
            draw(gc);
        });

        Image imageExit = new Image(new FileInputStream(ImagePaths.EXIT.toString()));
        Button buttonExit = new Button();
        buttonExit.setGraphic(new ImageView(imageExit));
        Tooltip tooltipExit = new Tooltip(Tooltips.EXIT.toString());
        buttonExit.setTooltip(tooltipExit);

        Button spinX = new Button("X");
        Button spinY = new Button("Y");
        Button spinZ = new Button("Z");

        ToolBar toolBar = new ToolBar();
        toolBar.setOrientation(Orientation.VERTICAL);
        toolBar.getItems().addAll(buttonTranslateLeft, buttonTranslateRight, buttonTranslateUp, buttonTranslateDown, new Separator(),
                                  buttonScaleUp, buttonScaleDown, new Separator(),
                                  buttonRotateX, buttonRotateY, buttonRotateZ, new Separator(),
                                  buttonShearLeft, buttonShearRight, new Separator(),
                                  buttonRestore, buttonExit, new Separator(),
                                  spinX, spinY, spinZ);

        // menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu(MENU_LABEL_FILE);
        MenuItem newData = new MenuItem(MENU_ITEM_LABEL_NEW_DATA);
        menu.getItems().add(newData);
        menuBar.getMenus().add(menu);

        newData.addEventHandler(ActionEvent.ACTION,
                event -> {
                    FileChooser fileChooser = new FileChooser();

                    configureFileChooser(fileChooser, true);
                    File verticesFile = fileChooser.showOpenDialog(primaryStage);
                    if (verticesFile != null) {
                        openVerticesFile(verticesFile);
                    }

                    configureFileChooser(fileChooser, false);
                    File linesFile = fileChooser.showOpenDialog(primaryStage);
                    if (linesFile != null) {
                        openLinesFile(linesFile);
                    }

                    if (verticesFile != null && linesFile != null) {
                        initShape();
                        draw(gc);
                    }
                }
        );

        // stage
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());

        borderPane.setTop(menuBar);
        borderPane.setCenter(wrapper);
        borderPane.setRight(toolBar);

        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initShape() {
        tNet = new TransformationMatrix();

        reflectY();

        // move to 0, 0
        translate(-SHAPE_DIMENSION/2, SHAPE_DIMENSION/2, -SHAPE_DIMENSION/2);

        // scale shape up to half the height of the canvas
        double scaleFactor = ((canvas.getHeight()/2) * SHAPE_DIMENSION)/(canvas.getHeight()/2);
        scale(scaleFactor, scaleFactor, scaleFactor);

        // move shape to center of canvas
        double xMiddle = canvas.getWidth()/2;
        double yMiddle = canvas.getHeight()/2;
        double ZMiddle = canvas.getHeight()/2;
        translate(xMiddle, yMiddle, ZMiddle);
    }

    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Matrix screenPoints = multiplyMatrix(initialPoints, tNet);
        for (Pair tmp : this.lines) {
            double x1 = screenPoints.getRow((int)tmp.getKey()).getX();
            double y1 = screenPoints.getRow((int)tmp.getKey()).getY();
            double x2 = screenPoints.getRow((int)tmp.getValue()).getX();
            double y2 = screenPoints.getRow((int)tmp.getValue()).getY();
            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser, boolean isSelectingVertices) {
        String dataFile = isSelectingVertices ? "vertices" : "lines";
        String title = "Choose data file containing list of " + dataFile;
        fileChooser.setTitle(title);

        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("dat", "*.dat")
        );
    }

    private Vertex parseVertexRow(String line) {
        String[] tokens = line.split(" ");
        if(tokens.length != 3)
            return null;
        Vertex vertex = new Vertex();
        Double x = Double.parseDouble(tokens[0]);
        Double y = Double.parseDouble(tokens[1]);
        Double z = Double.parseDouble(tokens[2]);
        vertex.setX(x);
        vertex.setY(y);
        vertex.setZ(z);
        return vertex;
    }

    private void openVerticesFile(File file) {
        Matrix vertices = new Matrix();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Vertex tmp = parseVertexRow(line);
                if (tmp == null)
                    break;
                vertices.addRow(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initialPoints = vertices;
    }

    private Pair<Integer, Integer> parseLineRow(String line) {
        String[] tokens = line.split(" ");
        if (tokens.length != 2)
            return null;

        Integer vertexOne = Integer.parseInt(tokens[0]);
        Integer vertexTwo = Integer.parseInt(tokens[1]);
        return new Pair<>(vertexOne, vertexTwo);
    }

    private void printLines() {
        for (Pair tmp : this.lines) {
            System.out.println(tmp.toString());
        }
    }

    private void openLinesFile(File file) {
        List<Pair> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pair<Integer, Integer> tmp = parseLineRow(line);
                if (tmp == null)
                    break;
                lines.add(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.lines = lines;

        // debugging
        // printLines();
    }

    private void translate(Double xShift, Double yShift, Double zShift) {
        Matrix translationMatrix = new TransformationMatrix();
        Vertex r4 = new Vertex(xShift, yShift, zShift, 1.0);
        translationMatrix.setRow(translationMatrix.size() - 1, r4);
        tNet = multiplyMatrix(tNet, translationMatrix);
    }

    private void reflectY() {
        Matrix reflectMatrix = new TransformationMatrix();
        Vertex r2 = new Vertex(0.0, -1.0, 0.0, 0.0);
        reflectMatrix.setRow(1, r2);
        tNet = multiplyMatrix(tNet, reflectMatrix);
    }

    private void scale(Double xScale, Double yScale, Double zScale) {
        Matrix scalingMatrix = new TransformationMatrix();
        Vertex r1 = new Vertex(xScale, 0.0, 0.0, 0.0);
        Vertex r2 = new Vertex(0.0, yScale, 0.0, 0.0);
        Vertex r3 = new Vertex(0.0, 0.0, zScale, 0.0);
        scalingMatrix.setRow(0, r1);
        scalingMatrix.setRow(1, r2);
        scalingMatrix.setRow(2, r3);
        tNet = multiplyMatrix(tNet, scalingMatrix);
    }

    private void rotateZ() {
        Matrix rotationMatrix = new TransformationMatrix();
        Vertex r1 = new Vertex(Math.cos(ROTATION_FACTOR), Math.sin(ROTATION_FACTOR), 0.0, 0.0);
        Vertex r2 = new Vertex(-Math.sin(ROTATION_FACTOR), Math.cos(ROTATION_FACTOR), 0.0, 0.0);
        rotationMatrix.setRow(0, r1);
        rotationMatrix.setRow(1, r2);
        tNet = multiplyMatrix(tNet, rotationMatrix);
    }

    private void rotateY() {
        Matrix rotationMatrix = new TransformationMatrix();
        Vertex r1 = new Vertex(Math.cos(ROTATION_FACTOR), 0.0, -Math.sin(ROTATION_FACTOR), 0.0);
        Vertex r3 = new Vertex(Math.sin(ROTATION_FACTOR), 0.0, Math.cos(ROTATION_FACTOR), 0.0);
        rotationMatrix.setRow(0, r1);
        rotationMatrix.setRow(2, r3);
        tNet = multiplyMatrix(tNet, rotationMatrix);
    }

    private void rotateX() {
        Matrix rotationMatrix = new TransformationMatrix();
        Vertex r2 = new Vertex(0.0, Math.cos(ROTATION_FACTOR), Math.sin(ROTATION_FACTOR), 0.0);
        Vertex r3 = new Vertex(0.0, -Math.sin(ROTATION_FACTOR), Math.cos(ROTATION_FACTOR), 0.0);
        rotationMatrix.setRow(1, r2);
        rotationMatrix.setRow(2, r3);
        tNet = multiplyMatrix(tNet, rotationMatrix);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
