import java.net.DatagramSocket;
import java.io.IOException;
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
    // TODO check if you need to be passing in data and packet everywhere!!
    public synchronized void onReceipt(DatagramPacket packet) {
        try {
            byte[] data = packet.getData();
            switch(data[TYPE_POS]) {
                case PUBLISH:
                    System.out.println("Received request to publish");
                    sendMessage(data, packet);
                    break;
                case SUBSCRIBE:
                    System.out.println("Received request to subscribe");
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
    public synchronized void sendMessage(byte[] receivedData, DatagramPacket receivedPacket) throws Exception {
        byte[] buffer = new byte[receivedPacket.getLength()-CONTROL_HEADER_LENGTH];
        System.arraycopy(receivedData, CONTROL_HEADER_LENGTH, buffer, 0, buffer.length);
		String content = new String(buffer);

        String[] splitContent = content.split(":");
        String topic = splitContent[0];
            
        for (Map.Entry<String, HashSet<InetSocketAddress>> entry : subscriberMap.entrySet()) {
            String subscriberTopic = entry.getKey();
            String regexSubscriberTopic = subscriberTopic.replace("*", ".*?");

            if(topic.matches(regexSubscriberTopic)) {
                HashSet<InetSocketAddress> subscribers = entry.getValue();
                Iterator<InetSocketAddress> i = subscribers.iterator();
                while(i.hasNext()) {
                    InetSocketAddress addr = i.next();
                    DatagramPacket packet= new DatagramPacket(receivedPacket.getData(), receivedPacket.getLength(), addr);
                    socket.send(packet);
                    System.out.println("Packet \"" + content + "\" send to " + addr);
                }
            }
        }
        sendAck(PUBACK, receivedPacket);
	}

    private void sendAck(byte ackType, DatagramPacket packet) throws IOException {
        byte[] data = new byte[1];
        data[TYPE_POS] = ackType;
        InetSocketAddress destinationAddr = (InetSocketAddress) packet.getSocketAddress();
        DatagramPacket ack = new DatagramPacket(data, data.length, destinationAddr);
        socket.send(ack);
    }

    // getTopic() function to implement

    private void subscribe(byte[] data, DatagramPacket packet) throws IOException {
        InetSocketAddress subscriberAddr = (InetSocketAddress) packet.getSocketAddress();
        String topic = getStringData(data, packet);

        if (!subscriberMap.containsKey(topic)) {
            // If the subscriberMap doesn't already contain the topic, create a new HashSet for it.
            HashSet<InetSocketAddress> subscribers = new HashSet<InetSocketAddress>();
            subscriberMap.put(topic, subscribers);

        }

        if (subscriberMap.get(topic).add(subscriberAddr))
            System.out.println("Subscription to \"" + topic + "\" added successfully.");

        System.out.println("Current subscribers to the topic \"" + topic + "\" are:");
        HashSet<InetSocketAddress> subscribersCheck = subscriberMap.get(topic);
        Iterator<InetSocketAddress> i = subscribersCheck.iterator();
        while(i.hasNext()) {
            System.out.println(i.next());
        }
        sendAck(SUBACK, packet);       
    }

    private void unsubscribe(byte[] data, DatagramPacket packet) throws IOException {
        InetSocketAddress subscriberAddr = (InetSocketAddress) packet.getSocketAddress();
        String topic = getStringData(data, packet);

        // Check that it's subscribed to first - null ptr exception
        if (subscriberMap.get(topic).remove(subscriberAddr))
            System.out.println("Subscription to " + topic + " removed successfully.");

        System.out.println("Current subscribers to the topic \"" + topic + "\" are:");
        HashSet<InetSocketAddress> subscribersCheck = subscriberMap.get(topic);
        Iterator<InetSocketAddress> i = subscribersCheck.iterator();
        while(i.hasNext()) {
            System.out.println(i.next());
        }
        sendAck(UNSUBACK, packet);    
    }

    public synchronized void start() throws Exception {
		System.out.println("Broker program starting...");
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
