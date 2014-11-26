package design;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Finger.Type;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

import design.KinectDesign;
import design.LeapDesign;
import design.Smoothing;

public class LeapController extends Listener implements Runnable{
	public static final double DRAW_TRESHOLD = 70;
	public static final int X_SENSITIVITY = 250;	// X sensitivity (inverted)
	public static final int Y_SENSITIVITY = 280;	// Y sensitivity (inverted)
	
	// Right hand correction , left hand correction
	public static final int Y_DELTA_CORRECTION = -300;
	public static final int [] X_DELTA_CORRECTION = new int[]{300, 1200};
	
	double [][][] trend = new double[2][7][2]; //index x y
	boolean isDrawing [] = new boolean[]{false, false};
	long sleepInterval = 33;
	long gestureInterval = 100;
	long lastGesture [] = new long[2];
	
	LeapDesign d;
	Controller c;
	boolean done = false;;
	
	public LeapController(LeapDesign d){
		this.d = d;
	}
	
	public void setController(Controller c){
		this.c = c;
	}
	
	@Override
	public void onInit(Controller arg0) {
		System.out.println("Leapmotion init");
	}
	
	@Override
	public void onConnect(Controller arg0) {
		System.out.println("Leapmotion connected");
	}
	
	@Override
	public void onDisconnect(Controller arg0) {
		System.out.println("Leapmotion disconnected");
	}
	
	@Override
	public void onExit(Controller arg0) {
		System.out.println("Leapmotion exited");
	}
	
	public boolean isDone(){
		return done;
	}
	
	public void isDone(boolean done){
		this.done = done;
	}
	
	@Override
	public void run() {
		while (!done){
			try {
				Thread.sleep(sleepInterval);
				performActions(c);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	void performActions(Controller c){
		Frame f = c.frame();
		// Set image for videoPanel
//		d.setImage(f.images().get(0));
		
		// Grab gestures and hand motions, process them
		Gesture g = f.gestures().get(0);
		Hand gestureHand = g != null ? g.hands().get(0) : null;
		HandList hands = f.hands();
		for (Hand hand : hands){
			int index = hand.isLeft() ? 1 : 0;
			if (gestureHand != null && gestureHand.id() == hand.id() && g.type() == Gesture.Type.TYPE_SWIPE){
				long time = System.currentTimeMillis();
				if (lastGesture[index] < time){
					SwipeGesture sg = new SwipeGesture(g);
					float x = sg.direction().getX();
					int dir = x > 0 ? 1 : -1;
					d.nextColor(index, dir);
				}
				lastGesture[index] = time + gestureInterval;
			}
			int closedFingers = 0;
			Vector hPos = hand.palmPosition();
			FingerList fingers = hand.fingers();
			Finger drawFinger = null;
			for (Finger finger : fingers){
				if (finger.type() != Type.TYPE_INDEX){
					if (distance(finger.tipPosition(), hPos) < DRAW_TRESHOLD){
						closedFingers++;
					}
				}
				else {
					drawFinger = finger;
				}
			}
			
			// Position the mouse, then check if it should fire any events

			if (drawFinger != null){
				double [] pos = new double[]{drawFinger.tipPosition().getX(), drawFinger.tipPosition().getY()};
				pos = Smoothing.doubleAverageSmoothing(pos, trend[index]);
				trend[index] = matrixPush(trend[index]);
				trend[index][0][0] = pos[0];
				trend[index][0][1] = pos[1];
				pos = Smoothing.exponentialSmoothing(pos, trend[index]);
				
				d.thisX[index] = convertX(index, pos[0]);
				d.thisY[index] = convertY(pos[1]);
				d.glass.repaint();
				
				if (closedFingers > 2){
					if (isDrawing[index]){
						d.dispatchMouseDrag(index);
					}
					else {
						d.dispatchMouseClick(index);
						isDrawing[index] = true;
					}
				}
				else {
					if (isDrawing[index]){
						isDrawing[index] = false;
						d.dispatchMouseRelease(index);
					}
				}
			}
		}
	}
	
	@Override
	public void onFrame(Controller c) {
	}
	
	private double[][] matrixPush(double [][] m){
		for (int i = m.length - 1; i > 0; i--){
			m[i] = m[i-1];
		}
		return m;
	}
	
	public int convertX(int index, double x){
		int newX = 0;
		int fw = KinectDesign.frameWidth;
		double coef = 1.0 * fw / X_SENSITIVITY;	
		newX = (int) (coef * x + X_DELTA_CORRECTION[index]);
		newX = Math.max(0, Math.min(fw, newX));
		return newX;
	}
	
	public int convertY(double y){
		int newY = 0;
		int fh = KinectDesign.frameHeight;
		double coef = 1.0 * fh / Y_SENSITIVITY;	
		newY = (int) (coef * y + Y_DELTA_CORRECTION);
		newY = fh - Math.max(0, Math.min(fh, newY));
		return newY;
	}
	
	public double distance(Vector v1, Vector v2){
		double dist = Math.sqrt(Math.pow(v2.getX() - v1.getX(), 2) 
							  + Math.pow(v2.getY() - v1.getY(), 2) 
							  + Math.pow(v2.getZ() - v1.getZ(), 2));
		return dist;
	}
}
