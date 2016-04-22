package reshi.ppt.networks;

import java.util.Arrays;

/**
 *  @author Marcin Regulski on 20.04.2016.
 */
class EthernetStuffer {

    char[] encodeData(char[] data) {
        StringBuilder encoded = new StringBuilder("01111110");
        int counter = 0;
        for (char aData : data) {
            if (aData == '1') {
                counter++;
            } else {
                counter = 0;
            }
            if (counter > 5) {
                encoded.append('0');
                counter = 0;
            }
            encoded.append(aData);
        }
        encoded.append("01111110");
        return encoded.toString().toCharArray();
    }

    char[] decodeData(char[] data)
    {
        if(!isValidData(data)) throw new IllegalArgumentException();
        StringBuilder decoded = new StringBuilder();
        int counter = 0;
        for (int i = 8; i < data.length-8; i++) {
            if(data[i] == '1') {
                counter++;
            }
            else {
                counter = 0;
            }
            decoded.append(data[i]);

            if(counter == 5) {  // skip one character ('0') after five consecutive '1's
                i++;
            }
        }
        return decoded.toString().toCharArray();
    }

    boolean isValidData(char[] data)
    {
        if (data.length < 16) return false;
        if (data[0] != '0') return false;
        for(int i = 1; i < 7; i++)
        {
            if(data[i] != '1') return false;
        }
        if (data[7] != '0') return false;
        if (data[data.length-8] != '0') return false;
        for(int i = data.length-7; i < data.length-1; i++)
        {
            if (data[i] != '1') return false;
        }
        return data[data.length - 1] == '0';
    }
}
