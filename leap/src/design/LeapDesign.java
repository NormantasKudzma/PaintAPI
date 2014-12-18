package design;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;

/**
 * GUI class for LeapMotion
 */
public class LeapDesign extends KinectDesign{
	LeapController leapMotion;
	Controller c;
	
	CustomColorChooser kcc;
	Image lastImage;
	JPanel leapVideoPanel;
	
	public LeapDesign(){
		setTitle("The amazing paint for LeapMotion, v1.1");
		initColorChooser();
		initVideoPanel();
	}
	
	/**
	 *  Creates controller, adds frame listener and starts processing thread
	 */
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
	
	/**
	 * Initializes video panel in the upper-right hand corner (currently not working)
	 */
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
	
	/**
	 * Initializes color chooser panel
	 */
	void initColorChooser(){
		JColorChooser jcc = (JColorChooser)((JPanel)topPanel.getComponents()[0]).getComponent(0);
		kcc = (CustomColorChooser) jcc.getChooserPanels()[0];
	}
	
	/**
	 * Method for choosing <i>next</i> color on the color chooser
	 * @param index - which user selected color
	 * @param direction - either to the left or to the right of current color
	 */
	void nextColor(int index, int direction){
		Color clr = kcc.getSelectedColors()[index];
		int clrNum = (getColorNum(clr) + direction) % kcc.all.length;
		if (clrNum < 0){
			clrNum = kcc.all.length - 1;
		}
		ActionEvent evt = new ActionEvent(kcc.all[clrNum], index, null);
		kcc.actionPerformed(evt);
	}
	
	/**
	 * 
	 * @param c - currently selected color
	 * @return Index of color in the array (or -1 if there's no such color)
	 */
	public int getColorNum(Color c){
		if (c != null){			
			for (int i = 0; i < kcc.all.length; i++){
				if (kcc.all[i].getBackground() == c){
					return i;
				}
			}
		}
		return -1;
	}
	
	public static void main(String [] args){
		new LeapDesign();
	}
}
