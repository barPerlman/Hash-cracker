package Client;

import DataStructures.Connections;
import Messages.DiscoverMessage;
import Messages.Message;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class UDPClient
{
public static final int messageLenInBytes = 586;

    public static void main(String args[]) throws Exception
    {
        String teamName = "Anony Moose Team";
        InetAddress localHostIPAddress = InetAddress.getByName("127.0.0.1");
        int port = 3117;
        InetAddress broadcastIP = InetAddress.getByName("255.255.255.255");
        Connections connections = new Connections();
        HelperFunctions helper = new HelperFunctions();


        InputPair hp = greeting();   //hp holds the hash and the size of the input string

        //the following send discover and wait for offers for 1 sec.
        long currTime = System.currentTimeMillis();
        long endTime = currTime + 1000;
        //send DISCOVER message
        //create socket for discover and offers
        DatagramSocket clientSocket = new DatagramSocket(); //create udp socket
        sendMessage(clientSocket,new DiscoverMessage(teamName),broadcastIP,port);
        //wait for OFFERs
        while (System.currentTimeMillis() < endTime){
            receiveOfferMessage(connections,clientSocket);
        }
        clientSocket.close();





        //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));


//        while (true) {
//            String sentence = inFromUser.readLine();
//
//            sendData = sentence.getBytes();
//
//            //packet to send
//            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, localHostIPAddress, port);
//            clientSocket.send(sendPacket);
//            //the received packet
//            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//            clientSocket.receive(receivePacket);
//            String modifiedSentence = new String(receivePacket.getData());
//            System.out.println("FROM SERVER:" + modifiedSentence);
//
//
//        }
    }

    private static void receiveOfferMessage(Connections con, DatagramSocket clientSocket) throws IOException {
        //the received packet
        byte[] receiveData = new byte[messageLenInBytes];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        /////////////////////////////////////////////////////////////////
        String receivedData = new String(receivePacket.getData());
        System.out.println(receivedData);
        if(getActualType(receivedData.charAt(32))==1){
            InetAddress serverAddress = receivePacket.getAddress();
            con.setNewServer(serverAddress.toString(),3117);
        }

    }

    private static int getActualType(char charAt) {
        return charAt + '0';
    }

    private static void sendMessage(DatagramSocket clientSocket, Message msg, InetAddress dstAddr, int port) throws IOException {

        //create socket and send a message
        byte[] sendData = msg.getMessageAsByteStream(); //get message stream to send
        //wrap the message with udp header
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dstAddr, port);
        clientSocket.send(sendPacket);  //send the packet to the socket

    }

    /**
     * the following get the hash output and the length of the input string from the user
     * @throws IOException
     */
    private static InputPair greeting() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Anony Moose Team. Please enter the hash:");
        int hashLen = 40;
        String hash;
        boolean is_correctLen;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        do{     //get the hash from the user

            hash = inFromUser.readLine();
            if(!(is_correctLen = (hash.length()==hashLen))){
                System.out.println("insert a 40 byte of hash please");
            }
        }while (!is_correctLen);

        System.out.println("Please enter the input string length:");
        int inputStrLen = sc.nextInt();
        return new InputPair(hash,inputStrLen);
    }
}