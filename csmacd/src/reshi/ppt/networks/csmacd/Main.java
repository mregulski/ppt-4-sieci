package reshi.ppt.networks.csmacd;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Main {

    static double packetProbability = 0.3;
    static int packetInterval = 250;
    private static int hostNumber = 10;
    static boolean logInfo = false;
    public static void main(String[] args) {

        ArrayList<Host> hosts = new ArrayList<>();
        Carrier carrier = new Carrier();
        Random r = new Random();


        for (int i = 0; i < hostNumber; i++) {
            hosts.add(new Host(i, carrier, r));
        }
        hosts.forEach(Host::start);
        int duration = 100; // test lasts

//        for (int time = 0; time < duration; time++) {
        while(true) {
//            logInfo("checking hosts...");
            boolean allFinished = true;
            for(Host x : hosts) {
                boolean xFin = x.isFinished();
//                logInfo(String.format("#%d : %d/%d (%b)", x.getNumber(), x.getSuccesses(),
//                        x.getPacketsToSend(), xFin));
                allFinished = allFinished && xFin;

            }
//            logInfo("checked. (" + allFinished + ")");
            if(allFinished) {
                logInfo("All hosts done.");
                hosts.forEach(Host::finish);
                logInfo("SIMULATION FINISHED.");
                break;
            }

            if(r.nextDouble() < packetProbability) {
                int idx = r.nextInt(hosts.size()-1);
                hosts.get(idx).addPacket();
            }

            try {
                Thread.sleep(packetInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(Host x : hosts) {
            logInfo("["+x.getNumber() + "] " + (x.isFinished() ? "done" : "busy"));
        }
    }

    private static void logInfo(String message) {
        if(logInfo) {
            System.out.println(Colors.BLUE + "[INFO] " + message + Colors.RESET);
        }
    }
}
