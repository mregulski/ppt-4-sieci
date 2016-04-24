package reshi.ppt.networks.csmacd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        ArrayList<Host> hosts = new ArrayList<>();
        Carrier carrier = new Carrier();
        Random r = new Random();
        for (int i = 0; i < 15; i++) {
            hosts.add(new Host(i, carrier, r));
        }
        hosts.forEach(Host::start);

    }
}
