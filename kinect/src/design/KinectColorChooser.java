package design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import core.LightweightMouseListener;

public class KinectColorChooser extends AbstractColorChooserPanel implements ActionListener {
	int maxSize;
	JToggleButton white;
	JToggleButton black;
	JToggleButton red;
	JToggleButton green;
	JToggleButton blue;
	JToggleButton magenta;
	JToggleButton cyan;
	JToggleButton yellow;
	JToggleButton gray;
	JToggleButton gray2;
	JToggleButton orange;
	JToggleButton pink;	
	JToggleButton [] all = {white, black, gray, gray2, pink, red, 
							orange, yellow, green, cyan, blue, magenta};
	Color [] colors = {Color.white, Color.black, Color.lightGray, Color.darkGray, Color.pink, Color.red,
					   Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta};
	
	public KinectColorChooser(Dimension d){
		this(d.width, d.height);
	}
	
	public KinectColorChooser(int w, int h){
		maxSize = w < h ? w : h;
		maxSize /= 2;
	}
	
	@Override
	protected void buildChooser() {
		setLayout(new GridLayout(0, 2));
		ButtonGroup buttons = new ButtonGroup();
		
		for (int i = 0; i < all.length; i++){
			JToggleButton b = new JToggleButton("", null, false);
			b.setUI(new CustomColorButtonUI());
			b.setBackground(colors[i]);
			b.setBorder(new LineBorder(Color.black, 1));
			b.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					ActionEvent evt = new ActionEvent(e.getSource(), 0, null);
					actionPerformed(evt);
				}
			});
			b.addActionListener(this);
			b.setPreferredSize(new Dimension(maxSize, maxSize));
			buttons.add(b);
			add(b);
			all[i] = b;			
		}		
	}

	@Override
	public String getDisplayName() {
		return "KinectColorChooser";
	}

	@Override
	public void updateChooser() {
		Color current = getColorFromModel();
		for (int i = 0; i < all.length; i++){
			if (colors[i] == current){
				all[i].setSelected(true);
				break;
			}
		}
	}
	
	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Color newColor = ((JToggleButton)e.getSource()).getBackground();
		getColorSelectionModel().setSelectedColor(newColor);
	}
}
