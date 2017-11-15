package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
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

    private List<Point3D> vertices = new ArrayList<>();
    private List<Pair> lines = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("COMP 4560 Assignment 5 - A00797801");

        final FileChooser fileChooser = new FileChooser();

        Button newDataButton = new Button("New data....");
        newDataButton.setStyle("fx-alignment: TOP-CENTER");

        newDataButton.addEventHandler(ActionEvent.ACTION,
                event -> {
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
                }
        );

        BorderPane root = new BorderPane();
        ToolBar toolbar = new ToolBar(newDataButton);
        root.setTop(toolbar);

        Scene scene = new Scene(root, 300, 250);
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();
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

        double xCoordinate = Double.parseDouble(tokens[0]);
        double yCoordinate = Double.parseDouble(tokens[1]);
        double zCoordinate = Double.parseDouble(tokens[2]);
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
        printLines();
    }

}
