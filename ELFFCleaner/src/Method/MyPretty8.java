/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package Method;

import java.io.*;

import com.sun.source.tree.MemberReferenceTree.ReferenceMode;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;
import static com.sun.tools.javac.code.Flags.*;
import static com.sun.tools.javac.code.Flags.ANNOTATION;
import com.sun.tools.javac.tree.DocCommentTable;
import com.sun.tools.javac.tree.JCTree;
import static com.sun.tools.javac.tree.JCTree.Tag.*;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeScanner;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Prints out a tree as an indented Java source program.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class MyPretty8 extends JCTree.Visitor {
    
    public MyPretty8(JCMethodDecl tree) {
        this.sourceOutput = true;
        this.out = new StringWriter();
        kindList = new Sequence(null,tree);
    }
    
    public ArrayList<String> kinds = new ArrayList<String>();
    public ArrayList<Sequence> kindLists = new ArrayList<Sequence>();
    public Sequence kindList;
    public int kindIndex = 0;
    private int methodsSeen = 0;
    
    public void resetPretty(){
        kindList.clear();
        kinds.clear();
        methodsSeen = 0;
    }


    /**
     * The digestion point for the code i.e. where it shall stop
     */
    private ArrayList<Tree.Kind> digestionPoint = new ArrayList<Tree.Kind>();

    /**
     * The StringBuilder for the method
     */
    public StringBuilder sb = new StringBuilder();

    /**
     * The List for holding the statements
     */
    private ArrayList<String> strings = new ArrayList<String>();

    /** Set when we are producing source output.  If we're not
     *  producing source output, we can sometimes give more detail in
     *  the output even though that detail would not be valid java
     *  source.
     */
    private final boolean sourceOutput;

    /** The output stream on which trees are printed.
     */
    Writer out;

    /** Indentation width (can be reassigned from outside).
     */
    public int width = 4;

    /** The current left margin.
     */
    int lmargin = 0;

    /** The enclosing class name.
     */
    Name enclClassName;

    /** A table mapping trees to their documentation comments
     *  (can be null)
     */
    DocCommentTable docComments = null;

    /**
     * A string sequence to be used when Pretty output should be constrained
     * to fit into a given size
     */
    private final static String trimSequence = "[...]";

    /**
     * Max number of chars to be generated when output should fit into a single line
     */
    private final static int PREFERRED_LENGTH = 20;

    /** Align code to be indented to left margin.
     */
    void align() throws IOException {
        for (int i = 0; i < lmargin; i++) out.write(" ");
    }

    /** Increase left margin by indentation width.
     */
    void indent() {
        lmargin = lmargin + width;
    }

    /** Decrease left margin by indentation width.
     */
    void undent() {
        lmargin = lmargin - width;
    }

    /** Enter a new precedence level. Emit a `(' if new precedence level
     *  is less than precedence level so far.
     *  @param contextPrec    The precedence level in force so far.
     *  @param ownPrec        The new precedence level.
     */
    void open(int contextPrec, int ownPrec) throws IOException {
        if (ownPrec < contextPrec) out.write("(");
    }

    /** Leave precedence level. Emit a `(' if inner precedence level
     *  is less than precedence level we revert to.
     *  @param contextPrec    The precedence level we revert to.
     *  @param ownPrec        The inner precedence level.
     */
    void close(int contextPrec, int ownPrec) throws IOException {
        if (ownPrec < contextPrec) out.write(")");
    }

    /** Print string, replacing all non-ascii character with unicode escapes.
     */
    public void print(Object s) throws IOException {
        out.write(Convert.escapeUnicode(s.toString()));
    }

    /** Print new line.
     */
    public void println() throws IOException {
        out.write(lineSep);
    }

    public static String toSimpleString(JCTree tree) {
        return toSimpleString(tree, PREFERRED_LENGTH);
    }

    public static String toSimpleString(JCTree tree, int maxLength) {
        return "";
    }

    String lineSep = System.getProperty("line.separator");

    /**************************************************************************
     * Traversal methods
     *************************************************************************/

    /** Exception to propogate IOException through visitXXX methods */
    public static class UncheckedIOException extends Error {
        static final long serialVersionUID = -4032692679158424751L;
        UncheckedIOException(IOException e) {
            super(e.getMessage(), e);
        }
    }

    /** Visitor argument: the current precedence level.
     */
    int prec;

    /** Visitor method: print expression tree.
     *  @param prec  The current precedence level.
     */
    public void printExpr(JCTree tree, int prec) throws IOException {
        int prevPrec = this.prec;
        try {
            this.prec = prec;
            if (tree == null) print("/*missing*/");
            else {
                tree.accept(this);
            }
        } catch (UncheckedIOException ex) {
            IOException e = new IOException(ex.getMessage());
            e.initCause(ex);
            throw e;
        } finally {
            this.prec = prevPrec;
        }
    }

    /** Derived visitor method: print expression tree at minimum precedence level
     *  for expression.
     */
    public void printExpr(JCTree tree) throws IOException {
        printExpr(tree, TreeInfo.noPrec);
    }

    /** Derived visitor method: print statement tree.
     */
    public void printStat(JCTree tree) throws IOException {
        printExpr(tree, TreeInfo.notExpression);
    }

    /** Derived visitor method: print list of expression trees, separated by given string.
     *  @param sep the separator string
     */
    public <T extends JCTree> void printExprs(List<T> trees, String sep) throws IOException {
        if (trees.nonEmpty()) {
            printExpr(trees.head);
            for (List<T> l = trees.tail; l.nonEmpty(); l = l.tail) {
                print(sep);
                printExpr(l.head);
            }
        }
    }

    /** Derived visitor method: print list of expression trees, separated by commas.
     */
    public <T extends JCTree> void printExprs(List<T> trees) throws IOException {
        printExprs(trees, ", ");
    }

    /** Derived visitor method: print list of statements, each on a separate line.
     */
    public void printStats(List<? extends JCTree> trees) throws IOException {
        for (List<? extends JCTree> l = trees; l.nonEmpty(); l = l.tail) {
            align();
            printStat(l.head);
            println();
        }
    }

    /** Print a set of modifiers.
     */
    public void printFlags(long flags) throws IOException {
        if ((flags & SYNTHETIC) != 0) print("/*synthetic*/ ");
        print(TreeInfo.flagNames(flags));
        if ((flags & ExtendedStandardFlags) != 0) print(" ");
        if ((flags & ANNOTATION) != 0) print("@");
    }

    public void printAnnotations(List<JCAnnotation> trees) throws IOException {
        for (List<JCAnnotation> l = trees; l.nonEmpty(); l = l.tail) {
            printStat(l.head);
            println();
            align();
        }
    }

    public void printTypeAnnotations(List<JCAnnotation> trees) throws IOException {
        for (List<JCAnnotation> l = trees; l.nonEmpty(); l = l.tail) {
            printExpr(l.head);
            print(" ");
        }
    }

    /** Print documentation comment, if it exists
     *  @param tree    The tree for which a documentation comment should be printed.
     */
    public void printDocComment(JCTree tree) throws IOException {
        if (docComments != null) {
            String dc = docComments.getCommentText(tree);
            if (dc != null) {
                print("/**"); println();
                int pos = 0;
                int endpos = lineEndPos(dc, pos);
                while (pos < dc.length()) {
                    align();
                    print(" *");
                    if (pos < dc.length() && dc.charAt(pos) > ' ') print(" ");
                    print(dc.substring(pos, endpos)); println();
                    pos = endpos + 1;
                    endpos = lineEndPos(dc, pos);
                }
                align(); print(" */"); println();
                align();
            }
        }
    }
//where
    static int lineEndPos(String s, int start) {
        int pos = s.indexOf('\n', start);
        if (pos < 0) pos = s.length();
        return pos;
    }

    /** If type parameter list is non-empty, print it enclosed in
     *  {@literal "<...>"} brackets.
     */
    public void printTypeParameters(List<JCTypeParameter> trees) throws IOException {
        if (trees.nonEmpty()) {
            print("<");
            printExprs(trees);
            print(">");
        }
    }

    /** Print a block.
     */
    public void printBlock(List<? extends JCTree> stats) throws IOException {
        print("{");
        println();
        indent();
        printStats(stats);
        undent();
        align();
        print("}");
    }

    /** Print a block.
     */
    public void printEnumBody(List<JCTree> stats) throws IOException {
        print("{");
        println();
        indent();
        boolean first = true;
        for (List<JCTree> l = stats; l.nonEmpty(); l = l.tail) {
            if (isEnumerator(l.head)) {
                if (!first) {
                    print(",");
                    println();
                }
                align();
                printStat(l.head);
                first = false;
            }
        }
        print(";");
        println();
        for (List<JCTree> l = stats; l.nonEmpty(); l = l.tail) {
            if (!isEnumerator(l.head)) {
                align();
                printStat(l.head);
                println();
            }
        }
        undent();
        align();
        print("}");
    }

    /** Is the given tree an enumerator definition? */
    boolean isEnumerator(JCTree t) {
        return t.hasTag(VARDEF) && (((JCVariableDecl) t).mods.flags & ENUM) != 0;
    }

    /** Print unit consisting of package clause and import statements in toplevel,
     *  followed by class definition. if class definition == null,
     *  print all definitions in toplevel.
     *  @param tree     The toplevel tree
     *  @param cdef     The class definition, which is assumed to be part of the
     *                  toplevel tree.
     */
    public void printUnit(JCCompilationUnit tree, JCClassDecl cdef) throws IOException {
        docComments = tree.docComments;
        printDocComment(tree);
        if (tree.pid != null) {
            print("package ");
            printExpr(tree.pid);
            print(";");
            println();
        }
        boolean firstImport = true;
        for (List<JCTree> l = tree.defs;
        l.nonEmpty() && (cdef == null || l.head.hasTag(IMPORT));
        l = l.tail) {
            if (l.head.hasTag(IMPORT)) {
                JCImport imp = (JCImport)l.head;
                Name name = TreeInfo.name(imp.qualid);
                if (name == name.table.names.asterisk ||
                        cdef == null ||
                        isUsed(TreeInfo.symbol(imp.qualid), cdef)) {
                    if (firstImport) {
                        firstImport = false;
                        println();
                    }
                    printStat(imp);
                }
            } else {
                printStat(l.head);
            }
        }
        if (cdef != null) {
            printStat(cdef);
            println();
        }
    }
    // where
    boolean isUsed(final Symbol t, JCTree cdef) {
        class UsedVisitor extends TreeScanner {
            @Override
            public void scan(JCTree tree) {
                if (tree!=null && !result) tree.accept(this);
            }
            boolean result = false;
            @Override
            public void visitIdent(JCIdent tree) {
                if (tree.sym == t) result = true;
            }
        }
        UsedVisitor v = new UsedVisitor();
        v.scan(cdef);
        return v.result;
    }

    /**************************************************************************
     * Visitor methods
     * @param tree
     *************************************************************************/

    @Override
    public void visitTopLevel(JCCompilationUnit tree) { 
        checkDigestion(tree);
        //System.out.println(tree.getKind().toString());
        try {
            printUnit(tree, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitImport(JCImport tree) { checkDigestion(tree);
        try {
            print("import ");
            if (tree.staticImport) print("static ");
            printExpr(tree.qualid);
            print(";");
            println();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitClassDef(JCClassDecl tree) { checkDigestion(tree);
        try {
            println(); align();
            printDocComment(tree);
            printAnnotations(tree.mods.annotations);
            printFlags(tree.mods.flags & ~INTERFACE);
            Name enclClassNamePrev = enclClassName;
            enclClassName = tree.name;
            if ((tree.mods.flags & INTERFACE) != 0) {
                print("interface " + tree.name);
                printTypeParameters(tree.typarams);
                if (tree.implementing.nonEmpty()) {
                    print(" extends ");
                    printExprs(tree.implementing);
                }
            } else {
                if ((tree.mods.flags & ENUM) != 0)
                    print("enum " + tree.name);
                else
                    print("class " + tree.name);
                printTypeParameters(tree.typarams);
                if (tree.extending != null) {
                    print(" extends ");
                    printExpr(tree.extending);
                }
                if (tree.implementing.nonEmpty()) {
                    print(" implements ");
                    printExprs(tree.implementing);
                }
            }
            print(" ");
            if ((tree.mods.flags & ENUM) != 0) {
                printEnumBody(tree.defs);
            } else {
                printBlock(tree.defs);
            }
            enclClassName = enclClassNamePrev;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitMethodDef(JCMethodDecl tree) {
        methodName = tree.getName().toString();
        //System.out.println(methodName);
        setMethodName(methodName);
        checkDigestion(tree);
        try {
            // when producing source output, omit anonymous constructors
//            if (tree.name == tree.name.table.names.init &&
//                    enclClassName == null &&
//                    sourceOutput) return;
            println(); align();
            printDocComment(tree);
            printExpr(tree.mods);
            printTypeParameters(tree.typarams);
            if (tree.name == tree.name.table.names.init) {
                print(enclClassName != null ? enclClassName : tree.name);
            } else {
                printExpr(tree.restype);
                print(" " + tree.name);
            }
            print("(");
            if (tree.recvparam!=null) {
                printExpr(tree.recvparam);
                if (tree.params.size() > 0) {
                    print(", ");
                }
            }
            printExprs(tree.params);
            print(")");
            if (tree.thrown.nonEmpty()) {
                print(" throws ");
                printExprs(tree.thrown);
            }
            if (tree.defaultValue != null) {
                print(" default ");
                printExpr(tree.defaultValue);
            }
            if (tree.body != null) {
                print(" ");
                printStat(tree.body);
            } else {
                print(";");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitVarDef(JCVariableDecl tree) { checkDigestion(tree);
        try {
            if (docComments != null && docComments.hasComment(tree)) {
                println(); align();
            }
            printDocComment(tree);
            if ((tree.mods.flags & ENUM) != 0) {
                print("/*public static final*/ ");
                print(tree.name);
                if (tree.init != null) {
                    if (sourceOutput && tree.init.hasTag(NEWCLASS)) {
                        print(" /*enum*/ ");
                        JCNewClass init = (JCNewClass) tree.init;
                        if (init.args != null && init.args.nonEmpty()) {
                            print("(");
                            print(init.args);
                            print(")");
                        }
                        if (init.def != null && init.def.defs != null) {
                            print(" ");
                            printBlock(init.def.defs);
                        }
                        return;
                    }
                    print(" /* = ");
                    printExpr(tree.init);
                    print(" */");
                }
            } else {
                printExpr(tree.mods);
                if ((tree.mods.flags & VARARGS) != 0) {
                    JCTree vartype = tree.vartype;
                    List<JCAnnotation> tas = null;
                    if (vartype instanceof JCAnnotatedType) {
                        tas = ((JCAnnotatedType)vartype).annotations;
                        vartype = ((JCAnnotatedType)vartype).underlyingType;
                    }
                    printExpr(((JCArrayTypeTree) vartype).elemtype);
                    if (tas != null) {
                        print(' ');
                        printTypeAnnotations(tas);
                    }
                    print("... " + tree.name);
                } else {
                    printExpr(tree.vartype);
                    print(" " + tree.name);
                }
                if (tree.init != null) {
                    print(" = ");
                    printExpr(tree.init);
                }
                if (prec == TreeInfo.notExpression) print(";");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitSkip(JCSkip tree) { checkDigestion(tree);
        try {
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBlock(JCBlock tree) { checkDigestion(tree);
        try {
            printFlags(tree.flags);
            printBlock(tree.stats);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitDoLoop(JCDoWhileLoop tree) { checkDigestion(tree);
        try {
            print("do ");
            printStat(tree.body);
            align();
            print(" while ");
            if (tree.cond.hasTag(PARENS)) {
                printExpr(tree.cond);
            } else {
                print("(");
                printExpr(tree.cond);
                print(")");
            }
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitWhileLoop(JCWhileLoop tree) { checkDigestion(tree);
        try {
            print("while ");
            if (tree.cond.hasTag(PARENS)) {
                printExpr(tree.cond);
            } else {
                print("(");
                printExpr(tree.cond);
                print(")");
            }
            print(" ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitForLoop(JCForLoop tree) { checkDigestion(tree);
        try {
            print("for (");
            if (tree.init.nonEmpty()) {
                if (tree.init.head.hasTag(VARDEF)) {
                    printExpr(tree.init.head);
                    for (List<JCStatement> l = tree.init.tail; l.nonEmpty(); l = l.tail) {
                        JCVariableDecl vdef = (JCVariableDecl)l.head;
                        print(", " + vdef.name + " = ");
                        printExpr(vdef.init);
                    }
                } else {
                    printExprs(tree.init);
                }
            }
            print("; ");
            if (tree.cond != null) printExpr(tree.cond);
            print("; ");
            printExprs(tree.step);
            print(") ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitForeachLoop(JCEnhancedForLoop tree) { checkDigestion(tree);
        try {
            print("for (");
            printExpr(tree.var);
            print(" : ");
            printExpr(tree.expr);
            print(") ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLabelled(JCLabeledStatement tree) { checkDigestion(tree);
        try {
            print(tree.label + ": ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitSwitch(JCSwitch tree) { checkDigestion(tree);
        try {
            print("switch ");
            if (tree.selector.hasTag(PARENS)) {
                printExpr(tree.selector);
            } else {
                print("(");
                printExpr(tree.selector);
                print(")");
            }
            print(" {");
            println();
            printStats(tree.cases);
            align();
            print("}");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitCase(JCCase tree) { checkDigestion(tree);
        try {
            if (tree.pat == null) {
                print("default");
            } else {
                print("case ");
                printExpr(tree.pat);
            }
            print(": ");
            println();
            indent();
            printStats(tree.stats);
            undent();
            align();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitSynchronized(JCSynchronized tree) { checkDigestion(tree);
        try {
            print("synchronized ");
            if (tree.lock.hasTag(PARENS)) {
                printExpr(tree.lock);
            } else {
                print("(");
                printExpr(tree.lock);
                print(")");
            }
            print(" ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTry(JCTry tree) { checkDigestion(tree);
        try {
            print("try ");
            if (tree.resources.nonEmpty()) {
                print("(");
                boolean first = true;
                for (JCTree var : tree.resources) {
                    if (!first) {
                        println();
                        indent();
                    }
                    printStat(var);
                    first = false;
                }
                print(") ");
            }
            printStat(tree.body);
            for (List<JCCatch> l = tree.catchers; l.nonEmpty(); l = l.tail) {
                printStat(l.head);
            }
            if (tree.finalizer != null) {
                print(" finally ");
                printStat(tree.finalizer);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitCatch(JCCatch tree) { checkDigestion(tree);
        try {
            print(" catch (");
            printExpr(tree.param);
            print(") ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitConditional(JCConditional tree) { checkDigestion(tree);
        try {
            open(prec, TreeInfo.condPrec);
            printExpr(tree.cond, TreeInfo.condPrec + 1);
            print(" ? ");
            printExpr(tree.truepart);
            print(" : ");
            printExpr(tree.falsepart, TreeInfo.condPrec);
            close(prec, TreeInfo.condPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitIf(JCIf tree) { checkDigestion(tree);
        try {
            print("if ");
            if (tree.cond.hasTag(PARENS)) {
                printExpr(tree.cond);
            } else {
                print("(");
                printExpr(tree.cond);
                print(")");
            }
            print(" ");
            printStat(tree.thenpart);
            if (tree.elsepart != null) {
                print(" else ");
                printStat(tree.elsepart);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitExec(JCExpressionStatement tree) { checkDigestion(tree);
        try {
            printExpr(tree.expr);
            if (prec == TreeInfo.notExpression) print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBreak(JCBreak tree) { checkDigestion(tree);
        try {
            print("break");
            if (tree.label != null) print(" " + tree.label);
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitContinue(JCContinue tree) { checkDigestion(tree);
        try {
            print("continue");
            if (tree.label != null) print(" " + tree.label);
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitReturn(JCReturn tree) { checkDigestion(tree);
        try {
            print("return");
            if (tree.expr != null) {
                print(" ");
                printExpr(tree.expr);
            }
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitThrow(JCThrow tree) { checkDigestion(tree);
        try {
            print("throw ");
            printExpr(tree.expr);
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAssert(JCAssert tree) { checkDigestion(tree);
        //System.out.println(tree.getKind().toString());
        try {
            print("assert ");
            printExpr(tree.cond);
            if (tree.detail != null) {
                print(" : ");
                printExpr(tree.detail);
            }
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitApply(JCMethodInvocation tree) { checkDigestion(tree);
        try {
            if (!tree.typeargs.isEmpty()) {
                if (tree.meth.hasTag(SELECT)) {
                    JCFieldAccess left = (JCFieldAccess)tree.meth;
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

    public void visitNewClass(JCNewClass tree) { checkDigestion(tree);
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
            throw new UncheckedIOException(e);
        }
    }

    public void visitNewArray(JCNewArray tree) { checkDigestion(tree);
        try {
            if (tree.elemtype != null) {
                print("new ");
                JCTree elem = tree.elemtype;
                printBaseElementType(elem);

                if (!tree.annotations.isEmpty()) {
                    print(' ');
                    printTypeAnnotations(tree.annotations);
                }
                if (tree.elems != null) {
                    print("[]");
                }

                int i = 0;
                List<List<JCAnnotation>> da = tree.dimAnnotations;
                for (List<JCExpression> l = tree.dims; l.nonEmpty(); l = l.tail) {
                    if (da.size() > i && !da.get(i).isEmpty()) {
                        print(' ');
                        printTypeAnnotations(da.get(i));
                    }
                    print("[");
                    i++;
                    printExpr(l.head);
                    print("]");
                }
                printBrackets(elem);
            }
            if (tree.elems != null) {
                print("{");
                printExprs(tree.elems);
                print("}");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLambda(JCLambda tree) { checkDigestion(tree);
        try {
            print("(");
            if (tree.paramKind == JCLambda.ParameterKind.EXPLICIT) {
                printExprs(tree.params);
            } else {
                String sep = "";
                for (JCVariableDecl param : tree.params) {
                    print(sep);
                    print(param.name);
                    sep = ",";
                }
            }
            print(")->");
            printExpr(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitParens(JCParens tree) { checkDigestion(tree);
        try {
            print("(");
            printExpr(tree.expr);
            print(")");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAssign(JCAssign tree) { checkDigestion(tree);
        try {
            open(prec, TreeInfo.assignPrec);
            printExpr(tree.lhs, TreeInfo.assignPrec + 1);
            print(" = ");
            printExpr(tree.rhs, TreeInfo.assignPrec);
            close(prec, TreeInfo.assignPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String operatorName(JCTree.Tag tag) {
        switch(tag) {
            case POS:     return "+";
            case NEG:     return "-";
            case NOT:     return "!";
            case COMPL:   return "~";
            case PREINC:  return "++";
            case PREDEC:  return "--";
            case POSTINC: return "++";
            case POSTDEC: return "--";
            case NULLCHK: return "<*nullchk*>";
            case OR:      return "||";
            case AND:     return "&&";
            case EQ:      return "==";
            case NE:      return "!=";
            case LT:      return "<";
            case GT:      return ">";
            case LE:      return "<=";
            case GE:      return ">=";
            case BITOR:   return "|";
            case BITXOR:  return "^";
            case BITAND:  return "&";
            case SL:      return "<<";
            case SR:      return ">>";
            case USR:     return ">>>";
            case PLUS:    return "+";
            case MINUS:   return "-";
            case MUL:     return "*";
            case DIV:     return "/";
            case MOD:     return "%";
            default: throw new Error();
        }
    }

    public void visitAssignop(JCAssignOp tree) { checkDigestion(tree);
        try {
            open(prec, TreeInfo.assignopPrec);
            printExpr(tree.lhs, TreeInfo.assignopPrec + 1);
            print(" " + operatorName(tree.getTag().noAssignOp()) + "= ");
            printExpr(tree.rhs, TreeInfo.assignopPrec);
            close(prec, TreeInfo.assignopPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitUnary(JCUnary tree) { checkDigestion(tree);
        try {
            int ownprec = TreeInfo.opPrec(tree.getTag());
            String opname = operatorName(tree.getTag());
            open(prec, ownprec);
            if (!tree.getTag().isPostUnaryOp()) {
                print(opname);
                printExpr(tree.arg, ownprec);
            } else {
                printExpr(tree.arg, ownprec);
                print(opname);
            }
            close(prec, ownprec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBinary(JCBinary tree) { checkDigestion(tree);
        try {
            int ownprec = TreeInfo.opPrec(tree.getTag());
            String opname = operatorName(tree.getTag());
            open(prec, ownprec);
            printExpr(tree.lhs, ownprec);
            print(" " + opname + " ");
            printExpr(tree.rhs, ownprec + 1);
            close(prec, ownprec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeCast(JCTypeCast tree) { checkDigestion(tree);
        try {
            open(prec, TreeInfo.prefixPrec);
            print("(");
            printExpr(tree.clazz);
            print(")");
            printExpr(tree.expr, TreeInfo.prefixPrec);
            close(prec, TreeInfo.prefixPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeTest(JCInstanceOf tree) { checkDigestion(tree);
        try {
            open(prec, TreeInfo.ordPrec);
            printExpr(tree.expr, TreeInfo.ordPrec);
            print(" instanceof ");
            printExpr(tree.clazz, TreeInfo.ordPrec + 1);
            close(prec, TreeInfo.ordPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitIndexed(JCArrayAccess tree) { checkDigestion(tree);
        try {
            printExpr(tree.indexed, TreeInfo.postfixPrec);
            print("[");
            printExpr(tree.index);
            print("]");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitSelect(JCFieldAccess tree) { checkDigestion(tree);
        try {
            printExpr(tree.selected, TreeInfo.postfixPrec);
            print("." + tree.name);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitReference(JCMemberReference tree) { checkDigestion(tree);
        try {
            printExpr(tree.expr);
            print("::");
            if (tree.typeargs != null) {
                print("<");
                printExprs(tree.typeargs);
                print(">");
            }
            print(tree.getMode() == ReferenceMode.INVOKE ? tree.name : "new");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitIdent(JCIdent tree) { checkDigestion(tree);
        try {
            print(tree.name);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLiteral(JCLiteral tree) { checkDigestion(tree);
        try {
            switch (tree.typetag) {
                case INT:
                    print(tree.value.toString());
                    break;
                case LONG:
                    print(tree.value + "L");
                    break;
                case FLOAT:
                    print(tree.value + "F");
                    break;
                case DOUBLE:
                    print(tree.value.toString());
                    break;
                case CHAR:
                    print("\'" +
                            Convert.quote(
                            String.valueOf((char)((Number)tree.value).intValue())) +
                            "\'");
                    break;
                case BOOLEAN:
                    print(((Number)tree.value).intValue() == 1 ? "true" : "false");
                    break;
                case BOT:
                    print("null");
                    break;
                default:
                    print("\"" + Convert.quote(tree.value.toString()) + "\"");
                    break;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeIdent(JCPrimitiveTypeTree tree) { checkDigestion(tree);
        try {
            switch(tree.typetag) {
                case BYTE:
                    print("byte");
                    break;
                case CHAR:
                    print("char");
                    break;
                case SHORT:
                    print("short");
                    break;
                case INT:
                    print("int");
                    break;
                case LONG:
                    print("long");
                    break;
                case FLOAT:
                    print("float");
                    break;
                case DOUBLE:
                    print("double");
                    break;
                case BOOLEAN:
                    print("boolean");
                    break;
                case VOID:
                    print("void");
                    break;
                default:
                    print("error");
                    break;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeArray(JCArrayTypeTree tree) { checkDigestion(tree);
        try {
            printBaseElementType(tree);
            printBrackets(tree);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Prints the inner element type of a nested array
    private void printBaseElementType(JCTree tree) throws IOException {
        printExpr(TreeInfo.innermostType(tree));
    }

    // prints the brackets of a nested array in reverse order
    // tree is either JCArrayTypeTree or JCAnnotatedTypeTree
    private void printBrackets(JCTree tree) throws IOException {
        JCTree elem = tree;
        while (true) {
            if (elem.hasTag(ANNOTATED_TYPE)) {
                JCAnnotatedType atype = (JCAnnotatedType) elem;
                elem = atype.underlyingType;
                if (elem.hasTag(TYPEARRAY)) {
                    print(' ');
                    printTypeAnnotations(atype.annotations);
                }
            }
            if (elem.hasTag(TYPEARRAY)) {
                print("[]");
                elem = ((JCArrayTypeTree)elem).elemtype;
            } else {
                break;
            }
        }
    }

    public void visitTypeApply(JCTypeApply tree) { checkDigestion(tree);
        try {
            printExpr(tree.clazz);
            print("<");
            printExprs(tree.arguments);
            print(">");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeUnion(JCTypeUnion tree) { checkDigestion(tree);
        try {
            printExprs(tree.alternatives, " | ");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeIntersection(JCTypeIntersection tree) { checkDigestion(tree);
        try {
            printExprs(tree.bounds, " & ");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeParameter(JCTypeParameter tree) { checkDigestion(tree);
        try {
            if (tree.annotations.nonEmpty()) {
                this.printTypeAnnotations(tree.annotations);
            }
            print(tree.name);
            if (tree.bounds.nonEmpty()) {
                print(" extends ");
                printExprs(tree.bounds, " & ");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitWildcard(JCWildcard tree) { checkDigestion(tree);
        try {
            print(tree.kind);
            if (tree.kind.kind != BoundKind.UNBOUND)
                printExpr(tree.inner);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitTypeBoundKind(TypeBoundKind tree) { checkDigestion(tree);
        try {
            print(String.valueOf(tree.kind));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitErroneous(JCErroneous tree) { checkDigestion(tree);
        try {
            print("(ERROR)");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLetExpr(LetExpr tree) { checkDigestion(tree);
        try {
            print("(let " + tree.defs + " in " + tree.expr + ")");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitModifiers(JCModifiers mods) {
        try {
            printAnnotations(mods.annotations);
            printFlags(mods.flags);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAnnotation(JCAnnotation tree) { checkDigestion(tree);
        try {
            print("@");
            printExpr(tree.annotationType);
            print("(");
            printExprs(tree.args);
            print(")");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAnnotatedType(JCAnnotatedType tree) { checkDigestion(tree);
        try {
            if (tree.underlyingType.hasTag(SELECT)) {
                JCFieldAccess access = (JCFieldAccess) tree.underlyingType;
                printExpr(access.selected, TreeInfo.postfixPrec);
                print(".");
                printTypeAnnotations(tree.annotations);
                print(access.name);
            } else if (tree.underlyingType.hasTag(TYPEARRAY)) {
                printBaseElementType(tree);
                printBrackets(tree);
            } else {
                printTypeAnnotations(tree.annotations);
                printExpr(tree.underlyingType);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTree(JCTree tree) { checkDigestion(tree);
        try {
            print("(UNKNOWN: " + tree + ")");
            println();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public ArrayList<String> getStrings() {
        return strings;
    }

    public void addDigestionPoint(Tree.Kind treeKind) {
        if(!digestionPoint.contains(treeKind)) {
            digestionPoint.add(treeKind);
        }
    }

    public void checkDigestion(JCTree tree) {
        Tree.Kind kind = tree.getKind();
        kindList.add(kind);
    }
    
    private void setMethodName(String methodName){
        if(methodsSeen == 0){
            kindList.setMethodName(methodName);
            methodsSeen++;
        }
    }

    public String methodName = "";

}
