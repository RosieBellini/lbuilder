package team1;

import java.util.ArrayList;
import java.util.Collections;

public class PathNodeTest {
    public static void main(String[] args) {
        ArrayList<PathNode> list = new ArrayList<PathNode>();

        Coordinate pos1 = new Coordinate(2, 2);
        Coordinate pos2 = new Coordinate(3, 3);
        Coordinate pos3 = new Coordinate(4, 4);
        Coordinate pos4 = new Coordinate(1, 1);
        Coordinate target = new Coordinate(5, 5);

        PathNode node1 = new PathNode(pos1, pos1, target);
        PathNode node2 = new PathNode(pos2, pos1, target);
        PathNode node3 = new PathNode(pos3, pos2, target);
        PathNode node4 = new PathNode(pos4, pos2, target);

        list.add(node2);
        list.add(node4);
        list.add(node1);
        list.add(node3);

        for (PathNode node : list) {
            System.out.println(node.getPosition());
        }

        Collections.sort(list);
        System.out.println();

        for (PathNode node : list) {
            System.out.println(node.getPosition());
        }

        System.out.println(node1.equals(pos1));

        System.out.println(list.contains(new Coordinate(3, 3)));
    }
}
