package reshi.ppt.networks.csmacd;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author Marcin
 */
class Host extends Thread {
    private String BEGIN_HEADER = "[BEGIN %d]";
    private String END_HEADER = "[END %d]";
    private String COLLISION = "[JAM %d]";
    private final int id;
    private int maxWaitTime = 1;
    private int packetsToSend = 5;
    private final Carrier carrier;
    private int successes = 0;
    private Random r;
    boolean SimDone = false;

    Host(int id, Carrier carrier, Random r) {
        this.id = id;
        this.carrier = carrier;
        this.r = r;
        this.BEGIN_HEADER = String.format(BEGIN_HEADER, id);
        this.END_HEADER = String.format(END_HEADER, id);
        this.COLLISION = Colors.BOLD + Colors.RED + String.format(COLLISION, id)  + Colors.RESET;
    }

    int getNumber() {
        return this.id;
    }

    int getSuccesses() {
        return this.successes;
    }

    int getPacketsToSend() {
        return this.packetsToSend;
    }

    void addPacket() {
        this.packetsToSend++;
    }
    boolean isFinished() {
        return successes >= packetsToSend;
    }

    void finish() {
        logInfo("Finishing...");
        this.SimDone = true;
    }

    private void logInfo(String message) {
        if(Main.logInfo) {
            System.out.println(Colors.BLUE + "[Host #" + this.id + "] " + message + Colors.RESET);
        }
    }

    private void success() {
        successes++;
        System.out.println(Colors.BOLD + Colors.GREEN + "[Host #" + this.id + String.format("] Success: (%d/%d)",
                this.successes, this.packetsToSend) + Colors.RESET);
        maxWaitTime = 1;
    }

    @Override
    public void run() {
        String message;
        while(!SimDone) {
            if(successes < packetsToSend)
            {
                message = Integer.toString(r.nextInt(10000));
                send(message);
            }
            else {
                logInfo("Nothing to send. Sleeping...");
                try {
                    Thread.sleep(Main.packetInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        logInfo("Done.");
    }

    private void send(String message) {
        try {
            while(!carrier.canTransmit()) {
                int waitTime = r.nextInt(maxWaitTime) * 100;
                if(this.maxWaitTime <= 8) {
                    this.maxWaitTime *= 2;
                }
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
                success();
            }
            else {
                carrier.push(COLLISION);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            carrier.push(COLLISION);
            if(this.maxWaitTime <= 8) {
                this.maxWaitTime *= 2;
            }
        }
    }
}
