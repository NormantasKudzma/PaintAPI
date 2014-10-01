package api;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class StarShape implements Shape{
	public class StarShapeIterator implements PathIterator{
		@Override
		public int currentSegment(float[] arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int currentSegment(double[] arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getWindingRule() {
			return PathIterator.WIND_NON_ZERO;
		}

		@Override
		public boolean isDone() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void next() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	public StarShape(){}
	
	public StarShape(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	@Override
	public boolean contains(Point2D p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return null;
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
