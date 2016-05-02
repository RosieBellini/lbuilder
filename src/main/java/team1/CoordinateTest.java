package team1;

import static org.junit.Assert.*;

import java.util.ArrayList;

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
		assertEquals("Check that value are 2 times for x",  10, newCoords.x);
		assertEquals("Check that value are 2 times for y",  20, newCoords.y);
	}
	
	/**
	 * Tests that the coordinate values are reversed by a multiple of -1
	 */
	@Test
	public void testReverse() {
		Coordinate newCoords = test.reverse();
		assertEquals("Check the value are now reversed for x", -5, newCoords.x);
		assertEquals("Check the value are now reversed for y", -10, newCoords.y);

	}
	
	/**
	 * Test method for inRange method. This method should return true if the inputted coordinates are within its bounds,
	 * otherwise it will return false:
	 */
	@Test
	public void testInRange() {
		assertEquals("Check that the coordinates are within the bounds, should return true:", true, test.inRange(0, 0, 6, 11));
		assertEquals("Check that the coordinates are within the bounds, this should return false as the upper bound for x is too low:", false, test.inRange(0, 0, 4, 11));
		assertEquals("Check that the coordinates are within the bounds, this should return false as the upper bound for y is too low:", false, test.inRange(0, 0, 6, 9));

	}
	
	
	/**
	 * Tests that the Array List returned is of the correct size for the static allValidCoordinates method.
	 */
	@Test
	public void testArraySize() {
		ArrayList<Coordinate> testValidCoords = Coordinate.allValidCoordinates(10, 20);
		// The ArrayList should be of a size 200, as 10x20=200.
		assertEquals("Check that the arraylist size is correct", 200, testValidCoords.size());

	}
	
	


}
