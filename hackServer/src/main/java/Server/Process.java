package Server;

import Messages.*;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * this class represents thread responsible for handling a received request message and create response message
 */
public class Process implements Runnable {

    private String messageStr;
    private InetAddress dstIPAddress;
    private int port;
    private HelperFunctions helper;
    private DatagramSocket socket;

    public Process(DatagramPacket receivePacket, DatagramSocket socket) {
        this.socket = socket;
        Message m = new Message();
        try {
            messageStr = m.getMessageAsStringFromBytesStream(receivePacket.getData());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        dstIPAddress = receivePacket.getAddress();
        port = receivePacket.getPort();
        helper = new HelperFunctions();

    }

    public void run() {
        //get the actual message to send in response
        Message responseMsg = getResponse();
        //transmitter is the thread responsible for sending the response to the client
        Transmitter transmitter = new Transmitter(responseMsg,dstIPAddress,port,helper,socket);
        transmitter.run();
    }

    private Message getResponse() {
        Message msg = new Message(messageStr);
        if(msg.getType()== Config.DISCOVER_OP_CODE){
            //return an offer message to the relevant received discover message
            return new OfferMessage(msg.getTeamName(),msg.getType(),msg.getHash(),msg.getOriginal_length(),msg.getOriginal_string_start(),msg.getOriginal_string_end());
        }
        else if(msg.getType()== Config.REQUEST_OP_CODE){
            return searchForMacthHash(msg);
        }
        else{
            return new NegAckMessage(msg.getTeamName(),Config.NEGACK_OP_CODE,msg.getHash(),msg.getOriginal_length(),"",msg.getOriginal_string_end());
        }
    }

    private Message searchForMacthHash(Message msg) {
        //ans = the deHash result
        String ans = helper.tryDeHash(msg.getOriginal_string_start(),msg.getOriginal_string_end(),msg.getHash());
        if(ans == null){
            return new NegAckMessage(msg.getTeamName(),Config.NEGACK_OP_CODE,msg.getHash(),msg.getOriginal_length(),"",msg.getOriginal_string_end());
        }
        return new AcknowledgeMessage(msg.getTeamName(),Config.ACK_OP_CODE,msg.getHash(),msg.getOriginal_length(),ans,msg.getOriginal_string_end());
    }
}
