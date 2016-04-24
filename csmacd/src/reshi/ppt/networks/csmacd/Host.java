package reshi.ppt.networks.csmacd;

/**
 * @author Marcin
 */
public class Host implements Runnable{
    private final int messageLength = 32;
    private final int id;
    private int waitTime = 0;
    private final Carrier carrier;

    public Host(int id) {
        this.id = id;
        this.carrier = null;
    }

    @Override
    public void run() {

    }
}
