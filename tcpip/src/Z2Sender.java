
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Z2Sender {
    private static final int datagramSize = 50;
    private static final int sleepTime = 50;
    static final int maxPacket = 50;
    private InetAddress localHost;
    private int destinationPort;
    private DatagramSocket socket;
    private SenderThread sender;
    private ReceiverThread receiver;
    private RetransmissionThread retransmission;
    private List<ConfirmablePacket> sentPackets = new LinkedList<>();
    final ReentrantLock lock = new ReentrantLock();


    public Z2Sender(int myPort, int destPort)
            throws Exception {
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        sender = new SenderThread();
        receiver = new ReceiverThread();
        retransmission = new RetransmissionThread();
    }

    class SenderThread extends Thread {
        public void run() {
            int i, x;
            try {
                for (i = 0; (x = System.in.read()) >= 0; i++) {
                    Z2Packet p = new Z2Packet(4 + 1);
                    p.putIntAt(i, 0);
                    p.data[4] = (byte) x;
                    DatagramPacket packet =
                            new DatagramPacket(p.data, p.data.length,
                                    localHost, destinationPort);
                    socket.send(packet);
                    lock.lock();
                    sentPackets.add(new ConfirmablePacket(i, p));
                    lock.unlock();
                    sleep(sleepTime);
                }
            } catch(ConcurrentModificationException conc) {
                conc.printStackTrace();
            }
            catch (Exception e) {
                System.out.println("Z2Sender.SenderThread.run: " + e);
            }
        }

    }

    class RetransmissionThread extends Thread {
        long retransmissionTimeout = 1000;
        long retransmissionInterval = 100;
        @Override
        public void run() {
            try {
                while (true) {
                    lock.lock();
                    for (ConfirmablePacket packet : sentPackets) {

                        if (Instant.now().toEpochMilli() - packet.getSentAt() <= retransmissionTimeout) {
                            Z2Packet p = packet.getPacket();
                            DatagramPacket repeated = new DatagramPacket(p.data, p.data.length,
                                    localHost, destinationPort);
                            socket.send(repeated);
                        }
                    }
                    lock.unlock();
                    sleep(retransmissionInterval);
                }
            } catch(ConcurrentModificationException conc) {
                conc.printStackTrace();
            }
            catch (Exception e) {
                System.out.println("Z2Sender.RetransmissionThread.run: " + e);
            }
        }
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
                    lock.lock();
                    ConfirmablePacket match = sentPackets.stream()
                            .filter(x -> x.getSeq() == p.getIntAt(0))
                            .findFirst()
                            .orElse(null);
                    lock.unlock();
                    if(match != null) {
                        // match.getPacket is same as p
                        System.out.println("\u001b[32mS:" + p.getIntAt(0) +
                                ": " + (char) p.data[4] + "\u001b[37m");
                        lock.lock();
                        List<ConfirmablePacket> confirmed = sentPackets.stream()
                                .filter((x) -> x.getSeq() <= match.getSeq())
                                .collect(Collectors.toList());
                        sentPackets.removeAll(confirmed);
                        lock.unlock();
                    }
                }
            } catch (ConcurrentModificationException conc) {
                conc.printStackTrace();
            } catch (Exception e) {
                System.out.println("Z2Sender.ReceiverThread.run: " + e);
            }
        }

    }

    public static void main(String[] args)
            throws Exception {
        Z2Sender sender = new Z2Sender(Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));
        sender.sender.start();
        sender.receiver.start();
        sender.retransmission.start();
    }


}
