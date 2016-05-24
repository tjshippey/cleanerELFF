/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

import Utils.ParameterParsing;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import static com.sun.tools.javac.code.Flags.ENUM;
import static com.sun.tools.javac.code.Flags.INTERFACE;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Modifier;
import static javax.lang.model.element.Modifier.ABSTRACT;
import javax.tools.*;
import org.elff.cleaner.FlagMethods;

/**
 *
 * @author Thomas Shippey - University of Hertfordshire
 * @version 16-Oct-2013
 */
public class MethodGrabber extends MyPretty8 {    

    private ArrayList<Method> methods = new ArrayList<>();
    private final File file;
    private SourcePositions sourcePositions;
    //private JavacTask javacTask;
    //private Iterable<? extends CompilationUnitTree> parseResult;
    private CompilationUnitTree cut;
    private String currentPackageName;
    private String className;
    private String classType;
    private final FlagMethods flagMethods;
    private int classVisits;
    private boolean testClass;
    private int currentClassLevel;
    HashMap<Integer,String> classNames;
    private HashMap<String, Integer> anonClassNamesVisited;
    private JCTree.JCClassDecl currentClass;
    private int classLevel;
    private boolean justReturnStringsForFlags;
    private ArrayList<String> methodStrings;
    private boolean canParseFile;

    public MethodGrabber(File file) {
        super(null); //UGLY please rewrite
        //super(new StringWriter(), false);        
        this.file = file;
        sourcePositions = null;
        flagMethods = new FlagMethods();
        classLevel=0;
        canParseFile = false;
    }

