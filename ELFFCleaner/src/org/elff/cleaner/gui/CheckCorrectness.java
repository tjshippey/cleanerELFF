/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.elff.cleaner.gui;

import Method.Method;
import au.com.bytecode.opencsv.CSVReader;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import org.elff.cleaner.MethodFlag;

/**
 *
 * @author ubamass
 */
public class CheckCorrectness extends javax.swing.JFrame {
    
    private final HashMap<Integer,CheckerMethod> methodsFile;
    private final HashMap<Integer, Method> methods;
    private String currentFaultLevel;
    private String currentMethodType;
    private String topDir = "/Users/ubamass/Google Drive/Brunel/Wednesday/WednesdayMethods_";
    private boolean fileFound;

    /**
     * Creates new form CheckCorrectness
     * @param methods
     */
    public CheckCorrectness(ArrayList<Method> methods) {
        this.methods = new HashMap<>();
        for(Method method : methods){
            this.methods.put(method.getMethodId(), method);
        }
        methodsFile = new HashMap<>();
        //String file = "/Users/ubamass/Seafile/ELFF/TestData/SystemMethods/E2.0MethodsWithFlags.csv";
        //fillMethods(methodsFile);
        initComponents();
        initialiseIDComboBox();
    }
    
    public CheckCorrectness(String csvFile){
        this.methods = new HashMap<>();
        this.methodsFile = new HashMap<>();        
        initComponents();
        this.methodTypeComboBox.setSelectedItem("Normal");
        this.faultCheckBox.setSelected(true);
        this.nonFaultyCheckBox.setSelected(false);
        this.currentFaultLevel = "faulty";
        this.currentMethodType = "Normal";
        reloadMethods();
    }

    private void initialiseIDComboBox() throws NumberFormatException {
        this.methodIDComboBox.removeAllItems();
        if(!methods.isEmpty()){
            for(Method method : methods.values()){
            //System.out.println(method.getMethodId());
            methodIDComboBox.addItem(method.getMethodId());
            }
            this.methodIDComboBox.setSelectedItem(0);
            int id = Integer.parseInt(this.methodIDComboBox.getSelectedItem().toString());
            changeInfo(id);
        }
    }

