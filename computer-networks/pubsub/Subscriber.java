// ReceiverProcess.java

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Subscriber {

	/**
	 * The method opens a socket on a given port and hostname - currently, this is fixed to
	 * port 12345 and the local host. It attempts to receive a packet of a certain size,
	 * retrieves the payload of the packet and attempts to interpret the first object in the
	 * payload as a string.
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

        try {
            System.out.println("Subscriber - Program start");
            System.out.println("Subscribed to topic(s): \"temperature\"");

            // extract destination from arguments
            address= InetAddress.getByName("192.168.10.30"); // InetAddress.getByName(args[0]);
            port= RECV_PORT;                     // Integer.parseInt(args[1]);

            // create buffer for data, packet and socket
            buffer= new byte[MTU];
            packet= new DatagramPacket(buffer, buffer.length);
            socket= new DatagramSocket(port, address);

            for (int i = 0; i < 10; i++) {
                // attempt to receive packet
               //  System.out.println("Trying to receive");
                socket.receive(packet);

                // extract data from packet
                buffer= packet.getData();
                bstream= new ByteArrayInputStream(buffer);
                ostream= new ObjectInputStream(bstream);

                // print data and end of program
                String data = ostream.readUTF();
                System.out.println("Data received. ");
                String[] splitData = data.split("\\s+");
                System.out.println("Data: " + data);
                System.out.println("Topic: " + splitData[0] + ", payload: " + splitData[1]);
            }  
            //System.out.println("Subscriber - Program end"); 
        }
        catch(Exception e) {
            e.printStackTrace();
        }

	}

}