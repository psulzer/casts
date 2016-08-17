/** IntBox class for Java published under the GNU General Public Licence
 *  (GPL) version 1 or any newer version
 *  
 *  Developers(!): Please read the end of this Java-Doc(!)
 *  
 * Class that holds exactly one int (e. g. for reference passing to methods).
 * IntBox objects will always hold a regular int (and not null - see
 * exclusion/example below). Default value (with default constructor) is 0.
 * This is achieved as there are no public constructors but only static
 * fabric methods (have nothing to do with the design pattern "fabric method"
 * from the "Gang of Four").
 *
 * All methods which will return an IntBox object will return an object
 * which boxes a correct int. There are no methods which can fail (throw
 * an exception). I. e. there are no methods like set(long l), instead
 * call set(int i) with "intBox.set((int)l);" (where you are responsible
 * for what you are doing ;-)) or (better) use the
 * set(long l, int defaultValue) method.
 * 
 * Following for example will not compile:
 * IntBox ib;
 * ...
 * int i = IntBox.tryParse("Dummy",ib);
 * 
 * Unfortunately the following cannot be prevented :-(
 * ib = null; //(WHY WANT YOU DO THAT!, The makeIntBox(...)
 *            // methods always return an IntBox object(!).
 * ...
 * int j = ib.set("123"); // Boom...
 * 
 * Usage:
 *
 * The most beneficial use of this class is to implement the static version
 * of the C#-functions Type.TryParse(string s,Type t) into Java (unfortunately 
 * currently just implemented for an int).
 * 
 * There is one difference to Integer.parse(String s) from Java: The tryParse
 * functions accept leading and trailing whitespace. E. g.
 * s="   123   "  will be parsed to 123, with Integer.parseInt(...) an
 * exception will be thrown. In contrary to (e. g.) scanf(...) (C) or
 * cin >> input (C++) s="123someNoneWhiteSpaceCharacters" will not be parsed
 * to 123, but tryParse(...) will return false and the IntBox-argument will
 * not be changed (even if it is null).
 * 
 * Speed:
 * The tryParse function is slightly faster than the standard java method
 * int i=Integer.parseInt(String s), even when used without any error
 * checking (try { Integer.parseInt... } catch ...
 * 
 * Here are the results for some benchmark tests I run:
 * 
 * All tests together (result of last loop) result in milli seconds:
 * Integer.parseInt       | 468 (standard Java parsing)
 * tryParseException      | 532 (commented out)
 * tryParseWithTable      | 562 (may be useful for CPUs with slow mult-op)
 * tryParseForward        | 453 (commented out)
 * tryParse               | 438
 * tryParseForwardAssign  | 437 (after tryParse an int is assigned, e. g.:
 * tryParseAssign         | 422  if (IntBox.tryParse(s,intBox)) i=intBox.v();)

 * Hint: The times are varying in different runs. Sometimes
 * tryParseForward is faster. As you can see above it even
 * happened that the two Assign-methods were faster than the
 * corresponding methods without Assign(!), albeit the former
 * have an extra assignment statement for the int variable.
 * Strange... But in general the trend was that tryParse (backward
 * parsing) is the fastest method. The tryParse methods are highly
 * optimized for speed (not for memory usage) and are not recommended
 * for educational purposes (how to parse a string to an int value).
 *
 * Note:
 * There is a function IntBox.tryParseWithTable(string s, IntBox intBox) which
 * may be useful on CPUs without or slow multiplication operation. See the
 * comment before the implementation of the function. On modern Intel processors
 * this function is slower than tryParse(...). Another implementation
 * tryParseForward(...) has been commented out, as it showed in a separate
 * benchmark program, that it is slower than tryParse (which parses backwards),
 * albeit the latter requires 2 instead of 1 multiplications inside the for(...)
 * loop.
 *
 * Example(s):
 *
 * With this class, it is possible to write the following code:
 *
 * int input;
 * string inputString;
 * do {
 *     ... any input (e. g. from console) to inputString...
 *     IntBox result = IntBox.makeIntBox();
 * } while ( ! IntBox.tryParse(inputString, result));
 * input=result.v(); // an alternative is: input=result.get();
 *
 * This class itself throws no exceptions. But of course if
 * you set an IntBox object to null and later use this object
 * a NullPointerException will be thrown.
 * 
 * ********
 * To Do: *
 * ********
 * 
 * WRITE MORE UNIT TESTS! (see directory src/test/java/utils/casts)
 * 
 * Implement a similar LongBox (should be easy) and DoubleBox class 
 * 
 * Port it to pure Java Compiler (JDK) and other IDEs than IntelliJ
 *
 * 
 * Created by Peter Sulzer on 2015-07-28.
 *
 * @author Peter Sulzer
 * Copyright (c) 2016, Peter Sulzer, Fürth, Fr, 2016-07-29
 * 
 * @version 1.1, added ltrimZeroes() methods, required for the tryParse()
 * methods. Without there may be false results if the string has leading
 * zeroes. There is now also another static public function:
 * public static String ltrimZeroes(String numberString)
 * which removes leading zeroes. Works for positive and negative "numbers".
 * 
 * Version 1.0, 2015-08-11 (tryParse functions are nearly fully tested
 * with unit-tests, a lot of the other functions (mostly very simple)
 * have also been already tested with unit tests (using JUnit). Developed
 * with JetBrains IntelliJ (Community Edition) version 2016.2.
 *
 * Developer notes:
 * 
 * I'm not so good with IntelliJ, Gradle, .... Unfortunately the resulting
 * name of the *.jar-file
 * 
 * (Menu: IntelliJ-Main-Menü->Build->Build Artifacts...)
 * 
 * is always "casts_main.jar". Rename it to "casts.jar" (manually) and copy
 * it to a directory of your classpath (or were it can be found in IntelliJ)
 */
