package sample;

public class Vertex {

    private Double xCoordinate;
    private Double yCoordinate;
    private Double zCoordinate;
    private Double homogeneousCoordinate;

    public Vertex() {
        xCoordinate = 0.0;
        yCoordinate = 0.0;
        zCoordinate = 0.0;
        homogeneousCoordinate = 1.0;
    }

    public Vertex(Double x, Double y, Double z) {
        xCoordinate = x;
        yCoordinate = y;
        zCoordinate = z;
        homogeneousCoordinate = 1.0;
    }

    public Vertex(Double x, Double y, Double z, Double h) {
        xCoordinate = x;
        yCoordinate = y;
        zCoordinate = z;
        homogeneousCoordinate = h;
    }

    public Double getX() {
        return xCoordinate;
    }

    public void setX(Double x) {
        xCoordinate = x;
    }

    public Double getY() {
        return yCoordinate;
    }

    public void setY(Double y) {
        yCoordinate = y;
    }

    public Double getZ() {
        return zCoordinate;
    }

    public void setZ(Double z) {
        zCoordinate = z;
    }

    public Double getH() {
        return homogeneousCoordinate;
    }

    public void setH(Double h) {
        homogeneousCoordinate = h;
    }

    @Override
    public String toString() {
        return "x: " + xCoordinate + " | y: " + yCoordinate + " | z: " + zCoordinate + " | h: " + homogeneousCoordinate;
    }
}
