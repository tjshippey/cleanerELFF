/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Method;

/**
 *
 * @author ubamass
 */
public enum TestAnnotation {
    
    BEFORE_CLASS("@BeforeClass"),
    AFTER_CLASS("@AfterClass"),
    BEFORE("@Before"),
    AFTER("@After"),
    TEST("@Test");
    
    private final String annotation;
    
    private TestAnnotation(String annotation){
        this.annotation = annotation;
    }
    
    public static boolean isInList(String text){
        for(TestAnnotation annotations : TestAnnotation.values()){
            if(annotations.name().equals(text)){
                return true;
            }
        }
        return false;
    }
   
    
    
}
