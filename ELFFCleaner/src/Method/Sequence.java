/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Method;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.tree.JCTree;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Thomas Shippey - University of Hertfordshire
 * @version 20-Jul-2011
 */
public class Sequence extends ArrayList<Kind> {

    private JCTree parent;
    private File file;
    private static int listNumberGen = 1000;
    private int listId = -1;
    private String methodName;
    private String startLine;
    private String endLine;

    public Sequence(File file, JCTree parent) {
        this.parent = parent;
        this.file = file;
        this.listId = listNumberGen++;
    }

    public File getFile() {
        return file;
    }

    public JCTree getParent() {
        return parent;
    }

    public int getListId() {
        return listId;
    }
    
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }
    
    public String getMethodName(){
        return methodName;
    }

    public String getEndLine() {
        return endLine;
    }

    public void setEndLine(String endLine) {
        this.endLine = endLine;
    }

    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }
    
    public String getNumberSequence(){
        String result = "";
        for(Kind k : this){
            result = result + k.ordinal() + " ";
        }
        result = result.trim(); //remove trailing space
        return result;
    }
    
    public String getKindSequence(){
        String result = "";
        for(Kind k : this){
            result = result + k.name() + " ";
        }
        result = result.trim(); // remove trailing space
        return result;
    }





}
