package design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import core.LightweightMouseListener;


public class KinectDesign extends PcDesign {	
	Kinect k;
	VideoPanel videoPanel;
	BufferedImage [] fakeMouse = new BufferedImage[2];
	JPanel rightPanel;
	
	BufferedImage [] cursor = new BufferedImage[2];
	BufferedImage [] cursorActive = new BufferedImage[2];
	protected int thisX[] = new int[2], thisY[] = new int[2];
	int sidePanelWidth;		// pakeisti i ploti ir tikrint abiem pusem
	int menuHeight = 68;	// irgi peles kontrolei svarbus	
	
	public KinectDesign(){	
		setVisible(false);					// Hide while initializing
		videoPanel = new VideoPanel();
		cl = getClass().getClassLoader();	// Class loader must be reinstantiated (different paths)
		
		thisX[0] = frameWidth / 2;
		thisY[0] = (int) (frameHeight *0.75);
		thisX[1] = -50;
		thisY[1] = -50;
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		setTitle("The amazing paint for PC + Microsoft Windows® Kinect™ v1, v0.101");		
		setUpKinect();
		
		setVisible(true);		// Setvisible before resizing to calculate new max sizes
		setUpMenuBar();
		initGlassPanel();
		setUpPanels();

		initDrawPanel(imgW, imgH);
		createNewImage(imgW, imgH);	
		//
		System.out.println();
	}
	
	protected void setUpMenuBar(){
		Dimension separatorDim = new Dimension(menuHeight / 2, menuHeight / 2);
		
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);
		
		try {
			// File handling
			JButton newfile = new JButton(new ImageIcon(ImageIO.read(cl.getResource(RES_PATH + "newfile.png"))));
			newfile.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					initDrawPanel(imgW, imgH);
					createNewImage(imgW, imgH);
				}
			});
			toolbar.add(newfile);
			
			JButton savefile = new JButton(new ImageIcon(ImageIO.read(cl.getResource(RES_PATH + "savefile.png"))));
			savefile.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					// SAVE WITH GENERIC NAME
				}
			});
			toolbar.add(savefile);
			
			JButton loadfile = new JButton(new ImageIcon(ImageIO.read(cl.getResource(RES_PATH + "loadfile.png"))));
			loadfile.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					// LOAD WITH GENERIC NAME
				}
			});
			toolbar.add(loadfile);
			
			toolbar.addSeparator(separatorDim);
			// Tools
			ButtonGroup tools = new ButtonGroup();
			
			final JToggleButton pencil = new JToggleButton(new ImageIcon(ImageIO.read(cl.getResource(RES_PATH + "pencil.png"))));
			pencil.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					setTool(Tools.BASIC);
					pencil.setSelected(true);
				}
			});
			toolbar.add(pencil);
			tools.add(pencil);
			tools.setSelected(pencil.getModel(), true);
			
			final JToggleButton strline = new JToggleButton(new ImageIcon(ImageIO.read(cl.getResource(RES_PATH + "strline.png"))));
			strline.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					setTool(Tools.STRLINE);
					strline.setSelected(true);
				}
			});
			toolbar.add(strline);
			tools.add(strline);
			
			final JToggleButton bucket = new JToggleButton(new ImageIcon(ImageIO.read(cl.getResource(RES_PATH + "bucket.png"))));
			bucket.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					setTool(Tools.BUCKET);
					bucket.setSelected(true);
				}
			});
			toolbar.add(bucket);
			tools.add(bucket);
			
			toolbar.addSeparator(separatorDim);
			// Edit functions
			JButton undo = new JButton(new ImageIcon(ImageIO.read(cl.getResource(RES_PATH + "undo.png"))));
			undo.addMouseListener(new LightweightMouseListener(){
				@Override
				public void mousePressed(MouseEvent e) {
					undo();
				}
			});
			toolbar.add(undo);
		}
		catch (Exception e){
			e.printStackTrace();
		}	
		
		// Add everything to JMenuBar
		JMenuBar menubar = getJMenuBar();		
		menubar.setPreferredSize(new Dimension(frameWidth, menuHeight));
		menubar.removeAll();
		menubar.revalidate();
		menubar.add(toolbar);
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
	
	@Override
	protected void initGlassPanel(){
		super.initGlassPanel();
		cursor = new BufferedImage[2];
		cursorActive = new BufferedImage[2];
		fakeMouse = new BufferedImage[2];
		try {
			for (int i = 0; i < cursor.length; i++){
				cursor[i] = ImageIO.read(cl.getResource(RES_PATH + "cursor" + (i+1) + ".png"));
				cursorActive[i] = ImageIO.read(cl.getResource(RES_PATH + "cursorActive" + (i+1) + ".png"));
				fakeMouse[i] = cursor[i];
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		glass = new JPanel(){
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (int i = 0; i < thisX.length; i++){
					g.drawImage(fakeMouse[i], thisX[i], thisY[i], null);
				}
				g.drawImage(glassImage, 0, 0, null);
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
		//drawContainerPanel.setBounds(sidePanelWidth, menuHeight, imgW, imgH);
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
	
	protected void dispatchMouseClick(boolean mainSkeleton){
		switchCursors(mainSkeleton, cursorActive);

		int index = mainSkeleton ? 1 : 0;
		MouseEvent e = new MouseEvent(this.glass, MouseEvent.MOUSE_PRESSED, 0, 0, thisX[index], thisY[index], 1, false);
		dispatchEvent(e);
	}
	
	protected void dispatchMouseDrag(boolean mainSkeleton){		
		// DISABLED DRAG CURRENTLY
//		if (drawContainerPanel.getBounds().contains(thisX, thisY)){
//			// What source?
//			MouseEvent e = new MouseEvent(this.drawContainerPanel, MouseEvent.MOUSE_DRAGGED, 0, 0,
//					thisX - sidePanelWidth, thisY - menuHeight, 1, false);
//			drawPanel.dispatchEvent(e);
//		}
//		
//		switchCursors(mainSkeleton, cursorActive);
	}
	
	protected void dispatchMouseRelease(boolean mainSkeleton){
		switchCursors(mainSkeleton, cursor);
		
		int index = mainSkeleton ? 1 : 0;
		MouseEvent e = new MouseEvent(this.glass, MouseEvent.MOUSE_RELEASED, 0, 0, thisX[index], thisY[index], 1, true);
		dispatchEvent(e);
	}
	
	protected void switchCursors(boolean mainSkeleton, BufferedImage [] arr){
		if (mainSkeleton){
			if (fakeMouse[0] != arr[0]){
				fakeMouse[0] = arr[0];
			}
		}
		else {
			if (fakeMouse[1] != arr[1]){
				fakeMouse[1] = arr[1];
			}
		}
	}
	
	public static void main(String[] args){
		new KinectDesign();
	}
}
