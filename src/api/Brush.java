package api;

import java.awt.Color;

public class Brush {
	private int size;
	private Color color;
	private PolygonBase shape;
	
	public Brush(){
		this(4, Color.black, new RectPolygon());
	}
	
	public Brush(int size){
		this(size, Color.black, new RectPolygon());
	}
	
	public Brush(int size, Color c){
		this(size, c, new RectPolygon());
	}
	
	public Brush(int size, Color color, PolygonBase shape){
		this.size = size;
		this.setColor(color);
		this.setShape(shape);
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

	public PolygonBase getShape() {
		return shape;
	}

	public void setShape(PolygonBase shape) {
		this.shape = shape;
	}
}
