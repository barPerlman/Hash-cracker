package Messages;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.UnsupportedEncodingException;

/**
 *  the message structure is equal to all types
 *  not mendatory fields in some message is ignored by the rcvr.
 */
public class Message {
    public static final int messageLenInBytes = 586;
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
        this.original_string_end = packetStr.substring(74+original_length);
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
        int actualType = type -'0';
        char newType = (char)actualType;
        return newType;
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
        byte[] bytesMessage = new byte[messageLenInBytes];
        addTeamNameToByteStream(bytesMessage);

        //add the type:
        bytesMessage[32] = new Character(getType()).toString().getBytes("UTF-8")[0];
        int j = 33;
        int lastJ=j;
        //add hash str
        byte[] hashBytes = this.hash.getBytes("UTF-8");
        for(int k = 0;k<40;k++) {
            if (k >= this.hash.length()) {
                bytesMessage[j] = (byte) ' ';
            } else {
                bytesMessage[j] = hashBytes[k];
            }
            j++;
            lastJ=j;
        }
        //add original length
        bytesMessage[lastJ] = new Character(original_length).toString().getBytes("UTF-8")[0];
        j = lastJ + 1;
        //add orig start
        byte[] startBytes = this.original_string_start.getBytes("UTF-8");
        for(int k = 0;k<256;k++) {
            if (k >= this.original_string_start.length()) {
                bytesMessage[j] = (byte) ' ';
            } else {
                bytesMessage[j] = startBytes[k];
            }
            j++;
            lastJ=j;
        }
        //add orig end
        j = lastJ;
        byte[] endBytes = this.original_string_end.getBytes("UTF-8");
        for(int k = 0;k<256;k++) {
            if (k >= this.original_string_end.length()) {
                bytesMessage[j] = (byte) ' ';
            } else {
                bytesMessage[j] = endBytes[k];
            }
            j++;
        }
        return bytesMessage;
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

    /////////////////////////////////////////////////////////////////////////////////////////
    public String getMessageAsStringFromBytesStream(byte[] stream) throws UnsupportedEncodingException {
        //byte[] bytesMessage = new byte[messageLenInBytes];
        String message="";
        //add the team name part into msg:
        message = Base64.encode(stream);
        Message strMessage = new Message(message);
        int newType=message.charAt(32) + '0';
        strMessage.setType((char)newType);

        String msg2= message.substring(0,32);
        msg2+=(char)newType;
        msg2+=message.substring(33);

//        byte[] teamNameBytes = this.teamName.getBytes("UTF-8");
//        for(int i=0;i<32;i++){
//            if(i>=this.teamName.length()){
//                bytesMessage[i] = (byte)' ';
//            }
//            else {
//                bytesMessage[i] = teamNameBytes[i];
//            }
//        }
//        //add the type:
//        bytesMessage[32] = new Character(type).toString().getBytes("UTF-8")[0];
//        int j = 33;
//        int lastJ=j;
//        //add hash str
//        byte[] hashBytes = this.hash.getBytes("UTF-8");
//        for(int k = 0;k<40;k++) {
//            if (k >= this.hash.length()) {
//                bytesMessage[j] = (byte) ' ';
//            } else {
//                bytesMessage[j] = hashBytes[k];
//            }
//            j++;
//            lastJ=j;
//        }
//        //add original length
//        bytesMessage[lastJ] = new Character(original_length).toString().getBytes("UTF-8")[0];
//        j = lastJ + 1;
//        //add orig start
//        byte[] startBytes = this.original_string_start.getBytes("UTF-8");
//        for(int k = 0;k<256;k++) {
//            if (k >= this.original_string_start.length()) {
//                bytesMessage[j] = (byte) ' ';
//            } else {
//                bytesMessage[j] = startBytes[k];
//            }
//            j++;
//            lastJ=j;
//        }
//        //add orig end
//        j = lastJ;
//        byte[] endBytes = this.original_string_end.getBytes("UTF-8");
//        for(int k = 0;k<256;k++) {
//            if (k >= this.original_string_end.length()) {
//                bytesMessage[j] = (byte) ' ';
//            } else {
//                bytesMessage[j] = endBytes[k];
//            }
//            j++;
//        }
//        return bytesMessage;
        return msg2;
    }

    private void setType(char newType) {
        this.type = newType;
    }
}
