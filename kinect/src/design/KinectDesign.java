package design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class KinectDesign extends PcDesign {	
	Kinect k;
	VideoPanel videoPanel;
	BufferedImage fakeMouse;
	JPanel glass;
	JPanel rightPanel;
	
	BufferedImage cursor;
	BufferedImage cursorActive;
	public int thisX, thisY;
	int sidePanelWidth;		// pakeisti i ploti ir tikrint abiem pusem
	int menuHeight = 64;	// irgi peles kontrolei svarbus

	public KinectDesign(){	
		setVisible(false);					// Hide while initializing
		videoPanel = new VideoPanel();
		cl = getClass().getClassLoader();	// Class loader must be reinstantiated (different paths)
		
		thisX = frameWidth / 2;
		thisY = (int) (frameHeight *0.75);		
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		setTitle("The amazing paint for PC + Microsoft Windows® Kinect™ v1, v0.101");
		setUpMenuBar();
		setUpKinect();
		
		setVisible(true);		// Setvisible before resizing to calculate new max sizes
		setUpGlassPane();
		setUpPanels();

		initDrawPanel(imgW, imgH);
		createNewImage(imgW, imgH);		
	}
	
	protected void setUpMenuBar(){
		Border genericBorder = new LineBorder(Color.black, 1);
		Font genericFont = new Font("Courier", Font.PLAIN, 30);
		Dimension d = new Dimension(menuHeight * 2, menuHeight);
		
		JMenuBar menubar = getJMenuBar();
		menubar.setPreferredSize(new Dimension(frameWidth, menuHeight));
		for (int i = 0; i < menubar.getMenuCount(); i++){
			JMenu menu = menubar.getMenu(i);
			menu.setBorder(genericBorder);
			menu.setFont(genericFont);
			menu.setPreferredSize(d);
			for (int j = 0; j < menu.getItemCount(); j++){
				JMenuItem item = menu.getItem(j);
				item.setBorder(genericBorder);
				item.setFont(genericFont);
				item.setPreferredSize(new Dimension(item.getPreferredSize().width, d.height));
			}
		}
	}
	
	protected void setUpKinect(){
		k = new Kinect();				
		if (k.start(true, Kinect.NUI_IMAGE_RESOLUTION_80x60, Kinect.NUI_IMAGE_RESOLUTION_640x480) == 0){
			System.out.println("Error starting kinect.");
		}
		k.computeUV(true);	// show video?
		//k.startSkeletonTracking(true); // track only head + hands? (not needed anymore)
		k.setNearMode(true);
		k.videoPanel = videoPanel;
		k.k = this;
	}
	
	protected void setUpGlassPane(){
		try {
			cursor = ImageIO.read(cl.getResource(RES_PATH + "cursor.png"));
			cursorActive = ImageIO.read(cl.getResource(RES_PATH + "cursorActive.png")); 
			fakeMouse = cursor;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		glass = new JPanel(){
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(fakeMouse, thisX, thisY, null);
			};
		};
		this.setGlassPane(glass);
		glass.setVisible(true);
		glass.setOpaque(false);
		glass.repaint();
	}
	
	protected void setUpPanels(){
		// Change the layout first of all
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		
		// Resize old components, so there's place for video frame
		Component [] c = topPanel.getComponents();		
		sidePanelWidth = (int) (this.getWidth() * 0.12f);
		
		// New dimensions for side panels
		Dimension maxSidePanelDim = new Dimension(sidePanelWidth, (int) (frameHeight * 0.95f));
		topPanel.setMaximumSize(maxSidePanelDim);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		
		int maxW = (int) (sidePanelWidth * 0.95f);
		int maxH = (int) (frameHeight * 0.95f);
		Dimension maxInnerPanelDim = new Dimension(maxW, maxH);
		
		frameWidth = this.getWidth();
		frameHeight = this.getHeight();
		imgW = (int) (frameWidth - 2 * sidePanelWidth * 0.99f);
		imgH = (int) (frameHeight * 0.95f - getJMenuBar().getHeight());
		drawContainerPanel.setMaximumSize(new Dimension(imgW, imgH));
		
		// Resize color chooser panel and swap swatches for our custom color chooser
		KinectColorChooser [] kcc = {new KinectColorChooser(maxInnerPanelDim)};	
		JColorChooser jcc = (JColorChooser)((JPanel)c[0]).getComponent(0);
		jcc.setChooserPanels(kcc);
		jcc.setMaximumSize(maxInnerPanelDim);
		c[0].setMaximumSize(maxSidePanelDim);

		// Changing the old size chooser (new is buttons, easier to use)
		JPanel opts = ((JPanel)c[1]);
		opts.remove(1);
		opts.remove(0);
		opts.setMaximumSize(new Dimension(maxInnerPanelDim.width, (int) (maxInnerPanelDim.height * 0.5f)));
		JPanel size = new JPanel();
		opts.add(size, 0);
		size.setLayout(new GridLayout(3, 1));
		final JLabel sizeLabel = new JLabel("8", SwingConstants.CENTER);
		sizeLabel.setBorder(new LineBorder(Color.black));
		sizeLabel.setFont(new Font("Courier", Font.BOLD, 42));
		JButton minus = new JButton("-");
		minus.setFont(new Font("Courier", Font.BOLD, 60));
		minus.addMouseListener(new core.LightweightMouseListener(){
			@Override
			public void mousePressed(MouseEvent e) {
				int size = Integer.parseInt(sizeLabel.getText());
				if (size > 4){
					size /= 2;
					sizeLabel.setText("" + size);
					updateBrush(size);
				}
			}
		});
		JButton plus = new JButton("+");
		plus.setFont(new Font("Courier", Font.BOLD, 60));
		plus.addMouseListener(new core.LightweightMouseListener(){
			@Override
			public void mousePressed(MouseEvent e) {
				int size = Integer.parseInt(sizeLabel.getText());
				if (size < 256){
					size *= 2;
					sizeLabel.setText("" + size);
					updateBrush(size);
				}
			}
		});
		updateBrush(8);				
		size.add(plus);
		size.add(sizeLabel);
		size.add(minus);
		opts.revalidate();
		opts.repaint();
		
		// New - rightside panel, top panel becomes leftside panel
		rightPanel = new JPanel();
		topPanel.remove(c[1]);
		topPanel.remove(c[2]);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setMaximumSize(maxSidePanelDim);
		videoPanel.setMaximumSize(new Dimension(maxSidePanelDim.width, maxSidePanelDim.width));
		rightPanel.add(videoPanel);
		rightPanel.add(c[1]);
		rightPanel.add(c[2]);
		add(rightPanel);
		
		rightPanel.revalidate();
		rightPanel.repaint();
		topPanel.revalidate();
		topPanel.repaint();
	}
	
	protected void dispatchMouseClick(){
		MouseEvent e;
		if (drawContainerPanel.getBounds().contains(thisX, thisY)){
			e = new MouseEvent(this.drawContainerPanel, MouseEvent.MOUSE_PRESSED, 0, 0, 
					thisX - sidePanelWidth, thisY - menuHeight, 1, false);
			drawPanel.dispatchEvent(e);
		}
		else {
			e = new MouseEvent(this.glass, MouseEvent.MOUSE_PRESSED, 0, 0, thisX, thisY, 1, false);
			this.dispatchEvent(e);
		}
		if (fakeMouse != cursorActive){
			fakeMouse = cursorActive;
		}
	}
	
	protected void dispatchMouseDrag(){		
		if (drawContainerPanel.getBounds().contains(thisX, thisY)){
			MouseEvent e = new MouseEvent(this.glass, MouseEvent.MOUSE_DRAGGED, 0, 0,
					thisX - sidePanelWidth, thisY - menuHeight, 1, false);
			drawPanel.dispatchEvent(e);
		}
		if (fakeMouse != cursorActive){
			fakeMouse = cursorActive;
		}
	}
	
	protected void dispatchMouseRelease(){
		fakeMouse = cursor;
	}
	
	public static void main(String[] args){
		new KinectDesign();
	}
}
