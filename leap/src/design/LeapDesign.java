package design;

import javax.swing.JFrame;

import com.leapmotion.leap.Controller;

import core.PaintBase;

public class LeapDesign extends KinectDesign{
	LeapController leapMotion;
	Controller c;
	
	public LeapDesign(){
		/////////////////////////////////////////
		// DEBUG 
		//new Thread(new MultiUserTest()).start();
		/////////////////////////////////////////
	}
	
	@Override
	protected void setUpDevice() {
		System.out.println("Setup device called");
		leapMotion = new LeapController(this);
		c = new Controller(leapMotion);
	}
	
	public static void main(String [] args){
		new LeapDesign();
	}
}
