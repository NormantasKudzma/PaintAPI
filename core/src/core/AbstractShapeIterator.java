package core;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.NoSuchElementException;

public class AbstractShapeIterator implements PathIterator {
	AffineTransform at;
	int index;
	int x;
	int y;
	int w;
	int h;
	int sz;
	float [] c;
	double rotation;
	
	public AbstractShapeIterator(AbstractShape ss, AffineTransform at, int size, float [] ctrl, double rotation){
		this.at = at;
		this.x = ss.x;
		this.y = ss.y;
		this.w = ss.width;
		this.h = ss.height;
		this.sz = size;
		this.rotation = rotation;
		c = ctrl;
		if (ss.width < 0 || ss.height < 0){
			index = sz + 1;
		}
	}
	
	@Override
	public int currentSegment(float[] points) {
		if (isDone()){
			throw new NoSuchElementException("AbstractShapeIterator is out of bounds");
		}
		if (index == 0){				
			points[0] = (float)(x + c[0] * w);
			points[1] = (float)(y + c[1] * h);

			at.rotate(Math.toRadians(-rotation), 0.5f, 0.5f);
			at.transform(points, 0, points, 0, 1);
			//System.out.println(points[0] + "\t" + points[1]);
			return SEG_MOVETO;
		}
		points[0] = (float)(x + c[2 * index] * w);
		points[1] = (float)(y + c[1 + 2 * index] * h);
		
		at.transform(points, 0, points, 0, 1);
		if (index == sz - 1){
			at.rotate(Math.toRadians(rotation));
		}
		//System.out.println(points[0] + "\t" + points[1]);
		return SEG_LINETO;
	}

	@Override
	public int currentSegment(double[] points) {
		if (isDone()){
			throw new NoSuchElementException("AbstractShapeIterator is out of bounds");
		}
		if (index == 0){	
			at.rotate(Math.toRadians(-rotation), x, y);
			points[0] = (float)(x + c[0] * w);
			points[1] = (float)(y + c[1] * h);
			at.transform(points, 0, points, 0, 1);
			return SEG_MOVETO;
		}
		points[0] = (float)(x + c[2 * index] * w);
		points[1] = (float)(y + c[1 + 2 * index] * h);
		
		at.transform(points, 0, points, 0, 1);
		if (index == sz - 1){
			at.rotate(Math.toRadians(rotation), x, y);
		}
		return SEG_LINETO;
	}

	@Override
	public int getWindingRule() {
		return PathIterator.WIND_NON_ZERO;
	}

	@Override
	public boolean isDone() {
		return index >= sz;
	}

	@Override
	public void next() {
		index++;
	}
}
