package design;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

public class AndroidDesign extends PcDesign{
	public AndroidDesign() {
		super();
		(new Thread(new CommServer(this))).start();
	}
	
	public synchronized void processAction(String str){
		System.out.println("Draw called " + str);
		String [] arr = str.split(" ");
		int [] coords = new int[arr.length];
		for (int i = 0; i < arr.length; i++){
			coords[i] = Integer.parseInt(arr[i]);
		}
		if (coords.length > 2){
			dispatchMouseDrag(coords);
		}
		else {
			dispatchMouseClick(coords);
		}
	}
	
	public void dispatchMouseDrag(int [] arr){
		paint.drawCenteredLine(arr[0], arr[1], arr[2], arr[3]);
		drawContainerPanel.repaint();
	}
	
	public void dispatchMouseClick(int [] arr){
		paint.drawCenteredPixel(arr[0], arr[1]);
		drawContainerPanel.repaint();
	}
	
	public synchronized String getImageBytes(){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(drawing, "jpg", baos);
			baos.flush();
			String arr = new String(baos.toByteArray());
			baos.close();
			return arr;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String [] args){
		new AndroidDesign();
	}
}
