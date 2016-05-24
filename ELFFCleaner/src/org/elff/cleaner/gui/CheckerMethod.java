/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.elff.cleaner.gui;

/**
 *
 * @author ubamass
 */
public class CheckerMethod {
    
    String[] methodLine;
    private final int id;
    //private static int totalMethods = 1000;
    
    public CheckerMethod(String[] methodLine){
        this.methodLine = methodLine;
        this.id = Integer.parseInt(methodLine[0]);
    }
    
    public String getFile(){
        return methodLine[1];
    }
    
    public String getClassName(){
        return methodLine[2];
    }
    
    public String getClassType(){
        return methodLine[3];
    }
    
    public String getMethodName(){
        return methodLine[4];
    }
    
    public String getMethodType(){
        return methodLine[5];
    }
    
    public String getStartLine(){
        return methodLine[7];
    }
    
    public String getEndLine(){
        return methodLine[8];
    }
    
    public String getFlag(){
        return methodLine[9];
    }
    
    @Override
    public String toString(){
        return id + "";
    }

    public Integer getId() {
       return id;
    }
    
}
