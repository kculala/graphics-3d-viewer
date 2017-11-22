package sample;

import java.util.Arrays;

public class TransformationMatrix extends Matrix {

    public TransformationMatrix() {
        super();
        Vertex r1 = new Vertex(1.0,0.0,0.0, 0.0);
        Vertex r2 = new Vertex(0.0,1.0,0.0, 0.0);
        Vertex r3 = new Vertex(0.0,0.0,1.0, 0.0);
        Vertex r4 = new Vertex(0.0, 0.0,0.0, 1.0);
        rows.addAll(Arrays.asList(r1, r2, r3, r4));
    }

}
