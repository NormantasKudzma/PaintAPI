package api;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.NoSuchElementException;

public class StarShape implements Shape{
	public class StarShapeIterator implements PathIterator{
		AffineTransform at;
		int index;
		int x;
		int y;
		int w;
		int h;
		
		public StarShapeIterator(StarShape ss, AffineTransform at){
			this.at = at;
			this.x = ss.x;
			this.y = ss.y;
			this.w = ss.width;
			this.h = ss.height;
			if (ss.width < 0 || ss.height < 0){
				index = 10;
			}
		}
		
		public final float [] ctrl = 
			{0.175f, 0.975f,   0.3f,   0.6f,
			 0f,     0.4f,     0.375f, 0.425f,
			 0.5f,   0f,       0.625f, 0.425f,
			 1.00f,  0.4f,     0.7f,   0.6f,
			 0.825f, 0.975f,   0.5f,   0.75f
			};
		
		@Override
		public int currentSegment(float[] points) {
			if (isDone()){
				throw new NoSuchElementException("StarShapeIterator is out of bounds");
			}
//			if (index == 9){
//				return SEG_CLOSE;
//			}
			if (index == 0){
				points[0] = (float)(x + ctrl[0] * w);
				points[1] = (float)(y + ctrl[1] * h);
				if (at != null){
					at.transform(points, 0, points, 0, 1);
				}
				return SEG_MOVETO;
			}
			points[0] = (float)(x + ctrl[2 * index] * w);
			points[1] = (float)(y + ctrl[1 + 2 * index] * h);
			if (at != null){
				at.transform(points, 0, points, 0, 1);
				//at.translate(points[0], points[1]);
			}
			return SEG_LINETO;
		}

		@Override
		public int currentSegment(double[] points) {
			if (isDone()){
				throw new NoSuchElementException("StarShapeIterator is out of bounds");
			}
			if (index == 9){
				return SEG_CLOSE;
			}
			if (index == 0){
				points[0] = (double)(x + ctrl[0] * w);
				points[1] = (double)(y + ctrl[1] * h);
				if (at != null){
					at.transform(points, 0, points, 0, 1);
				}
				return SEG_MOVETO;
			}
			points[0] = (double)(x + ctrl[2 * index] * w);
			points[1] = (double)(y + ctrl[1 + 2 * index] * h);
			if (at != null){
				//at.translate(points[0], points[1]);
				at.transform(points, 0, points, 0, 1);
			}
			return SEG_LINETO;
		}

		@Override
		public int getWindingRule() {
			return PathIterator.WIND_NON_ZERO;
		}

		@Override
		public boolean isDone() {
			return index > 9;
		}

		@Override
		public void next() {
			index++;
		}
	}
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	public final float [] c = 
		{0.175f, 0.975f,   0.3f,   0.6f,
		 0f,     0.4f,     0.375f, 0.425f,
		 0.5f,   0f,       0.625f, 0.425f,
		 1.00f,  0.4f,     0.7f,   0.6f,
		 0.825f, 0.975f,   0.5f,   0.75f
		};
	
	public StarShape(){}
	
	public StarShape(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	float sign(float x1, float y1, float x2, float y2, float x3, float y3){
	  return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
	}

	public boolean isPointInTriangle(float px, float py, float x1, float y1, float x2, float y2, float x3, float y3)
	{
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
		float fx = (float)x;
		float fy = (float)y;
		return (isPointInTriangle(fx, fy, c[2], c[3], c[8], c[9], c[14], c[15]) ||
				isPointInTriangle(fx, fy, c[2], c[3], c[14], c[15], c[16], c[17]) ||
				isPointInTriangle(fx, fy, c[0], c[1], c[2], c[3], c[18], c[19]) ||
				isPointInTriangle(fx, fy, c[2], c[3], c[4], c[5], c[6], c[7]) ||
				isPointInTriangle(fx, fy, c[10], c[11], c[12], c[13], c[14], c[15])
			   );
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
		return new StarShape.StarShapeIterator(this, at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new StarShape.StarShapeIterator(this, at);
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
