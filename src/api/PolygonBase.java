package api;

import java.awt.Polygon;

public class PolygonBase {
	protected int [] xPoints;
	protected int [] yPoints;	
	protected int [] xSizePoints;
	protected int [] ySizePoints;
	
	// Recalculate shape size depending on size
	// Point array sizes *SHOULD* be the same
	protected Polygon getPolygon(int x, int y, int size){
		for (int i = 0; i < xPoints.length; i++){
			xSizePoints[i] = x + xPoints[i] * size;
			ySizePoints[i] = y + yPoints[i] * size;			
		}
		return new Polygon(xSizePoints, ySizePoints, xPoints.length);
	}

}
