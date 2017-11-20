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

    public Double getxCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(Double x) {
        xCoordinate = x;
    }

    public Double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Double y) {
        yCoordinate = y;
    }

    public Double getzCoordinate() {
        return zCoordinate;
    }

    public void setzCoordinate(Double z) {
        zCoordinate = z;
    }

    public Double getHomogeneousCoordinate() {
        return homogeneousCoordinate;
    }

    public void setHomogeneousCoordinate(Double h) {
        homogeneousCoordinate = h;
    }

    @Override
    public String toString() {
        return "x: " + xCoordinate + " | y: " + yCoordinate + " | z: " + zCoordinate + " | h: " + homogeneousCoordinate;
    }
}
