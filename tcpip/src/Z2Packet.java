import java.util.Arrays;

public class Z2Packet {
    /**
     * Datagram data
     */
    byte[] data;

    /**
     * Creates an empty packet with specified size
     * @param size size of packet in bytes
     */
    public Z2Packet(int size)
    {
        data = new byte[size];
    }

    /**
     * create packet containing an array of bytes
     * @param b source array
     */
    public Z2Packet(byte[] b)
    {
        data = b;
    }

    /**
     * Save an int as bytes at any position
     * @param value number to save
     * @param idx position where value's first byte will be stored
     */
    public void putIntAt(int value, int idx)
    {
        data[idx] = (byte) ((value >> 24) & 0xFF);
        data[idx + 1] = (byte) ((value >> 16) & 0xFF);
        data[idx + 2] = (byte) ((value >> 8) & 0xFF);
        data[idx + 3] = (byte) ((value) & 0xFF);
    }

    /**
     * Read an int from 4 bytes at any position
     * @param idx index of the first byte to read
     * @return integer obtained from the byte at idx and next 3 bytes
     */
    public int getIntAt(int idx)
    {
        int x;
        x = (((int) data[idx]) & 0xFF) << 24;
        x |= (((int) data[idx + 1]) & 0xFF) << 16;
        x |= (((int) data[idx + 2]) & 0xFF) << 8;
        x |= (((int) data[idx + 3]) & 0xFF);
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Z2Packet)) { return false; }
        return this.getIntAt(0) == ((Z2Packet) obj).getIntAt(0)
                && this.data[4] == ((Z2Packet) obj).data[4];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
