package api;

public class RectPolygon extends PolygonBase {	
	public RectPolygon(){
		xPoints = new int[]{0, 0, 1, 1};
		yPoints = new int[]{0, 1, 1, 0};
		xSizePoints = new int[xPoints.length];
		ySizePoints = new int[yPoints.length];
	}
}
