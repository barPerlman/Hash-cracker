import java.io.*;
import java.net.*;

class UDPClient
{
    public static void main(String args[]) throws Exception
    {

        greeting();



        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        int port = 3117;
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        while (true) {
            String sentence = inFromUser.readLine();
            sendData = sentence.getBytes();

            //packet to send
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);
            //the received packet
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + modifiedSentence);
            //clientSocket.close();

        }
    }

    private static void greeting() {
        System.out.println("Welcome to CS Team. Please enter the hash:");
    }
}