    private void fillMethods(String csvFile) {
        try {
            this.methodIDComboBox.setEnabled(true);
            CSVReader reader = new CSVReader(new FileReader(csvFile),'\t');
            String[] nextLine = reader.readNext(); //header line
            while((nextLine = reader.readNext()) != null){
                //System.out.println(nextLine[0]);
                Method method = getMethodFromCSVLine(nextLine);
                methods.put(method.getMethodId(), method);
            }
            fileFound = true;
        } catch (FileNotFoundException ex) {
            System.out.println(csvFile + " not found");
            this.methodIDComboBox.setEnabled(false);
            setUpNoMethods();
            fileFound = false;
            //Logger.getLogger(CheckCorrectness.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckCorrectness.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        fileTxt = new javax.swing.JLabel();
        classNameTxt = new javax.swing.JLabel();
        classTypeTxt = new javax.swing.JLabel();
        methodNameTxt = new javax.swing.JLabel();
        methodTypeTxt = new javax.swing.JLabel();
        startLineTxt = new javax.swing.JLabel();
        flagTxt = new javax.swing.JLabel();
        methodIDComboBox = new javax.swing.JComboBox();
        methodTypeComboBox = new javax.swing.JComboBox();
        faultCheckBox = new javax.swing.JCheckBox();
        nonFaultyCheckBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileTextPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("File:");

        jLabel2.setText("Class Name:");

        jLabel3.setText("Method Name:");

        jLabel4.setText("Method Type:");

        jLabel5.setText("Class Type");

        jLabel6.setText("StartLine:");

        jLabel7.setText("Flag:");

        fileTxt.setText("jLabel8");

        classNameTxt.setText("jLabel8");

        classTypeTxt.setText("jLabel8");

        methodNameTxt.setText("jLabel8");

        methodTypeTxt.setText("jLabel8");

        startLineTxt.setText("jLabel8");

        flagTxt.setText("jLabel8");

        methodIDComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                methodIDComboBoxItemStateChanged(evt);
            }
        });
        methodIDComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodIDComboBoxActionPerformed(evt);
            }
        });

        methodTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Abstract", "Anonymous", "Constructor", "Empty Constructor", "Empty Method", "Getter", "Interface", "Normal", "Setter" }));
        methodTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodTypeComboBoxActionPerformed(evt);
            }
        });

        faultCheckBox.setText("Faulty");
        faultCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faultCheckBoxActionPerformed(evt);
            }
        });

        nonFaultyCheckBox.setText("Non Faulty");
        nonFaultyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nonFaultyCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(methodNameTxt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(methodTypeTxt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(classTypeTxt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startLineTxt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flagTxt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileTxt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(methodIDComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(methodTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(faultCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nonFaultyCheckBox))
                            .addComponent(classNameTxt))))
                .addGap(0, 163, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(methodIDComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(methodTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(faultCheckBox)
                    .addComponent(nonFaultyCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(fileTxt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(classNameTxt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(classTypeTxt))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(methodNameTxt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(methodTypeTxt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(startLineTxt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(flagTxt))
                .addContainerGap())
        );

        for(Method method : methods.values()){
            //System.out.println(method.getMethodId());
            methodIDComboBox.addItem(method.getMethodId());
        }

        fileTextPane.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fileTextPaneCaretUpdate(evt);
            }
        });
        fileTextPane.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                fileTextPaneCaretPositionChanged(evt);
            }
        });
        jScrollPane1.setViewportView(fileTextPane);
        TextLineNumber tln = new TextLineNumber(fileTextPane);
        this.jScrollPane1.setRowHeaderView(tln);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void methodIDComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodIDComboBoxActionPerformed
        // TODO add your handling code here:
        /*System.out.println(evt.paramString());
        String comboBoxId = this.methodIDComboBox.getSelectedItem().toString();
        int id = Integer.parseInt(comboBoxId);
        changeInfo(id);*/
    }//GEN-LAST:event_methodIDComboBoxActionPerformed

    private void fileTextPaneCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_fileTextPaneCaretPositionChanged
        // TODO add your handling code here:
        //int line=evt.getCaret().
    }//GEN-LAST:event_fileTextPaneCaretPositionChanged

    private void fileTextPaneCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_fileTextPaneCaretUpdate
        /*        // TODO add your handling code here:
        //System.out.println("updated caret");
        Element map = fileTextPane.getDocument().getDefaultRootElement();
        int offset = evt.getDot();
        System.out.println("Offset = " + offset);
        if(offset >= 0 && fileTextPane.getDocument().getLength() >= offset){
        //System.out.println("offset found");
        Method chosenMethod = null;
        int line = map.getElementIndex(offset);
        for(Method meth : methods.values()){
        int startLine = Integer.parseInt(meth.getStartLine());
        int endLine = Integer.parseInt(meth.getEndLine());
        if(line >= startLine && line <= endLine){
        //System.out.println("Should have changed method!");
        methodIDComboBox.setSelectedItem(meth.getMethodId());
        }
        }
        }*/
    }//GEN-LAST:event_fileTextPaneCaretUpdate

    private void methodTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodTypeComboBoxActionPerformed
        // TODO add your handling code here:
        currentMethodType = (String) this.methodTypeComboBox.getSelectedItem();
        reloadMethods();
    }//GEN-LAST:event_methodTypeComboBoxActionPerformed

    private void faultCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faultCheckBoxActionPerformed
        // TODO add your handling code here:
        this.nonFaultyCheckBox.setSelected(false);
        this.faultCheckBox.setSelected(true);
        currentFaultLevel = "faulty";
        reloadMethods();
    }//GEN-LAST:event_faultCheckBoxActionPerformed

    private void nonFaultyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonFaultyCheckBoxActionPerformed
        // TODO add your handling code here:
        this.faultCheckBox.setSelected(false);
        this.nonFaultyCheckBox.setSelected(true);
        currentFaultLevel = "nonFaulty";
        reloadMethods();
    }//GEN-LAST:event_nonFaultyCheckBoxActionPerformed

    private void methodIDComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_methodIDComboBoxItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            //System.out.println(evt.paramString());
            String comboBoxId = this.methodIDComboBox.getSelectedItem().toString();
            int id = Integer.parseInt(comboBoxId);
            changeInfo(id);
        }
    }//GEN-LAST:event_methodIDComboBoxItemStateChanged

    /*       /**
    * @param args the command line arguments
    /
    /*public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
    * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
    */
