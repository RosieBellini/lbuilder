package team1;

import java.util.ArrayList;
import java.util.Collections;

public class PathNodeTest {
    public static void main(String[] args) {
        ArrayList<PathNode> list = new ArrayList<PathNode>();
        Coordinate target = new Coordinate(5, 5);
        Coordinate pos1 = new Coordinate(2, 2);
        Coordinate pos2 = new Coordinate(3, 3);
        Coordinate pos3 = new Coordinate(4, 4);

        list.add(new PathNode(pos3, pos2, target));
        list.add(new PathNode(pos1, pos1, target));
        list.add(new PathNode(pos2, pos1, target));

        for (PathNode node : list) {
            System.out.println(node.getPosition());
        }

        Collections.sort(list);
        System.out.println();

        for (PathNode node : list) {
            System.out.println(node.getPosition());
        }
    }
}
