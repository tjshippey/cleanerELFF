/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.SourcePositions;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import static com.sun.tools.javac.tree.JCTree.Tag.SELECT;
import com.sun.tools.javac.tree.TreeInfo;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author ubamass
 */
public class AssertGrabber extends MyPretty8 {
    
    public static Asserts getAsserts(JCTree.JCMethodDecl tree, CompilationUnitTree cut, SourcePositions positions){
        AssertGrabber assertGrabber = new AssertGrabber(tree, cut, positions);
        tree.accept(assertGrabber);
        return assertGrabber.getAsserts();
    }

    Asserts asserts;
    CompilationUnitTree cut;
    SourcePositions positions;

    
    public AssertGrabber(JCTree.JCMethodDecl tree, CompilationUnitTree cut, SourcePositions positions) {
        super(null);
        asserts = new Asserts();
        this.cut = cut;
        this.positions = positions;
        
    }
    
    @Override
        public void visitApply(JCTree.JCMethodInvocation tree) { 
        if(isAssert(tree)){
            int lineNumber = getStartLinePositionOfAssert(tree);
            asserts.addAssert(lineNumber);
        }
        try {
            if (!tree.typeargs.isEmpty()) {
                if (tree.meth.hasTag(SELECT)) {
                    JCTree.JCFieldAccess left = (JCTree.JCFieldAccess)tree.meth;
                    printExpr(left.selected);
                    print(".<");
                    printExprs(tree.typeargs);
                    print(">" + left.name);
                } else {
                    print("<");
                    printExprs(tree.typeargs);
                    print(">");
                    printExpr(tree.meth);
                }
            } else {
                printExpr(tree.meth);
            }
            print("(");
            printExprs(tree.args);
            print(")");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isAssert(JCTree.JCMethodInvocation tree) {
        boolean result = false;
        Matcher matcher = Pattern.compile("\\bassert\\p{javaUpperCase}[a-zA-Z0-9]*\\(").matcher(tree.toString());
        if(!matcher.find()){ //TO-D0:
            result = calculateAssertClass(tree);
            
        }
        return result;
    }

    private int getStartLinePositionOfAssert(JCTree.JCMethodInvocation tree) {
        LineMap lineMap = cut.getLineMap();
        long startPosition = positions.getStartPosition(cut, tree);
        long startLineOfAssert = lineMap.getLineNumber(startPosition);        
        return (int) startLineOfAssert;
    }

    private Asserts getAsserts() {
        return asserts;
    }

    private boolean calculateAssertClass(JCTree.JCMethodInvocation tree) {
        
        boolean result = false;
        JCTree.JCExpression methSelect = tree.getMethodSelect();
        Symbol symbol = TreeInfo.symbol(methSelect);
        Element method = symbol;
        if(method != null){
            TypeElement typeElement = (TypeElement) method.getEnclosingElement();
            System.out.println(typeElement.getQualifiedName());
        }
        return result;
    }
    
    
    
}
