package reshi.ppt.networks.csmacd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcin
 */
class Carrier {
    ArrayList<String> data;

    Carrier() {
        data = new ArrayList<>();
    }
    /**
     * Check if transmission is possible.
     * @return true if no data has been pushed within a single interval.
     * @throws InterruptedException
     */
    boolean canTransmit() throws InterruptedException {
        int sizePre = this.data.size();
        Thread.sleep(100);
        return sizePre == this.data.size();
    }

    void push(String packet) {
        data.add(packet);
        System.out.println(packet);
    }
}
