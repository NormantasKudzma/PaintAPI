package core;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.NoSuchElementException;

public class StarShape extends AbstractShape{
	public static float [] ctrl = {0.175f, 0.975f,   0.3f,   0.6f,
								   0f,     0.4f,     0.375f, 0.425f,
								   0.5f,   0f,       0.625f, 0.425f,
								   1.00f,  0.4f,     0.7f,   0.6f,
								   0.825f, 0.975f,   0.5f,   0.75f
								  };
	
	public StarShape(int x, int y, int w, int h){
		this(x, y, w, h, 0);
	}
	
	public StarShape(int x, int y, int w, int h, double rotation){
		super(x, y, w, h, ctrl, rotation);
	}
}
