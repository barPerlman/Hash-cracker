package Messages;


/**
 * the following class represents a discover message
 */
public class DiscoverMessage extends Message {
    public DiscoverMessage(String teamName) {
        this.teamName = teamName;
        this.hash = "";
        this.type = 1;
        this.original_length = 0;
        this.original_string_start = "";
        this. original_string_end = "";
    }



}
