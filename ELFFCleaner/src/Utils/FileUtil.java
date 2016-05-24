/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Thomas Shippey - University of Hertfordshire
 * @version 08-Feb-2012
 */
public class FileUtil {

    public static boolean isJavaFile(File fileName) throws IOException {
        boolean result = false;
        int dot = fileName.getCanonicalPath().lastIndexOf(".");
        String extension = fileName.getCanonicalPath().substring(dot + 1);

        if (extension.equals("java")) {
            result = true;
        }

        return result;
    }

    public static String getFileWithoutExt(String fileName) {
        String result = "";
        int dot = fileName.indexOf('.');
        result = fileName.substring(0, dot);
        return result;
    }

    public static ArrayList<File> populateFileNames(File f, ArrayList<File> list) throws IOException {
        if (f.listFiles() != null) {
            for (File file : f.listFiles()) {
                if (file != null) {
                    if (file.isDirectory()) {
                        populateFileNames(file, list);
                    } else if (file.isFile() && FileUtil.isJavaFile(file)) {
                        list.add(file);
                    }
                }
            }
        }
        else if(f.isFile() && FileUtil.isJavaFile(f)){
            list.add(f);
        }
        return list;
    }

}
