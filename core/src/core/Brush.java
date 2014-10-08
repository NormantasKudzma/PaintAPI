package core;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Brush {
	private int size;
	private double rotation;
	private Color color;
	private CustomStroke stroke;
	
	// Only for internal use in PaintBase class
	public Brush(){}
	
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
