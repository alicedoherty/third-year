import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Broker extends Node {
    // private Map<String, ArrayList<InetSocketAddress>> subscriberTable;
    InetSocketAddress dstAddress;
    private Map<String, InetSocketAddress> subscriberMap;
    
    Broker() {
		try {
			// dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(BKR_PORT);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
        // subscriberTable = new HashMap<String, ArrayList<InetSocketAddress>>();
        //Map<String, InetSocketAddress> subscriberMap;
        subscriberMap = new HashMap<String, InetSocketAddress>();
        //subscriberMap.put("temp", "192.168.10.30");
	}

    // Receiver code
    public synchronized void onReceipt(DatagramPacket packet) {
        try {
            System.out.println("Received packet");
    
            byte[] data = packet.getData();
    
            switch(data[TYPE_POS]) {
                case PUBLISH:
                    System.out.println("Received request to publish.");
                    sendMessage(packet);
                    break;
                case SUBSCRIBE:
                    System.out.println("Received request to subscribe.");
                    // subscribe();
                    break;
                default:
                    System.out.println("Unexpected packet" + packet.toString());
            }

        }
        catch(Exception e) {e.printStackTrace();}
	}

    // Sender code - publish code
    public synchronized void sendMessage(DatagramPacket receivedPacket) throws Exception {
        // String input= "humidity 20";

        byte[] receivedData = receivedPacket.getData();
        byte[] buffer = new byte[receivedData[LENGTH_POS]];
		System.arraycopy(receivedData, HEADER_LENGTH, buffer, 0, buffer.length);
		String content = new String(buffer);

        byte[] buffer2 = content.getBytes();
        byte[] data = new byte[HEADER_LENGTH+buffer2.length];
        data[TYPE_POS] = PUBLISH;
        data[LENGTH_POS] = (byte)buffer2.length;
        System.arraycopy(buffer2, 0, data, HEADER_LENGTH, buffer2.length);

        // byte[] data = receivedPacket.getData();

        System.out.println("Sending packet...");

        // dstAddress = new InetSocketAddress(DEFAULT_DST, SUB_PORT);



        // subscriberTable.put(setTopic, subscribers);

        // Should be in subscribe()

        String[] splitContent = content.split("\\s+");
        String topic = splitContent[0];
        System.out.println("Topic is: " + topic);
        if(topic.equals("temperature")){
            

            InetSocketAddress dstAddress = subscriberMap.get(topic);
            // dstAddress = dstAddresses[0];
    
            DatagramPacket packet= new DatagramPacket(data, data.length, dstAddress);
    
            // packet.setSocketAddress(dstAddress);
            socket.send(packet);
            System.out.println("Packet sent");
            // this.wait();
        }

	}

    // private void subscribe() {
    //     String topic = "temperature";
    //     ArrayList<InetSocketAddress> subscribers = subscriberTable.get(topic);
    //     InetSocketAddress subscriberAddr = new InetSocketAddress(DEFAULT_DST, SUB_PORT);
    //     subscribers.add(subscriberAddr);
    //     subscriberTable.put(topic, subscribers);
    // }

    public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
        System.out.println("Subscription added");
        InetSocketAddress subscriberAddr = new InetSocketAddress(DEFAULT_DST, SUB_PORT);
        subscriberMap.put("temperature", subscriberAddr);
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
