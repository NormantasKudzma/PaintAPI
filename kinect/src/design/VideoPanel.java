package design;

import javax.media.opengl.GL2;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.VideoFrame;
import edu.ufl.digitalworlds.opengl.OpenGLPanel;

public class VideoPanel extends OpenGLPanel{
	private DepthMap map = null;
	private VideoFrame videoTexture;
	
	@Override
	public void draw() {
		GL2 gl = getGL2();
				
		pushMatrix();
		
		if (map != null){
			gl.glDisable(GL2.GL_LIGHTING);
    		gl.glEnable(GL2.GL_TEXTURE_2D);
    		gl.glColor3f(1f,1f,1f);
    		videoTexture.use(gl);
    		map.drawTexture(gl);
    		gl.glDisable(GL2.GL_TEXTURE_2D);
		}
		
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
	    
	    gl.glDisable(GL2.GL_LIGHTING);
	    gl.glLineWidth(2);
	    gl.glColor3f(1f,0f,0f);
	    
	    popMatrix();
	}
}
