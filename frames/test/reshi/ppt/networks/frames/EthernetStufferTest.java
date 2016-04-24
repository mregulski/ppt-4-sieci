package reshi.ppt.networks.frames;

import static org.junit.Assert.*;

/**
 *  @author Marcin Regulski on 22.04.2016.
 */
public class EthernetStufferTest {

    private static EthernetStuffer stuffer;
    @org.junit.BeforeClass
    public static void setUp() throws Exception {
        stuffer = new EthernetStuffer(5);
    }

    @org.junit.Test
    public void decodedDataSameAsUnencoded() throws Exception {
        String data = "001111110010111111011010101010100111";
        String encoded = stuffer.encodeFrame(data);
        String decoded = stuffer.decodeFrame(encoded);
        assertEquals(data, decoded);
    }

    @org.junit.Test
    public void isValidData() throws Exception {
        String data = "010101";
        assertEquals(false, stuffer.isValidFrame(data));
    }
}