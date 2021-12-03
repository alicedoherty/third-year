// Author: Alice Doherty

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public abstract class Node {
    //
    // OpenFlow Packet Types
	// Note: the majority of these aren't used but represent the different possible OpenFlow packets.
    //

    // Immutable messages
	protected static final byte OFPT_HELLO = 0;
	protected static final byte OFPT_ERROR = 1;
	protected static final byte OFPT_ECHO_REQUEST = 2;
	protected static final byte OFPT_ECHO_REPLY = 3;
	protected static final byte OFPT_EXPERIMENTER = 4;

	// Switch configuration messages
	protected static final byte OFPT_FEATURES_REQUEST = 5;
	protected static final byte OFPT_FEATURES_REPLY = 6;
	protected static final byte OFPT_GET_CONFIG_REQUEST = 7;
	protected static final byte OFPT_GET_CONFIG_REPLY = 8;
	protected static final byte OFPT_SET_CONFIG = 9;

	// Asynchronous messages
	protected static final byte OFPT_PACKET_IN = 10;
	protected static final byte OFPT_FLOW_REMOVED = 11;
	protected static final byte OFPT_PORT_STATUS = 12;

    // Controller command messages
	protected static final byte OFPT_PACKET_OUT = 13;
	protected static final byte OFPT_FLOW_MOD = 14;
	protected static final byte OFPT_GROUP_MOD = 15;
	protected static final byte OFPT_PORT_MOD = 16;
	protected static final byte OFPT_TABLE_MOD = 17;

	// Multipart messages
	protected static final byte OFPT_MULTIPART_REQUEST = 18;
	protected static final byte OFPT_MULTIPART_REPLY = 19;

    // Barrier messages
	protected static final byte OFPT_BARRIER_REQUEST = 20;
	protected static final byte OFPT_BARRIER_REPLY = 21;

	// Controller role change request messages
	protected static final byte OFPT_ROLE_REQUEST = 24;
	protected static final byte OFPT_ROLE_REPLY = 25;

	// Asynchronous message configuration
	protected static final byte OFPT_GET_ASYNC_REQUEST = 26;
	protected static final byte OFPT_GET_ASYNC_REPLY = 27;
	protected static final byte OFPT_SET_ASYNC = 28;

	// Meters and rate limiters configuration messages
	protected static final byte OFPT_METER_MOD = 29;

	// Controller role change event messages
	protected static final byte OFPT_ROLE_STATUS = 30;

    // Asynchronous messages
	protected static final byte OFPT_TABLE_STATUS = 31;

	// Request forwarding by the switch
	protected static final byte OFPT_REQUESTFORWARD = 32;
	
    // Bundle operations (multiple messages as a single operation)
	protected static final byte OFPT_BUNDLE_CONTROL = 33;
	protected static final byte OFPT_BUNDLE_ADD_MESSAGE = 34;

	// Controller Status async message
	protected static final byte OFPT_CONTROLLER_STATUS = 35;

    //
    // Other constants
    //
	protected static final int PACKETSIZE = 65000;
    protected static final int PORT_NUMBER = 51510;
	protected static final int CONTROL_HEADER_LENGTH = 2;

    //
    // Preconiguration table info indexes (used by Controller)
    //
    protected static final int DEST_ADDR = 0;
    protected static final int SRC_ADDR = 1;
    protected static final int ROUTER = 2;
    protected static final int ROUTER_IN = 3;
    protected static final int ROUTER_OUT = 4;

	//
	// Forwarding table info indexes (used by Routers)
	//
	protected static final int DEST = 0;
    protected static final int IN = 1;
    protected static final int OUT = 2;

    //
    // Header info indexes (TLV)
    //
    protected static final int TYPE = 0;
    protected static final int LENGTH = 1;

	protected static final byte NETWORK_ID = 1;

	DatagramSocket socket;
	Listener listener;
	CountDownLatch latch;

	Node() {
		latch= new CountDownLatch(1);
		listener= new Listener();
		listener.setDaemon(true);
		listener.start();
	}

	public abstract void onReceipt(DatagramPacket packet);

	protected byte[] makeDataByteArray(String message) {
		byte[] buffer = message.getBytes();
		byte[] data = new byte[CONTROL_HEADER_LENGTH + buffer.length];
		System.arraycopy(buffer, 0, data, CONTROL_HEADER_LENGTH, buffer.length);
		return data;
	}

	protected String getMessage(byte[] data, DatagramPacket packet) {
		int dstLength = data[LENGTH];
		byte[] buffer = new byte[packet.getLength()-CONTROL_HEADER_LENGTH-dstLength];
		System.arraycopy(data, CONTROL_HEADER_LENGTH+dstLength, buffer, 0, buffer.length);
		String message = new String(buffer);
		return message;
	}

	protected String getDestination(DatagramPacket packet) {
        byte[] data = packet.getData();
        int dstLength = data[LENGTH];

        byte[] buffer = new byte[dstLength];
		System.arraycopy(data, CONTROL_HEADER_LENGTH, buffer, 0, buffer.length);
		String destination = new String(buffer);
        return destination;
    }

	/**
	 *
	 * Listener thread
	 *
	 * Listens for incoming packets on a datagram socket and informs registered receivers about incoming packets.
	 */
	class Listener extends Thread {

		/*
		 *  Telling the listener that the socket has been initialized
		 */
		public void go() {
			latch.countDown();
		}

		/*
		 * Listen for incoming packets and inform receivers
		 */
		public void run() {
			try {
				latch.await();
				// Endless loop: attempt to receive packet, notify receivers, etc
				while(true) {
					DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
					socket.receive(packet);
					onReceipt(packet);
				}
			} catch (Exception e) {if (!(e instanceof SocketException)) e.printStackTrace();}
		}
	}
}