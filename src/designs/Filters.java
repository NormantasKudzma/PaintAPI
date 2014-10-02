package designs;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class Filters {
    
    // jpg and png filter classes for JFileChooser
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

    class pngSaveFilter extends FileFilter { 
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
