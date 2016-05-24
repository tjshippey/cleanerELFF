package Method;

import java.io.File;
import java.util.ArrayList;
import org.elff.cleaner.MethodFlag;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Thomas Shippey - University of Hertfordshire
 * @version 08-Oct-2013
 */
public class Method {
    
    private String methodName;
    private String file;
    private String startLine;
    private String endLine;
    private String code;
    private final Sequence sequence;
    private final String className;
    private final String classType;
    private final String packageName;
    private final String methodType;
    private MethodFlag flag;
    private static int totalMethods = 1000;
    private int methodId;
    private Asserts asserts;

    public Method(String packageName, String className, String classType, String methodType, String methodName, String file, String startLine, String endLine, String code, Sequence sequence, Asserts asserts) {
        this.methodName = methodName;
        this.file = file;
        this.startLine = startLine;
        this.endLine = endLine;
        this.code = code;
        this.sequence = sequence;
        this.packageName = packageName;
        this.className = className;
        this.classType = classType;
        this.methodType = methodType;
        this.flag = MethodFlag.UNDEFINED;
        this.methodId = totalMethods++;
        this.asserts = asserts;
    }

    public Method(String id, String packageStr, String fileName, String className, String classType, String methodName, String methodType, String sequence, String startLine, String endLine, String flag, Asserts asserts) {
        this.methodId = Integer.parseInt(id);
        this.className = className;
        this.classType = classType;
        this.packageName = packageStr;
        this.file = fileName;
        this.methodName = methodName;
        this.methodType = methodType;
        this.sequence = null;
        this.flag = MethodFlag.valueOf(flag);
        this.startLine = startLine;
        this.endLine = endLine;
        this.code = "";
        this.asserts = asserts;
    }
    

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the startLine
     */
    public String getStartLine() {
        return startLine;
    }

    /**
     * @param startLine the startLine to set
     */
    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    /**
     * @return the endLine
     */
    public String getEndLine() {
        return endLine;
    }

    /**
     * @param endLine the endLine to set
     */
    public void setEndLine(String endLine) {
        this.endLine = endLine;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    public Sequence getSequence(){
        return sequence;
    }
    
    public String getSequenceString(){
        String result = this.sequence.getKindSequence();
        return result;
    }
    
    public String getSequenceNumberString(){
        return sequence.getNumberSequence();
    }
    
    @Override
    public String toString(){
        String sep = "\t";
        return getMethodId() + sep + packageName + sep + file + sep + getClassName() + sep + classType + sep + methodName  + sep  
                + methodType + sep + getSequenceString() + sep + startLine
                 + sep + endLine + sep + getFlag(); //+ sep + asserts.size();
    }

    /**
     * @return the classType
     */
    public String getClassType() {
        return classType;
    }

    /**
     * @return the methodType
     */
    public String getMethodType() {
        return methodType;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(MethodFlag flag) {
        this.flag = flag;
    }

    /**
     * @return the methodId
     */
    public int getMethodId() {
        return methodId;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the flag
     */
    public MethodFlag getFlag() {
        return flag;
    }

    /**
     * @return the asserts
     */
    public Asserts getAsserts() {
        return asserts;
    }
    
    
    private String methodStringForAsserts(){
        String sep = "\t";
        return getMethodId() + sep + getPackageName() + sep + file + sep + getClassName() + sep + classType + sep + methodName  + sep  
                + methodType + sep + getSequenceString() + sep + startLine
                 + sep + endLine + sep + getFlag();
    }
    
    public ArrayList<String> getMethodAsserts(){
        ArrayList<String> methodAsserts = new ArrayList<>();
        for(int assertLine : this.asserts.getLineNumbersOfAsserts()){
            String assertStr = methodStringForAsserts() + "\t" + assertLine;
            methodAsserts.add(assertStr);
        }
        return methodAsserts;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }
    
    
    
    
    

}
