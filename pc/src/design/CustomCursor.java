package design;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

public class CustomCursor {
	
	public static Cursor pickerCursor;
	protected static final String RES_PATH = "res/";
	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();  
		Image image = toolkit.getImage(RES_PATH + "pipe.png");  
		Point hotSpot = new Point(0,0);
		pickerCursor = toolkit.createCustomCursor(image, hotSpot, "Pipe");  
	}
}
