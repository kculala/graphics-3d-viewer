package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
<<<<<<< Updated upstream
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
=======
import javafx.geometry.Point3D;
import javafx.scene.Group;
>>>>>>> Stashed changes
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
<<<<<<< Updated upstream
import javafx.scene.layout.StackPane;
=======
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
>>>>>>> Stashed changes
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

<<<<<<< Updated upstream

    private Desktop desktop = Desktop.getDesktop();
=======
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private List<Point3D> vertices = new ArrayList<>();
    private List<Pair> lines = new ArrayList<>();
    private double maxY = 0;
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
        newDataButton.addEventHandler(ActionEvent.ACTION,
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        configureFileChooser(fileChooser, true);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            openVerticesFile(file);
                        }
=======
        final FileChooser fileChooser = new FileChooser();
        newData.addEventHandler(ActionEvent.ACTION,
                event -> {
                    configureFileChooser(fileChooser, true);
                    File verticesFile = fileChooser.showOpenDialog(primaryStage);
                    if (verticesFile != null) {
                        openVerticesFile(verticesFile);
>>>>>>> Stashed changes
                    }
                }
        );

        newDataButton.addEventHandler(ActionEvent.ACTION,
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        configureFileChooser(fileChooser, false);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            openLinesFile(file);
                        }
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

<<<<<<< Updated upstream
        primaryStage.setScene(new Scene(root, 300, 250));
=======
        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
=======
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

>>>>>>> Stashed changes
    private void openVerticesFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
<<<<<<< Updated upstream
=======

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
>>>>>>> Stashed changes
    }

    private void openLinesFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
<<<<<<< Updated upstream
=======

        this.lines = lines;

        // debugging
        printLines();
>>>>>>> Stashed changes
    }

}
