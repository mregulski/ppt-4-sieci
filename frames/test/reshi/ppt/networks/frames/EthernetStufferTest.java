package reshi.ppt.networks.frames;

import static org.junit.Assert.*;

/**
 *  @author Marcin Regulski on 22.04.2016.
 */
public class EthernetStufferTest {

    private static EthernetStuffer stuffer;

    @org.junit.BeforeClass
    public static void setUp() throws Exception {
        stuffer = new EthernetStuffer(10);
    }
    @org.junit.Test
    public void encodeTest() {
        String data = "10011011001010101010";
        System.out.println(stuffer.encode(data));
    }
    @org.junit.Test
    public void decodedDataSameAsUnencodedSingleFrame() throws Exception {
        String data = "11111";
        String encoded = stuffer.encodeFrame(data);
        String decoded = stuffer.decodeFrame(encoded);
        assertEquals(data, decoded);
    }

    @org.junit.Test
    public void decodedDataSameAsUnencodedSingleFrame2() throws Exception {
        String data = "00000";
        String encoded = stuffer.encodeFrame(data);
        String decoded = stuffer.decodeFrame(encoded);
        assertEquals(data, decoded);
    }

    @org.junit.Test
    public void decodedDataSameAsUnencodedSingleFrame3() throws Exception {
        String data = "11110";
        String encoded = stuffer.encodeFrame(data);
        String decoded = stuffer.decodeFrame(encoded);
        assertEquals(data, decoded);
    }

    @org.junit.Test
    public void decodedDataSameAsUnencoded() throws Exception {
        String data = "001111110010111111011010101010100111";
        String encoded = stuffer.encodeFrame(data);
        String decoded = stuffer.decodeFrame(encoded);
        assertEquals(data, decoded);
    }

    @org.junit.Test
    public void decodedDataSameAsUnencoded2() throws Exception {
        String data = "00111111001011111101000000000001111111111111111111111111111111101101010011111111111111111101101010000000000000101010101010100111111011110010011010101010100111";
        String encoded = stuffer.encodeFrame(data);
        String decoded = stuffer.decodeFrame(encoded);
        assertEquals(data, decoded);
    }
}