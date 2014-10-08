package design;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.ufl.digitalworlds.gui.DWApp;

public class KinectDesign{
	Kinect k;
	VideoPanel videoPanel;

	public KinectDesign(){

		k = new Kinect();		
		
		if (k.start(true, Kinect.NUI_IMAGE_RESOLUTION_320x240, Kinect.NUI_IMAGE_RESOLUTION_640x480) == 0){
			System.out.println("Error starting kinect.");
		}
		k.computeUV(true);
		k.startSkeletonTracking(true);
		videoPanel = k.videoPanel = new VideoPanel();
		
	}
	
	
	public static void main(String[] args){
		new KinectDesign();
	}
}
