package core;

/**
 * Custom rectangle shape class
 */
public class RectShape extends AbstractShape {
	public static final float [] ctrl = {0, 0, 1, 0, 1, 1, 0, 1};
	
	public RectShape(int x, int y, int w, int h){
		this(x, y, w, h, 0);
	}
	
	public RectShape(int x, int y, int w, int h, double rotation){
		super(x, y, w, h, ctrl, rotation);
	}	
}
