package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
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

public class Main extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int GRID_SPACING = 10;
    private static final int SHAPE_DIMENSION = 20;
    private static final int SHAPE_DIMENSION_SCALED = SHAPE_DIMENSION * GRID_SPACING ;

    // offset to center of pane
    private static final int SHAPE_WIDTH_OFFSET = (WIDTH/2) - (SHAPE_DIMENSION_SCALED/2);
    private static final int SHAPE_HEIGHT_OFFSET = (HEIGHT/2) - (SHAPE_DIMENSION_SCALED/2) + SHAPE_DIMENSION_SCALED;

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

    private List<Point3D> vertices = new ArrayList<>();
    private List<Pair> lines = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle(SCENE_TITLE);
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(USAGE_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(INSTRUCTIONS);
//        alert.showAndWait();

        // toolbar
        Image imageLeft = new Image(new FileInputStream(ImagePaths.TRANSLATE_LEFT.toString()));
        Button buttonTranslateLeft = new Button();
        buttonTranslateLeft.setGraphic(new ImageView(imageLeft));
        Tooltip tooltipLeft = new Tooltip(Tooltips.TRANSLATE_LEFT.toString());
        buttonTranslateLeft.setTooltip(tooltipLeft);

        Image imageRight = new Image(new FileInputStream(ImagePaths.TRANSLATE_RIGHT.toString()));
        Button buttonTranslateRight = new Button();
        buttonTranslateRight.setGraphic(new ImageView(imageRight));
        Tooltip tooltipRight = new Tooltip(Tooltips.TRANSLATE_RIGHT.toString());
        buttonTranslateRight.setTooltip(tooltipRight);

        Image imageUp = new Image(new FileInputStream(ImagePaths.TRANSLATE_UP.toString()));
        Button buttonTranslateUp = new Button();
        buttonTranslateUp.setGraphic(new ImageView(imageUp));
        Tooltip tooltipUp = new Tooltip(Tooltips.TRANSLATE_UP.toString());
        buttonTranslateUp.setTooltip(tooltipUp);

        Image imageDown = new Image(new FileInputStream(ImagePaths.TRANSLATE_DOWN.toString()));
        Button buttonTranslateDown = new Button();
        buttonTranslateDown.setGraphic(new ImageView(imageDown));
        Tooltip tooltipDown = new Tooltip(Tooltips.TRANSLATE_DOWN.toString());
        buttonTranslateDown.setTooltip(tooltipDown);

        Image imageScaleUp = new Image(new FileInputStream(ImagePaths.SCALE_UP.toString()));
        Button buttonScaleUp = new Button();
        buttonScaleUp.setGraphic(new ImageView(imageScaleUp));
        Tooltip tooltipScaleUp = new Tooltip(Tooltips.SCALE_UP.toString());
        buttonScaleUp.setTooltip(tooltipScaleUp);

        Image imageScaleDown = new Image(new FileInputStream(ImagePaths.SCALE_DOWN.toString()));
        Button buttonScaleDown = new Button();
        buttonScaleDown.setGraphic(new ImageView(imageScaleDown));
        Tooltip tooltipScaleDown = new Tooltip(Tooltips.SCALE_DOWN.toString());
        buttonScaleDown.setTooltip(tooltipScaleDown);

        Image imageRotateX = new Image(new FileInputStream(ImagePaths.ROTATE_X.toString()));
        Button buttonRotateX = new Button();
        buttonRotateX.setGraphic(new ImageView(imageRotateX));
        Tooltip tooltipRotateX = new Tooltip(Tooltips.ROTATE_X.toString());
        buttonRotateX.setTooltip(tooltipRotateX);

        Image imageRotateY = new Image(new FileInputStream(ImagePaths.ROTATE_Y.toString()));
        Button buttonRotateY = new Button();
        buttonRotateY.setGraphic(new ImageView(imageRotateY));
        Tooltip tooltipRotateY = new Tooltip(Tooltips.ROTATE_Y.toString());
        buttonRotateY.setTooltip(tooltipRotateY);

        Image imageRotateZ = new Image(new FileInputStream(ImagePaths.ROTATE_Z.toString()));
        Button buttonRotateZ = new Button();
        buttonRotateZ.setGraphic(new ImageView(imageRotateZ));
        Tooltip tooltipRotateZ = new Tooltip(Tooltips.ROTATE_Z.toString());
        buttonRotateZ.setTooltip(tooltipRotateZ);

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

        // canvas
        Pane wrapper = new Pane();
        Canvas canvas = new Canvas();
        wrapper.getChildren().add(canvas);
        canvas.widthProperty().bind(wrapper.widthProperty());
        canvas.heightProperty().bind(wrapper.heightProperty());

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
                        GraphicsContext gc = canvas.getGraphicsContext2D();
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

    private void draw(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        for (Pair tmp : this.lines) {

            // debugging
            System.out.print(tmp.toString() + " | ");
            System.out.print(vertices.get((int)tmp.getKey() - 1).getX() + SHAPE_WIDTH_OFFSET + " " +
                    vertices.get((int)tmp.getKey() - 1).getY() + SHAPE_HEIGHT_OFFSET + " " +
                    vertices.get((int)tmp.getValue() - 1).getX() + SHAPE_WIDTH_OFFSET + " " +
                    vertices.get((int)tmp.getValue() - 1).getY() + SHAPE_HEIGHT_OFFSET);
            System.out.println();

            gc.strokeLine(vertices.get((int)tmp.getKey()).getX() + SHAPE_WIDTH_OFFSET,
                          (vertices.get((int)tmp.getKey()).getY() * -1) + SHAPE_HEIGHT_OFFSET,
                          vertices.get((int)tmp.getValue()).getX() + SHAPE_WIDTH_OFFSET,
                          (vertices.get((int)tmp.getValue()).getY() * -1) + SHAPE_HEIGHT_OFFSET);
        }
    }

    public static void main(String[] args) {
        launch(args);
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

    private Point3D parseVertexRow(String line) {
        String[] tokens = line.split(" ");
        if(tokens.length != 3)
            return null;
        double xCoordinate = Double.parseDouble(tokens[0]) * GRID_SPACING;
        double yCoordinate = Double.parseDouble(tokens[1]) * GRID_SPACING;
        double zCoordinate = Double.parseDouble(tokens[2]) * GRID_SPACING;
        return new Point3D(xCoordinate, yCoordinate, zCoordinate);
    }

    private void printVertices() {
        for (Point3D temp : this.vertices) {
            System.out.println(temp.toString());
        }
    }

    private void openVerticesFile(File file) {
        List<Point3D> vertices = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Point3D tmp = parseVertexRow(line);
                if (tmp == null)
                    break;
                vertices.add(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.vertices = vertices;

        // debugging
        printVertices();
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
        printLines();
    }

}
