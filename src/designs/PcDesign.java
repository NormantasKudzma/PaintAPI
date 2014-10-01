package designs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.Component;
import javax.imageio.ImageIO;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import java.io.File;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import api.CustomStroke;
import api.PaintBase;
import api.StarShape;

public class PcDesign extends JFrame{
	public class BrushShapeListener implements MouseListener {
		private String shapeName;
		
		public BrushShapeListener(String shapeName){
			this.shapeName = shapeName;
		}
		
		public String getShapeName(){
			return shapeName;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			updateBrush(paint.getBrush().getSize(), shapeName);
		}

		@Override
		public void mouseReleased(MouseEvent e) {}	
	}
	
	private static int frameWidth = 1280;
	private static int frameHeight = 720;
	private static final String RES_PATH = "res/";
	private static final int IMAGE_FORMAT = BufferedImage.TYPE_INT_ARGB;
	
	private JMenuBar menuBar;
	private JPanel drawContainerPanel;
	private JPanel drawPanel;
	private JPanel topPanel;
	//private PaintGUI paint;
        
        File filetosave;
        File filetoload;
        Filters f = new Filters();
	private PaintBase paint;
	private BufferedImage drawing;
	private ClassLoader cl = getClass().getClassLoader();
	private String currentShape = "rect";
	
	private int maxImgW = 0;
	private int maxImgH = 0;
	private int imgW = 0;
	private int imgH = 0;
	
	private int lastX = 0;
	private int lastY = 0;
	
	public PcDesign(){
		this(frameWidth, frameHeight);
                
	}
	
	public PcDesign(int frameW, int frameH){
		initFrame(frameW, frameH);
		paint = new PaintBase();
		initDrawPanel(imgW, imgH);
	}
	
	private void initFrame(int w, int h){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(w, h);
		setTitle("The amazing paint program for PC v0.1");
		setLocationRelativeTo(null);
		initLayout();
		// Set visible *ONLY* after initializing all components
		setVisible(true);
	}
	
	private void initLayout(){
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
	
	private void initDrawPanel(int w, int h){
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
		drawPanel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {			
				lastX = e.getX();
				lastY = e.getY();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
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
				float dist = (Math.abs(x - lastX) + Math.abs(y - lastY)) / 2f;
				float treshold = paint.getBrushSize() * 0.15f;
				if (dist > treshold){
					paint.drawCenteredLine(lastX, lastY, x, y);

				}
				else {
					paint.drawCenteredPixel(lastX, lastY);
				}
				lastX = x;
				lastY = y;
				drawPanel.repaint();
			}
		});
		drawContainerPanel.add(drawPanel, BorderLayout.CENTER);
		drawContainerPanel.revalidate();
				
