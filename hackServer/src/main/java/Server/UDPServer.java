package Server;

import java.net.*;

class UDPServer
{
    public static void main(String args[]) throws Exception
    {
        DatagramSocket serverSocket = new DatagramSocket(3117);
        byte[] receiveData = new byte[586];
        while(true)     //always listening
        {
            //this thread is responsible to listen to the port
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            //the following is the thread that responsible for processing the received message
            Process processor = new Process(receivePacket, serverSocket);
            processor.run();

        }
    }
}