package utils.casts;

//import org.omg.CORBA.Object;

import javax.validation.constraints.NotNull;

public class IntBox implements Cloneable {

    // BEGIN the following statements are only executed at the
    // first instantiation of an IntBox (i. e. only once) or
    // already compiled into the code at compile time:
        public static final int MAX_INT_LEN =
                String.valueOf(Integer.MAX_VALUE).length();
        public static final int MIN_INT_LEN =
                String.valueOf(Integer.MIN_VALUE).length();
        public static final int MAX_INT_LASTDEC =
                Integer.parseInt(String.valueOf(Integer.MAX_VALUE).substring(1));
        public static final int MAX_INT_FIRSTDIGIT =
                Integer.parseInt(String.valueOf(Integer.MAX_VALUE).substring(0, 1));
        public static final int MIN_INT_LASTDEC =
                -Integer.parseInt(String.valueOf(Integer.MIN_VALUE).substring(2));
        public static final int MIN_INT_FIRSTDIGIT =
                Integer.parseInt(String.valueOf(Integer.MIN_VALUE).substring(1,2));
        public static final int MAX_INT_FIRSTDEC =
                Integer.parseInt(String.valueOf(Integer.MAX_VALUE).substring(0,
                        String.valueOf(Integer.MAX_VALUE).length()-1));
        public static final int MAX_INT_LASTDIGIT=Integer.parseInt(
                String.valueOf(Integer.MAX_VALUE).substring(
                        String.valueOf(Integer.MAX_VALUE).length()-1));
        public static final int MIN_INT_FIRSTDEC =
                Integer.parseInt(String.valueOf(Integer.MIN_VALUE).substring(0,
                        String.valueOf(Integer.MIN_VALUE).length()-1));
        public static final int MIN_INT_LASTDIGIT=Integer.parseInt(
                String.valueOf(Integer.MIN_VALUE).substring(
                        String.valueOf(Integer.MIN_VALUE).length()-1));

