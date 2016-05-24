/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Name;
import java.io.IOException;

/**
 *
 * @author ubamass
 */
public class FieldMethodGrabber extends MyPretty8 {

    public FieldMethodGrabber() {
        super(null);
    }
    
    @Override
    public void visitNewClass(JCTree.JCNewClass tree) {
        //System.out.println(tree.clazz.toString());
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
    
}
