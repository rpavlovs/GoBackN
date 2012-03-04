package reciever;

public class reciever {

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
	}
	
	

}
