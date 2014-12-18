package core;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Custom brush, used in paint object class
 */
public class Brush {
	private int size;
	private double rotation;
	private Color color;
	private CustomStroke stroke;
	
	// Only for internal use in PaintBase class
	public Brush(){}
	
	/**
	 * Creates a brush with given parameters.
	 * @param size
	 * @param rotation
	 * @param color
	 * @param cc - shape
	 */
	public Brush(int size, double rotation, Color color, CustomStroke cc){
		setSize(size);
		setRotation(rotation);
		setColor(color);	
		setCustomStroke(cc);
	}
	
	public void setRotation(double r){
		rotation = r;
	}
	
	public double getRotation(){
		return rotation;
	}
	
	public void setSize(int size){
		if (size > 0){
			this.size = size;
		}
	}
	
	public int getSize(){
		return size;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (color != null){
			this.color = color;
		}
	}

	public CustomStroke getCustomStroke() {
		return stroke;
	}

	public void setCustomStroke(CustomStroke cc) {
		if (cc != null){
			this.stroke = cc;
		}
	}
}
