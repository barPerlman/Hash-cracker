package Messages;

public class RequestMessage extends Message {
    public RequestMessage(String teamName, char type, String hash,
                          char original_length, String original_string_start, String original_string_end){
        super(teamName,type,hash,original_length,original_string_start,original_string_end);
        this.type = 3;
    }
}
