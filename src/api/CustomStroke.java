package api;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

public class CustomStroke implements Stroke {
	private AffineTransform transform = new AffineTransform();
	private Shape ss;
	private float spc = 0;
	
	public CustomStroke(Shape shape, float spacing){
		ss = shape;
		spc = spacing;
		
		Rectangle2D bounds = shape.getBounds2D();
		transform.setToTranslation(-bounds.getCenterX(), -bounds.getCenterY());
		shape = transform.createTransformedShape(shape);
	}
	
	public void setSpacing(float s){
		spc = s;
	}
	
	public void setShape(Shape s){
		ss = s;
	}
	
	@Override
	public Shape createStrokedShape(Shape shape) {
		GeneralPath result = new GeneralPath();
		PathIterator it = new FlatteningPathIterator(shape.getPathIterator( null ), 1);
		float [] points = new float[6];
		int type = 0;
		float moveX, moveY, lastX, lastY, thisX, thisY;
		moveX = moveY = lastX = lastY = thisX = thisY = 0;
		float next = 0;
		
		while (!it.isDone()){
			type = it.currentSegment(points);
			switch (type){
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				result.moveTo(moveX, moveY);
				next = 0;
				break;
			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;				
			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];
				float dx = thisX - lastX, 
					  dy = thisY - lastY;
				float dist = (float)Math.sqrt(dx * dx + dy * dy);
				if (dist >= next){
					float r = 1.0f / dist;
					float angle = (float)Math.atan2(dy, dx);
					while (dist >= next){
						float x = lastX + next * dx * r;
						float y = lastY + next * dy * r;
						transform.setToTranslation(x, y);
						transform.rotate(angle);
						result.append(transform.createTransformedShape(ss), false);
						next += spc;
					}
				}
				next -= dist;
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}	
		return result;
	}	
	
	public Shape getShape(){
		return ss;
	}
}
