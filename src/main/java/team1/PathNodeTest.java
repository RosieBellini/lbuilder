package team1;
/**
 * Test class for PathNode.
 * @version 02/05/2016
 */
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PathNodeTests {

	// Variables to use for test methods:
	
	PathNode node1;
	PathNode node2;
	PathNode node3;
	PathNode node4;
	
	Coordinate coords1;
	Coordinate coords2;
	Coordinate coords3;
	Coordinate coords4;
	
	
	@Before
	public void setUp() throws Exception 
	{
		coords1 = new Coordinate(2,2);
		coords2 = new Coordinate(1,4);
		coords3 = new Coordinate(4,4);
		coords4 = new Coordinate(1,4);
		
		node1 = new PathNode(coords1, coords2);
		node2 = new PathNode(coords3, coords2);
		node3 = new PathNode(coords4, coords2);
		node4 = new PathNode(coords2, coords2);

	}

	/**
	 * Test for constructor PathNode(Coordinate position, PathNode parent,
                            Coordinate target, int gCost)
	 */
	@Test
	public void testPathNodeCoordinateCoordinate() {
		PathNode nodeTest = new PathNode(coords1, coords2);
	}
	
	/**
	 * Test method to check constructor throws IllegalArgumentException if gCost is negative.
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testPathNodeNegativegCost()
	{
		PathNode nodeTest = new PathNode(coords1, node2, coords3, -100);
	}
	
	/**
	 * Test to ensure null parameters are not allowed in constructor.
	 */
	@Test (expected = NullPointerException.class)
	public void testNullParameters()
	{
		PathNode nodeTest = new PathNode(null, node2, coords3, 10);
	}

	/**
	 * Test method to check for the manhattan distance between two coordinates.
	 */
	@Test
	public void testManhattanDistance() {
		assertEquals("Return the manhattan distance, should return 3", 3, PathNode.manhattanDistance(coords1, coords2));
	}

	/**
	 * Test method for checking if the accessor for hCost is set correctly:
	 */
	@Test
	public void testGetHCost() {
		assertEquals("Check H cost returns and is correct", 3, node1.getHCost());
	}

	/**
	 * Test method to check if accessor for gCost is set correctly for both constructors
	 */
	@Test
	public void testGetGCost() {
		PathNode nodeTest = new PathNode(coords1, node2, coords3, 10);
		assertEquals(10, nodeTest.getGCost());
		PathNode nodeTest2 = new PathNode(coords1, coords2);
		assertEquals(0, nodeTest2.getGCost());

	}

	/**
	 * Method to test getFCost accessor method, which returns the sum of gCost & hCost.
	 */
	@Test
	public void testGetFCost() {
		PathNode nodeTest = new PathNode(coords1, node2, coords3, 10);
		int fCost = PathNode.manhattanDistance(coords1, coords3) + 10;
		assertEquals("gCost + hCost = fCost", fCost, nodeTest.getFCost());
	}

	/**
	 * Check accessor method for position:
	 */
	@Test
	public void testGetPosition() {
		assertEquals("Check position accessor method works:", coords1, node1.getPosition());
	}

	/**
	 * Accessor method test for parent node of a PathNode
	 */
	@Test
	public void testGetParent() {
		PathNode nodeTest = new PathNode(coords1, node2, coords3, 2);
		assertEquals("Check parent node is set correctly", node2, nodeTest.getParent());
	}
	
	/**
	 * Test method for overriden equals method:
	 */
	@Test
	public void testEquals()
	{
		PathNode nodeTest = new PathNode(coords1, node2, coords3, 2);
		PathNode nodeTest2 = new PathNode(coords1, node2, coords3, 2);
		assertEquals(nodeTest, nodeTest2);
		
	}

}
