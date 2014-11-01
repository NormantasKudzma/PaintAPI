package design;

import java.util.Arrays;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class Kinect extends J4KSDK {	
	public static double X_TRESHOLD = 0.08;
	public static double Y_TRESHOLD = 0.08;
	public static double DRAW_TRESHOLD = 0.4;	// Distance in meters
	
	public static int X_DELTA_TRESHOLD = 500;	// X sensitivity
	public static int Y_DELTA_TRESHOLD = 600;	// Y sensitivity
	
	VideoPanel videoPanel;
	KinectDesign k;
	boolean isDrawing [] = new boolean[]{false, false};
	int rightHand = Skeleton.HAND_RIGHT;
	int leftHand = Skeleton.HAND_LEFT;
	int rightShoulder = Skeleton.SHOULDER_RIGHT;
	int headJoint = Skeleton.HEAD;
	
	double headOffsetX = -0.3;
	double headOffsetY = 0.45;
	
	// Data for smoothing
	double [][] trend = new double[7][3];
	double [] x = {trend[0][0], trend[1][0], trend[2][0], trend[3][0], trend[4][0], trend[5][0],
			   trend[6][0]/*, trend[7][0], trend[8][0], trend[9][0], trend[10][0], trend[11][0], trend[12][0]*/};
	double [] y = {trend[0][1], trend[1][1], trend[2][1], trend[3][1], trend[4][1], trend[5][1],
			   trend[6][1]/*, trend[7][1], trend[8][1], trend[9][1], trend[10][1], trend[11][1], trend[12][1]*/};
	double xmed = 0;
	double ymed = 0;
	int frame = 0;
	
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

	private void trackSkeleton(double [] drawHand, double [] drawShoulder, double [] controlHand, double [] deltaJoint, boolean first) {
		int index = first ? 0 : 1;
		
		// Fake mouse controls
		// SMOOTHING DISABLED ATM
//		xmed = median(x);
//		ymed = median(y);
//		if (frame > 6){
//			drawHand = exponentialSmoothing(drawHand);
//			drawHand = doubleAverageSmoothing(drawHand);
//		}
//		
//		trend = matrixPush(trend);
//		trend[0] = drawHand;
		
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
	
	private double[][] matrixPush(double [][] m){
		for (int i = m.length - 1; i > 0; i--){
			m[i] = m[i-1];
		}
		return m;
	}
	
	private double median(double [] arr){
		Arrays.sort(arr);
		int l = arr.length;
		if (l % 2 == 0){
			return (arr[l / 2] + arr[l / 2 - 1]) / 2.0;
		}
		else {
			return arr[l / 2];
		}
	}
	
	private double[] jitterSmoothing(double [] matrix){
		// Smooth out X and Y using jitter removal hybrid method
		if (jitterModule(matrix[0], trend[0][0], X_TRESHOLD)){
			matrix[0] = (trend[0][0] + matrix[0]) / 2.0;
		}
		if (jitterModule(matrix[1], trend[0][1], Y_TRESHOLD)){
			matrix[1] = (trend[0][1] + matrix[0]) / 2.0;
		}
		return matrix;
	}
	
	private boolean jitterModule(double x, double mx, double treshold){
		if (Math.abs(x - mx) > treshold){
			return true;
		}
		return false;
	}
	
	private double[] exponentialSmoothing(double [] matrix){
		// Exponential smoothing
		// Xn = a * sum-i=0->n[ (1-a)^i * X(n-i) ]
		// N = 7 ; a = 0.35;
		double a = 0.35;
		double invA = 1 - a;
		int n = 6;
		double sumX = 0;
		double sumY = 0;
		for (int i = 0; i < n; i++){
			sumX += Math.pow(invA, i) * trend[n - i][0];
			sumY += Math.pow(invA, i) * trend[n - i][1];
		}
		matrix[0] = a * sumX;
		matrix[1] = a * sumY;
		
		return matrix;
	}
	
	private double[] medianSmoothing(double [] matrix){
		// Smooth out X and Y using median, where element count N=13
		matrix[0] = (matrix[0] + xmed) / 2.0;
		matrix[1] = (matrix[1] + ymed) / 2.0;
		return matrix;
	}
	
	private double [] doubleAverageSmoothing(double [] matrix){
		// Smooth out X and Y movement using double moving average simplified formula
		// Xn = 5/9 * Xn + 4/9 * X(n-1) + 1/3 * X(n-2) - 2/9 * X(n-3) - 1/9 * X(n-4);		
		matrix[0] = 0.555 * matrix[0] + 0.444 * trend[0][0] + 0.333 * trend[1][0] - 0.222 * trend[2][0] - 0.111 * trend[3][0];
		matrix[1] = 0.555 * matrix[1] + 0.444 * trend[0][1] + 0.333 * trend[1][1] - 0.222 * trend[2][1] - 0.111 * trend[3][1];
		return matrix;
	}
	
	private int convertX(double dx, double x0){
		int fw = KinectDesign.frameWidth;
		double coef = 1.0 * fw / X_DELTA_TRESHOLD;	
		dx = (dx-x0) * 1000;
		int x = (int) (coef * (dx + X_DELTA_TRESHOLD / 2));
		x = Math.max(0, Math.min(fw, x));
		return x;
	}
	
	private int convertY(double dy, double y0){
		int fh = KinectDesign.frameHeight;
		double coef = 1.0 * fh / Y_DELTA_TRESHOLD;	
		dy = (dy-y0) * 1000;
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
