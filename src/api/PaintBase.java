package api;

import java.awt.Graphics;

public class PaintBase {
	protected Graphics g;
	
	public PaintBase(Graphics graphics){
		this.g = graphics;
	}
}