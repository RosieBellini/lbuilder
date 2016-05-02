package team1;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Small Test class for SokobanObject class. This is to test its two methods, charToSokobanObject() and getTopLayer().
 */
public class SokobanObjectTest {

	@Before
	public void setUp() throws Exception 
	{
		
	}

	/**
	 * This should return an illegal argument exception as an incorrect character has been entered:
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCharToSokobanObjectArgument() {
		SokobanObject.charToSokobanObject('h');
	}
	
	
	/**
	 * Check for a normal case using the excepted character @, for player:
	 */
	@Test
	public void testCharToSokobanObject(){
		assertEquals(SokobanObject.PLAYER, SokobanObject.charToSokobanObject('@'));
	}

	/**
	 * Check to ensure the correct object type is returned for layering:
	 */
	@Test
	public void testGetTopLayer() {
		assertEquals("Check that player is returned:", SokobanObject.PLAYER, SokobanObject.getTopLayer(SokobanObject.PLAYER_ON_GOAL));
		assertEquals("Check that box is returned:", SokobanObject.BOX, SokobanObject.getTopLayer(SokobanObject.BOX_ON_GOAL));
		assertEquals("Check goal, this should just return the goal back", SokobanObject.GOAL, SokobanObject.getTopLayer(SokobanObject.GOAL));
	}

}
