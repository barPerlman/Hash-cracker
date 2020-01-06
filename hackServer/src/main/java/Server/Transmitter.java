package Server;

import Messages.Message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Transmitter implements Runnable {
    private HelperFunctions helper;
    private Message responseMsg;
    private InetAddress dstIPAddress;
    private int port;
    private DatagramSocket socket;
    
    public Transmitter(Message responseMsg, InetAddress dstIPAddress, int port, HelperFunctions helper,DatagramSocket socket) {
        this.dstIPAddress=dstIPAddress;
        this.helper=helper;
        this.responseMsg=responseMsg;
        this.port=port;
        this.socket=socket;
    }

    public void run() {
        byte[] sendData = new byte[586];
        try {
            sendData = responseMsg.getMessageAsByteStream();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dstIPAddress, port);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
