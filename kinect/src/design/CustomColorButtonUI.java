package design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * A custom ButtonUI used in CustomColorChooser class.
 */
public class CustomColorButtonUI extends BasicButtonUI{
	boolean first = true;
	
	@Override
	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		Rectangle r = b.getBounds();
		Color c = b.getBackground();
		if (c == Color.black){
			c = Color.white;
		}
		else {
			c = Color.black;
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(c);
		g2d.setStroke(new BasicStroke(4));

		g2d.drawLine(0, 0, r.width, 0);
		g2d.drawLine(0, 0, r.width, r.height);
		g2d.drawLine(0, 0, 0, r.height);

		g2d.drawLine(r.width, r.height, r.width, 0);
		g2d.drawLine(r.width, r.height, 0, r.height);

		g2d.drawLine(0, r.height, r.width, 0);
		if (first){
			g2d.drawString("p1", r.width / 2, 15);
		}
		else {
			g2d.drawString("p2", r.width / 2, r.height - 5);
		}
	}
	
	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		JToggleButton b = (JToggleButton)c;
		if (b.isSelected()){
			paintButtonPressed(g, b);
		}
	}
}
