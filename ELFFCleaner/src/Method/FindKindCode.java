/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.tree.JCTree;
import java.io.File;

/**
 *
 * @author ubamass
 */
public class FindKindCode {
    
    public static void main(String[] args){
        String inputFile = "D:/OneDrive/PhD/SourceCode/Eclipse 3.0/plugins/org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java";
        String methodName = "readReadableNameTable(LString;)";
        MethodGrabber methodGrabber = new MethodGrabber(new File(inputFile));
        methodGrabber.findAllMethods();
        for(Method method : methodGrabber.getMethods()){
            //System.out.println(method.getMethodName());
            if(method.getMethodName().equals(methodName)){
                printKindsAndCode(method.getSequence());
            }
        }
        System.out.println("hello");
        
    }

    private static void printKindsAndCode(Sequence sequence) {
        PrettyKindPrinter pretty = new PrettyKindPrinter();
        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) sequence.getParent();
        method.accept(pretty);
        for(Kind kind : sequence){
            System.out.println(kind + " : " + kind);
        }
    }
    
}
