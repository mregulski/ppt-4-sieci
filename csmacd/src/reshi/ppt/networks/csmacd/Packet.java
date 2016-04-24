package reshi.ppt.networks.csmacd;

/**
 * @author Marcin
 * @date 2016-04-24
 */
public class Packet {
    private final int sourceId;
    private final int targetId;
    private final String content;

    public Packet(int source, int target, String content) {
        this.sourceId = source;
        this.targetId = target;
        this.content = content;
    }
}
