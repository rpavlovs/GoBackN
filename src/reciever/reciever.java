package receiver;

import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

public class receiver {

	
	InetAddress networkEmulatorAddr;
	int networkEmulatorACKPort;
	int N = 10;
	int maxDataLength = 500;
	int ACKsize;
	int ACKSeqExpected = 0;
	Queue<packet> packetsWindow = new LinkedList<packet>();
	Queue<packet> packetsReceived = new LinkedList<packet>();
	DatagramSocket receiverSocket;
	
	receiver(String networkEmulatorAddr, int networkEmulatorACKPort, int receiverPort) {
		try {
			this.networkEmulatorAddr = InetAddress.getByName(networkEmulatorAddr);
			this.receiverSocket = new DatagramSocket(receiverPort);
			ACKsize = packet.createACK(0).getUDPdata().length;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		this.networkEmulatorACKPort = networkEmulatorACKPort;
	}
	
	
	
	
	public void receive(String fileName) throws Exception {
	
	while(packetsReceived.size() != 0 && packetsWindow.size() != 0) {
		receivePackets();
		sendACK();
	}
	
	
	}
	
	
	
	
	void receivePackets() throws IOException {
		
		byte[] receiveData = 
	}
	
	
	
	
	void sendACK() throws Exception {
		
	}
	
	
	
	
	void logArrival(int seqNum) {
		System.out.println("Receiving: " + seqNum);
	}
	
	/**
	 * @param args
	 * 
	 * <hostname for the network emulator>
	 * <UDP port number used by the link emulator to receive ACKs from the receiver>
	 * <UDP port number used by the receiver to receive data from the emulator>
	 * <name of the file into which the received data is written>
	 */
	public static void main(String[] args) {
		
		if( args.length != 4) {
				System.err.println("Usage:\n\tsender \t<host address of the network emulator>\n" +
						"\t\t<UDP port number used by the emulator to receive data from the sender>\n" +
						"\t\t<UDP port number used by the sender to receive ACKs from the emulator>\n" +
						"\t\t<name of the file to be transferred>\n");
				System.exit(1);
		}
		
		try {
			receiver r = new receiver( args[0], Integer.parseInt(args[1]), Integer.parstInt(args[2]) );
			r.receive( args[3] );
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	

}
