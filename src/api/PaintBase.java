package api;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class PaintBase {
	protected Graphics g;
	protected Brush brush;
	
	public PaintBase(Graphics graphics){
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
		// STUB, algoritma sugalvot reikia, kvieciant drawpixel kiekvienai pozicijai
	}
	
	public void drawPixel(int x, int y){
		drawPixel(x, y, brush);
	}
	
	public void drawPixel(int x, int y, Brush b){
		Polygon p = b.getShape().getPolygon(x, y, b.getSize());
		g.fillPolygon(p);
	}
	
	public void setColor(Color c){
		brush.setColor(c);
		g.setColor(c);
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