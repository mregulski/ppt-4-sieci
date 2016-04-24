package reshi.ppt.networks.frames;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 *  @author Marcin Regulski on 20.04.2016.
 */

class EthernetStuffer {
    private final int FRAME_SIZE;
    private final String TERMINATOR = "01111110";

    EthernetStuffer(int frameSize) {
        FRAME_SIZE = frameSize;
    }

    /**
     * Split data into chunks of FRAME_SIZE 'bits', and encode them into frames.
     * @param data - String of 0s and 1s representing data to encode
     * @return data packed into frames.
     */
    String encode(String data) {
        if(data.length() < FRAME_SIZE) {
            return encodeFrame(data);
        }
        List<String> chunks = new ArrayList<>();
        String frames = "";
        int idx = 0;
        while (idx < data.length()) {
            if(idx+FRAME_SIZE < data.length()) {
                chunks.add(data.substring(idx, idx+FRAME_SIZE));
            }
            else {
                chunks.add(data.substring(idx));
            }
            idx += FRAME_SIZE;
        }
        for (String chunk :  chunks) {
            frames += encodeFrame(chunk);
        }

        return frames;
    }

    /**
     * Pack data into a frame. Add CRC at the end then bit-stuff the result
     * @param data - data to encode
     * @return TERMINATOR-delimited, bit-stuffed data with CRC added before final terminator.
     */
    String encodeFrame(String data) {

        StringBuilder encoded = new StringBuilder();
        // calculate CRC of raw_data
        CRC32 crc = new CRC32();
        crc.update(data.getBytes());
        String checkSum = String.format("%32s", Long.toBinaryString(crc.getValue())).replace(' ','0');

        char[] bits = Util.concat(data.toCharArray(), checkSum.toCharArray());

        // apply stuffing to data + CRC
        int counter = 0;
        for (char bit : bits) {
            if (bit == '1') {
                counter++;
            } else {
                counter = 0;
            }
            encoded.append(bit);
            if (counter == 5) {
                encoded.append('0');
                counter = 0;
            }

        }
        // wrap stuffed data with terminators
        return TERMINATOR + encoded + TERMINATOR;
    }

    /**
     * Detect and decode valid frames in a String.
     * @param frames - String of 0s and 1s containing 0+ valid frames. Invalid data between frames is discarded.
     * @return - data extracted from frames.
     */
    String decode(String frames) {
        int frame_start, frame_end;
        frame_start = frames.indexOf(TERMINATOR);
        List<String> data = new ArrayList<>();
        while(frame_start != -1) {
            frame_end = frames.indexOf(TERMINATOR, frame_start+8);
            String frame = frames.substring(frame_start, frame_end+8);
            data.add(decodeFrame(frame));
            frame_start = frames.indexOf(TERMINATOR, frame_end+8);
        }
        StringBuilder result = new StringBuilder();
        data.forEach(result::append);
        return result.toString();
    }

    /**
     * Decode a single frame.
     * @param frame - frame represented by a String of 0s and 1s
     * @return data contained in the frame
     */
    String decodeFrame(String frame)
    {
        char[] frameChars = frame.toCharArray();
        // check for corruption
        if(!isValidFrame(frame)) throw new IllegalArgumentException("Corrupted frame.");
        String stuffed = frame.substring(8, frameChars.length - 8);

        // unstuff frame
        StringBuilder unstuffed = new StringBuilder();
        int counter = 0;
        for (int i = 8; i < frameChars.length-8; i++) {
            if(frameChars[i] == '1') {
                counter++;
            }
            else {
                counter = 0;
            }
            unstuffed.append(frameChars[i]);
            if(counter == 5) {  // skip one character ('0') after five consecutive '1's
                i++;
                counter = 0;
            }
        }
        String checkSumExpected = unstuffed.substring(unstuffed.length()-32);
        String frame_data = unstuffed.substring(0, unstuffed.length()-32);

        CRC32 crc = new CRC32();
        crc.update(frame_data.getBytes());
        String checkSumActual = String.format("%32s", Long.toBinaryString(crc.getValue())).replace(' ','0');

        if(!checkSumActual.equals(checkSumExpected)) {
            throw new RuntimeException("CRC mismatch");
        }

        return frame_data;
    }

    /**
     * Check if String represents a valid frame: is long enough to contain required information
     * and is properly terminated.
     * @param data - string to check
     * @return true if data represents a correct frame
     */
    boolean isValidFrame(String data)
    {
        return data.length() > 2*TERMINATOR.length()+32
                && data.substring(0,8).equals(TERMINATOR)
                && data.substring(data.length()-8).equals(TERMINATOR);

    }
}
