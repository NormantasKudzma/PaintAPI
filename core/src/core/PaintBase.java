package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Class responsible for handling all graphical events
 */
public class PaintBase {
	// Brush related variables
	public static final int DEFAULT_SIZE = 4;
	public static final double DEFAULT_ROTATION = 0;
	public static final Color DEFAULT_COLOR = Color.black;
	public static final CustomStroke DEFAULT_STROKE = new CustomStroke(new Rectangle2D.Float(0, 0, DEFAULT_SIZE, DEFAULT_SIZE), DEFAULT_SIZE / 2);
	
	protected Graphics2D g;
	protected Brush brush;

	public PaintBase(){
		brush = new Brush();
		brush.setColor(DEFAULT_COLOR);
		brush.setSize(DEFAULT_SIZE);
		brush.setRotation(DEFAULT_ROTATION);
		brush.setCustomStroke(DEFAULT_STROKE);
	}
	
	public PaintBase(Graphics2D graphics){
		this();
		setGraphics(graphics);	
	}
	
	/**
	 * Methods to draw line
	 * @param xy1 - from
	 * @param xy2 - to
	 */
	public void drawLine(int x1, int y1, int x2, int y2){
		g.drawLine(x1, y1, x2, y2);	
	}
	
	public void drawVCenteredLine(int x1, int y1, int x2, int y2){
		int hs = brush.getSize() / 2;
		y1 -= hs; y2 -= hs;
		drawLine(x1, y1, x2, y2);
	}
	
	public void drawCenteredLine(int x1, int y1, int x2, int y2){
		int hs = brush.getSize() / 2;
		x1 -= hs; y1 -= hs;
		x2 -= hs; y2 -= hs;
		drawLine(x1, y1, x2, y2);
	}
	
	/**
	 * Methods to draw single point
	 * @param x - x position
	 * @param y - y position
	 */
	public void drawPixel(int x, int y){
		Shape s = brush.getCustomStroke().getShape();
		g.translate(x, y);
		g.fill(s);
		g.translate(-x, -y);		
	}
	
	public void drawCenteredPixel(int x, int y){
		int hs = brush.getSize() / 2;
		x -= hs;
		y -= hs;
		drawPixel(x, y);
	}	
	
	public void setCustomStroke(CustomStroke cc){
		g.setStroke(cc);
		brush.setCustomStroke(cc);
	}
	
	public CustomStroke getCustomStroke(){
		return brush.getCustomStroke();
	}
	
	public void setBrushRotation(double r){
		brush.setRotation(r);
	}
	
	public double getBrushRotation(){
		return brush.getRotation();
	}
	
	public void setBrushColor(Color c){
		brush.setColor(c);
		g.setColor(c);
	}
	
	public Color getBrushColor(){
		return brush.getColor();
	}
	
	public int getBrushSize(){
		return brush.getSize();
	}
	
	public void setBrushSize(int size){
		brush.setSize(size);
	}
	
	public Brush getBrush(){
		return brush;
	}
	
	public void setBrush(Brush brush){
		setBrushColor(brush.getColor());
		setBrushRotation(brush.getRotation());
		setBrushSize(brush.getSize());
		setCustomStroke(brush.getCustomStroke());
	}
	
	public Graphics2D getGraphics(){
		return g;
	}
	
	public void setGraphics(Graphics2D g){
		this.g = g;
		RenderingHints r = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				  RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(r);
		setBrushColor(brush.getColor());
		setBrushSize(brush.getSize());
		setCustomStroke(brush.getCustomStroke());
	}

	/**
	 * Methods to fill a closed shape
	 * @param x - x position
	 * @param y - y position
	 * @param oldClr - current pixel color
	 * @param newClr - new pixel color
	 * @param img - current image
	 * @param w - images width
	 */
	public void fill(int x, int y, int oldClr, int newClr, int [] img, int w){
		fill(x, y, oldClr, newClr, img, w, 0);
	}
	// 4-Way flood fill using queue
	public void fill(int x, int y, int oldClr, int newClr, int [] img, int w, int treshold){	
		int h = img.length / w - 1;
		if (x <0 || y < 0 || x > w || y > h){
			return;
		}
		int pixel = img[x + y * w];
		if (pixel == newClr || pixel != oldClr){
			return;
		}
		
		ArrayList<Point> q = new ArrayList<Point>(10000);
		q.add(new Point(x, y));
		while (!q.isEmpty()){
			Point p = q.remove(q.size() - 1);
			img[p.x + p.y * w] = newClr;
			// LEFT
			if (p.x != 0 && img[p.x - 1 + p.y * w] != newClr && alphaTreshold(img[p.x - 1 + p.y * w], oldClr, treshold)) q.add(new Point(p.x - 1, p.y));
			// RIGHT		
			if (p.x != w-1 && img[p.x + 1 + p.y * w] != newClr && alphaTreshold(img[p.x + 1 + p.y * w], oldClr, treshold)) q.add(new Point(p.x + 1, p.y));
			// UP
			if (p.y != h && img[p.x + (p.y + 1) * w] != newClr && alphaTreshold(img[p.x + (p.y + 1) * w], oldClr, treshold)) q.add(new Point(p.x, p.y + 1));
			// DOWN
			if (p.y != 0 && img[p.x + (p.y - 1) * w] != newClr && alphaTreshold(img[p.x + (p.y - 1) * w], oldClr, treshold)) q.add(new Point(p.x, p.y - 1));		
		}
	}
	
	/**
	 * Method calculates if color differs from other color by less than amount
	 * @param newClr
	 * @param oldClr
	 * @param treshold
	 * @return true if delta is less than treshold
	 */
	public boolean alphaTreshold(int newClr, int oldClr, int treshold){
		int mr = 0x00ff0000, mg = 0x0000ff00, mb = 0x000000ff;
		int or, og, ob, nr, ng, nb;
		or = oldClr & mr >>> 16;
		og = oldClr & mg >>> 8;
		ob = oldClr & mb;
		nr = newClr & mr >>> 16;
		ng = newClr & mg >>> 8;
		nb = newClr & mb;
		
		if (or > nr + treshold || or < nr - treshold ||
			og > ng + treshold || og < ng - treshold ||
			ob > nb + treshold || ob < nb - treshold){
			return false;
		}
		return true;
	}

	public void clearRect(int x, int y, int w, int h){
		g.clearRect(x, y, w, h);
	}

	public void setAntialiasing(boolean isOn){
		RenderingHints r;
		if (isOn){
			r = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
		  			   RenderingHints.VALUE_ANTIALIAS_ON);
		}
		else {
			r = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
					   RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		g.setRenderingHints(r);
	}
}