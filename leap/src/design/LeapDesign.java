package design;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;

public class LeapDesign extends KinectDesign{
	LeapController leapMotion;
	Controller c;
	
	KinectColorChooser kcc;
	Image lastImage;
	JPanel leapVideoPanel;
	
	public LeapDesign(){
		setTitle("The amazing paint for LeapMotion, v1.1");
		initColorChooser();
		initVideoPanel();
	}
	
	@Override
	protected void setUpDevice() {
		System.out.println("Setup device called");
		leapMotion = new LeapController(this);
		c = new Controller(leapMotion);
		c.enableGesture(Gesture.Type.TYPE_SWIPE);
		c.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
		leapMotion.setController(c);
			
		new Thread(leapMotion).start();
	}
	
	void initVideoPanel(){
		videoPanel.stopAnimation();
		rightPanel.remove(0);
		leapVideoPanel = new JPanel(){
			protected void paintComponent(java.awt.Graphics g) {
				if (lastImage != null) {
					g.drawImage(lastImage, 0, 0, null);
				}
			};
		};
		rightPanel.add(leapVideoPanel, 0);
		rightPanel.invalidate();
	}
	
	void initColorChooser(){
		JColorChooser jcc = (JColorChooser)((JPanel)topPanel.getComponents()[0]).getComponent(0);
		kcc = (KinectColorChooser) jcc.getChooserPanels()[0];
	}
	
	void nextColor(int index, int direction){
		Color clr = kcc.getSelectedColors()[index];
		int clrNum = (getColorNum(clr) + direction) % kcc.all.length;
		if (clrNum < 0){
			clrNum = kcc.all.length - 1;
		}
		ActionEvent evt = new ActionEvent(kcc.all[clrNum], index, null);
		kcc.actionPerformed(evt);
	}
	
	int getColorNum(Color c){
		for (int i = 0; i < kcc.all.length; i++){
			if (kcc.all[i].getBackground() == c){
				return i;
			}
		}
		return -1;
	}
	
//	void setImage(com.leapmotion.leap.Image img){
//		try {
//			if (leapVideoPanel != null){
//				byte [] arr = img.data();
//				InputStream in = new ByteArrayInputStream(arr);
//				BufferedImage bi = ImageIO.read(in);
//				lastImage = bi;
//				leapVideoPanel.repaint();
//			}
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public static void main(String [] args){
		new LeapDesign();
	}
}