/*    try {
    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
            javax.swing.UIManager.setLookAndFeel(info.getClassName());
            break;
        }
    }
} catch (ClassNotFoundException ex) {
    java.util.logging.Logger.getLogger(CheckCorrectness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
} catch (InstantiationException ex) {
    java.util.logging.Logger.getLogger(CheckCorrectness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
} catch (IllegalAccessException ex) {
    java.util.logging.Logger.getLogger(CheckCorrectness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
} catch (javax.swing.UnsupportedLookAndFeelException ex) {
    java.util.logging.Logger.getLogger(CheckCorrectness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
}
//</editor-fold>

/* Create and display the form */
/*java.awt.EventQueue.invokeLater(() -> {
    new CheckCorrectness().setVisible(true);
});
}*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel classNameTxt;
    private javax.swing.JLabel classTypeTxt;
    private javax.swing.JCheckBox faultCheckBox;
    private javax.swing.JTextPane fileTextPane;
    private javax.swing.JLabel fileTxt;
    private javax.swing.JLabel flagTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox methodIDComboBox;
    private javax.swing.JLabel methodNameTxt;
    private javax.swing.JComboBox methodTypeComboBox;
    private javax.swing.JLabel methodTypeTxt;
    private javax.swing.JCheckBox nonFaultyCheckBox;
    private javax.swing.JLabel startLineTxt;
    // End of variables declaration//GEN-END:variables

    private void changeInfo(int id){
        fileTextPane.getHighlighter().removeAllHighlights();
        Method method = null;
        if (!methods.isEmpty()){
            method = methods.get(id);
        }
        
        if(method != null){            
            String file = method.getFile();
            //System.out.println(System.getProperty("os.name"));
            if(System.getProperty("os.name").startsWith("Mac")){                
                file = file.replace("\\", "/");
                file = file.replace("D:/", "/Users/ubamass/");
            } else if(System.getProperty("os.name").startsWith("Win")){
                file = file.replace("\\", "/");
                file = file.replace("/Users/ubamass/","D:/");
            }
            String thisFile = file.replace("/Users/ubamass/Google Drive/Brunel/", "");
            this.fileTxt.setText(thisFile);
            this.classNameTxt.setText(method.getClassName());
            this.classTypeTxt.setText(method.getClassType());
            this.methodNameTxt.setText(method.getMethodName());
            this.methodTypeTxt.setText(method.getMethodType());
            this.startLineTxt.setText(method.getStartLine());
            this.flagTxt.setText(method.getFlag().toString());
            fillTextArea(file, method.getCode(), method.getStartLine(), method.getEndLine());
            this.setCaretPosition(method.getStartLine(), method.getEndLine());
        }        
    }
    
    private void fillTextArea(String file, String code, String startLine, String endLine){
        try {
            File textFile = new File(file);
            this.fileTextPane.read(new FileReader(textFile), null);
            //for(Method meth : methods.values()){
                //Color colour = this.getColour(meth.getFlag());
                //this.paintLines(meth.getStartLine(), meth.getEndLine(), colour);
            //}
            paintLines(startLine, endLine, Color.CYAN); //paint our selected method
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CheckCorrectness.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckCorrectness.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void paintLines(String startLine, String endLine, Color colour) {
        Element root = fileTextPane.getDocument().getDefaultRootElement();
        try {
            Highlighter.HighlightPainter painter;
            painter = new DefaultHighlighter.DefaultHighlightPainter(colour);
            int startLineInt = Integer.parseInt(startLine);
            int endLineInt = Integer.parseInt(endLine);
            if(startLineInt == 0){
                startLineInt = 1;
            }
            Element startLineElement = root.getElement(startLineInt - 1);
            Element endLineElement = root.getElement(endLineInt - 1);
            //System.out.println(startLineElement.getStartOffset() + endLineElement.getEndOffset() + colour.toString());
            fileTextPane.getHighlighter().addHighlight(startLineElement.getStartOffset(), 
                    endLineElement.getEndOffset(), painter);
        } catch (BadLocationException ex) {
            Logger.getLogger(CheckCorrectness.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setCaretPosition(String startLine, String endLine){
        int startLineInt = Integer.parseInt(startLine);
        if(startLineInt == 0){
                startLineInt = 1;
            }
        int endLineInt = Integer.parseInt(endLine);
        int middle = (startLineInt + endLineInt)/2;
        Element root = fileTextPane.getDocument().getDefaultRootElement();
        int startPosition = root.getElement(middle - 1).getStartOffset();
        this.fileTextPane.setCaretPosition(startPosition);
    }
    
    private Color getColour(MethodFlag flag){
        Color colour = Color.BLACK;
        switch (flag) {
            case ABSTRACT:
                colour = Color.BLUE;
                break;
            case GETTER:
                colour = Color.CYAN;
                break;
            case SETTER:
                colour = Color.ORANGE;
                break;
            case INTERFACE:
                colour = Color.YELLOW;
                break;
            case ANONYMOUS:
                colour = Color.GRAY;
                break;
            case CONSTRUCTOR:
                colour = Color.MAGENTA;
                break;
            case EMPTY_CONSTRUCTOR:
                colour = Color.DARK_GRAY;
                break;
            case NORMAL:
                colour = Color.GREEN;
                break;
            case EMPTY_METHOD:
                colour = Color.PINK;
                break;
        }
        return colour;
    }

    private Method getMethodFromCSVLine(String[] line) {
        String[] nextLine = line[0].split(",");
        if(nextLine[1] == null){
            nextLine = line[0].split("\t");
        }
        String id = nextLine[0];
        String packageStr = nextLine[1];
        String fileName = nextLine[2];
        String className = nextLine[3];
        String classType = nextLine[4];
        String methodName = nextLine[5];
        String methodType = nextLine[6];
        String sequence = nextLine[7];
        String startLine = nextLine[8];
        String endLine = nextLine[9];
        String flag = nextLine[10];
        Method method = new Method(id, packageStr, fileName, className, classType,
            methodName, methodType, sequence, startLine, endLine, flag, null);
        return method;
        
    }

    private void reloadMethods() {
        String methodType = this.currentMethodType.replace(" ", "_");
        String file = topDir + methodType + "_" + currentFaultLevel + ".csv";
        this.methods.clear();
        this.fillMethods(file);
        this.initialiseIDComboBox();
        this.repaint();
      
    }

    private void setUpNoMethods() {
        this.fileTxt.setText("No "+ this.currentMethodType + " methods are " + this.currentFaultLevel);
        this.classNameTxt.setText("");
        this.classTypeTxt.setText("");
        this.methodNameTxt.setText("");
        this.methodTypeTxt.setText("");
        this.startLineTxt.setText("");
        this.flagTxt.setText("");
        this.fileTextPane.setText("");
    }
}
