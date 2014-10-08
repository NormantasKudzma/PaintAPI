package core;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class TriangleShape extends AbstractShape {
	public static float [] ctrl = {0f, 1f, 0.5f, 0f, 1f, 1f};
	
	public TriangleShape(int x, int y, int w, int h){
		this(x, y, w, h, 0);
	}
	
	public TriangleShape(int x, int y, int w, int h, double rotation){
		super(x, y, w, h, ctrl, rotation);
	}
}
