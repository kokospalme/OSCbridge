package osc.bridge;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.List;

import com.illposed.osc.*;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

public class OSCbridge {
	
	public static OSCPortOut sender;	//OSC sender port object
	public static OSCPortIn receiver;	//OSC receiver port object
	public static String receiverIpAddress = "127.0.0.1"; // localhost-ip
	public static int oscSendPort = 17002; // sender Port
	static int oscReceivePort = 17003; // receiverport

	    public static void main(String[] args) throws IOException {
	    	sender = new OSCPortOut(InetAddress.getByName(receiverIpAddress), oscSendPort);
	    	
	    	setConnectionOsc();	//setup connection to OSC library
	    	sendExampleMsg();	//send "/beats stream 17003" to make OBC stream to that port
	    	listenForOSC();	//listen for OSC messages
	    }
	    
	    public static boolean setConnectionOsc() throws UnknownHostException{ //connect to OSC
            
            try {
				
				System.out.print("OSCbridge is now connected to " + InetAddress.getByName(receiverIpAddress).toString());
				System.out.println(", Port " + oscSendPort);
				return true;
			} catch (IOException e) {
				System.out.print("could not connect to OSC Address " + InetAddress.getByName(receiverIpAddress).toString());
				System.out.println(", Port " + oscSendPort);
				e.printStackTrace();
				return false;
			} 	
	    }
	    
	    public static boolean closeConnectionOsc() {	//close connection
            try {
				sender.close();
				System.out.println("OSC connection from OSCbridge is now closed.");
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("could not close OSC connection from OSCbridge");
				e.printStackTrace();
				return false;
			}
	    }
	    
	    public static void sendExampleMsg(){ // send an example msg from: https://obc-guide.deepsymmetry.org/open-beat-control/0.1.1/Messages.html#beats
            
	    	
	    	List<?> oscArgs = List.of("stream", oscReceivePort); 
            String addressPattern = "/beats";
            OSCMessage message = new OSCMessage(addressPattern, oscArgs);
            
            if(OSCMessage.isValidAddress(addressPattern) == false) {	//check if address is valid
            	System.out.println("no valid addressPattern.");
            	while(true) {}
            }
            
            System.out.println("sent osc: " + addressPattern + oscArgs);

            // Senden der OSC-Nachricht
            try {
				sender.send(message);
			} catch (IOException | OSCSerializeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public static ByteBuffer parseToBuffer(OSCMessage oscMessage, int bufsize){	//OSCmessage to buffer
	        ByteBuffer buffer = ByteBuffer.allocate(bufsize);
	        OSCSerializerAndParserBuilder oscSerializerAndParserBuilder = new OSCSerializerAndParserBuilder();
	        OSCSerializer serializer = oscSerializerAndParserBuilder.buildSerializer(buffer);
	        buffer.rewind();
	        try {
	            serializer.write(oscMessage);
	            buffer.flip();
	        } catch (OSCSerializeException e) {
	            e.printStackTrace();
	        }
	        return buffer;
	    }

	    public static OSCMessage parseToOSC(ByteBuffer buffer){	//buffer to OSC message
	        OSCSerializerAndParserBuilder oscSerializerAndParserBuilder = new OSCSerializerAndParserBuilder();
	        OSCParser parser = oscSerializerAndParserBuilder.buildParser();
	        OSCPacket oscMessage = null;
	        try {
	            oscMessage = parser.convert(buffer);
	        } catch (OSCParseException e) {
	            e.printStackTrace();
	        }
	        return (OSCMessage) oscMessage;
	    }

	        public static void listenForOSC() {
	            

	            try {
	                // Erstelle einen OSCPortIn auf dem angegebenen Port
	                OSCPortIn receiver = new OSCPortIn(oscReceivePort);

	                // Erstelle einen Listener, um eingehende OSC-Nachrichten zu verarbeiten
	                OSCPacketListener listener = new OSCPacketListener() {
	                    public void handlePacket(OSCPacket packet, InetSocketAddress source) {
	                        if (packet instanceof OSCMessage) {
	                            OSCMessage message = (OSCMessage) packet;
	                            System.out.println("Received OSC message from " + source.getAddress() + ": " + message.getAddress() + " " + message.getArguments());
	                        }
	                    }

						@Override
						public void handlePacket(OSCPacketEvent event) {	//??
							System.out.println("Received OSC msg1");
							
						}

						@Override
						public void handleBadData(OSCBadDataEvent event) {	//??
							System.out.println("Received OSC msg2");
							
						}
	                };
	                receiver.addPacketListener(listener);	//add listener to receiver
	                receiver.startListening();	//start listening

	                System.out.println("Listening for OSC messages on port " + oscReceivePort);

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
}