package Messages;

import Server.Config;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.UnsupportedEncodingException;

/**
 *  the message structure is equal to all types
 *  not mendatory fields in some message is ignored by the rcvr.
 */
public class Message {
    public static final int messageLenInBytes = Config.messagePacketSize;
    protected String teamName="";    //our team name
    protected char type;  //type of message
    protected String hash="";
    protected char original_length;
    protected String original_string_start="";
    protected String original_string_end="";

    //empty constructor
    public Message() {

    }
    public Message(String packetStr){
        this.teamName = packetStr.substring(0,32);

        this.type=packetStr.charAt(32);
        this.hash = packetStr.substring(33,73);
        this.original_length = packetStr.charAt(73);
        this.original_string_start = packetStr.substring(74,74+original_length);
        this.original_string_end = packetStr.substring(74+256,74+256+original_length);
    }

    public Message(String teamName, char type, String hash,
                   char original_length, String original_string_start, String original_string_end) {
        this.teamName = teamName;
        this.type = type;
        this.hash = hash;
        this.original_length = original_length;
        this.original_string_start = original_string_start;
        this.original_string_end = original_string_end;
    }

    public String getTeamName() {
        return teamName;
    }

    public char getType() {
        return this.type;
    }

    public String getHash() {
        return hash;
    }

    public char getOriginal_length() {
        return original_length;
    }

    public String getOriginal_string_start() {
        return original_string_start;
    }

    public String getOriginal_string_end() {
        return original_string_end;
    }

    public byte[] getMessageAsByteStream() throws UnsupportedEncodingException {
        byte[] bytesMessage = new byte[messageLenInBytes];  //the message as byte stream
        //add the team name
        addTeamNameToByteStream(bytesMessage);
        //add the type:
        bytesMessage[32] = (byte)this.type;
        int j = 33;     //current index in the building byte message
        int lastJ=j;
        //add hash str
        lastJ = addHashAsByteStream(bytesMessage, j, lastJ, this.hash, 40);
        //add original length
        bytesMessage[lastJ] = (byte)original_length;
        j = lastJ + 1;
        //add orig start
        lastJ = addOrigStartAsByteStream(bytesMessage, j, lastJ, this.original_string_start, 256);
        //add orig end
        j = lastJ;
        addOrigEndAsByteStream(bytesMessage, j);
        return bytesMessage;
    }

    private void addOrigEndAsByteStream(byte[] bytesMessage, int j) throws UnsupportedEncodingException {
        byte[] endBytes = this.original_string_end.getBytes("UTF-8");
        for(int k = 0;k<256;k++) {
            if (k >= this.original_string_end.length()) {
                bytesMessage[j] = (byte) ' ';
            } else {
                bytesMessage[j] = endBytes[k];
            }
            j++;
        }
    }

    private int addOrigStartAsByteStream(byte[] bytesMessage, int j, int lastJ, String original_string_start, int i) throws UnsupportedEncodingException {
        byte[] startBytes = original_string_start.getBytes("UTF-8");
        for (int k = 0; k < i; k++) {
            if (k >= original_string_start.length()) {
                bytesMessage[j] = (byte) ' ';
            } else {
                bytesMessage[j] = startBytes[k];
            }
            j++;
            lastJ = j;
        }
        return lastJ;
    }

    private int addHashAsByteStream(byte[] bytesMessage, int j, int lastJ, String hash, int i) throws UnsupportedEncodingException {
        byte[] hashBytes = hash.getBytes("UTF-8");
        for (int k = 0; k < i; k++) {
            if (k >= hash.length()) {
                bytesMessage[j] = (byte) ' ';
            } else {
                bytesMessage[j] = hashBytes[k];
            }
            j++;
            lastJ = j;
        }
        return lastJ;
    }

    private void addTeamNameToByteStream(byte[] bytesMessage) throws UnsupportedEncodingException {
        //add the team name part into msg:
        byte[] teamNameBytes = this.teamName.getBytes("UTF-8");
        for(int i=0;i<32;i++){
            if(i>=this.teamName.length()){
                bytesMessage[i] = (byte)' ';
            }
            else {
                bytesMessage[i] = teamNameBytes[i];
            }
        }
    }

    public String getMessageAsStringFromBytesStream(byte[] stream) throws UnsupportedEncodingException {
        String messageString = new String(stream);    //the converted byte array to string
        return messageString;
    }

}
