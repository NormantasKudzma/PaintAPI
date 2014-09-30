package api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class PaintBase {
	protected Graphics2D g;
	protected Brush brush;
	
	public PaintBase(Graphics2D graphics){
		this.g = graphics;
		brush = new Brush();
		setColor(Color.black);
	}

	public Brush getBrush(){
		return brush;
	}
	
	public void setBrush(Brush brush){
		this.brush = brush;
	}

	public void drawLine(int x1, int y1, int x2, int y2){
		drawLine(x1, y1, x2, y2, brush);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2, Brush b){
		g.setStroke(b.getCustomStroke());
		g.drawLine(x1, y1, x2, y2);
	}
	
	public void drawPixel(int x, int y){
		drawPixel(x, y, brush);
	}
	
	public void drawPixel(int x, int y, Brush b){
		Shape s = b.getCustomStroke().getShape();
		g.translate(x, y);
		g.fill(s);
		g.translate(-x, -y);		
	}
	
	public void setColor(Color c){
		brush.setColor(c);
		g.setColor(c);
	}
	
	public void setSize(int size){
		brush.setSize(size);
	}
	
	public Color getColor(){
		return brush.getColor();
	}
	
	public void drawCenteredPixel(int x, int y){
		int hs = brush.getSize() / 2;
		if (x - hs >= 0){
			x -= hs;
		}
		if (y - hs >= 0){
			y -= hs;
		}
		drawPixel(x, y);
	}
}