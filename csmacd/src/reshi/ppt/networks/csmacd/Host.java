package reshi.ppt.networks.csmacd;

import java.awt.*;
import java.util.Random;

/**
 * @author Marcin
 */
public class Host extends Thread {
    private String BEGIN_HEADER = "[BEGIN %d]";
    private String END_HEADER = "[END %d]";
    private String COLLISION = "[JAM %d]";
    private final int id;
    private int maxWaitTime = 10;
    private final Carrier carrier;
    private int successes = 0;
    Random r;
    public Host(int id, Carrier carrier, Random r) {
        this.id = id;
        this.carrier = carrier;
        this.r = r;
        this.BEGIN_HEADER = String.format(BEGIN_HEADER, id);
        this.END_HEADER = String.format(END_HEADER, id);
        this.COLLISION = String.format(COLLISION, id);
    }

    @Override
    public void run() {
        String message;
        int packetsToSend = 5;
        while(successes < packetsToSend) {
            message = Integer.toString(r.nextInt(1000));
            send(message);
        }
    }

    private void send(String message) {
        try {
            while(!carrier.canTransmit()) {
                int waitTime = r.nextInt(maxWaitTime) * 100;
                System.out.println("[" + this.id + "] waiting (" + waitTime + ")");
                Thread.sleep(waitTime);
            }
            carrier.push(BEGIN_HEADER);
            Thread.sleep(50);
            carrier.push(message);
            Thread.sleep(50);
            carrier.push(END_HEADER);
            String lastExpectedEnd = carrier.data.get(carrier.data.size()-1);
            String lastExpectedBegin = carrier.data.get(carrier.data.size()-3);
            if(lastExpectedEnd.equals(this.END_HEADER) && lastExpectedBegin.equals(this.BEGIN_HEADER)) {
                System.out.println(Colors.BOLD + Colors.GREEN + "[SUCCESS] Host " + this.id+ Colors.RESET);
                this.successes++;
            }
            else {
                carrier.push(Colors.BOLD + Colors.RED + COLLISION + Colors.RESET);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
