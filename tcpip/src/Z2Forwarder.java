import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class Z2Forwarder {

    private int destinationPort;
    private static final int datagramSize = 50;

    /**
     * Parameters in ms
     */
    private static final int capacity = 1000;
    private static final int minDelay = 200;
    private static final int maxDelay = 1000;
    private static final int sleepTime = 50;

    /**
     * Transmission reliability
     */
    private static final double reliability = 0.70;
    /**
     * Probability of sending a packet twice
     */
    private static final double duplicateChance = 0.3;


    private InetAddress localHost;

    private DatagramSocket socket;
    private DatagramPacket[] buffer;
    private int[] delay;

    private Receiver receiver;
    private Sender sender;

    private Random random;


    public Z2Forwarder(int myPort, int destPort) throws IOException {
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        buffer = new DatagramPacket[capacity];
        delay = new int[capacity];
        random = new Random();
        receiver = new Receiver();
        sender = new Sender();
    }

    private class Receiver extends Thread {

        void addToBuffer(DatagramPacket packet) {
            if (random.nextDouble() > reliability) return; // UTRATA PAKIETU
            int i;
            synchronized (buffer) {
                for (i = 0; i < capacity && buffer[i] != null; i++) ;
                if (i < capacity) {
                    delay[i] = minDelay
                            + (int) (random.nextDouble() * (maxDelay - minDelay));
                    buffer[i] = packet;
                }
            }
        }


        public void run() {
            while (true) {
                DatagramPacket packet =
                        new DatagramPacket(new byte[datagramSize], datagramSize);
                try {
                    socket.receive(packet);
                    Z2Packet pac = new Z2Packet(packet.getData());
                    System.out.println("\u001b[36m--> Received: " + pac.getIntAt(0)
                            + ": " + (char) pac.data[4] + "\u001b[37m");
                    addToBuffer(packet);
                    while (random.nextDouble() < duplicateChance) addToBuffer(packet);

                } catch (java.io.IOException e) {
                    System.out.println("Forwader.Receiver.run: " + e);
                }
            }
        }

    }

    private class Sender extends Thread {

        void checkBuffer()
                throws java.io.IOException {
            synchronized (buffer) {
                int i;
                for (i = 0; i < capacity; i++)
                    if (buffer[i] != null) {
                        delay[i] -= sleepTime;
                        if (delay[i] <= 0) {
                            buffer[i].setPort(destinationPort);
                            socket.send(buffer[i]);
                            Z2Packet packet = new Z2Packet(buffer[i].getData());
                            System.out.println("\u001B[31mSending: " + packet.getIntAt(0)
                                    + ": " + packet.data[4] + " -->\u001b[37m");
                            buffer[i] = null;
                        }
                    }
            }
        }


        public void run() {
            try {
                while (true) {
                    checkBuffer();
                    sleep(sleepTime);
                }
            } catch (Exception e) {
                System.out.println("Forwader.Sender.run: " + e);
            }
        }

    }


    public static void main(String[] args)
            throws IOException {
        Z2Forwarder forwarder = new Z2Forwarder(Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));
        forwarder.sender.start();
        forwarder.receiver.start();
    }


}

