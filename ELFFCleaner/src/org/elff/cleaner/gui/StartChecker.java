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
public class StartChecker {
    
    public static void main(String[] args){
        String fileStr = "/Users/ubamass/Google Drive/Brunel/methodsForWednesday.csv";
        CheckCorrectness checkCorrectness = new CheckCorrectness(fileStr);
        java.awt.EventQueue.invokeLater(() -> {
        checkCorrectness.setVisible(true);
        });
    }
    
}
