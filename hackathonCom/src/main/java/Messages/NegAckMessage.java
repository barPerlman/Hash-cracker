package Messages;

public class NegAckMessage extends Message {
    public NegAckMessage(String teamName, char type, String hash,
                              char original_length, String original_string_start, String original_string_end) {
        this.teamName = teamName;
        this.type = 5;
        this.hash = hash;
        this.original_length = original_length;
        this.original_string_start = original_string_start;
        this.original_string_end = original_string_end;
    }
}
