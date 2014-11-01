package design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.LightweightMouseListener;
import core.PaintBase;


public class KinectDesign extends PcDesign {	
	Kinect k;
	VideoPanel videoPanel;
	BufferedImage [] fakeMouse = new BufferedImage[2];
	JPanel rightPanel;
	
	BufferedImage [] cursor = new BufferedImage[2];
	BufferedImage [] cursorActive = new BufferedImage[2];
	protected int thisX[] = new int[2], thisY[] = new int[2];
	protected int lastX[] = new int[2], lastY[] = new int[2];
	protected int oldX[] = new int[2], oldY[] = new int[2];
	int sidePanelWidth;		// pakeisti i ploti ir tikrint abiem pusem
	int menuHeight = 68;	// irgi peles kontrolei svarbus	
	int xdc;				// Mouse drag x correction
	int ydc;				// Mouse drag y correction
	int xcc = 8;				// Mouse x click corr
	int ycc = 24;				// Mouse y click corr
	
	PaintBase paint2;	// constructed on the same graphics object, second user can have their own size/shape/clr etc.
	
	public class MultiUserTest implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(2500);				
				// FIRST USER TESTS
				// 130;300 red color
				thisX[0] = 130; thisY[0] = 300;
				dispatchMouseClick(0);
				Thread.sleep(250);
				thisX[0] = 300; thisY[0] = 690;
				dispatchMouseClick(0);
				
				Thread.sleep(500);
				thisX[0] = 600;
				Thread.sleep(250);
				dispatchMouseDrag(0);
				Thread.sleep(250);
				dispatchMouseRelease(0);
				Thread.sleep(250);
				
				// SECOND USER TEST
				thisX[1] = 130; thisY[1] = 380;
				dispatchMouseClick(1);
				Thread.sleep(250);
				thisX[1] = 300; thisY[1] = 690;
				dispatchMouseClick(1);
				
