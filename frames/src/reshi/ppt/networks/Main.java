package reshi.ppt.networks;

public class Main {

    public static void main(String[] args) {
	    String data = "1111101";
        EthernetStuffer stuffer = new EthernetStuffer();
        char[] encoded = stuffer.encodeData(data.toCharArray());
        System.out.println(encoded);
    }
}
