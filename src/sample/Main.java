package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private List<Point3D> vertices = new ArrayList<>();
    private List<Pair> lines = new ArrayList<>();
    private double maxY = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("COMP 4560 Assignment 5 - A00797801");
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Pane wrapper = new Pane();
        Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
        wrapper.getChildren().add(canvas);

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem newData = new MenuItem("New data...");

        newData.addEventHandler(ActionEvent.ACTION,
                event -> {
                    FileChooser fileChooser = new FileChooser();
                    configureFileChooser(fileChooser, true);
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        openVerticesFile(file);
                    }

                    configureFileChooser(fileChooser, false);
                    File linesFile = fileChooser.showOpenDialog(primaryStage);
                    if (linesFile != null) {
                        openLinesFile(linesFile);
                    }

                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    draw(gc);
                }
        );
        menu.getItems().add(newData);
        menuBar.getMenus().add(menu);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(wrapper);

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
        double xCoordinate = Double.parseDouble(tokens[0]) * 10;
        double yCoordinate = Double.parseDouble(tokens[1]) * 10;
        if (yCoordinate > maxY)
            maxY = yCoordinate;
        double zCoordinate = Double.parseDouble(tokens[2]) * 10;
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