        public static final int[][] DECVALS = {
                 {0,1,2,3,4,5,6,7,8,9}
                ,{0,10,20,30,40,50,60,70,80,90}
                ,{0,100,200,300,400,500,600,700,800,900}
                ,{0,1000,2000,3000,4000,5000,6000,7000,8000,9000}
                ,{0,10000,20000,30000,40000,50000,60000,70000,80000,90000}
                ,{0,100000,200000,300000,400000,500000,600000,700000,800000,900000}
                ,{0,1000000,2000000,3000000,4000000,5000000,
                  6000000,7000000,8000000,9000000}
                ,{0,10000000,20000000,30000000,40000000,50000000,
                  60000000,70000000,80000000,90000000}
                ,{0,100000000,200000000,300000000,400000000,500000000,
                  600000000,700000000,800000000,900000000}
                ,{0,1000000000,2000000000}
        };
    // END the following statements are only executed at the...
    // tested with debugger

    protected int _n;


    public final static String INT_OVERFLOW =
            Integer.MIN_VALUE+" <= first argument <= "+Integer.MAX_VALUE;
    public final static String UNINITALIZED_INTBOX =
            "Uninitialized IntBox (i. e. null) not allowed as parameter!";
    public final static String INVALID_ARGUMENT =
            "Argument must be an Integer in the range of: ";
    
    @NotNull
    public static IntBox makeIntBox() {
        return new IntBox();
    }
    @NotNull
    public static IntBox makeIntBox(int n) {
        return new IntBox(n);
    }
    @NotNull
    public static IntBox makeIntBox(long l, int defaultValue) {
        return new IntBox(l,defaultValue);
    }
    @NotNull
    public static IntBox makeIntBox(double d, int defaultValue) {
        return new IntBox(d,defaultValue);
    }
    @NotNull
    public static IntBox makeIntBox(String s, int defaultValue) {
        return new IntBox(s,defaultValue);
    }
    @NotNull
    public static IntBox makeIntBox(IntBox ib, int defaultValue) {
        return new IntBox(ib,defaultValue);
    }

    public IntBox() {
        _n=0; // 0 is the default value of IntBox, if all else fails
    }
    protected IntBox(int n) {
        _n=n;
    }
    // This works, but is not wanted:
    //private IntBox(long l) throws IllegalArgumentException {
    //    if (l < Integer.MAX_VALUE && l > Integer.MIN_VALUE)
    //        _n=(int)l;
    //    else
    //        throw new IllegalArgumentException(INT_OVERFLOW);
    //}
    protected IntBox(long l,int defaultValue) {
        if (l < Integer.MAX_VALUE && l > Integer.MIN_VALUE)
            _n=(int)l;
        else
            _n=defaultValue;
    }
    // This works, but is not wanted:
    //private IntBox(double d) {
    //    if (d < Integer.MAX_VALUE && d > Integer.MIN_VALUE)
    //        _n=(int)d;
    //    else
    //        throw new IllegalArgumentException(INT_OVERFLOW);
    //}
    protected IntBox(double d, int defaultValue) {
        if (d < Integer.MAX_VALUE && d > Integer.MIN_VALUE)
            _n=(int)d;
        else
            _n=defaultValue;
    }
    // This works, but is not wanted:
    //private IntBox(String s) throws IllegalArgumentException {
    //    try { _n=Integer.parseIntExcept(s); }
    //    catch (Exception e) {
    //        throw new IllegalArgumentException(
    //                INVALID_ARGUMENT+INT_OVERFLOW,e);
    //    }
    //}
    protected IntBox(String s, int defaultValue) {
        // Old:
        //try { _n=Integer.parseInt(s); }
        //catch (Exception e) {
        //    _n=defaultValue;
        //}
        // New, with tryParse of this class:
        if (!tryParse(s,this))
            _n=defaultValue;
    }
    // This works, but is not wanted:
    //private IntBox(IntBox ib) throws IllegalArgumentException {
    //    if (ib == null)
    //        throw new IllegalArgumentException(UNINITALIZED_INTBOX);
    //    _n=ib._n;
    //}
    protected IntBox(IntBox ib, int defaultValue) {
        if (ib == null)
            _n=defaultValue;
        else
            _n=ib._n;
    }

