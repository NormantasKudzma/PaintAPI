package core;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Jpg and Png filter classes for loading and saving images
 */
public class Filters {
    public class jpgSaveFilter extends FileFilter { 
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

    public class pngSaveFilter extends FileFilter { 
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
}
