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

    private static final String SCENE_TITLE = "COMP 4560 Assignment 5 - A00797801";
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

    private static final double SHAPE_DIMENSION = 20.0;

    // constants used for various transformations
    private static final double ROTATION = 0.05;
    private static final double HORIZONTAL_TRANSLATION = 75.0;
    private static final double VERTICAL_TRANSLATION = 35.0;
    private static final double SCALE = 0.1;

    // amount of time between each (continuous) rotation in milliseconds
    private static final long TIMER_DELAY = 50;

    // initial points
    private Matrix initialPoints = new Matrix();
    // cumulative transformation matrix
    private Matrix tNet = new Matrix();
    // points used to render shape at any given moment.
    private Matrix currentPoints = new Matrix();

    // current scaling applied to shape; used to calculate how much to translate a shear to get shape to original spot.
    private double currentScaleFactor;

    private List<Pair> lines = new ArrayList<>();
    private Canvas canvas;
    private GraphicsContext gc;

    private Timer timer;
    private boolean isContinuouslyRotating;

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle(SCENE_TITLE);
        primaryStage.setMaximized(true);
        Group root = new Group();
        Scene scene = new Scene(root, Color.BLACK);

        // canvas
        Pane wrapper = new Pane();
        canvas = new Canvas();
        wrapper.getChildren().add(canvas);
        canvas.widthProperty().bind(wrapper.widthProperty());
        canvas.heightProperty().bind(wrapper.heightProperty());

        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);

        isContinuouslyRotating = false;
        timer = new Timer();

        // toolbar
        Image imageLeft = new Image(new FileInputStream(ImagePaths.TRANSLATE_LEFT.toString()));
        Button buttonTranslateLeft = new Button();
        buttonTranslateLeft.setGraphic(new ImageView(imageLeft));
        Tooltip tooltipLeft = new Tooltip(Tooltips.TRANSLATE_LEFT.toString());
        buttonTranslateLeft.setTooltip(tooltipLeft);
        buttonTranslateLeft.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                translate(-HORIZONTAL_TRANSLATION, 0.0, 0.0);
                draw(gc);
            }
        });

        Image imageRight = new Image(new FileInputStream(ImagePaths.TRANSLATE_RIGHT.toString()));
        Button buttonTranslateRight = new Button();
        buttonTranslateRight.setGraphic(new ImageView(imageRight));
        Tooltip tooltipRight = new Tooltip(Tooltips.TRANSLATE_RIGHT.toString());
        buttonTranslateRight.setTooltip(tooltipRight);
        buttonTranslateRight.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                translate(HORIZONTAL_TRANSLATION, 0.0, 0.0);
                draw(gc);
            }
        });

        Image imageUp = new Image(new FileInputStream(ImagePaths.TRANSLATE_UP.toString()));
        Button buttonTranslateUp = new Button();
        buttonTranslateUp.setGraphic(new ImageView(imageUp));
        Tooltip tooltipUp = new Tooltip(Tooltips.TRANSLATE_UP.toString());
        buttonTranslateUp.setTooltip(tooltipUp);
        buttonTranslateUp.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                translate(0.0, -VERTICAL_TRANSLATION, 0.0);
                draw(gc);
            }
        });

        Image imageDown = new Image(new FileInputStream(ImagePaths.TRANSLATE_DOWN.toString()));
        Button buttonTranslateDown = new Button();
        buttonTranslateDown.setGraphic(new ImageView(imageDown));
        Tooltip tooltipDown = new Tooltip(Tooltips.TRANSLATE_DOWN.toString());
        buttonTranslateDown.setTooltip(tooltipDown);
        buttonTranslateDown.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                translate(0.0, VERTICAL_TRANSLATION, 0.0);
                draw(gc);
            }
        });

        Image imageScaleUp = new Image(new FileInputStream(ImagePaths.SCALE_UP.toString()));
        Button buttonScaleUp = new Button();
        buttonScaleUp.setGraphic(new ImageView(imageScaleUp));
        Tooltip tooltipScaleUp = new Tooltip(Tooltips.SCALE_UP.toString());
        buttonScaleUp.setTooltip(tooltipScaleUp);
        buttonScaleUp.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                double xMiddle = currentPoints.getRow(0).getX();
                double yMiddle = currentPoints.getRow(0).getY();
                double zMiddle = currentPoints.getRow(0).getZ();

                // move middle of shape to 0, 0
                translate(-xMiddle, -yMiddle, -zMiddle);

                scale(1.0 + SCALE);

                // move shape back
                translate(xMiddle, yMiddle, zMiddle);

                draw(gc);
            }
        });

        Image imageScaleDown = new Image(new FileInputStream(ImagePaths.SCALE_DOWN.toString()));
        Button buttonScaleDown = new Button();
        buttonScaleDown.setGraphic(new ImageView(imageScaleDown));
        Tooltip tooltipScaleDown = new Tooltip(Tooltips.SCALE_DOWN.toString());
        buttonScaleDown.setTooltip(tooltipScaleDown);
        buttonScaleDown.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                double xMiddle = currentPoints.getRow(0).getX();
                double yMiddle = currentPoints.getRow(0).getY();
                double zMiddle = currentPoints.getRow(0).getZ();

                // move middle of shape to 0,0
                translate(-xMiddle, -yMiddle, -zMiddle);

                scale(1.0 - SCALE);

                // move shape back
                translate(xMiddle, yMiddle, zMiddle);

                draw(gc);
            }
        });

        Image imageRotateX = new Image(new FileInputStream(ImagePaths.ROTATE_X.toString()));
        Button buttonRotateX = new Button();
        buttonRotateX.setGraphic(new ImageView(imageRotateX));
        Tooltip tooltipRotateX = new Tooltip(Tooltips.ROTATE_X.toString());
        buttonRotateX.setTooltip(tooltipRotateX);
        buttonRotateX.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                double xMiddle = currentPoints.getRow(0).getX();
                double yMiddle = currentPoints.getRow(0).getY();
                double zMiddle = currentPoints.getRow(0).getZ();

                // move middle of shape to 0, 0
                translate(-xMiddle, -yMiddle, -zMiddle);

                // rotate
                rotateX();

                // move shape back
                translate(xMiddle, yMiddle, zMiddle);

                draw(gc);
            }
        });

        Image imageRotateY = new Image(new FileInputStream(ImagePaths.ROTATE_Y.toString()));
        Button buttonRotateY = new Button();
        buttonRotateY.setGraphic(new ImageView(imageRotateY));
        Tooltip tooltipRotateY = new Tooltip(Tooltips.ROTATE_Y.toString());
        buttonRotateY.setTooltip(tooltipRotateY);
        buttonRotateY.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                double xMiddle = currentPoints.getRow(0).getX();
                double yMiddle = currentPoints.getRow(0).getY();
                double zMiddle = currentPoints.getRow(0).getZ();

                // move middle of shape to 0, 0
                translate(-xMiddle, -yMiddle, -zMiddle);

                // rotate
                rotateY();

                // move shape back
                translate(xMiddle, yMiddle, zMiddle);

                draw(gc);
            }
        });

        Image imageRotateZ = new Image(new FileInputStream(ImagePaths.ROTATE_Z.toString()));
        Button buttonRotateZ = new Button();
        buttonRotateZ.setGraphic(new ImageView(imageRotateZ));
        Tooltip tooltipRotateZ = new Tooltip(Tooltips.ROTATE_Z.toString());
        buttonRotateZ.setTooltip(tooltipRotateZ);
        buttonRotateZ.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                double xMiddle = currentPoints.getRow(0).getX();
                double yMiddle = currentPoints.getRow(0).getY();
                double zMiddle = currentPoints.getRow(0).getZ();

                // move middle of shape to 0, 0
                translate(-xMiddle, -yMiddle, -zMiddle);

                // rotate
                rotateZ();

                // move shape back
                translate(xMiddle, yMiddle, zMiddle);

                draw(gc);
            }
        });

        Image imageShearLeft = new Image(new FileInputStream(ImagePaths.SHEAR_LEFT.toString()));
        Button buttonShearLeft = new Button();
        buttonShearLeft.setGraphic(new ImageView(imageShearLeft));
        Tooltip tooltipShearLeft = new Tooltip(Tooltips.SHEAR_LEFT.toString());
        buttonShearLeft.setTooltip(tooltipShearLeft);
        buttonShearLeft.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                double xMiddle = currentPoints.getRow(0).getX() + (SHAPE_DIMENSION / 2 * this.currentScaleFactor);
                double yMiddle = currentPoints.getRow(0).getY() + (SHAPE_DIMENSION / 2 * this.currentScaleFactor);
                double zMiddle = currentPoints.getRow(0).getZ();

                // move shape to original position (bot left corner at 0,0)
                translate(-xMiddle, -yMiddle, -zMiddle);

                // shear
                shearLeft();

                // move shape back
                translate(xMiddle, yMiddle, zMiddle);

                draw(gc);
            }
        });

        Image imageShearRight = new Image(new FileInputStream(ImagePaths.SHEAR_RIGHT.toString()));
        Button buttonShearRight = new Button();
        buttonShearRight.setGraphic(new ImageView(imageShearRight));
        Tooltip tooltipShearRight = new Tooltip(Tooltips.SHEAR_RIGHT.toString());
        buttonShearRight.setTooltip(tooltipShearRight);
        buttonShearRight.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                double xMiddle = currentPoints.getRow(0).getX() + (SHAPE_DIMENSION / 2 * this.currentScaleFactor);
                double yMiddle = currentPoints.getRow(0).getY() + (SHAPE_DIMENSION / 2 * this.currentScaleFactor);
                double zMiddle = currentPoints.getRow(0).getZ();

                // move shape to original position (bot left corner at 0,0)
                translate(-xMiddle, -yMiddle, -zMiddle);

                // shear
                shearRight();

                // move shape back
                translate(xMiddle, yMiddle, zMiddle);

                draw(gc);
            }
        });

        Image imageRestore = new Image(new FileInputStream(ImagePaths.RESTORE.toString()));
        Button buttonRestore = new Button();
        buttonRestore.setGraphic(new ImageView(imageRestore));
        Tooltip tooltipRestore = new Tooltip(Tooltips.RESTORE.toString());
        buttonRestore.setTooltip(tooltipRestore);
        buttonRestore.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                initShape();
                draw(gc);
            }
        });

        Image imageExit = new Image(new FileInputStream(ImagePaths.EXIT.toString()));
        Button buttonExit = new Button();
        buttonExit.setGraphic(new ImageView(imageExit));
        Tooltip tooltipExit = new Tooltip(Tooltips.EXIT.toString());
        buttonExit.setTooltip(tooltipExit);
        buttonExit.setOnAction(e -> primaryStage.close());

        Button spinX = new Button("X");
        spinX.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                isContinuouslyRotating = true;
                timer.schedule(new cRotationX(), 0, TIMER_DELAY);
            }
        });

        Button spinY = new Button("Y");
        spinY.setOnAction(e -> {
            if(isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                isContinuouslyRotating = true;
                timer.schedule(new cRotationY(), 0, TIMER_DELAY);
            }
        });

        Button spinZ = new Button("Z");
        spinZ.setOnAction(e -> {
            if (isContinuouslyRotating) {
                isContinuouslyRotating = false;
                clearTimer();
            } else {
                isContinuouslyRotating = true;
                timer.schedule(new cRotationZ(), 0, 50);
            }
        });

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
        currentPoints = initialPoints;
        this.currentScaleFactor = 1.0;

        reflectY();

        // move to 0, 0
        translate(-SHAPE_DIMENSION/2, SHAPE_DIMENSION/2, 0.0);

        // scale shape up to half the height of the canvas
        double scaleFactor = ((canvas.getHeight()/2) * SHAPE_DIMENSION)/(canvas.getHeight()/2);
        scale(scaleFactor);

        // move shape to center of canvas
        double xMiddle = canvas.getWidth()/2;
        double yMiddle = canvas.getHeight()/2;
        translate(xMiddle, yMiddle, 0.0);
    }

    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentPoints = multiplyMatrix(currentPoints, tNet);
        for (Pair tmp : this.lines) {
            double x1 = currentPoints.getRow((int)tmp.getKey()).getX();
            double y1 = currentPoints.getRow((int)tmp.getKey()).getY();
            double x2 = currentPoints.getRow((int)tmp.getValue()).getX();
            double y2 = currentPoints.getRow((int)tmp.getValue()).getY();
            gc.strokeLine(x1, y1, x2, y2);
        }
        tNet = new TransformationMatrix();
    }

    // IO related methods
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
    }

    // transformation methods

    /**
     * Update tNet with a translation
     *
     * @param xShift the number of screen coordinates to translate in the positive x direction
     * @param yShift the number of screen coordinates to translate in the positive y direction
     * @param zShift the number of screen coordinates to translate in the positive z direction
     */
    private void translate(Double xShift, Double yShift, Double zShift) {
        Matrix translationMatrix = new TransformationMatrix();
        Vertex r4 = new Vertex(xShift, yShift, zShift, 1.0);
        translationMatrix.setRow(translationMatrix.size() - 1, r4);
        tNet = multiplyMatrix(tNet, translationMatrix);
    }

    /**
     * Update tNet with a reflection.<br>
     *     Used solely for flipping the shape's initial points due to y increasing downwards on screen
     *
     */
    private void reflectY() {
        Matrix reflectMatrix = new TransformationMatrix();
        Vertex r2 = new Vertex(0.0, -1.0, 0.0, 0.0);
        reflectMatrix.setRow(1, r2);
        tNet = multiplyMatrix(tNet, reflectMatrix);
    }

    /**
     * Update tNet with a scale.
     *
     * @param scale the factor to scale shape in (all axes)
     */
    private void scale(Double scale) {
        this.currentScaleFactor *= scale;
        Matrix scalingMatrix = new TransformationMatrix();
        Vertex r1 = new Vertex(scale, 0.0, 0.0, 0.0);
        Vertex r2 = new Vertex(0.0, scale, 0.0, 0.0);
        Vertex r3 = new Vertex(0.0, 0.0, scale, 0.0);
        scalingMatrix.setRow(0, r1);
        scalingMatrix.setRow(1, r2);
        scalingMatrix.setRow(2, r3);
        tNet = multiplyMatrix(tNet, scalingMatrix);
    }

    /**
     * Update tNet with a rotation about the x axis.
     */
    private void rotateX() {
        Matrix rotationMatrix = new TransformationMatrix();
        Vertex r2 = new Vertex(0.0, Math.cos(ROTATION), Math.sin(ROTATION), 0.0);
        Vertex r3 = new Vertex(0.0, -Math.sin(ROTATION), Math.cos(ROTATION), 0.0);
        rotationMatrix.setRow(1, r2);
        rotationMatrix.setRow(2, r3);
        tNet = multiplyMatrix(tNet, rotationMatrix);
    }

    /**
     * Update tNet with a rotation about the y axis.
     */
    private void rotateY() {
        Matrix rotationMatrix = new TransformationMatrix();
        Vertex r1 = new Vertex(Math.cos(ROTATION), 0.0, -Math.sin(ROTATION), 0.0);
        Vertex r3 = new Vertex(Math.sin(ROTATION), 0.0, Math.cos(ROTATION), 0.0);
        rotationMatrix.setRow(0, r1);
        rotationMatrix.setRow(2, r3);
        tNet = multiplyMatrix(tNet, rotationMatrix);
    }

    /**
     * Update tNet with a rotation about the z axis.
     */
    private void rotateZ() {
        Matrix rotationMatrix = new TransformationMatrix();
        Vertex r1 = new Vertex(Math.cos(ROTATION), Math.sin(ROTATION), 0.0, 0.0);
        Vertex r2 = new Vertex(-Math.sin(ROTATION), Math.cos(ROTATION), 0.0, 0.0);
        rotationMatrix.setRow(0, r1);
        rotationMatrix.setRow(1, r2);
        tNet = multiplyMatrix(tNet, rotationMatrix);
    }

    /**
     * Update tNet with a left shear in y axis.
     */
    private void shearLeft() {
        Matrix shearMatrix = new TransformationMatrix();
        Vertex r2 = new Vertex(0.1, 1.0, 0.0, 0.0);
        shearMatrix.setRow(1, r2);
        tNet = multiplyMatrix(tNet, shearMatrix);
    }

    /**
     * Update tNet with a right shear in y axis.
     */
    private void shearRight() {
        Matrix shearMatrix = new TransformationMatrix();
        Vertex r2 = new Vertex(-0.1, 1.0, 0.0, 0.0);
        shearMatrix.setRow(1, r2);
        tNet = multiplyMatrix(tNet, shearMatrix);
    }

    class cRotationX extends TimerTask {
        @Override
        public void run() {
            double xMiddle = currentPoints.getRow(0).getX();
            double yMiddle = currentPoints.getRow(0).getY();
            double zMiddle = currentPoints.getRow(0).getZ();

            // move middle of shape to 0, 0
            translate(-xMiddle, -yMiddle, -zMiddle);

            // rotate
            rotateX();

            // move shape back
            translate(xMiddle, yMiddle, zMiddle);

            draw(gc);
        }
    }

    class cRotationY extends TimerTask {
        @Override
        public void run() {
            double xMiddle = currentPoints.getRow(0).getX();
            double yMiddle = currentPoints.getRow(0).getY();
            double zMiddle = currentPoints.getRow(0).getZ();

            // move middle of shape to 0, 0
            translate(-xMiddle, -yMiddle, -zMiddle);

            // rotate
            rotateY();

            // move shape back
            translate(xMiddle, yMiddle, zMiddle);

            draw(gc);
        }
    }

    class cRotationZ extends TimerTask {
        @Override
        public void run() {
            double xMiddle = currentPoints.getRow(0).getX();
            double yMiddle = currentPoints.getRow(0).getY();
            double zMiddle = currentPoints.getRow(0).getZ();

            // move middle of shape to 0, 0
            translate(-xMiddle, -yMiddle, -zMiddle);

            // rotate
            rotateZ();

            // move shape back
            translate(xMiddle, yMiddle, zMiddle);

            draw(gc);
        }
    }

    private void clearTimer() {
        timer.cancel();
        timer.purge();
        timer = new Timer();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
