package design;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * Class, which creates different cursor icons for each tool available
 */
public class CustomCursor {
	protected static final String RES_PATH = "res/";
	public static Cursor PICKER_CURSOR;
	public static Cursor BASIC_CURSOR;
	public static Cursor BUCKET_CURSOR;
	public static Cursor LINE_CURSOR;
	
	private static String [] filenames = new String[]{"pencil", "strline", "bucket", "pipe"};
	private static Cursor [] cs = new Cursor[4];
	static {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit(); 
			ClassLoader cl = CustomCursor.class.getClassLoader();
			BufferedImage image;
			Point hotSpot = new Point(0,0);
			for (int i = 0; i < cs.length; i++){
				image = ImageIO.read(cl.getResource(RES_PATH + filenames[i] + ".png"));
				cs[i] = toolkit.createCustomCursor(image, hotSpot, filenames[i]);
			}
			
			BASIC_CURSOR = cs[0];
			LINE_CURSOR = cs[1];
			BUCKET_CURSOR = cs[2];
			PICKER_CURSOR = cs[3];
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
