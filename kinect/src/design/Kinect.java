package design;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class Kinect extends J4KSDK {	
	VideoPanel videoPanel;
	KinectDesign k;
	double [] oldWrist;
	boolean isDrawing = false;
	
	public Kinect(){
		super();
		oldWrist = new double[3];
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
			Skeleton s = Skeleton.getSkeleton(i, data, flags);
			if (flags[i]){
				trackSkeleton(s.get3DJoint(NUI_SKELETON_POSITION_WRIST_RIGHT));
			}
			videoPanel.skeletons[i] = s;
		}
	}

	private void trackSkeleton(double[] wrist) {		
		k.thisX = convertX(wrist[0]);
		k.thisY = convertY(wrist[1]);
		k.glass.repaint();
		
		if (wrist[2] < 1.5){
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
		double delta = 900;
		double coef = KinectDesign.frameWidth / delta;	
		dx *= 1000;
		int x = (int) (coef * (dx + delta / 2));
		x = Math.max(0, Math.min(KinectDesign.frameWidth, x));
		return x;
	}
	
	private int convertY(double dy){	
		double delta = 850;
		double coef = KinectDesign.frameHeight / delta;	
		dy *= 1000;
		int y = (int) (coef * (dy + delta / 2));
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
