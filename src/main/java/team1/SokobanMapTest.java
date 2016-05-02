package team1;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * SokobanMapTest class.
 * Used to test the SokobanMap class.
 */

public class SokobanMapTest {

	SokobanMap testMap;
	
	@Before
	public void setUp() throws Exception 
	{
		testMap =  new SokobanMap(15, 20, 300);
	}

	/**
	 * Testing constructor for making a new map from an old map, SokobanMap(SokobanMap mapToCopy)
	 */
	@Test
	public void testSokobanMapSokobanMap() {
		SokobanMap newMapCopy = new SokobanMap(testMap);
		assertEquals("Check new map is a copy for x value:", testMap.getXSize(), newMapCopy.getXSize());
		assertEquals("Check new map is a copy for y value:", testMap.getYSize(), newMapCopy.getYSize());
		assertEquals("Check new map is a copy for initial state:", testMap.getInitialState(), newMapCopy.getInitialState());
	}

	/**
	 * Test static method for shallow copy. Identical to the constructor copy aside from it been static.
	 */
	@Test
	public void testShallowCopy() 
	{
		SokobanMap newMapCopy = SokobanMap.shallowCopy(testMap, 300);
		assertEquals("Check new map is a copy for x value:", testMap.getXSize(), newMapCopy.getXSize());
		assertEquals("Check new map is a copy for y value:", testMap.getYSize(), newMapCopy.getYSize());
		assertEquals("Check new map is a copy for initial state:", testMap.getInitialState(), newMapCopy.getInitialState());
	}

	/**
	 * Simple accessor method test for getting Y Size of map.
	 */
	@Test
	public void testGetYSize() {
		assertEquals("Should return the Y size of the map", 20, testMap.getYSize());
	}

	/**
	 * Accessor method test for getting X Size of map.
	 */
	@Test
	public void testGetXSize() {
		assertEquals("Should return the X size of the map", 15, testMap.getXSize());
	}

	
	/**
	 * Accessor method test for getting the map's initial state
	 */
	@Test
	public void testGetInitialState() 
	{
		SaveState stateTest = new SaveState();
		assertEquals("Should return the initial state of the map", stateTest, testMap.getInitialState());
	}

	/**
	 * Mutator method test for setting the map's initial state:
	 */
	@Test
	public void testSetInitialState() {
		SaveState stateTest = new SaveState();
	}

	}
	

