package design;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class Kinect extends J4KSDK {	
	public static double MIN_TRESHOLD = 0.001;	// Min to smooth out movement when not moving hand (shaking)
	public static double MAX_TRESHOLD = 1.000;	// Max to remove random twitches
	public static double DRAW_TRESHOLD = 1.5;	// 1.5 Meters
	
	public static int X_DELTA_TRESHOLD = 750;
	public static int Y_DELTA_TRESHOLD = 850;
	
	VideoPanel videoPanel;
	KinectDesign k;
	double [] oldHand = new double[3];
	boolean isDrawing = false;
	int trackedJoint = Skeleton.HAND_RIGHT;
	
	public Kinect(){
		super();
	}	
	
	@Override
	public void onDepthFrameEvent(short[] depth, int[] U, int V[]) {
		DepthMap map = new DepthMap(depthWidth(), depthHeight(), depth);
		if (U != null && V != null) {
			map.setUV(U, V, videoWidth(), videoHeight());
		}
		if (videoPanel != null) {
			videoPanel.map = map;
		}
	}

	@Override
	public void onSkeletonFrameEvent(float[] data, boolean[] flags) {
		// Draw all skeletons
		for (int i = 0; i < Kinect.NUI_SKELETON_COUNT; i++) {
			CustomSkeleton s = CustomSkeleton.getSkeleton(i, data, flags);
			// Currently track only one skeleton
			if (flags[i]){
				trackSkeleton(s.get3DJoint(trackedJoint));
			}
			videoPanel.skeletons[i] = s;
		}
	}

	private void trackSkeleton(double[] hand) {
		double dx = hand[0] - oldHand[0];
		double dy = hand[1] - oldHand[1];
			
		oldHand = hand;
			
		double dist = Math.sqrt(dx*dx + dy*dy);		
		//System.out.printf("%30.30f\n", dist);
		if (dist > MAX_TRESHOLD || dist < MIN_TRESHOLD){
			return;
		}
		
		k.thisX = convertX(hand[0]);
		k.thisY = convertY(hand[1]);
		k.glass.repaint();
		
		// 
		if (hand[2] < DRAW_TRESHOLD){
			if (isDrawing){
				k.dispatchMouseDrag();
			}
			else {
				k.dispatchMouseClick();
				isDrawing = true;
			}				
		}
		else {
			if (isDrawing){
				isDrawing = false;
				k.dispatchMouseRelease();
			}
		}
	}
	
	private int convertX(double dx){
		double coef = KinectDesign.frameWidth / X_DELTA_TRESHOLD;	
		dx *= 1000;
		int x = (int) (coef * (dx + X_DELTA_TRESHOLD / 2));
		x = Math.max(0, Math.min(KinectDesign.frameWidth, x));
		return x;
	}
	
	private int convertY(double dy){
		double coef = KinectDesign.frameHeight / Y_DELTA_TRESHOLD;	
		dy *= 1000;
		int y = (int) (coef * (dy + Y_DELTA_TRESHOLD / 2));
		y = KinectDesign.frameHeight - Math.max(0, Math.min(KinectDesign.frameHeight, y));
		return y;
	}

	@Override
	public void onVideoFrameEvent(byte[] data) {
		if (videoPanel != null && videoPanel.videoTexture != null){
			videoPanel.videoTexture.update(videoWidth(), videoHeight(), data);
		}
	}
}
