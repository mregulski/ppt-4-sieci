package reshi.ppt.networks;

import static org.junit.Assert.*;

/**
 *  @author Marcin Regulski on 22.04.2016.
 */
public class EthernetStufferTest {

    private static EthernetStuffer stuffer;
    @org.junit.BeforeClass
    public static void setUp() throws Exception {
        stuffer = new EthernetStuffer();
    }
    @org.junit.Test
    public void encodeData() throws Exception {
        char[] data = "10101010".toCharArray();
        char[] encoded = stuffer.encodeData(data);
        assertArrayEquals("011111101010101001111110".toCharArray(), encoded);
    }

    @org.junit.Test
    public void encodeDataWithStuffing() throws Exception {
        char[] data = "0101111110101".toCharArray();
        char[] encoded = stuffer.encodeData(data);
        assertArrayEquals("011111100101111101010101111110".toCharArray(), encoded);
    }

    @org.junit.Test
    public void decodeDataSameAsUnencoded() throws Exception {
        char data[] = "001111110010111111011010101010100111".toCharArray();
        char[] encoded = stuffer.encodeData(data);
        char[] decoded = stuffer.decodeData(encoded);
        assertArrayEquals(data, decoded);
    }

    @org.junit.Test
    public void isValidData() throws Exception {
        char[] data = "010101".toCharArray();
        assertEquals(false, stuffer.isValidData(data));
    }
}