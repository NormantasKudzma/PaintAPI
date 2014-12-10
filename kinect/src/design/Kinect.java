package design;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

/**
 * Class responsible for handling Kinect events
 */
public class Kinect extends J4KSDK {
	/** Movements, smaller than these tresholds, won't be registered */
	public static double X_TRESHOLD = 0.08;
	public static double Y_TRESHOLD = 0.08;
	/** Minimum distance between assigned body parts to initiate drawing */
	public static double DRAW_TRESHOLD = 0.4;
	
	/** X and Y drawing sensitivity (inverted - lower values means higher sensitivity) */
	public static int X_SENSITIVITY = 500;
	public static int Y_SENSITIVITY = 600;
	
	VideoPanel videoPanel;
	KinectDesign k;
	boolean isDrawing [] = new boolean[]{false, false};
	/** Body parts responsible for drawing motions */
	int rightHand = Skeleton.HAND_RIGHT;
	int leftHand = Skeleton.HAND_LEFT;
	int rightShoulder = Skeleton.SHOULDER_RIGHT;
	int headJoint = Skeleton.HEAD;
	
	/** Arbitrary value for mouse movement corrections */
	double headOffsetX = -0.3;
	double headOffsetY = 0.45;
	
	/** Trend (or position history) is used for movement smoothing
	* <br>The length of history should be 5 <= len <= 13 
	* (mouse smoothing is disabled for current controller version) **/
	double [][] trend = new double[7][3];
	double [] x = {trend[0][0], trend[1][0], trend[2][0], trend[3][0], trend[4][0], trend[5][0],
			   trend[6][0]};
	double [] y = {trend[0][1], trend[1][1], trend[2][1], trend[3][1], trend[4][1], trend[5][1],
			   trend[6][1]/*, trend[7][1], trend[8][1], trend[9][1], trend[10][1], trend[11][1], trend[12][1]*/};
	double xmed = 0;
	double ymed = 0;
	int frame = 0;
	
	public Kinect(){
		super();
	}	
	
	/**
	 * Method on depth frame event updates video panel with depth data
	 */
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

	/**
	 * On skeleton frame event skeletons are drawn and movements are calculated
	 * @param data - skeleton data
	 * @param flags - is skeleton currently tracked
	 */
	@Override
	public void onSkeletonFrameEvent(float[] data, boolean[] flags) {
		// Draw all skeletons
		boolean firstSkeleton = true;
		for (int i = 0; i < Kinect.NUI_SKELETON_COUNT; i++) {
			CustomSkeleton s = CustomSkeleton.getSkeleton(i, data, flags);
			
			if (flags[i]){
				trackSkeleton(s.get3DJoint(rightHand), s.get3DJoint(rightShoulder), s.get3DJoint(leftHand), s.get3DJoint(headJoint), firstSkeleton);
				firstSkeleton = false;
			}
			videoPanel.skeletons[i] = s;
		}
		if (frame <= 6){
			frame++;
		}
	}

	/**
	 * Calculate movements for current skeleton
	 * @param drawHand - the hand, which user uses to draw with
	 * @param drawShoulder - draw hands' shoulder
	 * @param controlHand - the hand, which user uses to initiate mouse clicks with
	 * @param deltaJoint - control hands' joint from which offsets are calculated
	 * @param first - was this skeleton first to be tracked
	 */
	private void trackSkeleton(double [] drawHand, double [] drawShoulder, double [] controlHand, double [] deltaJoint, boolean first) {
		int index = first ? 0 : 1;
		
		k.thisX[index] = convertX(drawHand[0], deltaJoint[0]-headOffsetX);
		k.thisY[index] = convertY(drawHand[1], deltaJoint[1]-headOffsetY);
		k.glass.repaint();

		double dz = Math.abs(deltaJoint[2] - controlHand[2]);
		
		if (dz >= DRAW_TRESHOLD){
			if (isDrawing[index]){
				k.dispatchMouseDrag(index);
			}
			else {
				k.dispatchMouseClick(index);
				isDrawing[index] = true;
			}				
		}
		else {
			if (isDrawing[index]){
				isDrawing[index] = false;
				k.dispatchMouseRelease(index);
			}
		}
	}
	
	/**
	 * Pushes matrix down, freeing 1st element for new data
	 * @param m - matrix to be pushed down
	 * @return newly made matrix
	 */
	private double[][] matrixPush(double [][] m){
		for (int i = m.length - 1; i > 0; i--){
			m[i] = m[i-1];
		}
		return m;
	}
	
	/**
	 * Converts x value gained from device to x position on screen
	 * @param index - which user is performing action
	 * @param x - horizontal value 
	 * @return Converted x value which corresponds to a point on screen
	 */
	private int convertX(double dx, double x0){
		int fw = KinectDesign.frameWidth;
		double coef = 1.0 * fw / X_SENSITIVITY;	
		dx = (dx-x0) * 1000;
		int x = (int) (coef * (dx + X_SENSITIVITY / 2));
		x = Math.max(0, Math.min(fw, x));
		return x;
	}
	
	/**
	 * Converts y value gained from device to y position on screen
	 * @param index - which user is performing action
	 * @param y - vertical value 
	 * @return Converted y value which corresponds to a point on screen
	 */
	private int convertY(double dy, double y0){
		int fh = KinectDesign.frameHeight;
		double coef = 1.0 * fh / Y_SENSITIVITY;	
		dy = (dy-y0) * 1000;
		int y = (int) (coef * (dy + Y_SENSITIVITY / 2));
		y = fh - Math.max(0, Math.min(fh, y));
		return y;
	}
	
	/**
	 * On video frame event updates video panel with texture data
	 */
	@Override
	public void onVideoFrameEvent(byte[] data) {
		if (videoPanel != null && videoPanel.videoTexture != null){
			videoPanel.videoTexture.update(videoWidth(), videoHeight(), data);
		}
	}
}
