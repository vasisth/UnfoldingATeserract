package CubeObjects;

import java.util.ArrayList;
import java.util.List;

public class DualNode extends Vertex{
    List<Face> connectedFaces;


    public DualNode(String TAG, List<Double> coordinates) {
       // private String TAG;
        super(TAG, coordinates);
        this.connectedFaces = new ArrayList<Face>();
        //List<DualNode> dualnodeslist;

        setConnectedDualNodes(new ArrayList<DualNode>());
    }

    public DualNode(String TAG) {
        super(TAG);
        this.connectedFaces = new ArrayList<Face>();
        setConnectedDualNodes(new ArrayList<DualNode>());
    }

    public List<Face> getConnectedFaces() {
        return connectedFaces;
    }

    public void setConnectedFaces(List<Face> connectedFaces) {
        this.connectedFaces = connectedFaces;
    }
}
