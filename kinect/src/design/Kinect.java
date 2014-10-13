package design;

import java.util.Arrays;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class Kinect extends J4KSDK {	
	public static double MIN_TRESHOLD = 0.001;	// Min to smooth out movement when not moving hand (shaking)
	public static double MAX_TRESHOLD = 1.000;	// Max to remove random twitches
	//public static double DRAW_TRESHOLD = 0.3;	// Distance in meters
	public static double DRAW_TRESHOLD = 1.5;	// Distance in meters
	
	public static int X_DELTA_TRESHOLD = 850;
	public static int Y_DELTA_TRESHOLD = 850;
	
	VideoPanel videoPanel;
	KinectDesign k;
	double [] oldDrawHand = new double[3];
	double [] oldControlHand = null;
	boolean isDrawing = false;
	int trackedJoint = Skeleton.HAND_RIGHT;
	int controlJoint = Skeleton.HAND_LEFT;
	
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
				trackSkeleton(s.get3DJoint(trackedJoint), s.get3DJoint(controlJoint));
			}
			videoPanel.skeletons[i] = s;
		}
	}

	private void trackSkeleton(double [] drawHand, double [] controlHand) {
		// Mouse controls
		double dx = drawHand[0] - oldDrawHand[0];
		double dy = drawHand[1] - oldDrawHand[1];
			
		oldDrawHand = drawHand;
			
		double dist = Math.sqrt(dx*dx + dy*dy);		
		
		if (dist > MAX_TRESHOLD || dist < MIN_TRESHOLD){
			return;
		}
		
		k.thisX = convertX(drawHand[0]);
		k.thisY = convertY(drawHand[1]);
		k.glass.repaint();
		//System.out.println(/*"L\t" + Arrays.toString(controlHand) + */"\tR\t" + Arrays.toString(drawHand) + "\t" + k.thisX + "\t" + k.thisY);
		
		// Draw controls
//		if (oldControlHand == null){
//			oldControlHand = controlHand;
//		}
//		double dz = controlHand[2] - oldControlHand[2];
//		
//		if (Math.abs(dz) > DRAW_TRESHOLD){
//			oldControlHand = controlHand;
//		}
//		if (dz < -DRAW_TRESHOLD){
//			if (isDrawing){
//				k.dispatchMouseDrag();
//			}
//			else {
//				k.dispatchMouseClick();
//				isDrawing = true;
//			}	
//		}
//		else {
//			if (dz > DRAW_TRESHOLD){
//				if (isDrawing){
//					isDrawing = false;
//					k.dispatchMouseRelease();
//				}
//			}
//		}
//		System.out.println(dz);
		// OLD DRAW CONTROLS
		if (drawHand[2] < DRAW_TRESHOLD){
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
		int fw = KinectDesign.frameWidth;
		double coef = 1.0 * fw / X_DELTA_TRESHOLD;	
		dx *= 1000;
		int x = (int) (coef * (dx + X_DELTA_TRESHOLD / 2));
		x = Math.max(0, Math.min(fw, x));
		return x;
	}
	
	private int convertY(double dy){
		int fh = KinectDesign.frameHeight;
		double coef = 1.0 * fh / Y_DELTA_TRESHOLD;	
		dy *= 1000;
		int y = (int) (coef * (dy + Y_DELTA_TRESHOLD / 2));
		y = fh - Math.max(0, Math.min(fh, y));
		return y;
	}

	@Override
	public void onVideoFrameEvent(byte[] data) {
		if (videoPanel != null && videoPanel.videoTexture != null){
			videoPanel.videoTexture.update(videoWidth(), videoHeight(), data);
		}
	}
}
