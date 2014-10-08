package design;

import javax.swing.JFrame;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;
import edu.ufl.digitalworlds.j4k.VideoFrame;

public class Kinect extends J4KSDK {
	VideoPanel videoPanel;
	
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
		for(int i = 0; i < Kinect.NUI_SKELETON_COUNT; i++) {
			videoPanel.skeletons[i]=Skeleton.getSkeleton(i, data, flags);
		}
	}

	@Override
	public void onVideoFrameEvent(byte[] data) {
		if (videoPanel != null){
			videoPanel.videoTexture.update(videoWidth(), videoHeight(), data);
		}
	}
}
