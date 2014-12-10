package core;

/**
 * Custom star shaped class
 */
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
