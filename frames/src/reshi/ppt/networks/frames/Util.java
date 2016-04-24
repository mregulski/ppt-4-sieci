package reshi.ppt.networks.frames;

public class Util {

    public static char[] concat(char[] a, char[] b) {
        char[] c = new char[a.length+b.length];
        System.arraycopy(a,0,c,0,a.length);
        System.arraycopy(b,0,c,a.length, b.length);
        return c;
    }
}
