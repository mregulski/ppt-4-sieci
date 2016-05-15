import java.time.Instant;

/**
 * @author Marcin Regulski on 14.05.2016.
 */
final public class ConfirmablePacket {
    private int timesSent;
    private final int seq;
    private final long sentAt;
    private final Z2Packet packet;

    long getSentAt() {
        return sentAt;
    }
    int getSeq() {
        return seq;
    }

    Z2Packet getPacket() {
        return packet;
    }

    public int onResend() {
        return ++timesSent;
    }
    /**
     * Wraps an existing Z2Packet with known sequence number that has just been sent
     * (sentAt is set to Instant.now()).
     * @param seq sequential number of the packet
     * @param packet Z2Packet this object wraps
     */
    ConfirmablePacket(int seq, Z2Packet packet) {
        this.seq = seq;
        this.packet = packet;
        this.sentAt = Instant.now().toEpochMilli();
        this.timesSent = 0;
    }

}
