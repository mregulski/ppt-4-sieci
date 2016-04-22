package reshi.ppt.networks;

import java.util.Arrays;

/**
 * Created by mrmar on 20.04.2016.
 */
public class EthernetStuffer {

    public char[] encodeData(char[] data)
    {
        StringBuilder encoded =  new StringBuilder("01111110");
        int counter=0;
        for (int i = 0; i < data.length; i++) {
            if(data[i] == '1') {
                counter++;
            }
            if(counter == 5) {
                encoded.append('0');
                counter = 0;
            }
            encoded.append(data[i]);
        }
        encoded.append("01111110");
        return encoded.toString().toCharArray();
    }
}
