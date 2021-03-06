package design;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.CustomStroke;
import core.Filters;
import core.LightweightMouseListener;
import core.PaintBase;
import core.RectShape;
import core.Stack;
import core.StarShape;
import core.TriangleShape;

/**
 * A class, responsible for PC GUI.
 */
public class PcDesign extends JFrame{
	/**
	 * A lightweight mouse listener, used for listening to shape changes
	 */
	public class BrushShapeListener extends LightweightMouseListener {
		private String shapeName;
		private Container parent;
		
		public BrushShapeListener(String shapeName, Container p){
			this.shapeName = shapeName;
			parent = p;
		}
		
		public String getShapeName(){
			return shapeName;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			updateBrush(shapeName);
			parent.dispatchEvent(e);
		}
	}
	
	// Window and other variables
	protected static int frameWidth = 1280;
	protected static int frameHeight = 720;
	protected static final String RES_PATH = "res/";
	
	// Menu related variables
	protected JMenuBar menuBar;
	protected JPanel drawContainerPanel;
	protected JPanel drawPanel;
	protected JPanel topPanel;

	// Glass panel variables
	protected JPanel glass;
	protected PaintBase glassPaint;
	protected BufferedImage glassImage;
        
	// Drawing related variables
	protected JColorChooser jcc;    
    protected File filetosave;
    protected File filetoload;
    protected Filters f = new Filters();
	protected PaintBase paint;
	protected BufferedImage drawing;
	protected ClassLoader cl = getClass().getClassLoader();
	protected String currentShape = "rect";
	
	// Image related variables
	protected int maxImgW = 0;
	protected int maxImgH = 0;
	protected int imgW = 0;
	protected int imgH = 0;
	protected static final int IMAGE_FORMAT = BufferedImage.TYPE_INT_ARGB;
	
	protected int lastX = 0;
	protected int lastY = 0;
	protected int thisX = 0;
	protected int thisY = 0;
	
	// Tool and undo-history related variables
	protected Stack<BufferedImage> undoHistory;
	protected Tools tool = Tools.BASIC;
	protected Cursor cursor = CustomCursor.BASIC_CURSOR;
	
	public PcDesign(){
		this(frameWidth, frameHeight);
	}
	
	public PcDesign(int frameW, int frameH){
		if (frameW >= frameWidth && frameH >= frameHeight){
			initFrame(frameW, frameH);		
		}
		else {
			initFrame(frameWidth, frameHeight);
		}
		paint = new PaintBase();
		initGlassPanel();
		initDrawPanel(imgW, imgH);
		createNewImage(imgW, imgH);
		undoHistory = new Stack<BufferedImage>();
	}
	
	/**
	 * Initializes a transparent (glass) panel over the top of all components. Used for temporary drawing (lines etc.)
	 */
	protected void initGlassPanel(){
		glass = new JPanel(){
			protected void paintComponent(Graphics g) {				
				super.paintComponent(g);
				g.drawImage(glassImage, 0, 0, null);
			};
		};

		glassImage = new BufferedImage(frameWidth, frameHeight, IMAGE_FORMAT);
		this.setGlassPane(glass);
		glass.setVisible(true);
		glass.setOpaque(false);
		glass.repaint();
		glassPaint = new PaintBase((Graphics2D) glassImage.getGraphics());
	}
	
	/**
	 * Method draws a temporary line on the glass panel (for better line tool experience)
	 * @param xy1 - from
	 * @param xy2 - to
	 */
	protected void drawLineToolLine(int x1, int y1, int x2, int y2) {		
		Rectangle r = drawContainerPanel.getBounds();
		Rectangle rr = drawPanel.getBounds();
		Rectangle rrr = getJMenuBar().getBounds();
		int x = r.x + rr.x;
		int y = r.y + rr.y + rrr.height;
		x1 += x;
		x2 += x;
		y1 += y;
		y2 += y;		
		
		glassPaint.getGraphics().setComposite(AlphaComposite.Clear);
		glassPaint.setBrushColor(new Color(0, true));
		glassPaint.getGraphics().fillRect(0, 0, frameWidth, frameHeight);

		glassPaint.getGraphics().setComposite(AlphaComposite.SrcOver);
		glassPaint.setBrush(paint.getBrush());
		glassPaint.drawCenteredLine(x1, y1, x2, y2);
		glass.repaint();
	}