    public int v() { // v for "value"
        return _n;
    }
    public int get() { // alternative for above, for those who prefer
        return _n;     // explicit long(er) names
    }
    // _ as identifier is deprecated (may not be supported after Java SE 8)
    //public int _() { // alternative for above, for those who prefer it
    //    return _n;
    //}
    public int set(int n) {
        _n=n;
        return _n;
    }
    // This works, but is not wanted:
    //private int set(long l) throws IllegalArgumentException {
    //    if (l < Integer.MAX_VALUE && l > Integer.MIN_VALUE)
    //        _n=(int)l;
    //    else
    //        throw new IllegalArgumentException(INT_OVERFLOW);
    //    return _n;
    //}
    public int set(long l,int defaultValue) {
        if (l < Integer.MAX_VALUE && l > Integer.MIN_VALUE)
            _n=(int)l;
        else
            _n=defaultValue;
        return _n;
    }
    // This works, but is not wanted:
    //private int set(double d) throws IllegalArgumentException {
    //    if (d < Integer.MAX_VALUE && d > Integer.MIN_VALUE)
    //        _n=(int)d;
    //    else
    //        throw new IllegalArgumentException(INT_OVERFLOW);
    //    return _n;
    //}
    public int set(double d, int defaultValue) {
        if (d < Integer.MAX_VALUE && d > Integer.MIN_VALUE)
            _n=(int)d;
        else
            _n=defaultValue;
        return _n;
    }
    protected int set(IntBox ib) throws IllegalArgumentException {
        if (ib == null)
            throw new IllegalArgumentException(UNINITALIZED_INTBOX);
        return _n=ib._n;
    }
    // This won't work:
    //public int set(IntBox ib, int defaultValue) {
    //    if (ib == null)
    //        ib._n=defaultValue;
    //    return _n=ib._n;
    //}
    // This works, but is not wanted:
    //public IntBox setIntBox(IntBox intBox) throws IllegalArgumentException {
    //    if (intBox == null)
    //        throw new IllegalArgumentException(UNINITALIZED_INTBOX);
    //    else {
    //        return intBox;
    //    }
    //}
    protected IntBox setIntBox(IntBox intBox, int defaultValue) {
        if (intBox == null)
            return new IntBox(defaultValue);
        else {
            return intBox;
        }
    }
    // This works, but is not wanted:
    //private int set(String s) throws IllegalArgumentException {
    //    try { _n=Integer.parseInt(s); }
    //    catch (Exception e) {
    //        throw new IllegalArgumentException(
    //                INVALID_ARGUMENT+INT_OVERFLOW,e);
    //    }
    //    return _n;
    //}
    public int set(String s,int defaultValue) {
        try { _n=Integer.parseInt(s); }
        catch (Exception e) {
            _n=defaultValue;
        }
        return _n;
    }
    // This works, but is not wanted:
    //public IntBox parse(String s) throws IllegalArgumentException {
    //    int n;
    //    try { n=Integer.parseInt(s); }
    //    catch (Exception e) {
    //        throw new IllegalArgumentException(
    //                INVALID_ARGUMENT+INT_OVERFLOW,e);
    //    }
    //    return new IntBox(n);
    //}

    // no static int parse(String s) function, use Integer.parseInt(...):
    //public static int parse(String s) throws IllegalArgumentException {
    //    int n;
    //    try { n=Integer.parseInt(s); }
    //    catch (Exception e) {
    //        throw new IllegalArgumentException(INT_OVERFLOW,e);
    //    }
    //    return n;
    //}

    // tryParse which uses Integer.parseInt(String s), and catches
    // errors via ...catch(Exception e). Slow, so commented out:
    //public static boolean tryParseExcept(String s,IntBox intBox)
    //                                    throws IllegalArgumentException {
    //    if (intBox == null) {
    //        // Unfortunately this doesn't work:
    //        // intBox=new IntBox(); // intBox itself is passed by value(!)
    //        return false; // so we simply return false
    //    }
    //    try { intBox._n=Integer.parseInt(s); }
    //    catch (Exception e) {
    //        return false;
    //    }
    //    return true;
    //}


