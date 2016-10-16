/**
 * 
 */
package r;

import org.junit.Test;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * @author leslie
 *
 */
public class Test1 {

	@Test
	public void test1(){
//		callRJava();
		tableTest1();
	}
	
	public void callRJava() {
        Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            return;
        }
        
        //打印变量
        String version = re.eval("R.version.string").asString();
        System.out.println(version);

        //循环打印数组
        double[] arr = re.eval("rnorm(10)").asDoubleArray();
        for (double a : arr) {
            System.out.print(a + ",");
        }
        re.end();
    }
	
	private void tableTest1(){
        Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            return;
        }
        
        REXP x;
//        double[] c = re.eval("c(1,2,3,3,4,9,7,2,3)").asDoubleArray();
//        System.out.println(c);
        
        x = re.eval("table(cut(c(1,2,3,3,4,9,7,2,3), breaks=c(0,4,8,12)))");
        System.out.println("x: " + x);
        int[] v = x.asIntArray();
        System.out.println(v);
        for(int i : v){
        	System.out.println(i);
        }
        
        
//        String result = re.eval("table(x)").asString();
//        System.out.println(result);
	}
}
