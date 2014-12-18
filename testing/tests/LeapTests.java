package tests;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import design.LeapDesign;

public class LeapTests {
	@Test
	public void testColorIndexNotNull(){
		LeapDesign ld = new LeapDesign();
		
		Color valid = Color.black;
		Color nonexistant = Color.black;
		nonexistant = nonexistant.brighter();
		Color invalid = null;
		
		assertTrue("null shouldn't be a valid color.", ld.getColorNum(invalid) == -1);
		
		assertTrue("Nonexistent colors should return -1.", ld.getColorNum(nonexistant) == -1);
		
		assertTrue("Existent colors should return correct index value.", ld.getColorNum(valid) != -1);
	}
}
