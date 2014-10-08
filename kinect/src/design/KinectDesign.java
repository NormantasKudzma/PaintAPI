package design;

import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.VideoFrame;

public class KinectDesign extends J4KSDK{
	VideoFrame videoTexture; 
	
	
	public KinectDesign(){
		super();
		new PcDesign();
		videoTexture = new VideoFrame();
	}
	
	
	@Override
	public void onDepthFrameEvent(short[] packed_depth, int[] U, int V[]) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSkeletonFrameEvent(float[] data, boolean[] flags) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVideoFrameEvent(byte[] data) {
		// TODO Auto-generated method stub
		
	}
}
