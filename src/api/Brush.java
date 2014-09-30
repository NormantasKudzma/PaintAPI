package api;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Brush {
	private static final CustomStroke DEFAULT_STROKE = new CustomStroke(new Rectangle2D.Float(0, 0, 4, 4), 1.75f);
	
	private int size;
	private Color color;
	private CustomStroke stroke;
	
	public Brush(){
		this(4, Color.black, DEFAULT_STROKE);
	}
	
	public Brush(int size){
		this(size, Color.black, DEFAULT_STROKE);
	}
	
	public Brush(int size, Color c){
		this(size, c, DEFAULT_STROKE);
	}
	
	public Brush(int size, Color color, CustomStroke cc){
		setSize(size);
		setColor(color);
		setCustomStroke(cc);
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public CustomStroke getCustomStroke() {
		return stroke;
	}

	public void setCustomStroke(CustomStroke cc) {
		this.stroke = cc;
	}
}