	/**
	 * Initializes all window-related variables.
	 */
	protected void initFrame(int w, int h){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(w, h);
		setTitle("The amazing paint program for PC v0.1");
		setLocationRelativeTo(null);
		initLayout();
		setVisible(true);
	}
	
	/**
	 * Initializes component layout related variables
	 */
	protected void initLayout(){
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		initTopPanel();
		add(topPanel);		
					
		drawContainerPanel = new JPanel();
		drawContainerPanel.setMaximumSize(new Dimension(frameWidth, (int)(frameHeight * 0.75f)));
		add(drawContainerPanel);
		maxImgW = imgW = (int)(frameWidth * 0.95f);
		maxImgH = imgH = (int)(frameHeight * 0.7f);
		
		configureMenuBar();
	}
	
	/**
	 * Initializes all variables that are related to drawing panel
	 */
	protected void initDrawPanel(int w, int h){
        if (drawPanel != null){
			drawContainerPanel.remove(drawPanel);
			drawContainerPanel.revalidate();
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		drawing = null;
		
		imgW = w;
		imgH = h;
	
		drawPanel = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(drawing, 0, 0, null);
			}
		};		
		drawPanel.setPreferredSize(new Dimension(imgW, imgH));
		drawPanel.setBackground(Color.white);

		drawPanel.addMouseListener(new LightweightMouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				switch(tool){
					case STRLINE:
						paint.drawCenteredLine(lastX, lastY, e.getX(), e.getY());
						drawLineToolLine(-1, -1, -1, -1);
						break;
				}
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				BufferedImage img = cloneImage();
				undoHistory.push(img);
				int x = e.getX(), y = e.getY();	
				switch(tool){
					case BUCKET:{
						int [] arr = new int[drawing.getWidth() * drawing.getHeight()];
						int w = drawing.getWidth(), h = drawing.getHeight();
						arr = drawing.getRGB(0, 0, drawing.getWidth(), drawing.getHeight(), arr, 0, drawing.getWidth());
						int arrr [] = arr;
						paint.fill(x, y, drawing.getRGB(x, y), paint.getBrushColor().getRGB(), arrr, w, 0);
						drawing.setRGB(0, 0, w, h, arrr, 0, w);
						drawPanel.repaint();
						break;
					}						
					case PICKER:{
						Color c = new Color(drawing.getRGB(x, y));
						paint.setBrushColor(c);
						jcc.setColor(c);
						break;
					}
					case BASIC:{
						mouseClicked(e);
						break;
					}
				}
				lastX = x;
				lastY = y;
				thisX = x;
				thisY = y;			
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(cursor);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				paint.drawCenteredPixel(e.getX(), e.getY());
				drawPanel.repaint();
			}

		});
		drawPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				switch(tool) {
					case BASIC:{						
						float dist = (Math.abs(x - thisX) + Math.abs(y - thisY)) / 2f;
						float treshold = paint.getBrushSize() * 0.15f;
						if (dist > treshold){
							paint.drawCenteredLine(thisX, thisY, x, y);	
						}
						else {
							paint.drawCenteredPixel(x, y);
						}
						drawPanel.repaint();
						break;
					}
					case STRLINE:{
						drawLineToolLine(lastX, lastY, x, y);
						break;
					}
				}
				thisX = x;
				thisY = y;
			}
		});
		drawContainerPanel.add(drawPanel, BorderLayout.CENTER);
		drawContainerPanel.revalidate();				
		drawContainerPanel.repaint();
	}
	
	/**
	 * Creates top panel (for color/size/shape components)
	 */
	protected void initTopPanel(){
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.setMaximumSize(new Dimension(frameWidth, (int)(0.2 * frameHeight)));
		
		int maxHeight = (int)(topPanel.getMaximumSize().getHeight());
		int w = (int)topPanel.getMaximumSize().getWidth();
		
		// Color picker
		JPanel colorPicker = new JPanel();
		colorPicker.setMaximumSize(new Dimension((int)(w * 0.5), maxHeight));
		colorPicker.setBorder(BorderFactory.createTitledBorder("Color"));
		topPanel.add(colorPicker);
		
		jcc = new JColorChooser(Color.black);
		jcc.setColor(getForeground());
		AbstractColorChooserPanel [] acc = jcc.getChooserPanels();
		for (AbstractColorChooserPanel i : acc){
			if (!i.getDisplayName().equals("Swatches")){
				jcc.removeChooserPanel(i);
			}
		}
		jcc.setPreviewPanel(new JPanel());
		jcc.getSelectionModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				Color c = jcc.getColor();
				paint.setBrushColor(c);
			}
		});
		colorPicker.add(jcc);
		
		// Brush size picker
		JPanel brushSizePicker = new JPanel();
		brushSizePicker.setMaximumSize(new Dimension((int)(w * 0.25), maxHeight));
		brushSizePicker.setBorder(BorderFactory.createTitledBorder("Size"));
		brushSizePicker.setLayout(new GridLayout(0, 1));
		topPanel.add(brushSizePicker);
		
		Integer [] sizes = {1, 2, 4, 8, 12, 16, 24, 36, 48, 64, 128};
		JComboBox<Integer> sizebox = new JComboBox<Integer>(sizes);
		sizebox.setSelectedIndex(2);
		sizebox.setEditable(true);
		sizebox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				try {
					JComboBox<Integer> c = (JComboBox) e.getSource();	
					String Size = "" + c.getSelectedItem();
					int size = 0;
					try{	
						size = Integer.parseInt(Size);
						if (size > 0){
							updateBrush(size);
							return;
						}
					}
					catch(NumberFormatException er){ 	
					}
					c.getEditor().setItem(paint.getBrushSize());
						
				}
				catch (Exception ex){
					ex.printStackTrace();
				}
			}
		});
		brushSizePicker.add(sizebox);
		
		Double [] rotations = {0d, 30d, 60d, 90d, 120d, 150d, 180d, 210d, 240d, 270d, 300d, 330d};
		JComboBox<Double> rbox = new JComboBox<Double>(rotations);
		rbox.setSelectedIndex(0);
		rbox.setEditable(true);
		rbox.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JComboBox<Integer> c = (JComboBox) e.getSource();
					String Rotat = "" + c.getSelectedItem();
					
					try{ 
						 double i = Double.parseDouble(Rotat);
						 double r = (Double)c.getSelectedItem();
						 updateBrush(r);
					   }
					catch(NumberFormatException er){ 
						c.getEditor().setItem(paint.getBrushRotation());
					}
					
				}
				catch (Exception ex){
					ex.printStackTrace();
				}
			}		
		});
		brushSizePicker.add(rbox);
		
		// Brush shape picker
		JPanel brushShapePicker = new JPanel();
		brushShapePicker.setLayout(new GridLayout(2, 0, 5, 5));
		brushShapePicker.setMaximumSize(new Dimension((int)(w * 0.25), maxHeight));
		brushShapePicker.setBorder(BorderFactory.createTitledBorder("Shape"));
		topPanel.add(brushShapePicker);
			
		JLabel rect = new JLabel(new ImageIcon(cl.getResource(RES_PATH + "rect.png")));
		rect.addMouseListener(new BrushShapeListener("rect", brushShapePicker));
		rect.setOpaque(true);
		rect.setBackground(Color.orange);
		brushShapePicker.add(rect);
		
		JLabel circle = new JLabel(new ImageIcon(cl.getResource(RES_PATH + "circle.png")));
		circle.addMouseListener(new BrushShapeListener("circle", brushShapePicker));
		brushShapePicker.add(circle);
		
		JLabel star = new JLabel(new ImageIcon(cl.getResource(RES_PATH + "star.png")));
		star.addMouseListener(new BrushShapeListener("star", brushShapePicker));
		brushShapePicker.add(star);

		JLabel triangle = new JLabel(new ImageIcon(cl.getResource(RES_PATH + "triangle.png")));
		triangle.addMouseListener(new BrushShapeListener("triangle", brushShapePicker));
		brushShapePicker.add(triangle);
		
		final JLabel shapes [] = {rect, circle, star, triangle};
		
		brushShapePicker.addMouseListener(new BrushShapeListener("", null){
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					if (e.getSource() instanceof JLabel){
						JLabel highlight = ((JLabel)e.getSource());
					for (JLabel i : shapes){
						i.setOpaque(false);
						i.repaint();
					}
					highlight.setOpaque(true);
					highlight.setBackground(Color.orange);
					}					
				}
				catch (Exception ex){
					ex.printStackTrace();
				}
			}
		});
	}
	
	// Methods to update paint object's brush parameters
	protected void updateBrush(int size){
		updateBrush(size, paint.getBrushRotation(), currentShape);
	}
	
	protected void updateBrush(String type){
		updateBrush(paint.getBrushSize(), paint.getBrushRotation(), type);
	}
	
	protected void updateBrush(double rotation){
		updateBrush(paint.getBrushSize(), rotation, currentShape);
	}
	
	protected void updateBrush(int size, double rotation, String type){
		paint.setBrushSize(size);
		paint.setBrushRotation(rotation);
		currentShape = type;
		float spc = size * 0.1f;
		switch(type){
			case "rect":{
				paint.setCustomStroke(new CustomStroke(new RectShape(0, 0, size, size, rotation), spc));
				break;
			}
			case "circle":{
				paint.setCustomStroke(new CustomStroke(new Ellipse2D.Float(0, 0, size, size), spc));
				break;
			}		
			case "star":{
				paint.setCustomStroke(new CustomStroke(new StarShape(0, 0, size, size, rotation), spc));
				break;
			}
			case "triangle":{
				paint.setCustomStroke(new CustomStroke(new TriangleShape(0, 0, size, size, rotation), spc));
				break;
			}
		}
	}
	
	/**
	 * Creates a menu bar and adds all menu items
	 */
	protected void configureMenuBar(){
		JMenu file = new JMenu("File..");
		menuBar.add(file);
		
		JMenuItem newFile = new JMenuItem("New");
		newFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				promptCreateNewImage();
			}
		});
		KeyStroke ctrlN = KeyStroke.getKeyStroke("control N");
	    newFile.setAccelerator(ctrlN);
		file.add(newFile);
		
		JMenuItem loadFile = new JMenuItem("Load");
		loadFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = throwUnsavedWarning();
				if (result == JOptionPane.NO_OPTION){
					return;
				}
				
				JFileChooser c = new JFileChooser();
                                
                // Add extension filters to JFileChooser
                c.addChoosableFileFilter(f. new jpgSaveFilter());
                c.addChoosableFileFilter(f. new pngSaveFilter());
                
                c.setDialogTitle("Load drawing");
                int rVal = c.showOpenDialog(PcDesign.this);

                // Open pressed
                if(rVal==JFileChooser.APPROVE_OPTION) {
                    filetoload = c.getSelectedFile();
                    BufferedImage img = null;
                    
                    try{
                        img = ImageIO.read(filetoload);
                        initDrawPanel(img.getWidth(), img.getHeight());
                        createImageFrom(img);
                    }
                    catch (Exception y){
                        y.printStackTrace();
                    }
                }
			}
		});
		KeyStroke ctrlO = KeyStroke.getKeyStroke("control O");
                loadFile.setAccelerator(ctrlO);
		file.add(loadFile);
		
		JMenuItem saveFile = new JMenuItem("Save");
		saveFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = {"Local device", "Google drive", "Cancel"};
				JLabel warning = new JLabel("Save to:");
				int result = JOptionPane.showOptionDialog(null, warning, "Select where to save",
						JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[2]);
				
				if (result == JOptionPane.CANCEL_OPTION){
					return;
				}
				
				if (result == JOptionPane.YES_OPTION){
				JFileChooser c = new JFileChooser(){
                                    
                                    // Confirmation box if file with the same name already exists
                                    @Override
                                    public void approveSelection(){
                                        File f = getSelectedFile();
                                        if(f.exists() && getDialogType() == SAVE_DIALOG){
                                            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                                            switch(result){
                                                case JOptionPane.YES_OPTION:
                                                    super.approveSelection();
                                                    return;
                                                case JOptionPane.NO_OPTION:
                                                    return;
                                                case JOptionPane.CLOSED_OPTION:
                                                    return;
                                                case JOptionPane.CANCEL_OPTION:
                                                    cancelSelection();
                                                    return;
                                            }
                                        }
                                        super.approveSelection();
                                    }   
                                };
                                
                                // Add extension filters to JFileChooser
                                c.addChoosableFileFilter(f. new jpgSaveFilter());
                                c.addChoosableFileFilter(f. new pngSaveFilter());
                                
                                c.setDialogTitle("Save drawing to current device");
                                
                                // Set a default non-existing file name
                                int x = 0;
                                File jpg = new File(c.getCurrentDirectory() + "/Image_" + x + ".jpg");
                                File png = new File(c.getCurrentDirectory() + "/Image_" + x + ".png");
                                File def = new File(c.getCurrentDirectory() + "/Image_" + x);
                                
                                while(jpg.exists() || png.exists() || def.exists()){
                                	x++;
                                	jpg = new File(c.getCurrentDirectory() + "/Image_" + x + ".jpg");
                                	png = new File(c.getCurrentDirectory() + "/Image_" + x + ".png");
                                	def = new File(c.getCurrentDirectory() + "/Image_" + x);
                                }
                                
                                c.setSelectedFile(def);
                                int rVal = c.showSaveDialog(PcDesign.this);
                                // Save pressed
                                if(rVal==JFileChooser.APPROVE_OPTION) {
                                    String ext="png";

                                    String extension=c.getFileFilter().getDescription();

                                    if(extension.equals("*.jpg,*.JPG"))
                                    { 
                                        ext="jpg";
                                    }
                                    
                                    if(extension.equals("*.png,*.PNG"))
                                    { 
                                        ext="png";
                                    }
                                    
                                    filetosave = c.getSelectedFile();
                                    String fileName = filetosave.getAbsolutePath() + "." + ext;
                                    filetosave= new File(fileName);
                                    
                                    try{
                                        ImageIO.write(drawing, ext, filetosave);
                                    }
                                    catch (Exception y){
                                        y.printStackTrace();
                                    }
                                }
				}
				
				if (result == JOptionPane.NO_OPTION){
					return;
				}
			}
		});
		KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
	    saveFile.setAccelerator(ctrlS);
		file.add(saveFile);
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		file.add(exit);
		
		JMenu edit = new JMenu("Edit..");
		menuBar.add(edit);
		
		JMenuItem moreclrs = new JMenuItem("More colors");
		moreclrs.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(PcDesign.this, "Select a custom color", paint.getBrushColor());
				paint.setBrushColor(c);
			}			
		});
		edit.add(moreclrs);
		
		JMenuItem undo = new JMenuItem("Undo");
		undo.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		KeyStroke ctrlZ = KeyStroke.getKeyStroke("control Z");
        undo.setAccelerator(ctrlZ);
		edit.add(undo);  
		
		final JCheckBoxMenuItem aa = new JCheckBoxMenuItem("Antialiasing");
		aa.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				paint.setAntialiasing(aa.isSelected());
			}
		});
		aa.setSelected(true);
		edit.add(aa);

		JMenu tools = new JMenu("Tools..");
		menuBar.add(tools);
		
		JMenuItem basic = new JMenuItem("Basic");
		basic.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				setTool(Tools.BASIC);
				cursor = CustomCursor.BASIC_CURSOR;
			}
		});
		tools.add(basic);
		
		JMenuItem strline = new JMenuItem("Straight line");
		strline.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				setTool(Tools.STRLINE);
				cursor = CustomCursor.LINE_CURSOR;
			}
		});
		tools.add(strline);

		JMenuItem bucket = new JMenuItem("Bucket tool");
		bucket.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setTool(Tools.BUCKET);
				cursor = CustomCursor.BUCKET_CURSOR;
			}
		});
		tools.add(bucket);
		
		JMenuItem picker = new JMenuItem("Color picker");
		picker.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setTool(Tools.PICKER);
				cursor = CustomCursor.PICKER_CURSOR;				
			}
		});
		tools.add(picker);
		
		JMenu help = new JMenu("Help..");
		menuBar.add(help);
		
		JMenuItem howto = new JMenuItem("How to use paint");
		howto.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		help.add(howto);
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(PcDesign.this, new JLabel("Kas nors užpildys vėliau"));
			}
		});
		help.add(about);
	}
	
	/**
	 *  Initializes drawing panel with given image
	 *  @param b - image to be added to drawing panel
	 */
	public void createImageFrom(BufferedImage b){
		if (b != null){
			imgW = b.getWidth();
			imgH = b.getHeight();
			drawing = b;
			if (paint != null){
				paint.setGraphics((Graphics2D) b.getGraphics());
			}
			drawPanel.repaint();
		}
	}
	
	/**
	 * Creates a new blank image of given size
	 * @param w - width
	 * @param h - height
	 */
	public void createNewImage(int w, int h){
		w = w > maxImgW ? maxImgW : w;
		h = h > maxImgH ? maxImgH : h;
		if (w > 0 && h > 0){
			imgW = w;
			imgH = h;
			drawing = new BufferedImage(imgW, imgH, IMAGE_FORMAT);
			drawing.getGraphics().fillRect(0, 0, w, h);
			createImageFrom(drawing);
		}
	}
	
	/**
	 *  Show a dialog for creating new image
	 */
	public void promptCreateNewImage(){
		if (drawing != null){
			int result = throwUnsavedWarning();
			if (result == JOptionPane.NO_OPTION){
				return;
			}
			
		}
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
		decimalFormat.setGroupingUsed(false);
		JFormattedTextField x = new JFormattedTextField(decimalFormat);
		x.setText("" + imgW);
		JFormattedTextField y = new JFormattedTextField(decimalFormat);
		y.setText("" + imgH);
		
		JLabel xLabel = new JLabel("Image width ");
		JLabel yLabel = new JLabel("Image height ");
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		p.add(xLabel); p.add(x);
		p.add(yLabel); p.add(y);
		
		int result = JOptionPane.showConfirmDialog(null, p, "Create new image", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION){
			int w = Integer.parseInt(x.getText());
			int h = Integer.parseInt(y.getText());
			if (w < 0 || h < 0){
				// Invalid size, throw error?
				result = JOptionPane.showConfirmDialog(null, p, "Create new image", JOptionPane.OK_CANCEL_OPTION);
				return;
			}
			w = w > maxImgW ? maxImgW : w;
			h = h > maxImgH ? maxImgH : h;
			initDrawPanel(w, h);
			createNewImage(imgW, imgH);
		}				
	}
	
	/**
	 *  Show dialog, asking if a user wants to save current image
	 * 	@return Selected option (check JOptionPane for values)
	 */
	public int throwUnsavedWarning(){
		JLabel warning = new JLabel("You got some stuff you might want to save. Are you sure you want to continue?");
		int result = JOptionPane.showConfirmDialog(null, warning, "Last warning!", JOptionPane.YES_NO_OPTION);
		return result;
	}
	
	/**
	 * Clones current image
	 * @return Shallow cloned (not a reference) buffered image
	 */
	public BufferedImage cloneImage(){
		 ColorModel cm = drawing.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = drawing.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/**
	 * Sets current tool
	 * @param t - tool to be set
	 */
	public void setTool(Tools t){
		tool = t;
	}
	
	/**
	 * Un-does the last action
	 */
	protected void undo(){
		BufferedImage img = undoHistory.pop();
		if(img != null){
			initDrawPanel(img.getWidth(), img.getHeight());
			createImageFrom(img);
		}
	}
	
	public Point getImageSize(){
		return new Point(imgW, imgH);
	}
	
	public static void main(String [] args){
		new PcDesign();
	}
}