    // tryParse which parses the string itself (catches no exception)
    // and thereby using a table (so no multiplication is needed). This
    // may be useful with processors where a multiplication operation
    // is slow, compared to an indexed memory access (e. g. on
    // Motorola 68000 CPU this could be faster than
    // Integer.tryParse(String s,IntBox intBox). Not tested if it is
    // really faster, cause I have no access to a real 68000'er Computer
    // with a JRE.
    //public static boolean tryParse(String s,IntBox intBox) {
    // To run the unit tests uncomment previous line, name the real tryParse
    // method to "Old name" (see comments below) and comment next line
    public static boolean tryParseWithTable(String s,IntBox intBox) {
        if (intBox == null)
            // intBox=new IntBox(); // This doesn't work, as intBox itself is
                                    // passed by value
            return false; // so we simply return false
        s=s.trim();
        int len=s.length();
        boolean negative=false;
        int rslt=0, d, i, j;
        char c=s.charAt(0);
        if (c == '-')
            negative=true;
        if (len > MAX_INT_LEN) {
            s = ltrimZeroes(s);
            len = s.length();
        }
        if (len > MAX_INT_LEN) {
            if (!negative || len > MIN_INT_LEN)
                return false;
        }
        if (negative) {
            int minIntLen = MIN_INT_LEN - 2;
            for (i = len - 1, j = 0; i >= 1; --i, ++j) {
                c = s.charAt(i);
                if (!Character.isDigit(c))
                    return false;
                d = c-'0'; // convert current char (a digit) to int
                if (j >= minIntLen) {
                    if (d > MIN_INT_FIRSTDIGIT )
                        return false;
                    if (d == MIN_INT_FIRSTDIGIT && rslt < MIN_INT_LASTDEC)
                        return false;
                }
                rslt -= DECVALS[j][d];
            }
        } else {
            int maxIntLen = MAX_INT_LEN-1;
            for (i = len - 1, j = 0; i >= 0; --i, ++j) {
                c = s.charAt(i);
                if (!Character.isDigit(c))
                    return false;
                d = c-'0'; // convert current char (a digit) to int
                if (j >= maxIntLen) {
                    if (d > MAX_INT_FIRSTDIGIT)
                         return false;
                    if (d == MAX_INT_FIRSTDIGIT && rslt > MAX_INT_LASTDEC)
                        return false;
                }
                rslt += DECVALS[j][d];
            }
        }
        intBox._n=rslt;
        return true;
    }

    // ltrimZeroes() methods added 2016 08 16 (are required by tryParse() methods)
    public static String ltrimZeroes(String s) {
        if (s.charAt(0) == '-')
            return ltrimZeroesNegative(s);
        else
            return ltrimZeroesPositive(s);
    }
    protected static String ltrimZeroesNegative(String s) {
        int i=1;
        for ( ; s.charAt(i) == '0'; i++);
        return ("-"+s.substring(i));
    }
    protected static String ltrimZeroesPositive(String s) {
        int i=0;
        for ( ; s.charAt(i) == '0'; i++);
        return (s.substring(i));
    }

