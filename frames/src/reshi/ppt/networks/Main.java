package reshi.ppt.networks;

public class Main {

    public static void main(String[] args) {
	    String data = "0001111111001010101010101111111000";
        EthernetStuffer stuffer = new EthernetStuffer();
        char[] encoded = stuffer.encodeData(data.toCharArray());
        System.out.println("        " + data);
        System.out.println(encoded);
        char[] decoded = stuffer.decodeData(encoded);
        System.out.println("        " + new String(decoded));
    }
}
