/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shippey
 */
public class FilePrinter {
    
    public static void createCSVFile(String fileName, ArrayList<String> lines){
        FileWriter fw = null;
        try {
            File file = new File(fileName);
            file.createNewFile();
            fw = new FileWriter(file);
            Iterator it = lines.iterator();
            while (it.hasNext()){
                String line = (String) it.next();
                fw.append(line);
                fw.append("\n");

            }
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(FilePrinter.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
}
