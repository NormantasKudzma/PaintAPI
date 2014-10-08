package design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.jogamp.newt.event.InputEvent;


public class KinectDesign extends PcDesign {
	Kinect k;
	VideoPanel videoPanel;
	BufferedImage fakeMouse;
	JPanel glass;
	
	BufferedImage cursor;
	BufferedImage cursorActive;
	public int thisX, thisY;

	public KinectDesign(){		
		// KINECT INITIALIZATION
		k = new Kinect();				
		if (k.start(true, Kinect.NUI_IMAGE_RESOLUTION_320x240, Kinect.NUI_IMAGE_RESOLUTION_640x480) == 0){
			System.out.println("Error starting kinect.");
		}
		k.computeUV(true);
		k.startSkeletonTracking(false);
		k.setNearMode(true);
		videoPanel = k.videoPanel = new VideoPanel();
		k.k = this;
		// KINECT INITIALIZATION
		
		thisX = frameWidth / 2;
		thisY = (int) (frameHeight *0.75);
		
		cl = getClass().getClassLoader();
		
		setGlassPane();
		setUpVideoPanel();
	}
	
	protected void setGlassPane(){
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
	
	protected void setUpVideoPanel(){
		int newWidth = PcDesign.frameWidth;
		// Resize old components, so there's place for video frame
		Component [] c = topPanel.getComponents();
		for (Component i : c){
			i = (JPanel)i;
			Dimension d = i.getMaximumSize();
			newWidth -= d.width * 0.85f;
			i.setMaximumSize(new Dimension((int) (d.width * 0.85f), d.height));
		}		
		
		videoPanel.setMaximumSize(new Dimension(newWidth, topPanel.getHeight()));
		
		topPanel.add(videoPanel);
		topPanel.revalidate();
		topPanel.repaint();
	}
	
	protected void dispatchMouseClick(){
		MouseEvent e = new MouseEvent(this, MouseEvent.MOUSE_PRESSED, 0, 0, thisX, thisY, 1, false);
		this.dispatchEvent(e);
		if (fakeMouse != cursorActive){
			fakeMouse = cursorActive;
		}
		//System.out.println("Dispatch Mouse Click called");
	}
	
	protected void dispatchMouseDrag(){
		// FIX MOUSE DRAG
		MouseEvent e = new MouseEvent(this, MouseEvent.MOUSE_DRAGGED, 0, 0, thisX, thisY, 0, false);
		this.dispatchEvent(e);
		if (fakeMouse != cursorActive){
			fakeMouse = cursorActive;
		}
		//System.out.println("Dispatch Mouse drag called");
	}
	
	protected void dispatchMouseRelease(){
		fakeMouse = cursor;
	}
	
	public static void main(String[] args){
		new KinectDesign();
	}
}
