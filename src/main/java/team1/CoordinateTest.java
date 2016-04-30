package team1;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * CoordinateTest class. Used to test the Coordinate class. 
 */

public class CoordinateTest 
{	
	Coordinate test;
	Coordinate other;

	/**
	* Setup for all test methods.
	* Only two coordinate objects are setup which will be used throughout the class.
	*/
	@Before
	public void setUp() throws Exception 
	{
		test = new Coordinate(5, 10);
		other = new Coordinate(2, 4);
			
	}
	
	/**
	 * Method to test the constructor for basic values.
	 */
	@Test
	public void testConstructor()
	{
		Coordinate testCoordinate = new Coordinate(10, 20);
		assertNotNull(testCoordinate);
	}

	/**
	 * Tests adding two coordinate objects together:
	 */
	@Test
	public void testAdd() {
		Coordinate newCoords = test.add(other);
		assertEquals("Adding coordinate values together: check x value", 7, newCoords.x);
		assertEquals("check y value:", 14, newCoords.y);
	}
	
	/**
	 * Tests subtracting two coordinate objects together.
	 */
	@Test
	public void testSubtract() {
		Coordinate newCoords = test.subtract(other);
		assertEquals("Subtracting coordinate values together: check x value", 3, newCoords.x);
		assertEquals("check y value:", 6, newCoords.y);
	}
	
	/**
	 * Tests multiplying a coordinate object by an int.
	 */
	@Test
	public void testMult() {
		Coordinate newCoords = test.mult(2);
	}
	
	@Test
	public void testReverse() {
		fail("Not yet implemented");
	}

}
