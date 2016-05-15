import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Z2Receiver {
    private static final int datagramSize = 50;
    private InetAddress localHost;
    private int destinationPort;
    private DatagramSocket socket;

    private ReceiverThread receiver;
    private ConfirmationThread confirmer;
    private List<Z2Packet> receivedPackets;
    int currentSeq;
    private Lock lock = new ReentrantLock();

    public Z2Receiver(int myPort, int destPort)
            throws Exception {
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        receiver = new ReceiverThread();
        confirmer = new ConfirmationThread(3);
        receivedPackets = new ArrayList<>();
        currentSeq = 0;
    }

    private class ReceiverThread extends Thread {
        public void run() {
            try {
                while (true) {
                    byte[] data = new byte[datagramSize];
                    DatagramPacket packet =
                            new DatagramPacket(data, datagramSize);
                    socket.receive(packet);
                    Z2Packet p = new Z2Packet(packet.getData());
                    if (p.getIntAt(0) >= currentSeq) {
                        lock.lock();
                        receivedPackets.add(p);
                        lock.unlock();
                    }
                }
            } catch (Exception e) {
                System.out.println("Z2Receiver.ReceiverThread.run: " + e);
            }
        }
    }

    private class ConfirmationThread extends Thread {
        private int repeats;
        private int interval = 10;

        public ConfirmationThread(int repeats) {
            this.repeats = repeats;
        }
        @Override
        public void run() {
            try {
                while (true) {
                    lock.lock();
                    Z2Packet packetToPrint = receivedPackets.stream()
                            .filter((x) -> x.getIntAt(0) == currentSeq)
                            .findFirst()
                            .orElse(null);
                    lock.unlock();
                    if (packetToPrint != null) {
                        System.out.println("R:" + packetToPrint.getIntAt(0)
                                + ": " + (char) packetToPrint.data[4]);
                        DatagramPacket confirmation = new DatagramPacket(packetToPrint.data,
                                packetToPrint.data.length, localHost, destinationPort);
                        confirmation.setPort(destinationPort);
                        currentSeq++;
                        for (int i = 0; i < repeats; i++) {
                            socket.send(confirmation);
                        }
                        lock.lock();
                        List<Z2Packet> confirmed = receivedPackets.stream()
                                .filter((x) -> x.getIntAt(0) < currentSeq)
                                .collect(Collectors.toList());
                        receivedPackets.removeAll(confirmed);
                        lock.unlock();
                    }
                    sleep(interval);
                }
            } catch (Exception e) {
                System.out.println("Z2Receiver.ConfirmationThread.run: " + e);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
            throws Exception {
        Z2Receiver receiver = new Z2Receiver(Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));
        receiver.receiver.start();
        receiver.confirmer.start();
    }


}
