/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Strew;

/**
 *
 * @author ubamass
 */
public class StrewMetrics {
    
    public StrewMetrics(){
        
    }
    
    public double calculateSM1(int numberOfAssertions, int sloc){
        double result = 0.0;
        if(sloc != 0){
            result = (double) numberOfAssertions/ (double) sloc;
        }
        return result;
    }
    
}
