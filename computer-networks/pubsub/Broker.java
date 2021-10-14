import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Broker extends Node {
    InetSocketAddress dstAddress;
    private Map<String, HashSet<InetSocketAddress>> subscriberMap;
    
    Broker() {
		try {
			socket= new DatagramSocket(BKR_PORT);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
        subscriberMap = new HashMap<String, HashSet<InetSocketAddress>>();
	}

    // Receiver code
    public synchronized void onReceipt(DatagramPacket packet) {
        try {
            byte[] data = packet.getData();
            switch(data[TYPE_POS]) {
                case PUBLISH:
                    System.out.println("Received request to publish.");
                    sendMessage(data);
                    break;
                case SUBSCRIBE:
                    System.out.println("Received request to subscribe.");
                    subscribe(data, packet);
                    break;
                case UNSUBSCRIBE:
                    System.out.println("Received request to unsubscribe");
                    unsubscribe(data, packet);
                    break;
                default:
                    System.out.println("Received unexpected packet" + packet.toString());
            }

        }
        catch(Exception e) {e.printStackTrace();}
	}

    // Sender code - publish code
    public synchronized void sendMessage(byte[] receivedData) throws Exception {
        // byte[] receivedData = receivedPacket.getData();
        byte[] buffer = new byte[receivedData[LENGTH_POS]];
		System.arraycopy(receivedData, HEADER_LENGTH, buffer, 0, buffer.length);
		String content = new String(buffer);

        // byte[] buffer2 = content.getBytes();
        // byte[] data = new byte[HEADER_LENGTH+buffer2.length];
        // data[TYPE_POS] = PUBLISH;
        // data[LENGTH_POS] = (byte)buffer2.length;
        // System.arraycopy(buffer2, 0, data, HEADER_LENGTH, buffer2.length);

        // byte[] data = receivedPacket.getData();
        System.out.println("Publishing packet: " + content);
       //  System.out.println("Sending packet...");

        String[] splitContent = content.split(":");
        String topic = splitContent[0];
        System.out.println("Topic is: " + topic);
        if(subscriberMap.containsKey(topic)) {
            HashSet<InetSocketAddress> subscribers = subscriberMap.get(topic);
            Iterator<InetSocketAddress> i = subscribers.iterator();
            while(i.hasNext()) {
                // System.out.println(i.next());
                DatagramPacket packet= new DatagramPacket(receivedData, receivedData.length, i.next());
                //System.out.println(i.next());
                socket.send(packet);
                System.out.println("Packet sent");
            }
        }

	}

    // getTopic() function to implement

    private void subscribe(byte[] data, DatagramPacket packet) {
        InetSocketAddress subscriberAddr = (InetSocketAddress) packet.getSocketAddress();
        String topic = getStringData(data);

        // subscriberMap.computeIfAbsent(topic, k -> new HashSet<>()).add(subscriberAddr);

        if (!subscriberMap.containsKey(topic)) {
            // If the subscriberMap doesn't already contain the topic, create a new HashSet for it.
            HashSet<InetSocketAddress> subscribers = new HashSet<InetSocketAddress>();
            subscriberMap.put(topic, subscribers);

        }

        if (subscriberMap.get(topic).add(subscriberAddr))
            System.out.println("Subscription to " + topic + " added successfully.");

        System.out.println("Current subscribers to topic " + topic + " are:");
        HashSet<InetSocketAddress> subscribersCheck = subscriberMap.get(topic);
        Iterator<InetSocketAddress> i = subscribersCheck.iterator();
        while(i.hasNext()) {
            System.out.println(i.next());
        }       
    }

    private void unsubscribe(byte[] data, DatagramPacket packet) {
        InetSocketAddress subscriberAddr = (InetSocketAddress) packet.getSocketAddress();
        String topic = getStringData(data);

        if (subscriberMap.get(topic).remove(subscriberAddr))
            System.out.println("Subscription to " + topic + "removed successfully.");

        System.out.println("Current subscribers to topic " + topic + " are:");
        HashSet<InetSocketAddress> subscribersCheck = subscriberMap.get(topic);
        Iterator<InetSocketAddress> i = subscribersCheck.iterator();
        while(i.hasNext()) {
            System.out.println(i.next());
        }    
    }

    public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
        
        // InetSocketAddress subscriberAddr = new InetSocketAddress(DEFAULT_DST, SUB_PORT);
        // subscriberMap.put("temperature", subscriberAddr);
        // System.out.println("Subscription added");
        //subscribe();
		while (true) {
			this.wait();
		}
	}

	public static void main(String[] args) {
		try {
			(new Broker()).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
