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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Main extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 550;
    private static final int GRID_SPACING = 10;

    private List<Point3D> vertices = new ArrayList<>();
    private List<Pair> lines = new ArrayList<>();
    private double maxY = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("COMP 4560 Assignment 5 - A00797801");
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Usage");
        alert.setHeaderText(null);
        alert.setContentText("Please select two (2) files:\n" +
                "The first file should contain a list of 3 numbers representing a vertex\n" +
                "e.g. 10 10 0\n" +
                "The second file should contain a list of 2 numbers representing a line\n" +
                "e.g. 1 2");
        alert.showAndWait();

        // toolbar
        Button translateLeft = new Button("Left");
        Button translateRight = new Button("Right");
        Button translateUp = new Button("Up");
        Button translateDown = new Button("Down");
        Button scaleUp = new Button("Scale Up");
        Button scaleDown = new Button("Scale Down");
        Button rotateX = new Button("Rotate about X");
        Button rotateY = new Button("Rotate about Y");
        Button rotateZ = new Button("Rotate Down");
        Button shearLeft = new Button("Shear Left");
        Button shearRight = new Button("Shear Right");
        Button restore = new Button("Restore");
        Button exit = new Button("Exit");
        Button spinX = new Button("Spin X");
        Button spinY = new Button("Spin Y");
        Button spinZ = new Button("Spin Z");

        ToolBar toolBar = new ToolBar();
        toolBar.setOrientation(Orientation.VERTICAL);
        toolBar.getItems().addAll(translateLeft, translateRight, translateUp, translateDown, new Separator(),
                                  scaleUp, scaleDown, new Separator(),
                                  rotateX, rotateY, rotateZ, new Separator(),
                                  shearLeft, shearRight, new Separator(),
                                  restore, exit, new Separator(),
                                  spinX, spinY, spinZ);

        // canvas
        Pane wrapper = new Pane();
        Canvas canvas = new Canvas();
        wrapper.getChildren().add(canvas);
        canvas.widthProperty().bind(wrapper.widthProperty());
        canvas.heightProperty().bind(wrapper.heightProperty());

        // menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem newData = new MenuItem("New data...");
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
        gc.setLineWidth(3);
        for (Pair tmp : this.lines) {

            // debugging
            System.out.print(tmp.toString() + " | ");
            System.out.print(vertices.get((int)tmp.getKey() - 1).getX() + " " +
                    vertices.get((int)tmp.getKey() - 1).getY() + " " +
                    vertices.get((int)tmp.getValue() - 1).getX() + " " +
                    vertices.get((int)tmp.getValue() - 1).getY());
            System.out.println();

            gc.strokeLine(vertices.get((int)tmp.getKey()).getX(),
                    (vertices.get((int)tmp.getKey()).getY() * -1) + maxY,
                          vertices.get((int)tmp.getValue()).getX(),
                    (vertices.get((int)tmp.getValue()).getY() * -1) + maxY);
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
        if (yCoordinate > maxY)
            maxY = yCoordinate;
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
