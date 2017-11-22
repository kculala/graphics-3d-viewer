package sample;

import java.util.ArrayList;
import java.util.List;

public class Matrix {

    List<Vertex> rows = new ArrayList<>();

    public Matrix() {
    }

    public Vertex getRow(int index) {
        return rows.get(index);
    }

    public void setRow(int index, Vertex vertex) {
        rows.set(index, vertex);
    }

    public void addRow(Vertex vertex) {
        rows.add(vertex);
    }

    public int size() {
        return rows.size();
    }

    public static Matrix multiplyMatrix(Matrix matrixA, Matrix matrixB) {
        Matrix matrixC = new Matrix();
        for (int i = 0; i < matrixA.rows.size(); i++) {
            Vertex vertex = new Vertex();
            Double x = (matrixA.getRow(i).getX() * matrixB.getRow(0).getX()) +
                       (matrixA.getRow(i).getY() * matrixB.getRow(1).getX()) +
                       (matrixA.getRow(i).getZ() * matrixB.getRow(2).getX()) +
                       (matrixA.getRow(i).getH() * matrixB.getRow(3).getX());
            Double y = (matrixA.getRow(i).getX() * matrixB.getRow(0).getY()) +
                       (matrixA.getRow(i).getY() * matrixB.getRow(1).getY()) +
                       (matrixA.getRow(i).getZ() * matrixB.getRow(2).getY()) +
                       (matrixA.getRow(i).getH() * matrixB.getRow(3).getY());
            Double z = (matrixA.getRow(i).getX() * matrixB.getRow(0).getZ()) +
                       (matrixA.getRow(i).getY() * matrixB.getRow(1).getZ()) +
                       (matrixA.getRow(i).getZ() * matrixB.getRow(2).getZ()) +
                       (matrixA.getRow(i).getH() * matrixB.getRow(3).getZ());
            Double h = (matrixA.getRow(i).getX() * matrixB.getRow(0).getH()) +
                       (matrixA.getRow(i).getY() * matrixB.getRow(1).getH()) +
                       (matrixA.getRow(i).getZ() * matrixB.getRow(2).getH()) +
                       (matrixA.getRow(i).getH() * matrixB.getRow(3).getH());
            vertex.setX(x);
            vertex.setY(y);
            vertex.setZ(z);
            vertex.setH(h);
            matrixC.addRow(vertex);
        }
        return matrixC;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        for (Vertex row : rows) {
            string.append(row).append("\n");
        }
        return string.toString();
    }
}
