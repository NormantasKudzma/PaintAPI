package design;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

public class AndroidDesign extends PcDesign{
	public AndroidDesign() {
		super();
		(new Thread(new CommServer(this))).start();
	}
	
	public void processAction(String str){
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
	
	public String getImageBytes(){
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ImageIO.write(drawing, "jpg", byteStream);
			byteStream.flush();
			String arr = new String(byteStream.toByteArray());
			byteStream.close();
			System.out.println("Returned some array..");
			return arr;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Returned null");
		return null;
	}
	
	public static void main(String [] args){
		new AndroidDesign();
	}
}
