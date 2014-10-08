package core;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class AbstractShape implements Shape{
	public int x;
	public int y;
	public int width;
	public int height;
	public int sz;
	public double rotation;
	
	public float [] c;
	
	public AbstractShape(){}
	
	public AbstractShape(int x, int y, int w, int h, float [] ctrl, double rotation){
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.c = ctrl;
		this.rotation = rotation;
	}
	
	float sign(float x1, float y1, float x2, float y2, float x3, float y3){
	  return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
	}

	public boolean isPointInTriangle(float px, float py, float x1, float y1, float x2, float y2, float x3, float y3){
	  boolean b1, b2, b3;

	  b1 = sign(px, py, x1, y1, x2, y2) < 0.0f;
	  b2 = sign(px, py, x2, y2, x3, y3) < 0.0f;
	  b3 = sign(px, py, x3, y3, x1, y1) < 0.0f;

	  return ((b1 == b2) && (b2 == b3));
	}
	
	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Rectangle getBounds() {
		Rectangle bounds = new Rectangle(x, y, width, height);
		return bounds;
	}

	@Override
	public Rectangle2D getBounds2D() {
		Rectangle bounds = new Rectangle(x, y, width, height);
		return bounds;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new AbstractShapeIterator(this, at, c.length / 2, c, rotation);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new AbstractShapeIterator(this, at, c.length / 2, c, rotation);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return false;
	}
}
