package designs;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import api.PaintGUI;

public class PcDesign extends JFrame{
	private static int frameWidth = 1280;
	private static int frameHeight = 720;
	
	private JMenuBar menuBar;
	private JPanel drawPanel;
	private JPanel topPanel;
	private PaintGUI paint;
	
	public PcDesign(){
		this(frameWidth, frameHeight);
		
	}
	
	public PcDesign(int frameW, int frameH){
		initFrame(frameW, frameH);
		
	}
	
	private void initFrame(int w, int h){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(w, h);
		setTitle("Paint for PC");
		setLocationRelativeTo(null);
		initLayout();
		// Set visible *ONLY* after initializing all components
		setVisible(true);
	}
	
	private void initLayout(){
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		topPanel = new JPanel();
		//topPanel.setMaximumSize(new Dimension(width, (int)()));
		
		drawPanel = new JPanel();
		drawPanel.setMaximumSize(new Dimension());
		configureMenuBar();
	}
	
	private void configureMenuBar(){
		// STUB
	}
	
	public static void main(String [] args){
		new PcDesign();
	}
}
