/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Method;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ubamass
 */
public class AnonymousMethodGrabber extends MyPretty8 {
    
    private final ArrayList<AnonymousMethod> anonymousMethods; //holds the class and the method tree
    private String className,tempClassName,lastVisitedClass;
    private final HashMap<String, Integer> classNamesVisited = new HashMap<>();
    private final HashMap<String, ArrayList<JCTree.JCMethodDecl>> methods;    

    public AnonymousMethodGrabber() {
        super(null); //UGLY please rewrite
        anonymousMethods = new ArrayList<>();
        methods = new HashMap<>();
        className="";
    }
    
    public AnonymousMethodGrabber(String parentClass){
        super(null);
        anonymousMethods = new ArrayList<>();
        methods = new HashMap<>();
        className = parentClass;
    }
    
    @Override
    public void visitNewClass(JCTree.JCNewClass tree) {
        if(tree.def !=null){
            //className = className + "$" + tree.clazz.toString();
            List<JCTree> members = tree.def.getMembers();
            String classNameTemp = className + "$" + tree.clazz.toString();
            setTempClassName(classNameTemp);
            for(JCTree member:members){
                if (member instanceof JCTree.JCMethodDecl) {
                    AnonymousMethodGrabber amg2 = new AnonymousMethodGrabber(tempClassName);
                    member.accept(amg2);
                    JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) member;
                    AnonymousMethod anonymousMethod = new AnonymousMethod(method, tempClassName);
                    anonymousMethods.add(anonymousMethod);
                    anonymousMethods.addAll(amg2.anonymousMethods);
                }
            }
        }     
        try {
            if (tree.encl != null) {
                printExpr(tree.encl);
                print(".");
            }
            print("new ");
            if (!tree.typeargs.isEmpty()) {
                print("<");
                printExprs(tree.typeargs);
                print(">");
            }
            if (tree.def != null && tree.def.mods.annotations.nonEmpty()) {
                printTypeAnnotations(tree.def.mods.annotations);
            }
            printExpr(tree.clazz);
            print("(");
            printExprs(tree.args);
            print(")");
            if (tree.def != null) {
                Name enclClassNamePrev = enclClassName;
                enclClassName =
                        tree.def.name != null ? tree.def.name :
                            tree.type != null && tree.type.tsym.name != tree.type.tsym.name.table.names.empty
                                ? tree.type.tsym.name : null;
                if ((tree.def.mods.flags & Flags.ENUM) != 0) print("/*enum*/");
                printBlock(tree.def.defs);
                enclClassName = enclClassNamePrev;
            }
        } catch (IOException e) {
            throw new MyPretty8.UncheckedIOException(e);
        }
    }

    private void setTempClassName(String classTreeName) {
        int timesVisited = classNamesVisited.getOrDefault(classTreeName,-1);
        if(timesVisited == -1){
            tempClassName = classTreeName;                
            classNamesVisited.put(classTreeName, ++timesVisited);
        } else {
            tempClassName = classTreeName + "_" + timesVisited;  
            classNamesVisited.put(classTreeName, ++timesVisited);
        }
        if(tempClassName.contains("<")){
            tempClassName = tempClassName.substring(0,tempClassName.indexOf("<"));
        }
        
    }

    /**
     * @return the anonymousMethods
     */
    public ArrayList<AnonymousMethod> getAnonymousMethods() {
        return anonymousMethods;
    }
    
    public String getClassName() {
        return className;
    }

    /**
     * @return the classNamesVisited
     */
    public HashMap<String, Integer> getClassNamesVisited() {
        return classNamesVisited;
    }

    public void removeMethods() {
        this.anonymousMethods.clear();   }
    

    
    
    
        
        
}
