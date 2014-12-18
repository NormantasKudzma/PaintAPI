package tests;

import java.awt.Dimension;
import java.awt.Point;

import junit.framework.TestCase;

import org.junit.Test;

import design.PcDesign;

public class PcDesignTests extends TestCase{	
	
	@Test
	public void testNewImageShouldNotCreateNegativeSizeTest(){
		Point negative = new Point(-1, -1);
		Point zero = new Point(0, 0);
		Point normal = new Point(320, 240);
		Point big = new Point(2400, 2400);
		
		PcDesign pc = new PcDesign();
		
		pc.createNewImage(normal.x, normal.y);		
		assertTrue("It should be able to create a normal sized image.", pc.getImageSize().equals(normal));
		
		pc.createNewImage(zero.x, zero.y);
		assertFalse("0x0 size images should not be created.", pc.getImageSize().equals(zero));
		
		pc.createNewImage(negative.x, negative.y);
		assertFalse("Negative size images should not be created.", pc.getImageSize().equals(negative));
		
		pc.createNewImage(big.x, big.y);
		assertFalse("Too large images should not be created.", pc.getImageSize().equals(big));
	}

	@Test
	public void testFrameSizeShouldNotBeInvalid(){
		Dimension negative = new Dimension(-1, -1);
		Dimension zero = new Dimension(0, 0);
		Dimension normal = new Dimension(1280, 720);
		Dimension big = new Dimension(2400, 2400);
		
		PcDesign pc = new PcDesign(negative.width, negative.height);
		assertFalse("Negative size window should not be created.", pc.getSize().equals(negative));
		
		pc = new PcDesign(zero.width, zero.height);
		assertFalse("Zero size window should not be created.", pc.getSize().equals(zero));
		
		pc = new PcDesign(big.width, big.height);
		assertFalse("Too big sized window should not be created.", pc.getSize().equals(big));
		
		pc = new PcDesign(normal.width, normal.height);
		assertTrue("Normal size window should be created.", pc.getSize().equals(normal));
	}
}
