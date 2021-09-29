import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Broker {
    /**
	 * TODO
     * 
	 * @param args
	 */

    public static void main(String[] args) {
		final int RECV_PORT = 12345;
		final int MTU = 1500;

        DatagramPacket packet;
		DatagramSocket socket;
		InetAddress address;
		int port;

		ObjectInputStream ostream;
		ByteArrayInputStream bstream;
		byte[] buffer;

        DatagramPacket packet2;
        DatagramSocket socket2;
        ObjectOutputStream ostream2;
		ByteArrayOutputStream bstream2;
		byte[] buffer2;
        

        try {
            System.out.println("Broker - Program start");

            // extract destination from arguments
            address= InetAddress.getByName("192.168.10.20"); // InetAddress.getByName(args[0]);
            port= RECV_PORT;                     // Integer.parseInt(args[1]);

            // create buffer for data, packet and socket
            buffer= new byte[MTU];
            packet= new DatagramPacket(buffer, buffer.length);
            socket= new DatagramSocket(port, address);

            Map<String, String> subscriberMap;
            subscriberMap = new HashMap<String, String>();
            subscriberMap.put("temp", "192.168.10.30");

            for (int i = 0; i < 5; i++) {
                // attempt to receive packet
                System.out.println("Trying to receive");
                socket.receive(packet);

                // extract data from packet
                buffer= packet.getData();
                bstream= new ByteArrayInputStream(buffer);
                ostream= new ObjectInputStream(bstream);

                String data = ostream.readUTF();

                // print data and end of program
                System.out.println("Data: " + data);

                bstream2= new ByteArrayOutputStream();
                ostream2= new ObjectOutputStream(bstream2);

                // sending data onto subscriber
                String[] splitData = data.split("\\s+");

                if (splitData[0].equals("temp")) {
                    
                    ostream2.writeUTF(splitData[1]);

                    ostream2.flush();
                    buffer2= bstream2.toByteArray();
        
    
                    InetAddress subscriberAddress = InetAddress.getByName("192.168.10.30");
                    // create packet addressed to destination
                    packet2= new DatagramPacket(buffer2, buffer2.length,
                            subscriberAddress, port);
        
                    // create socket and send packet
                    socket2= new DatagramSocket();
                    socket2.send(packet2);
                }
            }  
            System.out.println("Broker - Program end");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}