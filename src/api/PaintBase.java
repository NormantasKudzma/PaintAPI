package api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class PaintBase {
	public static final int DEFAULT_SIZE = 4;
	public static final Color DEFAULT_COLOR = Color.black;
	public static final CustomStroke DEFAULT_STROKE = new CustomStroke(new Rectangle2D.Float(0, 0, DEFAULT_SIZE, DEFAULT_SIZE), DEFAULT_SIZE / 2);
	
	protected Graphics2D g;
	protected Brush brush;
	
	public PaintBase(Graphics2D graphics){
		this.g = graphics;
		brush = new Brush();
		setBrushColor(DEFAULT_COLOR);
		setBrushSize(DEFAULT_SIZE);
		setCustomStroke(DEFAULT_STROKE);
	}

	public Brush getBrush(){
		return brush;
	}
	
	public void setBrush(Brush brush){
		this.brush = brush;
	}
	
	public void drawLine(int x1, int y1, int x2, int y2){
		g.drawLine(x1, y1, x2, y2);
	}
	
	public void drawCenteredLine(int x1, int y1, int x2, int y2){
		int hs = brush.getSize() / 2;
		x1 -= hs; y1 -= hs;
		x2 -= hs; y2 -= hs;
		drawLine(x1, y1, x2, y2);
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
	
	public void setBrushColor(Color c){
		brush.setColor(c);
		g.setColor(c);
	}
	
	public void setBrushSize(int size){
		brush.setSize(size);
	}
	
	public void setCustomStroke(CustomStroke cc){
		g.setStroke(cc);
		brush.setCustomStroke(cc);
	}
	
	public Color getColor(){
		return brush.getColor();
	}
	
	public int getBrushSize(){
		return brush.getSize();
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