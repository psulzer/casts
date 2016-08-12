package utils.casts;
//import java.util.SortedMap;

/**
 * Created by hps on 01.08.2016.
 */

public class IntBoxMain {
    public static void main(String[] args) {
        IntBox ib = new IntBox();
        System.out.printf("new IntBox(), value=%d%n",ib.v());
        boolean b = IntBox.tryParseExcept("137",ib);
        System.out.printf("\"137\" parsed to %d%n",ib.v());
        System.out.printf("ib.toString()=\"%s\"%n",ib);
        String sl=Long.toString((long)Integer.MAX_VALUE+1L);
        b=IntBox.tryParseExcept(sl,ib);
        System.out.printf("IntBox.tryParseExcept(\"%1s\",ib)=%2b%n",sl,b); 
        System.out.printf("MAX_INT_LASTDEC=%d%n",IntBox.MAX_INT_LASTDEC);
        System.out.printf("MAX_INT_LASTDEC=%d%n",IntBox.MIN_INT_LASTDEC);
        // test tryParse:
        String s="172"; b=IntBox.tryParse(s,ib);
        System.out.printf("IntBox.tryParse(\"%s\",ib)=%b; ib=%d%n",s,b,ib.v());
        s="2147483647"; b=IntBox.tryParse(s,ib);
        System.out.printf("IntBox.tryParse(\"%s\",ib)=%b; ib=%d%n",s,b,ib.v());
        s="2147483648"; b=IntBox.tryParse(s,ib);
        System.out.printf("IntBox.tryParse(\"%s\",ib)=%b; ib=%d%n",s,b,ib.v());
        s="-2147483648"; b=IntBox.tryParse(s,ib);
        System.out.printf("IntBox.tryParse(\"%s\",ib)=%b; ib=%d%n",s,b,ib.v());
        s="2147483649"; b=IntBox.tryParse(s,ib);
        System.out.printf("IntBox.tryParse(\"%s\",ib)=%b; ib=%d%n",s,b,ib.v());
    }
}
