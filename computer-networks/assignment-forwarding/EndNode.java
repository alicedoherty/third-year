import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class EndNode extends Node {
    private InetSocketAddress dstAddress;

    EndNode() {
        try {
            socket = new DatagramSocket(PORT_NUMBER);
            listener.go();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public synchronized void onReceipt(DatagramPacket packet) {
    }

    public synchronized void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the node you want to send a message to: ");


    }

    private void start() {
        System.out.println("EndNode program starting...");

        Scanner scanner = new Scanner(System.in);
        boolean finished = false;

        while(!finished) {
            System.out.println("Do you want to send or receive a message?");
            System.out.println("Enter SEND or WAIT: ");

            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("SEND")) {
                sendMessage();
                finished = true;
            } else if (choice.equalsIgnoreCase("WAIT")) {
                System.out.println("Waiting for messages...");
                finished = true;
            } else if (choice.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye ;(");
                finished = true;
            } else {
                System.out.println("Invalid input.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
		try {
			(new EndNode()).start();
			System.out.println("EndNode program completed.");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}
