package Client;

import DataStructures.Connections;
import Messages.DiscoverMessage;
import Messages.Message;
import Messages.RequestMessage;

import java.io.*;
import java.net.*;

import java.util.Scanner;

class UDPClient
{
    public static boolean found = false;
    public static int nacksCount = 0;
    public static void main(String args[]) throws Exception
    {
        //IP we works with
        InetAddress localHostIPAddress = InetAddress.getByName("127.0.0.1");
        InetAddress broadcastIP = InetAddress.getByName("255.255.255.255");

        Connections connections = new Connections();    //servers connected data structure
        HelperFunctions helper = new HelperFunctions();

        InputPair hp = greeting();   //hp holds the hash and the size of the input string

        //the following send discover and wait for offers for timeOutForOffers sec.
        long currTime = System.currentTimeMillis();
        long endTime = currTime + Config.timeOutForOffers;
        //create socket for discover and offers
        DatagramSocket clientSocket = new DatagramSocket(); //create udp socket
        clientSocket.setSoTimeout(Config.timeOutForOffers);
        //send DIscover message
        sendMessage(clientSocket,new DiscoverMessage(Config.teamName),broadcastIP,Config.listeningPort);
        //wait for OFFERs for 1 second
        addReceivedOffersToDS(connections, endTime, clientSocket);

        if(connections.getHostsAddresses().size()==0){
            System.out.println("Couldn't find servers for the task");
            System.exit(1);
        }
        //get the domains of possible strings:
        String[] domainsArr = helper.divideToDomains(hp.getHashLen(),connections.getHostsAddresses().size());

        //send the domains to the offered servers
        clientSocket.setSoTimeout(Config.timeOutForSearcesInDomain);
        sendRequestsToAll(Config.listeningPort, connections, hp, clientSocket, domainsArr);

        //receive responses for the requests from servers for only timeOutForSearcesInDomain sec.:
        currTime = System.currentTimeMillis();
        endTime = currTime + Config.timeOutForSearcesInDomain;
        String ans = "hash source were not found!";
        ans = getResponse(connections, endTime, clientSocket, ans);
        clientSocket.close();
        System.out.println(ans);
        }

    private static String getResponse(Connections connections, long endTime, DatagramSocket clientSocket, String ans) throws IOException {
        while (System.currentTimeMillis() < endTime && !found && nacksCount<connections.getHostsAddresses().size()){
            try {
                //get offers and add the offers servers to DB
                ans = receiveResponse(clientSocket);
            }catch (SocketTimeoutException e){
            }
        }
        return ans;
    }

    private static void addReceivedOffersToDS(Connections connections, long endTime, DatagramSocket clientSocket) throws IOException {
        while (System.currentTimeMillis() < endTime){
            try {
                //get offers and add the offers servers to DB
                receiveOfferMessage(connections,clientSocket);
            }catch (SocketTimeoutException e){
            }
        }
    }


    private static String receiveResponse(DatagramSocket clientSocket) throws IOException {
        //the received packet
        byte[] receiveData = new byte[Config.messagePacketSize];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String receivedData = new String(receivePacket.getData());

        //verify this message is response for us
        if(isMyTeam(receivedData)) {
            if (isAckMessage(receivedData)) {
                found = true;
                Message msg = new Message(receivedData);
                return msg.getOriginal_string_start();
            }
            else if(isNackMessage(receivedData)){
                nacksCount++;
            }
        }
        return "hash source were not found";
    }

    private static boolean isNackMessage(String receivedData) {
        Message msg = new Message(receivedData);
        return msg.getType()==Config.NEGACK_OP_CODE;
    }

    private static boolean isAckMessage(String receivedData) {
        Message msg = new Message(receivedData);
        return msg.getType()==Config.ACK_OP_CODE;
    }

    private static void sendRequestsToAll(int port, Connections connections, InputPair hp, DatagramSocket clientSocket, String[] domainsArr) throws IOException {
        int domIndex=0;
        for(InetAddress serverIP : connections.getHostsAddresses().keySet()){
            char c = (char) hp.getHashLen();
            RequestMessage msg = new RequestMessage(Config.teamName,Config.REQUEST_OP_CODE,hp.getHash(),c,domainsArr[domIndex],domainsArr[domIndex+1]);
            sendMessage(clientSocket, msg,serverIP,port);
            domIndex =domIndex+1;
        }
    }

    /**
     * receive packets and add the offers to offers DS
     * @param con
     * @param clientSocket
     * @throws IOException
     */
    private static void receiveOfferMessage(Connections con, DatagramSocket clientSocket) throws IOException {
        //the received packet
        byte[] receiveData = new byte[Config.messagePacketSize];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String receivedData = new String(receivePacket.getData());
        if(isOfferMessage(receivedData)&&isMyTeam(receivedData)){
            InetAddress serverAddress = receivePacket.getAddress();
            con.setNewServer(serverAddress,3117);
        }

    }

    private static boolean isMyTeam(String receivedData) {
        Message msg = new Message(receivedData);
        String teamNamePaddedWithSpaces = Config.teamName;
        for (int i=0;i<32-Config.teamName.length();i++){
            teamNamePaddedWithSpaces+=" ";
        }
        return msg.getTeamName().equals(teamNamePaddedWithSpaces);
    }

    private static boolean isOfferMessage(String receivedData) {

        return getActualType(receivedData.charAt(32))== Config.OFFER_OP_CODE;
    }

    private static char getActualType(char charAt) {
        return (char)charAt;
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
        System.out.println("Welcome to "+Config.teamName+". Please enter the hash:");
        int hashLen = 40;
        String hash;
        boolean is_correctLen;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        do{     //get the hash from the user

            hash = inFromUser.readLine();
            int len = hash.length();
            if(!(is_correctLen = (hash.length()==hashLen))){
                System.out.println("insert a 40 byte of hash please");
            }
        }while (!is_correctLen);

        System.out.println("Please enter the input string length:");
        int inputStrLen = sc.nextInt();
        return new InputPair(hash,inputStrLen);
    }
}