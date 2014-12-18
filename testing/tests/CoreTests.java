package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Test;

import core.Brush;
import core.PaintBase;
import core.Stack;

public class CoreTests {
	@Test
	public void testZeroNegativeBrushSize(){
		PaintBase p = new PaintBase();
		Brush b = p.getBrush();
		
		int negative = -1;
		int zero = 0;
		int normal = 5;
		int expectedLast = b.getSize();
		
		b.setSize(negative);
		assertEquals("Negative brush size should be invalid.", expectedLast, b.getSize());
		
		b.setSize(zero);
		assertEquals("Zero brush size should be invalid.", expectedLast, b.getSize());
		
		b.setSize(normal);
		assertFalse(normal + " should be valid brush size.", b.getSize() != normal);
	}
	
	@Test
	public void testBrushColorNotNull(){
		PaintBase p = new PaintBase();
		Brush b = p.getBrush();
		
		Color normal = Color.blue;
		Color invalid = null;
		
		b.setColor(normal);
		assertTrue(normal + " should be valid brush color.", b.getColor().equals(normal));
		
		b.setColor(invalid);
		assertTrue("null shouldn't be a valid brush color.", b.getColor() != null);
	}
	
	@Test(expected = java.lang.NegativeArraySizeException.class)
	public void testStackSizePositive(){
		Stack<Integer> stack;
		
		int negative = -1;
		int zero = 0;
		int positive = 1;
		
		stack = new Stack(negative);
		assertTrue("Stack size should be > 0.", stack.getStackSize() > 0);
		
		stack = new Stack(zero);
		assertTrue("Stack size should be > 0.", stack.getStackSize() > 0);
		
		stack = new Stack(positive);
		assertTrue("Stack size should be > 0.", stack.getStackSize() > 0);
	}
}
