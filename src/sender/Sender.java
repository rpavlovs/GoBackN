package sender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

public class Sender {

	InetAddress networkEmulatorAddr;
	int networkEmulatorPort;
	int N = 10;
	int maxDataLength = 500;
	int ACKsize;
	int ACKSeqExpected = 0;
	MyTimer timer = new MyTimer( new Long(10000) );
	Queue<Packet> packetsWindow = new LinkedList<Packet>();
	Queue<Packet> packetsToSend = new LinkedList<Packet>();
	DatagramSocket senderSocket;
	MyLogger seqNumLog, ACKLog;

	Sender(String networkEmulatorAddr, int networkEmulatorPort,	int senderACKPort) {
		try {
			this.networkEmulatorAddr = InetAddress.getByName(networkEmulatorAddr);
			this.senderSocket  = new DatagramSocket(senderACKPort);
			ACKsize = Packet.createACK(0).getUDPdata().length;
			seqNumLog = new MyLogger("seqnum.log", "Sequence number log");
			ACKLog = new MyLogger("ack.log", "Acknowlegement log");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		this.networkEmulatorPort = networkEmulatorPort;
	}

	public void send(String fileName) throws Exception {

		// Read the file and fill the queue of packets to be sent
		BufferedReader in = new BufferedReader(new FileReader(fileName));

		int seqNum = 0;
		while(true) {
			int dataLength = 0;
			String data = "";
			int ch;
			++seqNum;
			while ((ch = in.read()) != -1 && dataLength < maxDataLength) {
				data += ch;
				++dataLength;
			}
			if(dataLength == 0) break;
			try {
				packetsToSend.add( Packet.createPacket(seqNum, data) );
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		in.close();
		
		while(packetsToSend.size() != 0 && packetsWindow.size() != 0){
			sendPackets();
			recieveACK();
		}
		
		sendEOT();
	}
	
	// Send/re-send all packets possible at this time
	void sendPackets() throws IOException {		
		// Start the timer
		timer.start();
		
		// Fill up the packet window
		while(packetsToSend.size() != 0 && packetsWindow.size() < N) {
			Packet nextToSend = packetsToSend.poll();
			packetsWindow.add(nextToSend);
		}
		
		// Send all packet in packet window through UPD
		for (Packet p : packetsWindow) {
			seqNumLog.write( Integer.toString(p.getSeqNum()) );
			byte[] sendData = p.getUDPdata();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
					networkEmulatorAddr, networkEmulatorPort);
			senderSocket.send(sendPacket);
		}
	}
	
	// Listen for acknowledgements until timer expires
	void recieveACK() throws Exception {
		byte[] ackUPD = new byte[ACKsize];
		DatagramPacket recievePacket = new DatagramPacket(ackUPD, ACKsize);
		while( !timer.isExpired() ) {
			senderSocket.receive(recievePacket);
			Packet ack = Packet.parseUDPdata(recievePacket.getData());
			ACKLog.write( Integer.toString(ack.getSeqNum()) );
			if(ack.getSeqNum() == packetsWindow.peek().getSeqNum()) {
				packetsWindow.poll();
			}
		}
	}
	
	void sendEOT() throws Exception {
		byte[] sendData = Packet.createEOT(0).getUDPdata();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
				networkEmulatorAddr, networkEmulatorPort);
		senderSocket.send(sendPacket);
	}

	/**
	 * @param args
	 * 
	 * <host address of the network emulator>
	 * <UDP port number used by the emulator to receive data from the sender>
	 * <UDP port number used by the sender to receive ACKs from the emulator>
	 * <name of the file to be transferred>
	 * 
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
			Sender s = new Sender(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			s.send( args[3] );
		} catch( Exception e ) {
			System.err.println(e.getMessage());
		}
	}

}
