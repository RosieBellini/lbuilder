package team1;

import java.util.HashSet;
import java.util.Set;

public class SaveStateTest {
    public static void main(String args[]) {
        Coordinate wPos1 = new Coordinate(1, 1);
        Set<Coordinate> box1 = new HashSet<Coordinate>();
        Set<Coordinate> wall1 = new HashSet<Coordinate>();
        Set<Coordinate> goal1= new HashSet<Coordinate>();
        box1.add(new Coordinate(0,1));
        wall1.add(new Coordinate(0,2));
        goal1.add(new Coordinate(0,3));
        SaveState state1 = new SaveState(wPos1, box1, wall1, goal1);

        Coordinate wPos2 = new Coordinate(1, 1);
        Set<Coordinate> box2 = new HashSet<Coordinate>();
        Set<Coordinate> wall2 = new HashSet<Coordinate>();
        Set<Coordinate> goal2= new HashSet<Coordinate>();
        box2.add(new Coordinate(0,1));
        wall2.add(new Coordinate(0,2));
        goal2.add(new Coordinate(0,3));
        SaveState state2 = new SaveState(wPos2, box2, wall2, goal2);

        Coordinate wPos3 = new Coordinate(1, 1);
        Set<Coordinate> box3 = new HashSet<Coordinate>();
        Set<Coordinate> wall3 = new HashSet<Coordinate>();
        Set<Coordinate> goal3= new HashSet<Coordinate>();
        box3.add(new Coordinate(0,1));
        wall3.add(new Coordinate(0,1));
        goal3.add(new Coordinate(0,3));
        SaveState state3 = new SaveState(wPos3, box3, wall3, goal3);

        System.out.println(state1.equals(state2));
        System.out.println(state1.equals(state3));
    }
}
