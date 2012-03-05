package receiver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

public class receiver {

	private final int SeqNumModulo = 32;
	
	InetAddress networkEmulatorAddr;
	int networkEmulatorACKPort;
	int N = 10;
	int maxDataLength = 500;
	int maxPacketSize;
	int seqNumExpected = 0;
	int ACKseqNumToSend = -1;
	Queue<packet> packetsReceived = new LinkedList<packet>();
	DatagramSocket receiverSocket;
	MyLogger arrivalLog;

	receiver(String networkEmulatorAddr, int networkEmulatorACKPort, int receiverPort) {
		try {
			this.networkEmulatorAddr = InetAddress.getByName(networkEmulatorAddr);
			this.receiverSocket = new DatagramSocket(receiverPort);
			maxPacketSize = packet.createPacket(0, new String( new char[500]) ).getUDPdata().length;
			arrivalLog = new MyLogger("arrival.log", "Arrival Log");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		this.networkEmulatorACKPort = networkEmulatorACKPort;
	}

	public void receive(String fileName) throws Exception {
		
		byte[] receiveData = new byte[maxPacketSize];
		DatagramPacket receivedPacketUDP = new DatagramPacket(receiveData, maxPacketSize);
		
		while(true) {
			receiverSocket.receive(receivedPacketUDP);
			packet recievedPacket = packet.parseUDPdata(receivedPacketUDP.getData());
			arrivalLog.write(Integer.toString(recievedPacket.getSeqNum()));
			
			// Break when EOT Received
			if(recievedPacket.getType() == 2) break;
			
			// If the seqNum is correct, store the packet, update ACKtoSend, seqNumExpected
			if(recievedPacket.getSeqNum() == seqNumExpected) {
				packetsReceived.add(recievedPacket);
				ACKseqNumToSend = recievedPacket.getSeqNum();
				seqNumExpected = (seqNumExpected + 1) % SeqNumModulo;
			}
			
			// Send ACK
			if(ACKseqNumToSend != -1) {
				packet ack = packet.createACK(ACKseqNumToSend);
				byte[] sendData = ack.getUDPdata();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
						networkEmulatorAddr, networkEmulatorACKPort);
				receiverSocket.send(sendPacket);
			}
		}
		
		storePacketsToFile(fileName);
		
		//Send EOT back
		packet eot = packet.createEOT(0);
		byte[] sendData = eot.getUDPdata();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
				networkEmulatorAddr, networkEmulatorACKPort);
		receiverSocket.send(sendPacket);

	}

	void storePacketsToFile(String fileName) throws Exception {
		FileWriter outFile = new FileWriter(fileName);
		PrintWriter out = new PrintWriter(outFile);
		
		for (packet p : packetsReceived) {
			out.print(p.getData());
		}
			
		out.close();
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
			receiver r = new receiver( args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]) );
			r.receive( args[3] );
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}


}