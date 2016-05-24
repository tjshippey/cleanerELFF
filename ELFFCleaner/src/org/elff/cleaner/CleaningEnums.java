/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elff.cleaner;

/**
 *
 * @author ubamass
 */
//public class CleaningEnums {
    
    public enum CleaningEnums {GETTER_NORMAL("METHOD MODIFIERS IDENTIFIER BLOCK RETURN IDENTIFIER"), 
    EMPTY_CONSTRUCTOR("METHOD"),
    SETTER_NORMAL("METHOD MODIFIERS PRIMITIVE_TYPE VARIABLE MODIFIERS IDENTIFIER BLOCK EXPRESSION_STATEMENT ASSIGNMENT MEMBER_SELECT IDENTIFIER IDENTIFIER");
        private final String sequence;
        private CleaningEnums(String sequence){
            this.sequence = sequence;
        }
    }
    
//}
