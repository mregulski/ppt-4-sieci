package reshi.ppt.networks.csmacd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcin
 * @date 2016-04-24
 */
public class Carrier {
    ArrayList<String> data;

    public Carrier() {
        data = new ArrayList<>();
    }
    /**
     * Check if transmission is possible.
     * @return
     * @throws InterruptedException
     */
    public boolean canTransmit() throws InterruptedException {
        int sizePre = this.data.size();
        Thread.sleep(1000);
        return sizePre == this.data.size();
    }

    public void push(String packet) {
        data.add(packet);
        System.out.println(packet);
    }
}
