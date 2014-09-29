package api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

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
//		int hs = b.getSize() / 2;
//		x1 -= hs;
//		y1 -= hs;
//		x2 -= hs;
//		y2 -= hs;
//		drawPixel(x1, y1, b);
//		drawPixel(x2, y2, b);
		g.setStroke(new BasicStroke(b.getSize()));
		g.drawLine(x1, y1, x2, y2);
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