    public void findAllMethods() {
        Iterable<? extends CompilationUnitTree> parseResult = null;
                try {
            JavacTask javacTask = astParseFile(file);
            if(javacTask != null){
                parseResult = javacTask.parse();
                sourcePositions = Trees.instance(javacTask).getSourcePositions();               
            }
        } catch (IOException ex) {
            Logger.getLogger(MethodGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (CompilationUnitTree c : parseResult) {
            cut = c;           
            try {
                currentPackageName = cut.getPackageName().toString();
                testClass = checkTestClass(cut);
            } catch (java.lang.NullPointerException ex) {
                currentPackageName = "UNKNOWN";
            }
            JCCompilationUnit topLevel = (JCCompilationUnit) cut;
            this.classNames = new HashMap<>();
            visitTopLevel(topLevel);
        }
    }
    
    public ArrayList<String> findAllMethodStrings(){
        justReturnStringsForFlags = true;
        methodStrings = new ArrayList<>();
        findAllMethods();
        return methodStrings;
    }
    
    @Override
    public void visitClassDef(JCClassDecl tree) {
        classVisits++;
        classLevel++;
        String thisClassName = tree.getSimpleName().toString();
        //System.out.println(tree.getSimpleName().toString());
        populateClassName(thisClassName);
        checkClassType(tree);
        currentClass = tree;
        grabMethodsFromClass(tree.getMembers());
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
        classLevel--;
    }

    private void populateClassName(String thisClassName) {
        if(thisClassName.contains("<")){
            thisClassName = thisClassName.substring(0,thisClassName.indexOf("<"));
        }
        int newClassLevel = classLevel;
        //System.out.println(this.lmargin);
        if(newClassLevel == 1){
            classNames.put(1, thisClassName);
            className = thisClassName + "";
        }
        else {
            if(newClassLevel > currentClassLevel){
                classNames.put(newClassLevel, thisClassName);
                className = className + "$" + thisClassName;
            }
            else if(newClassLevel < currentClassLevel){
                for(int i = currentClassLevel; i >= newClassLevel; i--){
                    String classNameRemove = "$" + classNames.get(i);
                    className = className.replace(classNameRemove,"");
                    classNames.remove(i);
                }
                classNames.put(newClassLevel, thisClassName);
                className = className + "$" + thisClassName;
            }
            else {
                String classNameRemove = "$" + classNames.get(newClassLevel);
                className = className.replace(classNameRemove, "$" + thisClassName);
                classNames.put(newClassLevel, thisClassName);
            }
        }
        currentClassLevel = newClassLevel;
        //System.out.println(className);
        
    }

    private void grabMethodsFromClass(List<JCTree> trees) {
        anonClassNamesVisited = new HashMap<>();
        AnonymousMethodGrabber anonMethodGrabber = new AnonymousMethodGrabber();
        for (JCTree member : trees) {
            if (member instanceof JCTree.JCMethodDecl) {
                JCMethodDecl method = (JCMethodDecl) member;
                method.accept(anonMethodGrabber);//check for anonymous methods
                grabMethod(method);
                getAnonymousMethods(anonMethodGrabber);
                anonMethodGrabber.removeMethods();
            } else if(member instanceof JCTree.JCVariableDecl){
                grabMethodsFromField((JCVariableDecl) member, anonMethodGrabber);
            }
        }
    }

    private void getAnonymousMethods(AnonymousMethodGrabber anonMethodGrabber) {
        if (anonMethodGrabber.getAnonymousMethods().size() > 0) {
            classLevel++;
            currentClassLevel++;
            for (AnonymousMethod anonMethod : anonMethodGrabber.getAnonymousMethods()) {
                classType = "ANONYMOUS";
                String classNameExtension = anonMethod.getClassName();
                className = className + classNameExtension;
                grabMethod(anonMethod.getMethodTree());
                className = className.replace(classNameExtension, "");
            }
            currentClassLevel--;
            classLevel--;
        }
        checkClassType(currentClass); //revert method back to normal after extracting anonymous methods
    }

    private void checkClassType(JCClassDecl tree) {
        classType = "NORMAL";
        if((tree.mods.flags & INTERFACE) !=0){
            classType = "INTERFACE";
        }
        else if((tree.mods.flags & ENUM) !=0){
            classType = "ENUM";
        }
        else if(testClass || methodsHaveTestAnnotations(tree)){
            classType = "TEST";
        }
        else if(classVisits > 1){
            classType="INNER_CLASS";
        }
        JCModifiers mods = tree.getModifiers();
        for(Modifier mod : mods.getFlags()){
            if(mod == ABSTRACT){
                classType = "ABSTRACT";
            }
        }
    }

    private void grabMethod(JCMethodDecl tree) {        
        String methodType = checkMethodType(tree);
        String methodNameStr = getMethodNameStr(tree).replaceAll((", "), "");
        MethodLines mls = new MethodLines();
        mls.getLines(cut, sourcePositions, tree);
        //Asserts asserts = AssertGrabber.getAsserts(tree, cut, sourcePositions);
        String startLine = mls.getStartLine() + "";
        String endLine = mls.getEndLine() + "";
        String code = tree.toString();
        MyPretty8 pretty = new MyPretty8(tree);
        tree.accept(pretty);
        Sequence sequence = pretty.kindList;
        Method method = new Method(currentPackageName, className, classType, methodType, methodNameStr, file.getPath(), startLine, endLine, code, sequence,null);
        flagMethods.flagMethod(method);
        if(justReturnStringsForFlags){
            grabMethodString(method);
        } else {
            methods.add(method);
        }
    }
    
    private void grabMethodString(Method method){
        String methodStr = method.getPackageName()+"."+method.getClassName() + "\t" + method.getMethodName() + "\t"
               + method.getSequenceString() + "\t" + method.getFlag().name();
        methodStrings.add(methodStr);
    }

    private String getMethodNameStr(JCMethodDecl tree) {
        //String methodParams = getMethodParams(tree,paramNames);
        //String treeName = tree.getName().toString();
        String methodNameStr = createUnifiedMethodName(tree);
        if(methodNameStr.contains("<init>")){
            int lastDollar = className.lastIndexOf("$");
            String classNameTemp = className.substring(lastDollar + 1);
            methodNameStr = methodNameStr.replaceFirst("<init>",classNameTemp);
        }
        //treeName + methodParams;
        return methodNameStr;
    }
    
    private String getMethodParams(JCMethodDecl method, boolean paramNames){
        String result = "(";
        int numberParams = 0;
        for(JCVariableDecl param : method.params){
            String paramStr = param.getType().toString();
            String paramName = "";
            //System.out.println(paramStr + " = paramStr");
            if(paramNames){
                paramName = " " + param.name.toString();
            }
            int angleBracket = paramStr.indexOf("<");
            if(angleBracket > -1){
                paramStr = paramStr.substring(0, angleBracket);
            }
            int lastDot = paramStr.lastIndexOf(".");
            if(lastDot > -1){
                paramStr = paramStr.substring(lastDot + 1);              
            }
            result += paramStr + paramName + ", ";
        }
        int lastComma = result.lastIndexOf(",");
        if(lastComma > 0){
            result = result.substring(0, lastComma);
        }
        result = result + ")";
        return result;
    }

    private String checkMethodType(JCMethodDecl tree) {
        String methodType = "NORMAL";
        JCModifiers mods = tree.mods;
        for(Modifier mod : mods.getFlags()){
            if(mod == Modifier.ABSTRACT){
                methodType = "ABSTRACT";
            }
        }
        if(classType.equals("INTERFACE")){
            methodType = "INTERFACE";
        }
        if(classType.equals("ANONYMOUS")){
            methodType = "ANONYMOUS";
        }
        if(testClass){
            methodType = "TEST";
        }
        return methodType;
    }

    /**
     * @return the methods
     */
    public ArrayList<Method> getMethods() {
        return methods;
    }
    
    public final JavacTask astParseFile(File file) throws IOException {
        JavacTask javacTask = null;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
        if(canParseFile(compiler, fileManager, diagnosticsCollector, fileObjects)){
            javacTask = (JavacTask) task;
            canParseFile = true;
        }
        return javacTask;
    }

    private boolean canParseFile(JavaCompiler compiler, StandardJavaFileManager fileManager, DiagnosticCollector<JavaFileObject> diagnosticsCollector, Iterable<? extends JavaFileObject> fileObjects) {
        boolean result = true;
        JavaCompiler.CompilationTask testTask = compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
            testTask.call();
            if (diagnosticsCollector.getDiagnostics().size() >= 0) {
                //System.out.println(diagnosticsCollector.getDiagnostics().size());
                for (Diagnostic dia : diagnosticsCollector.getDiagnostics()) {
                    //System.out.println(dia.getMessage(Locale.ENGLISH));
                    if("compiler.err.premature.eof".equals(dia.getCode())){
                        result = false;
                        //System.out.println(dia.getMessage(Locale.ENGLISH));
                    }                    
                }
            }
        return result;
    }

    private boolean checkTestClass(CompilationUnitTree cut) {
        boolean result = false;
        //for(ImportTree anImport : cut.getImports()){
            //JCImport jcImport = (JCImport) anImport;
            //System.out.println(jcImport.qualid.toString());
//            if(jcImport.qualid.toString().contains("junit.") || jcImport.qualid.toString().contains("org.hamcrest.Matchers;")){
//                result = true;
//            }
            if(cut.getSourceFile().getName().endsWith("Test.java")){
                result = true;
            } else if (cut.getPackageName().toString().contains(".test.")){
                result = true;
            } else if(cut.getSourceFile().getName().contains("/test/")){
                result = true;
            }
            
        //}
        return result;
    }

    /**
     * @return the javacTask
     */
    public boolean isParsable() {
        return canParseFile;
    }

    private void grabMethodsFromField(JCTree.JCVariableDecl tree, AnonymousMethodGrabber anonMethodGrabber) {
        tree.accept(anonMethodGrabber);
        getAnonymousMethods(anonMethodGrabber);
        anonMethodGrabber.removeMethods();
    }
    
    private String createUnifiedMethodName(JCMethodDecl tree) {
        StringBuilder result = new StringBuilder();
        //result.append(className.replace('.', '/')).append(' ');
        result.append(tree.getName().toString());
        result.append("(");
        for(JCTree.JCVariableDecl argument : tree.params){
            JCTree.JCExpression vt = argument.vartype;
            String parameter = ParameterParsing.getParamaterType(vt+"");
            result.append(parameter);
        }
        result.append(")");
        //result.append(getParamaterType(returnType));
        return result.toString();
    }

    private boolean hasTestAnnotation(JCMethodDecl tree) {
        boolean result = false;
        List<JCTree.JCAnnotation> annotations = tree.mods.annotations;
        for( JCTree.JCAnnotation annotation : annotations){
            String annotationStr = annotation.toString();
            result = TestAnnotation.isInList(annotationStr);
            
        }
        return result;
    }

    private boolean methodsHaveTestAnnotations(JCClassDecl tree) {
        boolean result = false;
        List<JCTree> members = tree.getMembers();
        for(JCTree member : members){
            if(member instanceof JCTree.JCMethodDecl){
                result = hasTestAnnotation((JCMethodDecl) member);
            }
        }
        return result;
    }
    
    
    private class MethodLines {
        
        private long startLine;
        private long endLine;
        
        public MethodLines(){
            
        }

        public void getLines(CompilationUnitTree cut, SourcePositions sourcePositions, JCMethodDecl tree) {
            LineMap lineMap = cut.getLineMap();
            long startPosition = sourcePositions.getStartPosition(cut, tree);
            if(startPosition == -1L){            
                String methodLine = getMethodNameStr(tree);
                String file = cut.getSourceFile().toString();
                startLine = getSpecificLineFromText(file, methodLine);
            } else {
                startLine = lineMap.getLineNumber(startPosition);
            }
            long endPosition = sourcePositions.getEndPosition(cut, tree);
            endLine = lineMap.getLineNumber(endPosition);
        }
        
        public int getSpecificLineFromText(String fileUrl, String methodLine) {
            File file = new File(fileUrl);
            int lineNumber = 0;
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = "";
                while((line = bufferedReader.readLine()) != null){
                    if(line.contains(methodLine)){                        
                        return lineNumber + 1;
                    }
                    lineNumber++;
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MethodGrabber.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MethodGrabber.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return 0;
        }

        /**
         * @return the startLine
         */
        public long getStartLine() {
            return startLine;
        }

        /**
         * @return the endLine
         */
        public long getEndLine() {
            return endLine;
        }        
    }

}
