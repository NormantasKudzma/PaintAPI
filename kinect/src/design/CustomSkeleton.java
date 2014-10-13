package design;

import javax.media.opengl.GL2;

import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class CustomSkeleton extends Skeleton{
	public static float TRACKING_POINT_RADIUS = 12;
		
	public static CustomSkeleton getSkeleton(int id, float[] data, boolean[] flags){
		if(id < 0 || id >= J4KSDK.NUI_SKELETON_COUNT || data == null){
			return null;
		}
		CustomSkeleton sk = new CustomSkeleton();
		sk.setPlayerID(id);
		if(flags != null){
			sk.setIsTracked(flags[id]);
		}
		
		float skeleton_data[] = new float[J4KSDK.NUI_SKELETON_POSITION_COUNT*3];
		System.arraycopy( data, id*J4KSDK.NUI_SKELETON_POSITION_COUNT*3, skeleton_data, 0, J4KSDK.NUI_SKELETON_POSITION_COUNT*3 );
		sk.setJointPositions(skeleton_data);
		
		return sk;
	}
	
	// Custom draw method to track only hands
	@Override
	public void draw(GL2 gl) {
		if (isTracked()){
			double [] lw = get3DJoint(HAND_LEFT);
			double [] rw = get3DJoint(HAND_RIGHT);
			
		    gl.glColor3f(0f, 1f, 0f);
			gl.glEnable(GL2.GL_POINT_SMOOTH);
			gl.glPointSize(TRACKING_POINT_RADIUS);
			gl.glBegin(GL2.GL_POINTS);
				gl.glVertex3d(-lw[0], lw[1], -lw[2]);
				gl.glVertex3d(-rw[0], rw[1], -rw[2]);
			gl.glEnd();
		}
	}
}