    // Old name:
    //public static boolean tryParseBackward(String s,IntBox intBox) {
    // New name (as this method seems to be the fastest):
    public static boolean tryParse(String s,IntBox intBox) {
        if (intBox == null)
            // intBox=new IntBox(); // This doesn't work, as intBox itself is
            // passed by value and cannot changed for the caller. I. e.
            // "out"-arguments of C# cannot be simulated in Java.
            return false; // so we simply return false
        s=s.trim();
        int len=s.length();
        int rslt=0, d, dfirst=0, i, j;
        char c=s.charAt(0);
        if (c == '-') {
            if (len > MIN_INT_LEN) { // corrected (added) 2016 08 17
                s = ltrimZeroesNegative(s);
                len = s.length();
            }
            if (len >= MIN_INT_LEN) {
                c = s.charAt(1);
                if (!Character.isDigit(c))
                    return false;
                dfirst = c-'0';
                if (len > MIN_INT_LEN || dfirst > MIN_INT_FIRSTDIGIT)
                    return false;
            }
            for (i = len - 1, j = 1; i >= 2; --i, j *= 10) {
                c = s.charAt(i);
                if (!Character.isDigit(c))
                    return false;
                rslt -= (c-'0')*j;
            }
            if (len < MIN_INT_LEN) {
                c = s.charAt(i);
                if (!Character.isDigit(c))
                    return false;
                rslt -= (c-'0')*j;
            } else {
                if (dfirst >= MIN_INT_FIRSTDIGIT && rslt < MIN_INT_LASTDEC)
                    return false;
                rslt -= dfirst * j;
            }
        } else {
            if (len > MAX_INT_LEN) { // corrected (added) 2016 08 16
                s = ltrimZeroesPositive(s);
                len=s.length();
            }
            if (len >= MAX_INT_LEN) {
                c = s.charAt(0);
                if (!Character.isDigit(c))
                    return false;
                dfirst = c-'0';
                if (len > MAX_INT_LEN || dfirst > MAX_INT_FIRSTDIGIT)
                    return false;
            }
            for (i = len - 1, j = 1; i >= 1; --i, j *= 10) {
                c = s.charAt(i);
                if (!Character.isDigit(c))
                    return false;
                rslt += (c-'0')*j;
            }
            if (len < MAX_INT_LEN) {
                c = s.charAt(i);
                if (!Character.isDigit(c))
                    return false;
                rslt += (c-'0')*j;
            } else { // corrected: 2015 08 17
                if (dfirst >= MAX_INT_FIRSTDIGIT && rslt > MAX_INT_LASTDEC)
                    return false;
                rslt += dfirst * j;
            }
        }
        intBox._n=rslt;
        return true;
    }

    // Commented out, cause it is slightly slower than tryParse(Backward): 
    //public static boolean tryParseForward(String s,IntBox intBox) {
    //    if (intBox == null)
    //        return false;
    //    s=s.trim();
    //    int len=s.length();
    //    char c;
    //    int rslt=0,i=0,d=0;
    //    c=s.charAt(i++);
    //    if (c == '-') {
    //        for (int iend=len-1; i < iend; ++i) {
    //            c=s.charAt(i);
    //            rslt *= 10;
    //            if (!Character.isDigit(c))
    //                return false;
    //            rslt -= c-'0';
    //        }
    //        c=s.charAt(i);
    //        if (!Character.isDigit(c))
    //            return false;
    //        d=c-'0';
    //        if (len >= MIN_INT_LEN) {
    //            if (len > MIN_INT_LEN)
    //                return false;
    //            if (rslt >= MIN_INT_FIRSTDEC) {
    //                if (rslt > MIN_INT_FIRSTDEC)
    //                    return false;
    //                if (d > MIN_INT_LASTDIGIT)
    //                    return false;
    //            }
    //        }
    //        rslt *= 10;
    //        rslt -= d;
    //    } else {
    //        int iend;
    //        for (i=0, iend=len-1; i < iend; ++i) {
    //            c=s.charAt(i);
    //            rslt *= 10;
    //            if (!Character.isDigit(c))
    //                return false;
    //            rslt += c-'0';
    //        }
    //        c=s.charAt(i);
    //        if (!Character.isDigit(c))
    //            return false;
    //        d=c-'0';
    //        if (len >= MAX_INT_LEN) {
    //            if (len > MAX_INT_LEN)
    //                return false;
    //            if (d > MAX_INT_LASTDIGIT)
    //                return false;
    //        }
    //        rslt *= 10;
    //        rslt += d;
    //    }
    //    intBox._n=rslt;
    //    return true;
    //}
    
    @Override
    public Object clone() {
        //super.clone();
        return new IntBox(this._n);
    }

    // toString():
    public String toString() {
        return Integer.toString(_n);
    }
}
