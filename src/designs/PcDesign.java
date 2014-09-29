package designs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.Component;
import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import java.io.File;

import api.PaintGUI;

public class PcDesign extends JFrame{
	private static int frameWidth = 1280;
	private static int frameHeight = 720;
	
	private JMenuBar menuBar;
	private JPanel drawPanel;
	private JPanel topPanel;
	private PaintGUI paint;
        
        File filetosave;
        File filetoload;
	
	public PcDesign(){
		this(frameWidth, frameHeight);
                
	}
	
	public PcDesign(int frameW, int frameH){
		initFrame(frameW, frameH);
		paint = new PaintGUI(drawPanel.getGraphics());
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
				
		initDrawPanel();
		add(drawPanel);
		configureMenuBar();
	}
	
	private void initDrawPanel(){
		drawPanel = new JPanel();
		drawPanel.setMaximumSize(new Dimension(frameWidth, (int)(0.75 * frameHeight)));
		drawPanel.setBorder(new LineBorder(Color.black, 2));
		drawPanel.setBackground(Color.white);
		drawPanel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// STUB, draw				
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
				mousePressed(e);
			}
		});
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
		
		// Brush size picker
		JPanel brushSizePicker = new JPanel();
		brushSizePicker.setMaximumSize(new Dimension((int)(w * 0.25), maxHeight));
		brushSizePicker.setBorder(BorderFactory.createTitledBorder("Size"));
		topPanel.add(brushSizePicker);
		
		// Brush shape picker
		JPanel brushShapePicker = new JPanel();
		brushShapePicker.setMaximumSize(new Dimension((int)(w * 0.25), maxHeight));
		brushShapePicker.setBorder(BorderFactory.createTitledBorder("Shape"));
		topPanel.add(brushShapePicker);
	}
        
        // Screenshot of drawPanel is saved to BufferedImage and BufferedImage later is saved as an image file
        private static BufferedImage getScreenShot(Component component){
            BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            
            component.paint(image.getGraphics());
            
            return image;
        }
	
	private void configureMenuBar(){
		JMenu file = new JMenu("File..");
		menuBar.add(file);
		
		JMenuItem newFile = new JMenuItem("New");
		newFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// STUB, create new file
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
                                c.addChoosableFileFilter(new jpgSaveFilter());
                                c.addChoosableFileFilter(new pngSaveFilter());
                                
                                c.setDialogTitle("Load drawing");
                                int rVal = c.showOpenDialog(PcDesign.this);
				
                                // Open pressed
                                if(rVal==JFileChooser.APPROVE_OPTION) {
                                    filetoload = c.getSelectedFile();
                                    BufferedImage img = null;
                                    
                                    try{
                                        img = ImageIO.read(filetoload);
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
                                c.addChoosableFileFilter(new jpgSaveFilter());
                                c.addChoosableFileFilter(new pngSaveFilter());
                                
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
        
        // jpg and png filter classes for JFileChooser

    class jpgSaveFilter extends FileFilter
    { 
        public boolean accept(File f)
        {
            if (f.isDirectory())
            {
                return false;
            }

            String s = f.getName();

            return s.endsWith(".jpg")||s.endsWith(".JPG");
        }

        public String getDescription() 
        {
            return "*.jpg,*.JPG";
        }

    }

    class pngSaveFilter extends FileFilter
    { 
        public boolean accept(File f)
        {
            if (f.isDirectory())
            {
                return false;
            }

            String s = f.getName();

            return s.endsWith(".png")||s.endsWith(".PNG");
        }

        public String getDescription() 
        {
            return "*.png,*.PNG";
        }

    }
	
	public static void main(String [] args){
		new PcDesign();
	}
}