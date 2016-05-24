/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sequencer;

import Method.Method;
import Method.MethodGrabber;
import Utils.FilePrinter;
import Utils.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elff.cleaner.FlagMethods;

/**
 *
 * @author ubamass
 */
public class GetMethods {
    private static String root="";
    public static String getRoot(){
        if (root==null){
            root = "file:///Volumes/SkyDisk";
            try {
                Properties prop = new Properties();
                FileInputStream input = new FileInputStream("nbproject/private/config.properties");

                // load a properties file
                prop.load(input);
                String proot = prop.getProperty("root");
                if (proot != null) {
                    root = proot;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
        }
        return root;
    }
    
    String topDir;
    ArrayList<Method> methods;
    ArrayList<String> methodStrings;
    private final String HEADER = "Id \t Package \t File \t ClassName \t ClassType \t MethodName \t"
            + "MethodType \t Sequence \t StartLine \t EndLine \t Flag";
    
    /*public static void main(String[] args){
    
    
    
    String[] projects = {"pacman", "penfold", "hector", "knitware"};
    String path="/repos/trunk/netstream/knitware/knitware-core";
    String[] sandboxes={""};
    String[] fileNames={"all.csv"};
    
    for (int i=0;i<projects.length;i++){
    
    String lpath = "netstream/" + projects[i] + "/" + projects[i] + "-core";
    String topDir="/Users/comqdhb/Desktop/sky/snapshot/"+lpath;
    // = "../ELFF_Filter_Tests";
    //String topDir = "/src/";
    GetMethods gm = new GetMethods(topDir);
    gm.getAllMethods();
    //CheckCorrectness checkCorrectness = new CheckCorrectness(gm.methods);
    /* Create and display the form 
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            //checkCorrectness.setVisible(true);
        }
});

//gm.flagAllMethods();
printCSVMethods(gm,projects[i]+".csv");
}
}*/
    
    public static void main(String[] args){
        //String topDir = ""; //Users/ubamass/Google Drive/Brunel/halo-core/";//knitware/knitware-acceptance-test/src/test/java/sky/sns/knitware/c3c/infrastructure/audit/AuditMessagesOutgoingToStrataAndIncomingFromStrataTest.java";
        String topDir = "/Users/ubamass/svnRepos/trunk/netstream/pacman/pacman-core";//E:\\SVNRepos\\repoWCs\\xaware\\XAware\\trunk";//\\drjava\\src\\edu\\rice\\cs\\drjava\\ui\\JarOptionsDialog.java";
        
        System.out.println(new File(topDir).exists());
        GetMethods gm = new GetMethods(topDir);
        gm.getAllMethods();
        gm.soutMethods();
        /*               CheckCorrectness checkCorrectness = new CheckCorrectness(gm.methods);
        java.awt.EventQueue.invokeLater(() -> {
        checkCorrectness.setVisible(true);
        });*/
        //gm.printCSVMethods("D:/Dropbox/Brunel/Flagging/drjava2008.csv");
        //gm.printAsserts("/Users/ubamass/Google Drive/Brunel/pacman_Asserts_July27.csv");
        
    }

    public void printCSVMethods(String outputFileName) {
        ArrayList<String> meths = new ArrayList<>();
        meths.add(HEADER + "\t Asserts");
        for(Method method : methods){
            meths.add(method.toString());
        }
        FilePrinter.createCSVFile(outputFileName, meths);//"/Users/ubamass/Seafile/ELFF/TestData/SystemMethods/TestMethodsWithFlags.csv"
    }
    
    public GetMethods(String topDir){
        this.topDir = topDir;
        methods = new ArrayList<>();
    }
    
    public void getAllMethods(){
        ArrayList<File> files = getAllFiles(topDir);
        MethodGrabber methodGrabber = null;
        for(File file :files){
            //System.out.println(file.getName());
            methodGrabber = new MethodGrabber(file);
            //if(methodGrabber.isParsable()){
                methodGrabber.findAllMethods();
                methods.addAll(methodGrabber.getMethods());
            //}
            methodGrabber.resetPretty();
        }
        sortMethods();
    }
    
    public void populateAllMethodsStrings(){
        methodStrings = new ArrayList<>();
        ArrayList<File> files = getAllFiles(topDir);
        for(File file : files){
            MethodGrabber methodGrabber = new MethodGrabber(file);
            if(methodGrabber.isParsable()){
                methodStrings.addAll(methodGrabber.findAllMethodStrings());
            }
        }
    }
    
    private void sortMethods(){
        //soutMethods();
        Collections.sort(methods, new Comparator<Method>(){

            @Override
            public int compare(Method method1, Method method2) {
                int result;
                if(method1.getMethodId() > method2.getMethodId()){
                    result = 1;
                }
                else {
                    result = -1;
                }
                return result;
            }
            
        });
    }

    private void soutMethods() {
        for(Method method : methods){
            System.out.println(method.toString());
        }
    }

    private ArrayList<File> getAllFiles(String topDir) {
        ArrayList<File> files = new ArrayList<>();
        File topDirFile = new File(topDir);
        try {
            FileUtil.populateFileNames(topDirFile, files);
        } catch (IOException ex) {
            Logger.getLogger(GetMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return files;
    }
    
    public void flagAllMethods(){
        FlagMethods flagMethods = new FlagMethods();
        flagMethods.flagMethods(methods);
    }
    
    private void printAsserts(String outputFile){
        ArrayList<String> allAssertStrings = new ArrayList<>();
        allAssertStrings.add(HEADER + "\t AssertLine");
        for(Method method : this.methods){
            System.out.println(method.getMethodName());
            if(method.getMethodAsserts().size() > 0){
                allAssertStrings.addAll(method.getMethodAsserts());
            }
        }
        FilePrinter.createCSVFile(outputFile, allAssertStrings);        
    }
    
    public ArrayList<Method> getMethods(){
        return methods;
    }
    
    public ArrayList<String> getMethodStrings(){
        return methodStrings;
    }

    private void soutMethodsStrings() {
        for (String string : methodStrings) {
            System.out.println(string);
        }
    }
    
}
