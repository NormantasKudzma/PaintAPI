package design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;


public class KinectDesign extends PcDesign {	
	Kinect k;
	VideoPanel videoPanel;
	BufferedImage fakeMouse;
	JPanel glass;
	
	BufferedImage cursor;
	BufferedImage cursorActive;
	public int thisX, thisY;
	int topSize;

	public KinectDesign(){	
		setVisible(false);
		videoPanel = new VideoPanel();
		cl = getClass().getClassLoader();
		
		thisX = frameWidth / 2;
		thisY = (int) (frameHeight *0.75);
		
		
		setTitle("The amazing paint for PC + Microsoft Windows® Kinect™ v1, v0.101");
		
		setUpGlassPane();
		setUpTopPanel();
		// KINECT INITIALIZATION
		k = new Kinect();				
		if (k.start(true, Kinect.NUI_IMAGE_RESOLUTION_320x240, Kinect.NUI_IMAGE_RESOLUTION_640x480) == 0){
			System.out.println("Error starting kinect.");
		}
		k.computeUV(true);
		k.startSkeletonTracking(false); // buvo false jei crashins
		k.setNearMode(true);
		k.videoPanel = videoPanel;
		k.k = this;
		// KINECT INITIALIZATION	
		setVisible(true);
	}
	
	protected void setUpGlassPane(){
		try {
			cursor = ImageIO.read(cl.getResource(RES_PATH + "cursor.png"));
			cursorActive = ImageIO.read(cl.getResource(RES_PATH + "cursorActive.png")); 
			fakeMouse = cursor;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		glass = new JPanel(){
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(fakeMouse, thisX, thisY, null);
			};
		};
		this.setGlassPane(glass);
		glass.setVisible(true);
		glass.setOpaque(false);
		glass.repaint();
	}
	
	protected void setUpTopPanel(){
		int newWidth = PcDesign.frameWidth;
		// Resize old components, so there's place for video frame
		Component [] c = topPanel.getComponents();		
		topSize = topPanel.getHeight();
		
		for (int i = 0; i < c.length; i++){
			JPanel p = (JPanel)c[i];
			Dimension d = p.getMaximumSize();
			newWidth -= d.width * 0.85f;
			p.setMaximumSize(new Dimension((int) (d.width * 0.85f), d.height));
		}		
		
		// Changing JColorChooser's color palette to our huge color buttons
		Dimension d = c[0].getMaximumSize();
		d.width *= 0.85f;
		d.height *= 0.80f;
		KinectColorChooser [] kcc = {new KinectColorChooser(d)};	
		JColorChooser jcc = (JColorChooser)((JPanel)c[0]).getComponent(0);
		jcc.setChooserPanels(kcc);
		
		// Changing the old size chooser (new is buttons, easier to use)
		JPanel opts = ((JPanel)c[1]);
		opts.remove(0);
		JPanel size = new JPanel();
		opts.add(size, 0);
		size.setLayout(new GridLayout(1, 3));
		final JLabel sizeLabel = new JLabel("8", SwingConstants.CENTER);
		sizeLabel.setBorder(new LineBorder(Color.black));
		sizeLabel.setFont(new Font("Serif", Font.BOLD, 35));
		JButton minus = new JButton("-");
		minus.setFont(new Font("Serif", Font.BOLD, 35));
		minus.addMouseListener(new core.LightweightMouseListener(){
			@Override
			public void mousePressed(MouseEvent e) {
				int size = Integer.parseInt(sizeLabel.getText());
				if (size > 4){
					size /= 2;
					sizeLabel.setText("" + size);
					updateBrush(size);
				}
			}
		});
		JButton plus = new JButton("+");
		plus.setFont(new Font("Serif", Font.BOLD, 35));
		plus.addMouseListener(new core.LightweightMouseListener(){
			@Override
			public void mousePressed(MouseEvent e) {
				int size = Integer.parseInt(sizeLabel.getText());
				if (size < 256){
					size *= 2;
					sizeLabel.setText("" + size);
					updateBrush(size);
				}
			}
		});
		updateBrush(8);				
		size.add(minus);
		size.add(sizeLabel);
		size.add(plus);
		opts.revalidate();
		opts.repaint();
		
		videoPanel.setMaximumSize(new Dimension(newWidth, topPanel.getHeight()));		
		topPanel.add(videoPanel);
		topPanel.revalidate();
		topPanel.repaint();
	}
	
	protected void dispatchMouseClick(){
		MouseEvent e;
		if (drawContainerPanel.getBounds().contains(thisX, thisY)){
			e = new MouseEvent(this.drawContainerPanel, MouseEvent.MOUSE_PRESSED, 0, 0, thisX, thisY - topSize, 1, false);
			drawPanel.dispatchEvent(e);
		}
		else {
			e = new MouseEvent(this.glass, MouseEvent.MOUSE_PRESSED, 0, 0, thisX, thisY, 1, false);
			this.dispatchEvent(e);
		}
		if (fakeMouse != cursorActive){
			fakeMouse = cursorActive;
		}
	}
	
	protected void dispatchMouseDrag(){		
		if (drawContainerPanel.getBounds().contains(thisX, thisY)){
			MouseEvent e = new MouseEvent(this.glass, MouseEvent.MOUSE_DRAGGED, 0, 0, thisX, thisY - topSize, 1, false);
			drawPanel.dispatchEvent(e);
		}
		if (fakeMouse != cursorActive){
			fakeMouse = cursorActive;
		}
	}
	
	protected void dispatchMouseRelease(){
		fakeMouse = cursor;
	}
	
	public static void main(String[] args){
		new KinectDesign();
	}
}
