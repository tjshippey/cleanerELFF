/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

import com.sun.tools.javac.tree.JCTree;

/**
 *
 * @author ubamass
 */
    public class AnonymousMethod {
        
        private JCTree.JCMethodDecl methodTree;
        private String className;
        
        public AnonymousMethod(JCTree.JCMethodDecl methodTree, String className){
            this.methodTree = methodTree;
            this.className = className;
        }

        /**
         * @return the methodTree
         */
        public JCTree.JCMethodDecl getMethodTree() {
            return methodTree;
        }

        /**
         * @return the className
         */
        public String getClassName() {
            return className;
        }
        
        
    }
