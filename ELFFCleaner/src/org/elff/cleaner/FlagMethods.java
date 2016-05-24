/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elff.cleaner;

import Method.Method;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ubamass
 */
public class FlagMethods {
    
    private final String GETTER_NORMAL ="METHOD MODIFIERS IDENTIFIER BLOCK RETURN IDENTIFIER";
    private final String SETTER_NORMAL = "BLOCK EXPRESSION_STATEMENT ASSIGNMENT MEMBER_SELECT IDENTIFIER IDENTIFIER";
    private final String GETTER_TWO ="METHOD MODIFIERS ARRAY_TYPE IDENTIFIER BLOCK RETURN IDENTIFIER";
    private final String GETTER_THREE="METHOD MODIFIERS IDENTIFIER VARIABLE MODIFIERS PRIMITIVE_TYPE BLOCK RETURN METHOD_INVOCATION MEMBER_SELECT MEMBER_SELECT IDENTIFIER IDENTIFIER";
    private final String GETTER_FOUR="METHOD MODIFIERS IDENTIFIER VARIABLE MODIFIERS PRIMITIVE_TYPE BLOCK RETURN ARRAY_ACCESS IDENTIFIER IDENTIFIER";
    private final String SETTER_TWO="BLOCK EXPRESSION_STATEMENT ASSIGNMENT MEMBER_SELECT IDENTIFIER IDENTIFIER";
    private final String SETTER_THREE="BLOCK EXPRESSION_STATEMENT METHOD_INVOCATION MEMBER_SELECT MEMBER_SELECT IDENTIFIER IDENTIFIER IDENTIFIER";
    private final String SETTER_FOUR="BLOCK EXPRESSION_STATEMENT ASSIGNMENT ARRAY_ACCESS IDENTIFIER IDENTIFIER IDENTIFIER";
    private final String SETTER_END_ONE = "BLOCK EXPRESSION_STATEMENT ASSIGNMENT MEMBER_SELECT IDENTIFIER IDENTIFIER";
    private final String SETTER_END_TWO = "BLOCK EXPRESSION_STATEMENT ASSIGNMENT IDENTIFIER IDENTIFIER";
    private final String SETTER_END_THREE = "BLOCK EXPRESSION_STATEMENT ASSIGNMENT ARRAY_ACCESS IDENTIFIER IDENTIFIER IDENTIFIER";
    private final String SETTER_END_FOUR = "BLOCK EXPRESSION_STATEMENT METHOD_INVOCATION MEMBER_SELECT IDENTIFIER IDENTIFIER IDENTIFIER";
    private final ArrayList<String> GETTERS; 
    private final ArrayList<String> SETTERS; 
    
    public FlagMethods() {
        GETTERS = new ArrayList<>();
        GETTERS.add(GETTER_NORMAL);
        GETTERS.add(GETTER_TWO);
        GETTERS.add(GETTER_THREE);
        GETTERS.add(GETTER_FOUR);
        SETTERS = new ArrayList<>();
        SETTERS.add(SETTER_NORMAL);
        SETTERS.add(SETTER_TWO);
        SETTERS.add(SETTER_THREE);
        SETTERS.add(SETTER_FOUR);
        SETTERS.add(SETTER_END_ONE);
        SETTERS.add(SETTER_END_TWO);
        SETTERS.add(SETTER_END_THREE);
        SETTERS.add(SETTER_END_FOUR);
    }

    public void flagMethods(ArrayList<Method> methods) {
        for (Method method : methods) {
            flagMethod(method);
        }
    }

    public void flagMethod(Method method) {
        MethodFlag flag;
        String methodType = method.getMethodType();
        switch (methodType) {
            case "NORMAL":
                flag = getNormalMethodFlag(method);
                break;
            case "ABSTRACT":
                flag = MethodFlag.ABSTRACT;
                break;
            case "INTERFACE":
                flag = MethodFlag.INTERFACE;
                break;
            case "ENUM": flag = MethodFlag.ENUM;
                break;
            case "ANONYMOUS": flag = MethodFlag.ANONYMOUS;
                break;
            case "TEST": flag = MethodFlag.TESTCODE;
                break;
            default: flag = MethodFlag.UNDEFINED;
                break;
        }
        method.setFlag(flag);
    }

    private MethodFlag getNormalMethodFlag(Method method) {
        MethodFlag flag = MethodFlag.NORMAL;
        //String sequence = method.getSequenceString();
        boolean emptyMethod = emptyMethodBlock(method.getCode());
        if (emptyMethod) {
            if(Character.isUpperCase(method.getMethodName().codePointAt(0))){
                flag = MethodFlag.EMPTY_CONSTRUCTOR;
            } else {
                flag = MethodFlag.EMPTY_METHOD;
            }
        } else {
            if (Character.isUpperCase(method.getMethodName().codePointAt(0))) {
                flag = MethodFlag.CONSTRUCTOR;
            } else if (checkGetter(method)) {
                flag = MethodFlag.GETTER;
            } else if (checkSetter(method)) {
                flag = MethodFlag.SETTER;
            } 
        }        
        return flag;
    }
    
    private boolean emptyMethodBlock(String code){
        boolean result = true;
        int countOfSemiColon = code.indexOf(";");
        if(countOfSemiColon > 0){
            result = false;
        }
        return result;
    }
    
    private boolean checkGetter(Method method){
        boolean result = false;
        String sequence = method.getSequenceString();
        String methodName = method.getMethodName();
        boolean startsWithGet = checkMethodGetOrSet(methodName, "get") || checkMethodGetOrSet(methodName, "is");
        if(GETTERS.contains(method.getSequenceString())){
            result = true;
        }
        else {
            int semiColons = StringUtils.countMatches(method.getCode(), ";");
            int blockReturns = StringUtils.countMatches(sequence, "BLOCK RETURN");
            if(semiColons == 1 && blockReturns == 1 && startsWithGet){
                result = true;
            }
        }
        return result;
    }
    
    private boolean checkSetter(Method method){
        boolean result = false;
        String sequence = method.getSequenceString();
        String methodName = method.getMethodName();
        boolean startsWithSet = checkMethodGetOrSet(methodName, "set");
        int semiColons = StringUtils.countMatches(method.getCode(), ";");
        if(semiColons == 1){
            if(SETTERS.contains(sequence)){
                result = true;
            }
            else if(StringUtils.endsWith(sequence, this.SETTER_END_ONE) || StringUtils.endsWith(sequence, this.SETTER_END_TWO)){
                result = true;
            }
            else if(startsWithSet){
                result = true;
            }
        }
        return result;
    }

    private boolean checkMethodGetOrSet(String methodName, String start) {
        boolean startsWithSet = StringUtils.startsWith(methodName, start) && Character.isUpperCase(methodName.charAt(3));
        return startsWithSet;
    }

}
