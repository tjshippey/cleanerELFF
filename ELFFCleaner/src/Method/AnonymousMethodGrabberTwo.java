/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

import com.sun.tools.javac.code.Flags;
import static com.sun.tools.javac.code.Flags.ENUM;
import static com.sun.tools.javac.code.Flags.INTERFACE;
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
public class AnonymousMethodGrabberTwo extends MyPretty8 {

    String outerClassName;
    String newClassName;
    String tempClassName;
    private final HashMap<String, Integer> classNamesVisited = new HashMap<>();
    private ArrayList<AnonymousMethod> methods;
    boolean classExists;
    int newClassHits;
    private int classVisits;

    public AnonymousMethodGrabberTwo(String className) {
        super(null);
        this.outerClassName = className;
        this.methods = new ArrayList<>();
        classExists = false;
        newClassHits = 0;
        newClassName = "";
    }

    @Override
    public void visitNewClass(JCTree.JCNewClass tree) {
        //System.out.println(tree.clazz.toString());
        classVisits++;
        JCTree.JCClassDecl treeDef = tree.def;
        if (treeDef != null) {
            //System.out.println(tree.clazz.toString());
            if(tree.clazz.toString().contains("FileFilter")){
                System.out.println(tree.clazz.toString());
            }
            List<JCTree> members = tree.def.getMembers();
            for (JCTree member : members) {
                if (member instanceof JCTree.JCMethodDecl) {
                    String className = outerClassName + "$" + tree.clazz.toString();
                    System.out.println(className);
//                    AnonymousMethod aMethod = new AnonymousMethod((JCTree.JCMethodDecl) member.getTree(), className);
//                    this.methods.add(aMethod);
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
                enclClassName
                        = tree.def.name != null ? tree.def.name
                                : tree.type != null && tree.type.tsym.name != tree.type.tsym.name.table.names.empty
                                        ? tree.type.tsym.name : null;
                if ((tree.def.mods.flags & Flags.ENUM) != 0) {
                    print("/*enum*/");
                }
                printBlock(tree.def.defs);
                enclClassName = enclClassNamePrev;

            }
        } catch (IOException e) {
            throw new MyPretty8.UncheckedIOException(e);
        }
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        if (!classExists) {
            String className = newClassName;
            AnonymousMethod aMethod = new AnonymousMethod(tree, className);
            this.methods.add(aMethod);
        }
        try {
            printDocComment(tree);
            printExpr(tree.mods);
            printTypeParameters(tree.typarams);
            if (tree.name == tree.name.table.names.init) {
                //print(enclClassName != null ? enclClassName : tree.name);
            } else {
                printExpr(tree.restype);
                print(" " + tree.name);
            }
            print("(");
            printExprs(tree.params);
            print(")");
            if (tree.thrown.nonEmpty()) {
                print(" throws ");
                printExprs(tree.thrown);
            }
            if (tree.body != null) {
                print(" ");
                printStat(tree.body);
            } else {
                print(";");
            }
        } catch (IOException e) {

        }
    }

    public ArrayList<AnonymousMethod> getMethods() {
        return methods;
    }

    private void setTempClassName(String classTreeName) {
        int timesVisited = classNamesVisited.getOrDefault(classTreeName, -1);
        if (timesVisited == -1) {
            tempClassName = classTreeName;
            classNamesVisited.put(classTreeName, ++timesVisited);
        } else {
            tempClassName = classTreeName + "_" + timesVisited;
            classNamesVisited.put(classTreeName, ++timesVisited);
        }
        if (tempClassName.contains("<")) {
            tempClassName = tempClassName.substring(0, tempClassName.indexOf("<"));
        }

    }

}