				Thread.sleep(500);
				thisX[1] = 600;
				Thread.sleep(250);
				dispatchMouseDrag(1);
				Thread.sleep(250);
				dispatchMouseRelease(1);
				Thread.sleep(250);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}		
	}
	
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
		
		paint2 = new PaintBase(paint.getGraphics());
		setVisible(true);		// Setvisible before resizing to calculate new max sizes
		setUpMenuBar();
		initGlassPanel();
		setUpPanels();

		initDrawPanel(imgW, imgH);
		createNewImage(imgW, imgH);

		/////////////////////////////////////////
		// DEBUG 
		//new Thread(new MultiUserTest()).start();
		/////////////////////////////////////////
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
		final KinectColorChooser [] kcc = {new KinectColorChooser(maxInnerPanelDim)};	
		JColorChooser jcc = (JColorChooser)((JPanel)c[0]).getComponent(0);
		jcc.getSelectionModel().removeChangeListener(((DefaultColorSelectionModel)jcc.getSelectionModel()).getChangeListeners()[0]);
		jcc.getSelectionModel().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				Color [] clrs = kcc[0].getSelectedColors();
				paint.setBrushColor(clrs[0]);
				paint2.setBrushColor(clrs[1]);
			}		
		});
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
		final JLabel sizeLabel = new JLabel("16", SwingConstants.CENTER);
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
		updateBrush(16);
		size.add(plus);
		size.add(sizeLabel);
		size.add(minus);
		opts.revalidate();
		opts.repaint();
		
		// Overriding drawpanel's mouse listener (for multiuser support)
		// Even though the logic is pretty much the same except calling paint methods from
		// two different PaintBase objects (different colors and shapes/sizes if wanted)
		
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
		
		xdc = sidePanelWidth;
		ydc = menuHeight + drawPanel.getBounds().y;
	}
	
	// Passing player's index via `time` parameter (use getWhen() to parse)
	// if using mouse to draw - you'll be treated as P1, but select colors as P2
	// this means you're not really able to choose colors with mouse.
	// Explanation - getWhen returns time, called from mouse this will be unix time
	// value.
	protected void dispatchMouseClick(int index){
		switchCursors(index, cursorActive);

		MouseEvent e = new MouseEvent(this.glass, MouseEvent.MOUSE_PRESSED, index, 0, thisX[index] + xcc, thisY[index] + ycc, 1, false);
		dispatchEvent(e);
		oldX[index] = lastX[index] = thisX[index];
		oldY[index] = lastY[index] = thisY[index];
	}
	
	// Logical override of mouseDragged in pcdesign (don't pass too many mousedrag events..)
	protected void dispatchMouseDrag(int index){
		if (thisX[index] > sidePanelWidth && thisX[index] < frameWidth - sidePanelWidth && thisY[index] > menuHeight){
			switch(tool) {
				case BASIC:{						
					if (index == 0) {
						paint.drawCenteredLine(lastX[index] - xdc, lastY[index] - ydc, thisX[index] - xdc, thisY[index] - ydc);	
					}
					else {
						paint2.drawCenteredLine(lastX[index] - xdc, lastY[index] - ydc, thisX[index] - xdc, thisY[index] - ydc);	
					}
					break;
				}
				case STRLINE:{
					drawLineToolLine(oldX[index] - xdc, oldY[index] - ydc, lastX[index] - xdc, lastY[index] - ydc);
					break;
				}
			}
			lastX[index] = thisX[index];
			lastY[index] = thisY[index];
		}
		
		switchCursors(index, cursorActive);
	}
	
	protected void dispatchMouseRelease(int index){
		switchCursors(index, cursor);
		
		MouseEvent e = new MouseEvent(this.glass, MouseEvent.MOUSE_RELEASED, index, 0, thisX[index] + xcc, thisY[index] + ycc, 1, true);
		dispatchEvent(e);
	}
	
	protected void switchCursors(int index, BufferedImage [] arr){
		if (fakeMouse[index] != arr[index]){
			fakeMouse[index] = arr[index];
		}
	}
	
	@Override
	protected void updateBrush(int size, double rotation, String type) {
		super.updateBrush(size, rotation, type);
		paint2.setBrushSize(paint.getBrushSize());
		paint2.setBrushRotation(paint.getBrushRotation());
		paint2.setCustomStroke(paint.getCustomStroke());
	}
	
	// Overriding this method to assign new mouse listeners to paint2 object
	@Override
	protected void initDrawPanel(int w, int h) {
		super.initDrawPanel(w, h);
		drawPanel.removeMouseListener(drawPanel.getMouseListeners()[0]);
		drawPanel.addMouseListener(new LightweightMouseListener(){

			@Override
			public void mouseReleased(MouseEvent e) {
				int index = e.getWhen() > 0 ? 1 : 0;
				PaintBase paintObj = index == 0 ? paint : paint2;
				switch(tool){
					case STRLINE:
						paintObj.drawCenteredLine(oldX[index] - xdc, oldY[index] - ydc, lastX[index] - xdc, lastY[index] - ydc);
						drawLineToolLine(-1, -1, -1, -1);
						break;
				}
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				int index = e.getWhen() > 0 ? 1 : 0;
				PaintBase paintObj = index == 0 ? paint : paint2;
				BufferedImage img = cloneImage();
				undoHistory.push(img);
				int x = e.getX(), y = e.getY();	
				switch(tool){
					case BUCKET:{
						int [] arr = new int[drawing.getWidth() * drawing.getHeight()];
						int w = drawing.getWidth(), h = drawing.getHeight();
						arr = drawing.getRGB(0, 0, drawing.getWidth(), drawing.getHeight(), arr, 0, drawing.getWidth());
						int arrr [] = arr;
						paintObj.fill(x, y, drawing.getRGB(x, y), paintObj.getBrushColor().getRGB(), arrr, w, 0);
						drawing.setRGB(0, 0, w, h, arrr, 0, w);
						drawPanel.repaint();
						break;
					}						
					case PICKER:{
						Color c = new Color(drawing.getRGB(x, y));
						paintObj.setBrushColor(c);
						break;
					}
					case BASIC:{
						mouseClicked(e);
						break;
					}
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = e.getWhen() > 0 ? 1 : 0;
				PaintBase paintObj = index == 0 ? paint : paint2;
				paintObj.drawCenteredPixel(e.getX(), e.getY());
				drawPanel.repaint();
			}
		});
	}
	
	// Overriding method to assign new graphics object to paint2 object
	@Override
	public void createImageFrom(BufferedImage b) {
		super.createImageFrom(b);
		if (paint2 != null){
			paint2.setGraphics((Graphics2D) b.getGraphics());
		}
	}
	
	public static void main(String[] args){
		new KinectDesign();
	}
}
