/**
 * Created by hps on 01.08.2016.
 * Unit Tests for Class IntBox
 */

// If you get the error/warning:
// "package name utils.cast does not correspond to the file path"
// at the following package statement see:
// http://stackoverflow.com/questions/26440623/package-name-does-not-correspond-to-the-file-path-intellij
// (answer from Bohuslav Burghardt Oct 19 '14). This has worked for me.
package utils.casts;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class IntBox_setTest1 {
    @Test
    public void tryParse_test() {
        // IntBox ib = new IntBox(); // Old. New with static fabric method:
        IntBox ib = IntBox.makeIntBox(); // static members are initialized
        assertEquals(0,ib.get());
        // Old:
        //IntBox ib2 = new IntBox("123",0); // static members are not initialized, they were
                                          // already initialized (at construction of ib)
        // New with static fabric method:
        //IntBox ib2 = IntBox.makeIntBox("123",0); // static members are not
        //   // initialized, they were already initialized (at construction of ib)
        //
        //assertEquals(123,ib2.v());
        boolean b = false;
        String s = String.valueOf(Integer.MIN_VALUE);
        b = IntBox.tryParse(s,ib);
        assertEquals(true,b);
        assertEquals(Integer.MIN_VALUE,ib.v());
        s = String.valueOf((long)Integer.MAX_VALUE+1L);
        b = IntBox.tryParse(s,ib);
        assertEquals(false,b);
        assertEquals(Integer.MIN_VALUE,ib.v());
        s = String.valueOf(Integer.MAX_VALUE);
        b = IntBox.tryParse(s,ib);
        assertEquals(true,b);
        assertEquals(Integer.MAX_VALUE,ib.v());
        s = String.valueOf((long)Integer.MIN_VALUE-1L);
        b = IntBox.tryParse(s,ib);
        assertEquals(false,b);
        assertEquals(Integer.MAX_VALUE,ib.v());
        int i = 123;
        s = " "+ String.valueOf(i)+" ";
        b = IntBox.tryParse(s,ib);
        assertEquals(true,b);
        assertEquals(i,ib.v());
        int i2 = 456;
        s = String.valueOf(2)+"a";
        b = IntBox.tryParse(s,ib);
        assertEquals(false,b);
        assertEquals(i,ib.v());
        i=20034;
        s=String.valueOf(i);
        b=IntBox.tryParse(s,ib);
        assertEquals(true,b);
        assertEquals(i,ib.v());
        b=IntBox.tryParseWithTable(s,ib);
        assertEquals(true,b);
        assertEquals(i,ib.v());
        i=-20034;
        s=String.valueOf(i);
        b=IntBox.tryParseWithTable(s,ib);
        assertEquals(true,b);
        assertEquals(i,ib.v());
        b=IntBox.tryParse(s,ib);
        assertEquals(true,b);
        assertEquals(i,ib.v());
    }
    
    @Test
    public void set_test() { // tests also clone()
        // test default constructor:
        IntBox ib=IntBox.makeIntBox((long)Integer.MAX_VALUE+1L,0);
        assertEquals(0,ib.v());
        assertEquals(0,ib.get());
        int i=ib.v();
        assertEquals(0,i);
        IntBox ib2 = (IntBox)ib.clone();
        int j=ib2.set(123);
        assertEquals(0,ib.v());
        assertEquals(123,j);
        assertEquals(j,ib2.v());
        //i=ib.set(1L);
        //assertEquals(1,i);
    }
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Test
    public void useUnsetIntBox_test() { //
        IntBox ib;
        // Following line will NOT compile :-)
        // IntBox.tryParse("Dummy",ib);
        ib=IntBox.makeIntBox("Dummy",0);
        // Testing JUnit:
        // If next statement is commented out, the test will fail :-)
        ib=null; // No possibility to prevent this unfortunately :-(
        exception.expect(NullPointerException.class);
        // Testing JUnit:
        // with following statement instead of above the test fails :-)
        //exception.expect(IllegalArgumentException.class);
        int i = ib.set(123); // Boom... (in real code, not in this test code)
        //*********************************************
        // The code below will not be executed ! ! !  *
        // Cause an exception is thrown in line above *
        // (but this exception is catched by JUnit,   *
        // so we don't see it in our code).           *
        //*********************************************
        ib = IntBox.makeIntBox(123);
        //String s="456";
        //java.lang.Object o = s;
        //exception.expect(CloneNotSupportedException.class);
        //java.lang.Object o = ib;
        IntBox ib2=(IntBox)ib.clone();
        IntBox ib3=ib2;
        ib2.set(321);
        assertEquals(123,ib.v());
        System.out.printf("%d%n",ib.v());
        assertEquals(321,ib2.v());
        System.out.printf("%d%n",ib2.v());
    }

    //@Rule
    //public final ExpectedException exception = ExpectedException.none();
    //@Test
    //public void set_ThrowsIllegalArgumentException() {
    //    IntBox ib=new IntBox();
    //    long l=(long)Integer.MAX_VALUE+1L;
    //    long lm=(long)Integer.MIN_VALUE-1L;
    //    exception.expect(IllegalArgumentException.class);
        //int i=ib.set(l);
        //exception.expect(IllegalArgumentException.class);
        //i=ib.set(lm);
        //double d=(double)Integer.MAX_VALUE+1.0;
        //double dm=(double)Integer.MIN_VALUE-1.0;
        //exception.expect(IllegalArgumentException.class);
        //i=ib.set(dm);
        //exception.expect(IllegalArgumentException.class);
        //i=ib.set(dm);
    //}
}
