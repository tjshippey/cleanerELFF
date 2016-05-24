/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.HashMap;

/**
 *
 * @author ubamass
 */
public class ParameterParsing {
    
        private static final HashMap<String,String> lookup=makeLookup();
    
    
    public static String getParamaterType(String argument) {
        String clean=argument.replace("[]", "");
        int p=(argument.length()-clean.length())/2;
        if(clean.contains("...")){
            clean = clean.replaceAll("\\.{3}", "");
            p++;
        }        
        String result=lookup.get(clean);
        if (result==null){
//            int lastDot = clean.lastIndexOf(".");
//            clean = clean.substring(lastDot+1);
            int slash = clean.lastIndexOf(".");
            if(slash>0){
                clean = clean.substring(slash+1);
            }
            result="L"+clean.replace('.', '/')+";";
        }
        while (p>0){
            result="["+result;
            p--;
        }
        result = result.replaceAll("<.*>", "");
        return result;
    }
    
        private static HashMap<String, String> makeLookup() {
        HashMap<String,String> result=new HashMap<>();
        result.put("boolean", "Z");
        result.put("byte", "B");
        result.put("char", "C");
        result.put("short", "S");
        result.put("int", "I");
        result.put("long", "J");
        result.put("float", "F");
        result.put("double", "D");
        result.put("void", "V");
        return result;
     }
    
}