		createNewImage(imgW, imgH);
		drawContainerPanel.repaint();
	}
	
	private void initTopPanel(){
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
		
		final JColorChooser jcc = new JColorChooser(Color.black);
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
		topPanel.add(brushSizePicker);
		
		Integer [] sizes = {1, 2, 4, 8, 12, 16, 24};
		JComboBox<Integer> sizebox = new JComboBox<Integer>(sizes);
		sizebox.setSelectedIndex(2);
		sizebox.setEditable(true);
		sizebox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				try {
					JComboBox<Integer> c = (JComboBox) e.getSource();
					int size = (Integer)c.getSelectedItem();
					updateBrush(size, currentShape);
				}
				catch (Exception ex){
					// Input was not an integer, do nothing, supposedly
					// ex.printStackTrace();
				}
			}
		});
		brushSizePicker.add(sizebox);
		
		// Brush shape picker
		JPanel brushShapePicker = new JPanel();
		brushShapePicker.setLayout(new GridLayout(2, 0, 5, 5));
		brushShapePicker.setMaximumSize(new Dimension((int)(w * 0.25), maxHeight));
		brushShapePicker.setBorder(BorderFactory.createTitledBorder("Shape"));
		topPanel.add(brushShapePicker);
			
		JLabel rect = new JLabel(new ImageIcon(cl.getResource(RES_PATH + "rect.png")));
		rect.addMouseListener(new BrushShapeListener("rect"));
		brushShapePicker.add(rect);
		
		JLabel circle = new JLabel(new ImageIcon(cl.getResource(RES_PATH + "circle.png")));
		circle.addMouseListener(new BrushShapeListener("circle"));
		brushShapePicker.add(circle);
		
		JLabel star = new JLabel(new ImageIcon(cl.getResource(RES_PATH + "star.png")));
		star.addMouseListener(new BrushShapeListener("star"));
		brushShapePicker.add(star);
	}
	
	private void updateBrush(int size, String type){
		paint.setBrushSize(size);
		currentShape = type;
		switch(type){
			case "rect":{
				paint.setCustomStroke(new CustomStroke(new Rectangle2D.Float(0, 0, size, size), size * 0.25f));
				break;
			}
			case "circle":{
				paint.setCustomStroke(new CustomStroke(new Ellipse2D.Float(0, 0, size, size), size * 0.25f));
				break;
			}		
			case "star":{
				paint.setCustomStroke(new CustomStroke(new StarShape(0, 0, size, size), size * 0.25f));
				break;
			}
		}
	}
        
        // Screenshot of drawPanel is saved to BufferedImage and BufferedImage later is saved as an image file
        private static BufferedImage getScreenShot(JPanel panel){
            int w = panel.getWidth();
            int h = panel.getHeight();
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            panel.printAll(g);
            return bi;
        }
        
        // File is loaded to drawPanel
        private void setImage(BufferedImage image){
            if (drawing != null){
			JLabel warning = new JLabel("You got some stuff you might want to save. Are you sure you want to load a different image?");
			int result = JOptionPane.showConfirmDialog(null, warning, "Last warning!", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION){
				return;
			}
			if (drawPanel != null){
				drawContainerPanel.remove(drawPanel);
				drawContainerPanel.revalidate();
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			drawing = image;
                        
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
                        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
                        decimalFormat.setGroupingUsed(false);
                        JFormattedTextField x = new JFormattedTextField(decimalFormat);
                        x.setText("" + imgW);
                        JFormattedTextField y = new JFormattedTextField(decimalFormat);
                        y.setText("" + imgH);

                        if (result == JOptionPane.YES_OPTION){
                            int w = Integer.parseInt(x.getText());
                            int h = Integer.parseInt(y.getText());
                            if (w < 0 || h < 0){
                                    // Invalid size, throw error?
                                    return;
                            }
                            w = w > maxImgW ? maxImgW : w;
                            h = h > maxImgH ? maxImgH : h;
                            initDrawPanel(w, h);
                            createImageFrom(image);
                        }
		}
        }
	
	private void configureMenuBar(){
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
			public void actionPerformed(ActionEvent arg0) {
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
                                        setImage(img);
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
                                    
                                    BufferedImage img = getScreenShot(drawPanel);
                                    filetosave = c.getSelectedFile();
                                    
                                    try{
                                        ImageIO.write(img, ext, filetosave);
                                    }
                                    catch (Exception y){
                                        y.printStackTrace();
                                    }
                                }
			}
		});
		KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
	    saveFile.setAccelerator(ctrlS);
		file.add(saveFile);
	}
	
	public void createImageFrom(BufferedImage b){
		imgW = b.getWidth();
		imgH = b.getHeight();
		drawing = b;
		if (paint != null){
			paint.setGraphics((Graphics2D) b.getGraphics());
		}
		drawPanel.repaint();
	}
	
	public void createNewImage(int w, int h){
		imgW = w;
		imgH = h;
		drawing = new BufferedImage(imgW, imgH, IMAGE_FORMAT);
		if (paint != null){
			paint.setGraphics((Graphics2D) drawing.getGraphics());
		}
		drawPanel.repaint();
	}
	
	public void promptCreateNewImage(){
		if (drawing != null){
			JLabel warning = new JLabel("You got some stuff you might want to save. Are you sure you want to create a new image?");
			int result = JOptionPane.showConfirmDialog(null, warning, "Last warning!", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION){
				return;
			}
			if (drawPanel != null){
				drawContainerPanel.remove(drawPanel);
				drawContainerPanel.revalidate();
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			drawing = null;
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
				return;
			}
			w = w > maxImgW ? maxImgW : w;
			h = h > maxImgH ? maxImgH : h;
			initDrawPanel(w, h);
		}				
	}
	
	public static void main(String [] args){
		new PcDesign();
	}
}
