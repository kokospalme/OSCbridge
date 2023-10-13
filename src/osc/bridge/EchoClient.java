package osc.bridge;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class EchoClient {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public EchoClient() throws UnknownHostException, SocketException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendEcho(ByteBuffer byteBuffer, int port) throws IOException {
        buf = byteBuffer.array();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(
          packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }
}