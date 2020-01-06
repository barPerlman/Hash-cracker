package Messages;

public class OfferMessage extends Message {
    public OfferMessage(String teamName, char type, String hash,
                   char original_length, String original_string_start, String original_string_end) {
        this.teamName = teamName;
        this.hash = "";
        this.type = 2;
        this.original_length = 0;
        this.original_string_start = "";
        this. original_string_end = "";    }
}
