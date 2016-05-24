/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

import java.util.ArrayList;

/**
 *
 * @author ubamass
 */
public class Asserts extends ArrayList<Integer>{
   
    
    public Asserts(){
        super();
    }
    
    public void addAssert(int lineNumber){
        this.add(lineNumber);
    }

    /**
     * @return the numberOfAsserts
     */
    public int getNumberOfAsserts() {
        return this.size();
    }

    /**
     * @return the lineNumbersOfAsserts
     */
    public ArrayList<Integer> getLineNumbersOfAsserts() {
        return this;
    }
    
    
    @Override
    public String toString(){
        return getNumberOfAsserts() + "\t" + getLineNumberStr();
    }

    private String getLineNumberStr() {
        String result = "lineNumbers = (";
        for(int lineNumber : this.getLineNumbersOfAsserts()){
            result += lineNumber + ",";
        }
        result = result.substring(0, result.lastIndexOf(","));
        result += ")";
        return result;
    }
}
